package cliente;

import comun.ConfiguracionPoliticas;
import comun.EstadoConexion;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cliente TCP con políticas de reconexión, reintentos y timeout
 */
public class Cliente {
    private static final String HOST_DEFAULT = "localhost";
    private static final int PUERTO_DEFAULT = 8080;

    private final String host;
    private final int puerto;
    private final String nombreCliente;

    private final ConfiguracionPoliticas configuracion;
    private final ConfiguracionTimeout configuracionTimeout;
    private final PoliticaReconexion politicaReconexion;
    private final PoliticaReintentos politicaReintentos;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private final AtomicBoolean conectado;
    private final AtomicBoolean ejecutando;
    private Thread hiloLectura;

    public Cliente(String nombreCliente, String host, int puerto,
                   ConfiguracionPoliticas configuracion,
                   ConfiguracionTimeout configuracionTimeout) {
        this.nombreCliente = nombreCliente;
        this.host = host;
        this.puerto = puerto;
        this.configuracion = configuracion;
        this.configuracionTimeout = configuracionTimeout;

        this.politicaReconexion = new PoliticaReconexion();
        this.politicaReintentos = new PoliticaReintentos(configuracion);

        this.conectado = new AtomicBoolean(false);
        this.ejecutando = new AtomicBoolean(true);
    }

    public Cliente(String nombreCliente, String host, int puerto) {
        this(nombreCliente, host, puerto,
             new ConfiguracionPoliticas(),
             new ConfiguracionTimeout());
    }

    public Cliente(String nombreCliente) {
        this(nombreCliente, HOST_DEFAULT, PUERTO_DEFAULT);
    }

    /**
     * Conecta al servidor aplicando políticas de reintentos y timeout
     */
    public boolean conectar() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║      CLIENTE: " + nombreCliente + "                 ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Servidor: " + host + ":" + puerto + "            ║");
        System.out.println("║  Max reintentos: " + configuracion.getMaxReintentos() + "                   ║");
        System.out.println("║  Timeout: " + configuracionTimeout.getTimeoutConexion() + "ms                   ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // Aplicar política de reintentos
        while (politicaReintentos.puedeReintentar() && ejecutando.get()) {
            politicaReintentos.registrarIntento();
            politicaReconexion.actualizarEstado(EstadoConexion.CONECTANDO);

            if (intentarConexion()) {
                politicaReconexion.actualizarEstado(EstadoConexion.CONECTADO);
                conectado.set(true);
                politicaReintentos.reiniciarContador();

                // Iniciar hilo de lectura
                iniciarHiloLectura();
                return true;
            }

            // Si no se conectó, esperar antes de reintentar
            if (politicaReintentos.puedeReintentar()) {
                politicaReconexion.actualizarEstado(EstadoConexion.REINTENTANDO);
                politicaReintentos.esperarAntesDeReintentar();
            }
        }

        // No se pudo conectar después de todos los reintentos
        politicaReconexion.actualizarEstado(EstadoConexion.TIMEOUT);
        System.err.println("[CLIENTE " + nombreCliente + "] ❌ No se pudo conectar después de " +
                         politicaReintentos.getIntentosRealizados() + " intentos");
        return false;
    }

    /**
     * Intenta una conexión única con timeout
     */
    private boolean intentarConexion() {
        try {
            System.out.println("[CLIENTE " + nombreCliente + "] Intentando conectar a " +
                             host + ":" + puerto + "...");

            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(host, puerto),
                          (int) configuracionTimeout.getTimeoutConexion());

            // Configurar timeouts de lectura/escritura
            socket.setSoTimeout((int) configuracionTimeout.getTimeoutLectura());

            // Configurar flujos
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("[CLIENTE " + nombreCliente + "] ✓ Conectado exitosamente");
            return true;

        } catch (SocketTimeoutException e) {
            System.err.println("[CLIENTE " + nombreCliente + "] ⏱ Timeout de conexión");
            politicaReconexion.actualizarEstado(EstadoConexion.TIMEOUT);
            cerrarRecursos();
            return false;

        } catch (IOException e) {
            System.err.println("[CLIENTE " + nombreCliente + "] ✗ Error de conexión: " + e.getMessage());
            politicaReconexion.actualizarEstado(EstadoConexion.ERROR);
            cerrarRecursos();
            return false;
        }
    }

    /**
     * Inicia el hilo que lee mensajes del servidor
     */
    private void iniciarHiloLectura() {
        hiloLectura = new Thread(() -> {
            try {
                String mensaje;
                while (conectado.get() && ejecutando.get() && (mensaje = entrada.readLine()) != null) {
                    System.out.println("[SERVIDOR → " + nombreCliente + "] " + mensaje);
                }
            } catch (IOException e) {
                if (conectado.get()) {
                    System.err.println("[CLIENTE " + nombreCliente + "] Conexión perdida: " + e.getMessage());
                    manejarDesconexion();
                }
            }
        }, "Hilo-Lectura-" + nombreCliente);

        hiloLectura.start();
    }

    /**
     * Maneja la desconexión inesperada y aplica política de reconexión
     */
    private void manejarDesconexion() {
        conectado.set(false);
        politicaReconexion.actualizarEstado(EstadoConexion.DESCONECTADO);
        cerrarRecursos();

        // Intentar reconectar si está habilitado
        if (politicaReconexion.debeReconectar() && ejecutando.get()) {
            System.out.println("[CLIENTE " + nombreCliente + "] Intentando reconexión...");
            politicaReintentos.reiniciarContador();
            conectar();
        }
    }

    /**
     * Envía un mensaje al servidor
     */
    public void enviarMensaje(String mensaje) {
        if (!conectado.get() || salida == null) {
            System.err.println("[CLIENTE " + nombreCliente + "] No conectado. No se puede enviar mensaje.");
            return;
        }

        try {
            salida.println(mensaje);
            System.out.println("[CLIENTE " + nombreCliente + " → SERVIDOR] " + mensaje);
        } catch (Exception e) {
            System.err.println("[CLIENTE " + nombreCliente + "] Error enviando mensaje: " + e.getMessage());
            manejarDesconexion();
        }
    }

    /**
     * Desconecta del servidor
     */
    public void desconectar() {
        System.out.println("[CLIENTE " + nombreCliente + "] Desconectando...");
        ejecutando.set(false);
        conectado.set(false);
        politicaReconexion.setReconexionHabilitada(false);

        enviarMensaje("DESCONECTAR");
        cerrarRecursos();

        if (hiloLectura != null && hiloLectura.isAlive()) {
            try {
                hiloLectura.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("[CLIENTE " + nombreCliente + "] Desconectado");
    }

    /**
     * Cierra los recursos de red
     */
    private void cerrarRecursos() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("[CLIENTE " + nombreCliente + "] Error cerrando recursos: " + e.getMessage());
        }
    }

    /**
     * Modo interactivo para enviar mensajes
     */
    public void modoInteractivo() {
        if (!conectado.get()) {
            System.out.println("[CLIENTE " + nombreCliente + "] Debe conectarse primero");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n[CLIENTE " + nombreCliente + "] Modo interactivo. Escriba mensajes (escriba 'salir' para terminar):");

        while (conectado.get() && ejecutando.get()) {
            System.out.print(nombreCliente + "> ");
            String mensaje = scanner.nextLine();

            if (mensaje.equalsIgnoreCase("salir")) {
                break;
            }

            enviarMensaje(mensaje);
        }
    }

    // Getters y configuración de políticas
    public void setReconexionHabilitada(boolean habilitada) {
        politicaReconexion.setReconexionHabilitada(habilitada);
    }

    public void setReintentosHabilitados(boolean habilitados) {
        politicaReintentos.setHabilitada(habilitados);
    }

    public boolean isConectado() {
        return conectado.get();
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public EstadoConexion getEstadoConexion() {
        return politicaReconexion.getEstadoActual();
    }

    public PoliticaReconexion getPoliticaReconexion() {
        return politicaReconexion;
    }

    public PoliticaReintentos getPoliticaReintentos() {
        return politicaReintentos;
    }
}
