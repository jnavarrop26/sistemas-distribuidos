package pruebas;

import cliente.Cliente;
import cliente.ConfiguracionTimeout;
import comun.ConfiguracionPoliticas;

/**
 * ESCENARIO 1: Sin lanzar el servidor, los clientes ejecutan política de reintentos y timeout
 *
 * Descripción:
 * - No se inicia el servidor
 * - Se lanzan N clientes que intentan conectarse
 * - Los clientes aplican políticas de reintentos con timeout
 * - Los clientes deben agotar sus reintentos y terminar con timeout
 */
public class EscenarioUno {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  ESCENARIO 1: Clientes sin servidor - Políticas de Reintentos");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // Configuración de políticas
        ConfiguracionPoliticas config = new ConfiguracionPoliticas();
        config.setMaxReintentos(3); // 3 reintentos
        config.setTiempoEsperaEntreReintentos(2000); // 2 segundos entre reintentos

        ConfiguracionTimeout configTimeout = new ConfiguracionTimeout();
        configTimeout.setTimeoutConexion(3000); // 3 segundos de timeout por intento

        // Número de clientes a lanzar
        int numeroClientes = 3;

        System.out.println("📋 Configuración:");
        System.out.println("   • Servidor: NO INICIADO (simulación de servidor caído)");
        System.out.println("   • Número de clientes: " + numeroClientes);
        System.out.println("   • Max reintentos por cliente: " + config.getMaxReintentos());
        System.out.println("   • Timeout por intento: " + configTimeout.getTimeoutConexion() + "ms");
        System.out.println("   • Espera entre reintentos: " + config.getTiempoEsperaEntreReintentos() + "ms\n");

        System.out.println("🔄 Iniciando prueba...\n");

        // Crear y lanzar clientes en hilos separados
        Thread[] hilosClientes = new Thread[numeroClientes];

        for (int i = 0; i < numeroClientes; i++) {
            final int numCliente = i + 1;
            hilosClientes[i] = new Thread(() -> {
                Cliente cliente = new Cliente("Cliente-" + numCliente, "localhost", 8080, config, configTimeout);

                // Habilitar reconexión automática
                cliente.setReconexionHabilitada(true);

                // Intentar conectar (aplicará política de reintentos)
                boolean conectado = cliente.conectar();

                if (!conectado) {
                    System.out.println("\n[RESULTADO] Cliente-" + numCliente +
                                     " no pudo conectarse después de " +
                                     cliente.getPoliticaReintentos().getIntentosRealizados() +
                                     " intentos");
                }
            }, "Hilo-Cliente-" + numCliente);

            hilosClientes[i].start();
        }

        // Esperar a que todos los clientes terminen
        for (Thread hilo : hilosClientes) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("  ✅ ESCENARIO 1 COMPLETADO");
        System.out.println("  Todos los clientes agotaron sus reintentos");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
