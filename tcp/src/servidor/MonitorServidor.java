package servidor;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Monitor que supervisa el servidor y ejecuta reinicios automáticos
 */
public class MonitorServidor extends Thread {
    private final Servidor servidor;
    private final PoliticaReinicio politicaReinicio;
    private final AtomicBoolean monitoreando;
    private final long intervaloChequeo = 1000; // 1 segundo

    public MonitorServidor(Servidor servidor, PoliticaReinicio politicaReinicio) {
        this.servidor = servidor;
        this.politicaReinicio = politicaReinicio;
        this.monitoreando = new AtomicBoolean(true);
        this.setDaemon(true);
        this.setName("Monitor-Servidor");
    }

    @Override
    public void run() {
        System.out.println("[MONITOR] Monitor de servidor iniciado");

        while (monitoreando.get()) {
            try {
                // Verificar si el servidor está inactivo y debe reiniciarse
                if (!politicaReinicio.isServidorActivo() &&
                    politicaReinicio.isReinicioHabilitado()) {

                    System.out.println("[MONITOR] Servidor caído detectado. Aplicando política de reinicio...");
                    politicaReinicio.esperarAntesDeReiniciar();

                    if (politicaReinicio.debeReiniciar()) {
                        System.out.println("[MONITOR] Reiniciando servidor...");
                        servidor.reiniciar();
                    }
                }

                Thread.sleep(intervaloChequeo);

            } catch (InterruptedException e) {
                System.out.println("[MONITOR] Monitor interrumpido");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[MONITOR] Error en monitor: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("[MONITOR] Monitor de servidor detenido");
    }

    /**
     * Detiene el monitoreo
     */
    public void detenerMonitoreo() {
        monitoreando.set(false);
        this.interrupt();
    }

    public boolean isMonitoreando() {
        return monitoreando.get();
    }
}
