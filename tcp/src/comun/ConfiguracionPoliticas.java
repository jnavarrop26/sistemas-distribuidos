package comun;

/**
 * Configuración general de políticas para cliente y servidor
 */
public class ConfiguracionPoliticas {
    private int maxReintentos;
    private long tiempoEsperaEntreReintentos; // milisegundos
    private long timeoutConexion; // milisegundos
    private boolean reinicioAutomaticoHabilitado;
    private long tiempoEsperaReinicio; // milisegundos

    public ConfiguracionPoliticas() {
        // Valores por defecto
        this.maxReintentos = 5;
        this.tiempoEsperaEntreReintentos = 3000; // 3 segundos
        this.timeoutConexion = 5000; // 5 segundos
        this.reinicioAutomaticoHabilitado = false;
        this.tiempoEsperaReinicio = 2000; // 2 segundos
    }

    public int getMaxReintentos() {
        return maxReintentos;
    }

    public void setMaxReintentos(int maxReintentos) {
        this.maxReintentos = maxReintentos;
    }

    public long getTiempoEsperaEntreReintentos() {
        return tiempoEsperaEntreReintentos;
    }

    public void setTiempoEsperaEntreReintentos(long tiempoEsperaEntreReintentos) {
        this.tiempoEsperaEntreReintentos = tiempoEsperaEntreReintentos;
    }

    public long getTimeoutConexion() {
        return timeoutConexion;
    }

    public void setTimeoutConexion(long timeoutConexion) {
        this.timeoutConexion = timeoutConexion;
    }

    public boolean isReinicioAutomaticoHabilitado() {
        return reinicioAutomaticoHabilitado;
    }

    public void setReinicioAutomaticoHabilitado(boolean reinicioAutomaticoHabilitado) {
        this.reinicioAutomaticoHabilitado = reinicioAutomaticoHabilitado;
    }

    public long getTiempoEsperaReinicio() {
        return tiempoEsperaReinicio;
    }

    public void setTiempoEsperaReinicio(long tiempoEsperaReinicio) {
        this.tiempoEsperaReinicio = tiempoEsperaReinicio;
    }

    @Override
    public String toString() {
        return "ConfiguracionPoliticas{" +
                "maxReintentos=" + maxReintentos +
                ", tiempoEsperaEntreReintentos=" + tiempoEsperaEntreReintentos + "ms" +
                ", timeoutConexion=" + timeoutConexion + "ms" +
                ", reinicioAutomaticoHabilitado=" + reinicioAutomaticoHabilitado +
                ", tiempoEsperaReinicio=" + tiempoEsperaReinicio + "ms" +
                '}';
    }
}
