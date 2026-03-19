package cliente;

import comun.ConfiguracionPoliticas;

/**
 * Política de reintentos para el cliente
 */
public class PoliticaReintentos {
    private final ConfiguracionPoliticas configuracion;
    private int intentosRealizados;
    private boolean habilitada;

    public PoliticaReintentos(ConfiguracionPoliticas configuracion) {
        this.configuracion = configuracion;
        this.intentosRealizados = 0;
        this.habilitada = true;
    }

    /**
     * Verifica si se pueden hacer más reintentos
     */
    public boolean puedeReintentar() {
        if (!habilitada) {
            System.out.println("[POLÍTICA REINTENTOS] Política deshabilitada");
            return false;
        }

        if (intentosRealizados >= configuracion.getMaxReintentos()) {
            System.out.println("[POLÍTICA REINTENTOS] Máximo de reintentos alcanzado: " +
                             intentosRealizados + "/" + configuracion.getMaxReintentos());
            return false;
        }

        return true;
    }

    /**
     * Registra un intento de conexión
     */
    public void registrarIntento() {
        intentosRealizados++;
        System.out.println("[POLÍTICA REINTENTOS] Intento #" + intentosRealizados +
                         " de " + configuracion.getMaxReintentos());
    }

    /**
     * Espera el tiempo configurado antes de reintentar
     */
    public void esperarAntesDeReintentar() {
        try {
            long tiempo = configuracion.getTiempoEsperaEntreReintentos();
            System.out.println("[POLÍTICA REINTENTOS] Esperando " + tiempo + "ms antes de reintentar...");
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            System.err.println("[POLÍTICA REINTENTOS] Espera interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Reinicia el contador de reintentos
     */
    public void reiniciarContador() {
        intentosRealizados = 0;
        System.out.println("[POLÍTICA REINTENTOS] Contador reiniciado");
    }

    /**
     * Habilita o deshabilita la política
     */
    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
        System.out.println("[POLÍTICA REINTENTOS] Política " +
                         (habilitada ? "HABILITADA" : "DESHABILITADA"));
    }

    public boolean isHabilitada() {
        return habilitada;
    }

    public int getIntentosRealizados() {
        return intentosRealizados;
    }

    public int getMaxReintentos() {
        return configuracion.getMaxReintentos();
    }
}
