package servidor;

import comun.ConfiguracionPoliticas;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Política de reinicio automático del servidor
 */
public class PoliticaReinicio {
    private final ConfiguracionPoliticas configuracion;
    private final AtomicBoolean reinicioHabilitado;
    private final AtomicBoolean servidorActivo;
    private int contadorReinicios;

    public PoliticaReinicio(ConfiguracionPoliticas configuracion) {
        this.configuracion = configuracion;
        this.reinicioHabilitado = new AtomicBoolean(configuracion.isReinicioAutomaticoHabilitado());
        this.servidorActivo = new AtomicBoolean(false);
        this.contadorReinicios = 0;
    }

    /**
     * Habilita o deshabilita el reinicio automático
     */
    public void setReinicioHabilitado(boolean habilitado) {
        this.reinicioHabilitado.set(habilitado);
        System.out.println("[POLÍTICA REINICIO] Reinicio automático: " +
                          (habilitado ? "HABILITADO" : "DESHABILITADO"));
    }

    /**
     * Verifica si el reinicio automático está habilitado
     */
    public boolean isReinicioHabilitado() {
        return reinicioHabilitado.get();
    }

    /**
     * Marca el servidor como activo
     */
    public void marcarServidorActivo() {
        servidorActivo.set(true);
    }

    /**
     * Marca el servidor como inactivo
     */
    public void marcarServidorInactivo() {
        servidorActivo.set(false);
    }

    /**
     * Verifica si el servidor está activo
     */
    public boolean isServidorActivo() {
        return servidorActivo.get();
    }

    /**
     * Ejecuta el reinicio del servidor si está habilitado
     * @return true si se debe reiniciar, false en caso contrario
     */
    public boolean debeReiniciar() {
        if (!reinicioHabilitado.get()) {
            System.out.println("[POLÍTICA REINICIO] Reinicio no habilitado, servidor permanecerá apagado");
            return false;
        }

        if (servidorActivo.get()) {
            System.out.println("[POLÍTICA REINICIO] Servidor aún activo, no requiere reinicio");
            return false;
        }

        contadorReinicios++;
        System.out.println("[POLÍTICA REINICIO] Reinicio #" + contadorReinicios + " será ejecutado");
        return true;
    }

    /**
     * Espera el tiempo configurado antes de reiniciar
     */
    public void esperarAntesDeReiniciar() {
        try {
            long tiempo = configuracion.getTiempoEsperaReinicio();
            System.out.println("[POLÍTICA REINICIO] Esperando " + tiempo + "ms antes de reiniciar...");
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            System.err.println("[POLÍTICA REINICIO] Espera interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public int getContadorReinicios() {
        return contadorReinicios;
    }

    public ConfiguracionPoliticas getConfiguracion() {
        return configuracion;
    }
}
