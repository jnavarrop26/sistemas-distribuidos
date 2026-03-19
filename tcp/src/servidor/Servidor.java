package servidor;

import comun.ConfiguracionPoliticas;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servidor TCP con políticas de reinicio automático
 */
public class Servidor {
    private static final int PUERTO_DEFAULT = 8080;

    private final int puerto;
    private final ConfiguracionPoliticas configuracion;
    private final PoliticaReinicio politicaReinicio;
    private final MonitorServidor monitor;

    private ServerSocket serverSocket;
    private final List<ManejadorCliente> clientes;
    private final AtomicBoolean ejecutando;
    private final AtomicInteger contadorClientes;
    private Thread hiloAceptacion;

    public Servidor(int puerto, ConfiguracionPoliticas configuracion) {
        this.puerto = puerto;
        this.configuracion = configuracion;
        this.politicaReinicio = new PoliticaReinicio(configuracion);
        this.monitor = new MonitorServidor(this, politicaReinicio);

        this.clientes = new ArrayList<>();
        this.ejecutando = new AtomicBoolean(false);
        this.contadorClientes = new AtomicInteger(0);
    }

    public Servidor(int puerto) {
        this(puerto, new ConfiguracionPoliticas());
    }

    public Servidor() {
        this(PUERTO_DEFAULT, new ConfiguracionPoliticas());
    }

    /**
     * Inicia el servidor
     */
    public void iniciar() {
        if (ejecutando.get()) {
            System.out.println("[SERVIDOR] El servidor ya está ejecutándose");
            return;
        }

        try {
            serverSocket = new ServerSocket(puerto);
            ejecutando.set(true);
            politicaReinicio.marcarServidorActivo();

            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║      SERVIDOR INICIADO                   ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  Puerto: " + puerto + "                            ║");
            System.out.println("║  Reinicio automático: " +
                             (politicaReinicio.isReinicioHabilitado() ? "SÍ" : "NO") + "             ║");
            System.out.println("╚══════════════════════════════════════════╝");

            // Iniciar monitor si no está ejecutándose
            if (!monitor.isAlive()) {
                monitor.start();
            }

            // Iniciar hilo de aceptación de clientes
            hiloAceptacion = new Thread(this::aceptarClientes, "Hilo-Aceptacion");
            hiloAceptacion.start();

        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error al iniciar servidor: " + e.getMessage());
            ejecutando.set(false);
            politicaReinicio.marcarServidorInactivo();
        }
    }

    /**
     * Acepta conexiones de clientes
     */
    private void aceptarClientes() {
        while (ejecutando.get()) {
            try {
                Socket socketCliente = serverSocket.accept();
                int idCliente = contadorClientes.incrementAndGet();

                ManejadorCliente manejador = new ManejadorCliente(socketCliente, idCliente);
                synchronized (clientes) {
                    clientes.add(manejador);
                }
                manejador.start();

                System.out.println("[SERVIDOR] Nuevo cliente aceptado. Total de clientes: " +
                                 clientes.size());

            } catch (IOException e) {
                if (ejecutando.get()) {
                    System.err.println("[SERVIDOR] Error aceptando cliente: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Detiene el servidor
     */
    public void detener() {
        if (!ejecutando.get()) {
            System.out.println("[SERVIDOR] El servidor ya está detenido");
            return;
        }

        System.out.println("[SERVIDOR] Deteniendo servidor...");
        ejecutando.set(false);
        politicaReinicio.marcarServidorInactivo();

        // Desconectar todos los clientes
        synchronized (clientes) {
            for (ManejadorCliente cliente : clientes) {
                cliente.desconectar();
            }
            clientes.clear();
        }

        // Cerrar server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error al cerrar server socket: " + e.getMessage());
        }

        // Esperar a que termine el hilo de aceptación
        if (hiloAceptacion != null && hiloAceptacion.isAlive()) {
            try {
                hiloAceptacion.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("[SERVIDOR] Servidor detenido");
    }

    /**
     * Reinicia el servidor (usado por el monitor)
     */
    public synchronized void reiniciar() {
        System.out.println("[SERVIDOR] === REINICIANDO SERVIDOR ===");
        detener();

        // Esperar un poco antes de reiniciar
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        iniciar();
    }

    /**
     * Habilita o deshabilita el reinicio automático
     */
    public void setReinicioAutomatico(boolean habilitado) {
        politicaReinicio.setReinicioHabilitado(habilitado);
    }

    /**
     * Detiene el servidor y el monitor
     */
    public void apagar() {
        System.out.println("[SERVIDOR] Apagado completo del servidor...");
        detener();
        monitor.detenerMonitoreo();
    }

    // Getters
    public boolean isEjecutando() {
        return ejecutando.get();
    }

    public int getPuerto() {
        return puerto;
    }

    public PoliticaReinicio getPoliticaReinicio() {
        return politicaReinicio;
    }

    public int getCantidadClientes() {
        synchronized (clientes) {
            return clientes.size();
        }
    }

    public ConfiguracionPoliticas getConfiguracion() {
        return configuracion;
    }
}
