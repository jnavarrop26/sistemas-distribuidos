package pruebas;

import cliente.Cliente;
import cliente.ConfiguracionTimeout;
import comun.ConfiguracionPoliticas;
import servidor.Servidor;

import java.util.Scanner;

/**
 * ESCENARIO 3: Servidor con reinicio automático habilitado
 *
 * Descripción:
 * - Se inicia el servidor (CON política de reinicio automático)
 * - Se conectan N clientes
 * - Se apaga el servidor
 * - Los clientes ejecutan política de reintentos
 * - El servidor SE REINICIA automáticamente
 * - Los clientes se reconectan exitosamente
 */
public class EscenarioTres {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  ESCENARIO 3: Servidor con Reinicio Automático");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // Configuración de políticas
        ConfiguracionPoliticas configServidor = new ConfiguracionPoliticas();
        configServidor.setReinicioAutomaticoHabilitado(true); // ✅ CON reinicio automático
        configServidor.setTiempoEsperaReinicio(3000); // 3 segundos antes de reiniciar

        ConfiguracionPoliticas configCliente = new ConfiguracionPoliticas();
        configCliente.setMaxReintentos(10); // Más reintentos para dar tiempo al servidor
        configCliente.setTiempoEsperaEntreReintentos(4000); // 4 segundos entre reintentos

        ConfiguracionTimeout configTimeout = new ConfiguracionTimeout();
        configTimeout.setTimeoutConexion(2000);

        int numeroClientes = 3;

        System.out.println("📋 Configuración:");
        System.out.println("   • Servidor: Puerto 8080");
        System.out.println("   • Reinicio automático servidor: HABILITADO ✅");
        System.out.println("   • Tiempo espera reinicio: " + configServidor.getTiempoEsperaReinicio() + "ms");
        System.out.println("   • Número de clientes: " + numeroClientes);
        System.out.println("   • Max reintentos clientes: " + configCliente.getMaxReintentos());
        System.out.println("   • Reconexión automática clientes: HABILITADA\n");

        // 1. Iniciar servidor
        System.out.println("🚀 Paso 1: Iniciando servidor con reinicio automático...\n");
        Servidor servidor = new Servidor(8080, configServidor);
        servidor.setReinicioAutomatico(true); // ✅ Habilitar reinicio automático
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
            clientes[i].setReintentosHabilitados(true);

            hilosClientes[i] = new Thread(() -> {
                clientes[numCliente - 1].conectar();

                // Mantener vivos enviando mensajes periódicos
                int contadorMensajes = 0;
                while (true) {
                    try {
                        Thread.sleep(5000);
                        if (clientes[numCliente - 1].isConectado()) {
                            contadorMensajes++;
                            clientes[numCliente - 1].enviarMensaje("Mensaje #" + contadorMensajes +
                                                                   " desde Cliente-" + numCliente);
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }, "Hilo-Cliente-" + numCliente);

            hilosClientes[i].start();
        }

        // Esperar a que todos se conecten
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3. Instrucción para apagar servidor manualmente
        System.out.println("\n⚠️  Paso 3: Servidor ejecutándose con " + servidor.getCantidadClientes() + " clientes conectados");
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  Presione ENTER para APAGAR el servidor (se reiniciará)   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        // 4. Apagar servidor (el monitor lo reiniciará automáticamente)
        System.out.println("\n🛑 Paso 4: Apagando servidor...\n");
        servidor.detener();

        System.out.println("\n📊 Observando comportamiento:");
        System.out.println("   • Los clientes detectarán la desconexión");
        System.out.println("   • Ejecutarán política de reintentos");
        System.out.println("   • El monitor detectará que el servidor está caído");
        System.out.println("   • El servidor SE REINICIARÁ automáticamente");
        System.out.println("   • Los clientes SE RECONECTARÁN al servidor reiniciado\n");

        // Esperar para observar el reinicio y reconexión
        System.out.println("⏳ Esperando reinicio automático del servidor...\n");
        try {
            Thread.sleep(30000); // 30 segundos para observar todo el proceso
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 5. Verificar reconexiones
        System.out.println("\n✓ Paso 5: Verificando estado de clientes...\n");
        int clientesConectados = 0;
        for (int i = 0; i < clientes.length; i++) {
            if (clientes[i].isConectado()) {
                clientesConectados++;
                System.out.println("   ✅ Cliente-" + (i + 1) + ": CONECTADO (Reconexiones exitosas: " +
                                 clientes[i].getPoliticaReconexion().getReconexionesExitosas() + ")");
            } else {
                System.out.println("   ❌ Cliente-" + (i + 1) + ": DESCONECTADO");
            }
        }

        System.out.println("\n📈 Estadísticas del servidor:");
        System.out.println("   • Reiniciado: " + (servidor.isEjecutando() ? "SÍ" : "NO"));
        System.out.println("   • Clientes conectados actualmente: " + servidor.getCantidadClientes());
        System.out.println("   • Reinicios realizados: " + servidor.getPoliticaReinicio().getContadorReinicios());

        // 6. Desconectar clientes y apagar servidor
        System.out.println("\n\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  Presione ENTER para finalizar la prueba              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        scanner.nextLine();

        System.out.println("\n🔌 Paso 6: Finalizando prueba...\n");
        for (Cliente cliente : clientes) {
            if (cliente != null) {
                cliente.desconectar();
            }
        }

        servidor.apagar();

        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("  ✅ ESCENARIO 3 COMPLETADO");
        System.out.println("  • Servidor apagado y reiniciado automáticamente");
        System.out.println("  • Clientes reconectados exitosamente: " + clientesConectados + "/" + numeroClientes);
        System.out.println("  • Reintentos ejecutados correctamente");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
