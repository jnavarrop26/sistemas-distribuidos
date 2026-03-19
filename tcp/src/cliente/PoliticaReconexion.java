package cliente;

import comun.EstadoConexion;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Política de reconexión automática para el cliente
 */
public class PoliticaReconexion {
    private final AtomicBoolean reconexionHabilitada;
    private EstadoConexion estadoActual;
    private int reconexionesExitosas;
    private long tiempoUltimaReconexion;

    public PoliticaReconexion() {
        this.reconexionHabilitada = new AtomicBoolean(true);
        this.estadoActual = EstadoConexion.DESCONECTADO;
        this.reconexionesExitosas = 0;
        this.tiempoUltimaReconexion = 0;
    }

    /**
     * Habilita o deshabilita la reconexión automática
     */
    public void setReconexionHabilitada(boolean habilitada) {
        this.reconexionHabilitada.set(habilitada);
        System.out.println("[POLÍTICA RECONEXIÓN] Reconexión automática: " +
                          (habilitada ? "HABILITADA" : "DESHABILITADA"));
    }

    /**
     * Verifica si la reconexión está habilitada
     */
    public boolean isReconexionHabilitada() {
        return reconexionHabilitada.get();
    }

    /**
     * Actualiza el estado de la conexión
     */
    public void actualizarEstado(EstadoConexion nuevoEstado) {
        EstadoConexion estadoAnterior = this.estadoActual;
        this.estadoActual = nuevoEstado;

        if (nuevoEstado == EstadoConexion.CONECTADO &&
            estadoAnterior != EstadoConexion.CONECTADO) {
            reconexionesExitosas++;
            tiempoUltimaReconexion = System.currentTimeMillis();
            System.out.println("[POLÍTICA RECONEXIÓN] Reconexión exitosa #" + reconexionesExitosas);
        }

        System.out.println("[POLÍTICA RECONEXIÓN] Estado: " + estadoAnterior.getDescripcion() +
                         " -> " + nuevoEstado.getDescripcion());
    }

    /**
     * Verifica si se debe intentar reconectar
     */
    public boolean debeReconectar() {
        if (!reconexionHabilitada.get()) {
            System.out.println("[POLÍTICA RECONEXIÓN] Reconexión deshabilitada");
            return false;
        }

        if (estadoActual == EstadoConexion.CONECTADO) {
            System.out.println("[POLÍTICA RECONEXIÓN] Ya está conectado, no requiere reconexión");
            return false;
        }

        System.out.println("[POLÍTICA RECONEXIÓN] Reconexión necesaria");
        return true;
    }

    public EstadoConexion getEstadoActual() {
        return estadoActual;
    }

    public int getReconexionesExitosas() {
        return reconexionesExitosas;
    }

    public long getTiempoUltimaReconexion() {
        return tiempoUltimaReconexion;
    }
}
