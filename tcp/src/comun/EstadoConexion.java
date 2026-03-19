package comun;

/**
 * Estados posibles de una conexión cliente-servidor
 */
public enum EstadoConexion {
    DESCONECTADO("Desconectado"),
    CONECTANDO("Intentando conectar"),
    CONECTADO("Conectado exitosamente"),
    ERROR("Error de conexión"),
    TIMEOUT("Tiempo de espera agotado"),
    REINTENTANDO("Reintentando conexión");

    private final String descripcion;

    EstadoConexion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
