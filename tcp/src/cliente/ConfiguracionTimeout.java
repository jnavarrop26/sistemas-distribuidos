package cliente;

/**
 * Configuración de timeouts para el cliente
 */
public class ConfiguracionTimeout {
    private long timeoutConexion; // milisegundos
    private long timeoutLectura; // milisegundos
    private long timeoutEscritura; // milisegundos
    private long timeoutReintento; // milisegundos

    public ConfiguracionTimeout() {
        // Valores por defecto
        this.timeoutConexion = 5000; // 5 segundos
        this.timeoutLectura = 10000; // 10 segundos
        this.timeoutEscritura = 5000; // 5 segundos
        this.timeoutReintento = 3000; // 3 segundos
    }

    public long getTimeoutConexion() {
        return timeoutConexion;
    }

    public void setTimeoutConexion(long timeoutConexion) {
        this.timeoutConexion = timeoutConexion;
    }

    public long getTimeoutLectura() {
        return timeoutLectura;
    }

    public void setTimeoutLectura(long timeoutLectura) {
        this.timeoutLectura = timeoutLectura;
    }

    public long getTimeoutEscritura() {
        return timeoutEscritura;
    }

    public void setTimeoutEscritura(long timeoutEscritura) {
        this.timeoutEscritura = timeoutEscritura;
    }

    public long getTimeoutReintento() {
        return timeoutReintento;
    }

    public void setTimeoutReintento(long timeoutReintento) {
        this.timeoutReintento = timeoutReintento;
    }

    @Override
    public String toString() {
        return "ConfiguracionTimeout{" +
                "timeoutConexion=" + timeoutConexion + "ms" +
                ", timeoutLectura=" + timeoutLectura + "ms" +
                ", timeoutEscritura=" + timeoutEscritura + "ms" +
                ", timeoutReintento=" + timeoutReintento + "ms" +
                '}';
    }
}
