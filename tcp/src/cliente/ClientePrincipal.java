package cliente;

import comun.ConfiguracionPoliticas;

import java.util.Scanner;

/**
 * Clase principal ejecutable para el Cliente
 * Proporciona una interfaz interactiva para conectar y enviar mensajes
 */
public class ClientePrincipal {
    private static Cliente cliente;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║         CLIENTE TCP - INTERFAZ PRINCIPAL       ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        // Configurar cliente
        configurarCliente();

        // Mostrar menú principal
        mostrarMenu();

        scanner.close();
    }

    private static void configurarCliente() {
        System.out.print("Ingrese el nombre del cliente [default: MiCliente]: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            nombre = "MiCliente";
        }

        System.out.print("Ingrese el host del servidor [default: localhost]: ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) {
            host = "localhost";
        }

        System.out.print("Ingrese el puerto [default: 8080]: ");
        String puertoStr = scanner.nextLine().trim();
        int puerto = 8080;
        if (!puertoStr.isEmpty()) {
            try {
                puerto = Integer.parseInt(puertoStr);
            } catch (NumberFormatException e) {
                System.out.println("Puerto inválido, usando 8080");
            }
        }

        // Configuración de políticas
        ConfiguracionPoliticas config = new ConfiguracionPoliticas();
        ConfiguracionTimeout configTimeout = new ConfiguracionTimeout();

        System.out.print("\n¿Configurar políticas avanzadas? (s/n) [default: n]: ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if (respuesta.equals("s") || respuesta.equals("si")) {
            configurarPoliticas(config, configTimeout);
        }

        // Crear cliente
        cliente = new Cliente(nombre, host, puerto, config, configTimeout);
        cliente.setReconexionHabilitada(true);
        cliente.setReintentosHabilitados(true);

        System.out.println("\n✓ Cliente configurado correctamente");
    }

    private static void configurarPoliticas(ConfiguracionPoliticas config, ConfiguracionTimeout configTimeout) {
        System.out.println("\n═══ CONFIGURACIÓN DE POLÍTICAS ═══");

        System.out.print("Máximo de reintentos [default: 5]: ");
        String maxReintentos = scanner.nextLine().trim();
        if (!maxReintentos.isEmpty()) {
            try {
                config.setMaxReintentos(Integer.parseInt(maxReintentos));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, usando 5");
            }
        }

        System.out.print("Tiempo entre reintentos en ms [default: 3000]: ");
        String tiempoReintentos = scanner.nextLine().trim();
        if (!tiempoReintentos.isEmpty()) {
            try {
                config.setTiempoEsperaEntreReintentos(Long.parseLong(tiempoReintentos));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, usando 3000");
            }
        }

        System.out.print("Timeout de conexión en ms [default: 5000]: ");
        String timeoutConexion = scanner.nextLine().trim();
        if (!timeoutConexion.isEmpty()) {
            try {
                configTimeout.setTimeoutConexion(Long.parseLong(timeoutConexion));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, usando 5000");
            }
        }

        System.out.print("Timeout de lectura en ms [default: 10000]: ");
        String timeoutLectura = scanner.nextLine().trim();
        if (!timeoutLectura.isEmpty()) {
            try {
                configTimeout.setTimeoutLectura(Long.parseLong(timeoutLectura));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, usando 10000");
            }
        }
    }

    private static void mostrarMenu() {
        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║                  MENÚ PRINCIPAL                ║");
            System.out.println("╠════════════════════════════════════════════════╣");
            System.out.println("║  1. Conectar al servidor                       ║");
            System.out.println("║  2. Enviar mensaje                             ║");
            System.out.println("║  3. Modo interactivo (chat)                    ║");
            System.out.println("║  4. Ver estado de conexión                     ║");
            System.out.println("║  5. Desconectar                                ║");
            System.out.println("║  6. Salir                                      ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.print("\nSeleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    conectarServidor();
                    break;
                case "2":
                    enviarMensaje();
                    break;
                case "3":
                    modoInteractivo();
                    break;
                case "4":
                    verEstado();
                    break;
                case "5":
                    desconectar();
                    break;
                case "6":
                    ejecutando = false;
                    if (cliente.isConectado()) {
                        cliente.desconectar();
                    }
                    System.out.println("\n¡Hasta luego!");
                    break;
                default:
                    System.out.println("❌ Opción inválida");
            }
        }
    }

    private static void conectarServidor() {
        if (cliente.isConectado()) {
            System.out.println("\n⚠ Ya está conectado al servidor");
            return;
        }

        System.out.println("\n══════════════════════════════════════");
        System.out.println("  CONECTANDO AL SERVIDOR");
        System.out.println("══════════════════════════════════════");

        boolean conectado = cliente.conectar();

        if (conectado) {
            System.out.println("\n✓ Conexión establecida exitosamente");
        } else {
            System.out.println("\n❌ No se pudo establecer la conexión");
        }
    }

    private static void enviarMensaje() {
        if (!cliente.isConectado()) {
            System.out.println("\n❌ Debe conectarse al servidor primero");
            return;
        }

        System.out.print("\nIngrese el mensaje a enviar: ");
        String mensaje = scanner.nextLine();

        if (!mensaje.trim().isEmpty()) {
            cliente.enviarMensaje(mensaje);
        } else {
            System.out.println("❌ El mensaje no puede estar vacío");
        }
    }

    private static void modoInteractivo() {
        if (!cliente.isConectado()) {
            System.out.println("\n❌ Debe conectarse al servidor primero");
            return;
        }

        System.out.println("\n══════════════════════════════════════");
        System.out.println("  MODO INTERACTIVO (CHAT)");
        System.out.println("══════════════════════════════════════");
        System.out.println("Escriba 'salir' para volver al menú\n");

        cliente.modoInteractivo();
    }

    private static void verEstado() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║           ESTADO DE CONEXIÓN                   ║");
        System.out.println("╠════════════════════════════════════════════════╣");
        System.out.println("║  Cliente: " + cliente.getNombreCliente() + "                           ║");
        System.out.println("║  Conectado: " + (cliente.isConectado() ? "SÍ ✓" : "NO ✗") + "                          ║");
        System.out.println("║  Estado: " + cliente.getEstadoConexion() + "                    ║");
        System.out.println("║  Reconexión: " + (cliente.getPoliticaReconexion().isReconexionHabilitada() ? "HABILITADA" : "DESHABILITADA") + "               ║");
        System.out.println("╚════════════════════════════════════════════════╝");
    }

    private static void desconectar() {
        if (!cliente.isConectado()) {
            System.out.println("\n⚠ No está conectado al servidor");
            return;
        }

        System.out.println("\n══════════════════════════════════════");
        System.out.println("  DESCONECTANDO DEL SERVIDOR");
        System.out.println("══════════════════════════════════════");

        cliente.desconectar();
        System.out.println("\n✓ Desconectado correctamente");
    }
}
