package servidor;

import comun.ConfiguracionPoliticas;

import java.util.Scanner;

/**
 * Clase principal ejecutable para el Servidor
 * Proporciona una interfaz interactiva para controlar el servidor
 */
public class ServidorPrincipal {
    private static Servidor servidor;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║        SERVIDOR TCP - INTERFAZ PRINCIPAL       ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        // Configurar servidor
        configurarServidor();

        // Mostrar menú principal
        mostrarMenu();

        scanner.close();
    }

    private static void configurarServidor() {
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

        System.out.print("\n¿Habilitar reinicio automático? (s/n) [default: s]: ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        boolean reinicioAuto = !respuesta.equals("n") && !respuesta.equals("no");
        config.setReinicioAutomaticoHabilitado(reinicioAuto);

        if (reinicioAuto) {
            System.out.print("Tiempo de espera antes de reiniciar en ms [default: 2000]: ");
            String tiempoReinicio = scanner.nextLine().trim();
            if (!tiempoReinicio.isEmpty()) {
                try {
                    config.setTiempoEsperaReinicio(Long.parseLong(tiempoReinicio));
                } catch (NumberFormatException e) {
                    System.out.println("Valor inválido, usando 2000");
                }
            }
        }

        // Crear servidor
        servidor = new Servidor(puerto, config);

        System.out.println("\n✓ Servidor configurado correctamente");
        System.out.println("  Puerto: " + puerto);
        System.out.println("  Reinicio automático: " + (reinicioAuto ? "SÍ" : "NO"));
    }

    private static void mostrarMenu() {
        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║                  MENÚ PRINCIPAL                ║");
            System.out.println("╠════════════════════════════════════════════════╣");
            System.out.println("║  1. Iniciar servidor                           ║");
            System.out.println("║  2. Detener servidor                           ║");
            System.out.println("║  3. Reiniciar servidor                         ║");
            System.out.println("║  4. Ver estado del servidor                    ║");
            System.out.println("║  5. Habilitar/Deshabilitar reinicio automático ║");
            System.out.println("║  6. Apagar servidor completamente              ║");
            System.out.println("║  7. Salir                                      ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.print("\nSeleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    iniciarServidor();
                    break;
                case "2":
                    detenerServidor();
                    break;
                case "3":
                    reiniciarServidor();
                    break;
                case "4":
                    verEstado();
                    break;
                case "5":
                    toggleReinicioAutomatico();
                    break;
                case "6":
                    apagarServidor();
                    break;
                case "7":
                    ejecutando = false;
                    if (servidor.isEjecutando()) {
                        System.out.println("\n⚠ Apagando servidor antes de salir...");
                        servidor.apagar();
                    }
                    System.out.println("\n¡Hasta luego!");
                    break;
                default:
                    System.out.println("❌ Opción inválida");
            }
        }
    }

    private static void iniciarServidor() {
        if (servidor.isEjecutando()) {
            System.out.println("\n⚠ El servidor ya está ejecutándose");
            return;
        }

        System.out.println("\n══════════════════════════════════════");
        System.out.println("  INICIANDO SERVIDOR");
        System.out.println("══════════════════════════════════════");

        servidor.iniciar();

        // Dar un momento para que el servidor inicie
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (servidor.isEjecutando()) {
            System.out.println("\n✓ Servidor iniciado exitosamente");
            System.out.println("  Escuchando en puerto: " + servidor.getPuerto());
            System.out.println("  Esperando conexiones de clientes...");
        } else {
            System.out.println("\n❌ Error al iniciar el servidor");
        }
    }

    private static void detenerServidor() {
        if (!servidor.isEjecutando()) {
            System.out.println("\n⚠ El servidor no está ejecutándose");
            return;
        }

        System.out.println("\n══════════════════════════════════════");
        System.out.println("  DETENIENDO SERVIDOR");
        System.out.println("══════════════════════════════════════");

        servidor.detener();
        System.out.println("\n✓ Servidor detenido correctamente");
    }

    private static void reiniciarServidor() {
        if (!servidor.isEjecutando()) {
            System.out.println("\n⚠ El servidor no está ejecutándose. Use 'Iniciar servidor'");
            return;
        }

        System.out.println("\n══════════════════════════════════════");
        System.out.println("  REINICIANDO SERVIDOR");
        System.out.println("══════════════════════════════════════");

        servidor.reiniciar();
        System.out.println("\n✓ Servidor reiniciado correctamente");
    }

    private static void verEstado() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║           ESTADO DEL SERVIDOR                  ║");
        System.out.println("╠════════════════════════════════════════════════╣");
        System.out.println("║  Puerto: " + servidor.getPuerto() + "                                  ║");
        System.out.println("║  Estado: " + (servidor.isEjecutando() ? "EJECUTANDO ✓" : "DETENIDO ✗") + "                       ║");
        System.out.println("║  Clientes conectados: " + servidor.getCantidadClientes() + "                     ║");
        System.out.println("║  Reinicio automático: " +
                           (servidor.getPoliticaReinicio().isReinicioHabilitado() ? "SÍ" : "NO") + "                     ║");
        System.out.println("╚════════════════════════════════════════════════╝");
    }

    private static void toggleReinicioAutomatico() {
        boolean estadoActual = servidor.getPoliticaReinicio().isReinicioHabilitado();
        boolean nuevoEstado = !estadoActual;

        servidor.setReinicioAutomatico(nuevoEstado);

        System.out.println("\n✓ Reinicio automático " +
                          (nuevoEstado ? "HABILITADO" : "DESHABILITADO"));

        if (nuevoEstado) {
            System.out.println("\n📋 El servidor se reiniciará automáticamente si se cae");
            System.out.println("   Tiempo de espera antes de reiniciar: " +
                             servidor.getConfiguracion().getTiempoEsperaReinicio() + "ms");
        } else {
            System.out.println("\n⚠ El servidor NO se reiniciará automáticamente si se cae");
        }
    }

    private static void apagarServidor() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("  APAGANDO SERVIDOR COMPLETAMENTE");
        System.out.println("══════════════════════════════════════");
        System.out.println("\n⚠ Esto detendrá el servidor y el monitor");
        System.out.print("¿Está seguro? (s/n): ");

        String confirmacion = scanner.nextLine().trim().toLowerCase();

        if (confirmacion.equals("s") || confirmacion.equals("si")) {
            servidor.apagar();
            System.out.println("\n✓ Servidor apagado completamente");
            System.out.println("  Puede volver a iniciarlo con la opción 1");
        } else {
            System.out.println("\n❌ Operación cancelada");
        }
    }
}
