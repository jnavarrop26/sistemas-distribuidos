package servidor;

import java.io.*;
import java.net.Socket;

/**
 * Maneja la comunicación con un cliente individual
 */
public class ManejadorCliente extends Thread {
    private final Socket socketCliente;
    private final int idCliente;
    private BufferedReader entrada;
    private PrintWriter salida;
    private boolean activo;

    public ManejadorCliente(Socket socketCliente, int idCliente) {
        this.socketCliente = socketCliente;
        this.idCliente = idCliente;
        this.activo = true;
        this.setName("Manejador-Cliente-" + idCliente);
    }

    @Override
    public void run() {
        try {
            // Configurar flujos de entrada/salida
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(socketCliente.getOutputStream(), true);

            System.out.println("[SERVIDOR] Cliente #" + idCliente + " conectado desde " +
                             socketCliente.getInetAddress().getHostAddress());

            // Enviar mensaje de bienvenida
            salida.println("Conectado al servidor. Tu ID es: " + idCliente);

            // Escuchar mensajes del cliente
            String mensaje;
            while (activo && (mensaje = entrada.readLine()) != null) {
                System.out.println("[SERVIDOR] Cliente #" + idCliente + ": " + mensaje);

                // Responder al cliente
                salida.println("Servidor recibió: " + mensaje);

                // Comando especial para desconectar
                if (mensaje.equalsIgnoreCase("DESCONECTAR")) {
                    System.out.println("[SERVIDOR] Cliente #" + idCliente + " solicitó desconexión");
                    break;
                }
            }

        } catch (IOException e) {
            if (activo) {
                System.err.println("[SERVIDOR] Error con cliente #" + idCliente + ": " + e.getMessage());
            }
        } finally {
            desconectar();
        }
    }

    /**
     * Desconecta el cliente
     */
    public void desconectar() {
        activo = false;
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
            }
            System.out.println("[SERVIDOR] Cliente #" + idCliente + " desconectado");
        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error al desconectar cliente #" + idCliente + ": " + e.getMessage());
        }
    }

    public int getIdCliente() {
        return idCliente;
    }

    public boolean isActivo() {
        return activo;
    }
}
