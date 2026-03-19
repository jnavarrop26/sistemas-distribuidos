package pruebas;

import cliente.Cliente;
import cliente.ConfiguracionTimeout;
import comun.ConfiguracionPoliticas;
import servidor.Servidor;

import java.util.Scanner;

/**
 * ESCENARIO 2: Servidor sin reinicio automático
 *
 * Descripción:
 * - Se inicia el servidor (SIN política de reinicio automático)
 * - Se conectan N clientes
 * - Se apaga el servidor manualmente
 * - Los clientes ejecutan política de reintentos
 * - El servidor NO se reinicia automáticamente
 */
public class EscenarioDos {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  ESCENARIO 2: Servidor sin Reinicio Automático");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // Configuración de políticas
        ConfiguracionPoliticas configServidor = new ConfiguracionPoliticas();
        configServidor.setReinicioAutomaticoHabilitado(false); // ❌ SIN reinicio automático

        ConfiguracionPoliticas configCliente = new ConfiguracionPoliticas();
        configCliente.setMaxReintentos(5);
        configCliente.setTiempoEsperaEntreReintentos(3000);

        ConfiguracionTimeout configTimeout = new ConfiguracionTimeout();
        configTimeout.setTimeoutConexion(2000);

        int numeroClientes = 3;

        System.out.println("📋 Configuración:");
        System.out.println("   • Servidor: Puerto 8080");
        System.out.println("   • Reinicio automático servidor: DESHABILITADO ❌");
        System.out.println("   • Número de clientes: " + numeroClientes);
        System.out.println("   • Max reintentos clientes: " + configCliente.getMaxReintentos());
        System.out.println("   • Reconexión automática clientes: HABILITADA\n");

        // 1. Iniciar servidor
        System.out.println("🚀 Paso 1: Iniciando servidor...\n");
        Servidor servidor = new Servidor(8080, configServidor);
        servidor.setReinicioAutomatico(false); // Asegurar que NO se reinicie
        servidor.iniciar();

        // Esperar a que el servidor esté listo
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2. Conectar N clientes
        System.out.println("\n👥 Paso 2: Conectando " + numeroClientes + " clientes...\n");
        Cliente[] clientes = new Cliente[numeroClientes];
        Thread[] hilosClientes = new Thread[numeroClientes];

        for (int i = 0; i < numeroClientes; i++) {
            final int numCliente = i + 1;
            clientes[i] = new Cliente("Cliente-" + numCliente, "localhost", 8080,
                                     configCliente, configTimeout);
            clientes[i].setReconexionHabilitada(true);

            hilosClientes[i] = new Thread(() -> {
                clientes[numCliente - 1].conectar();

                // Mantener vivos enviando mensajes periódicos
                while (clientes[numCliente - 1].isConectado()) {
                    try {
                        Thread.sleep(5000);
                        clientes[numCliente - 1].enviarMensaje("Ping desde Cliente-" + numCliente);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }, "Hilo-Cliente-" + numCliente);

            hilosClientes[i].start();
        }

        // Esperar a que todos se conecten
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3. Instrucción para apagar servidor manualmente
        System.out.println("\n⚠️  Paso 3: Servidor ejecutándose con " + servidor.getCantidadClientes() + " clientes conectados");
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  Presione ENTER para APAGAR el servidor manualmente   ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        // 4. Apagar servidor
        System.out.println("\n🛑 Paso 4: Apagando servidor...\n");
        servidor.detener();

        System.out.println("\n📊 Observando comportamiento de clientes...");
        System.out.println("   • Los clientes detectarán la desconexión");
        System.out.println("   • Ejecutarán política de reintentos");
        System.out.println("   • El servidor NO se reiniciará automáticamente\n");

        // Esperar para observar los reintentos
        try {
            Thread.sleep(20000); // 20 segundos para observar reintentos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 5. Desconectar clientes
        System.out.println("\n🔌 Paso 5: Desconectando clientes...\n");
        for (Cliente cliente : clientes) {
            if (cliente != null) {
                cliente.desconectar();
            }
        }

        servidor.apagar();

        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("  ✅ ESCENARIO 2 COMPLETADO");
        System.out.println("  • Servidor apagado manualmente");
        System.out.println("  • Clientes ejecutaron política de reintentos");
        System.out.println("  • Servidor NO se reinició (política deshabilitada)");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
