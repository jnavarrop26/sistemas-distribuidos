package servidor;

import comun.ConfiguracionPoliticas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Interfaz gráfica para el Servidor TCP
 */
public class ServidorGUI extends JFrame {
    private Servidor servidor;
    private ConfiguracionPoliticas config;

    // Componentes de configuración
    private JTextField txtPuerto;
    private JCheckBox chkReinicioAutomatico;
    private JSpinner spinnerTiempoReinicio;

    // Componentes de control
    private JButton btnIniciar;
    private JButton btnDetener;
    private JButton btnReiniciar;
    private JButton btnLimpiarLog;

    // Componentes de información
    private JLabel lblEstado;
    private JLabel lblPuerto;
    private JLabel lblClientes;
    private JLabel lblReinicioAuto;
    private JTextArea txtLog;
    private JScrollPane scrollLog;

    // Estado
    private boolean servidorIniciado = false;

    public ServidorGUI() {
        setTitle("Servidor TCP - Interfaz Gráfica");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        config = new ConfiguracionPoliticas();
        config.setReinicioAutomaticoHabilitado(true);

        inicializarComponentes();
        configurarEventos();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarAplicacion();
            }
        });
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel superior - Configuración
        panelPrincipal.add(crearPanelConfiguracion(), BorderLayout.NORTH);

        // Panel central - Log
        panelPrincipal.add(crearPanelLog(), BorderLayout.CENTER);

        // Panel derecho - Estado e información
        panelPrincipal.add(crearPanelEstado(), BorderLayout.EAST);

        // Panel inferior - Controles
        panelPrincipal.add(crearPanelControles(), BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private JPanel crearPanelConfiguracion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Configuración del Servidor",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Puerto
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Puerto:"), gbc);

        gbc.gridx = 1;
        txtPuerto = new JTextField("8080", 10);
        panel.add(txtPuerto, gbc);

        // Reinicio automático
        gbc.gridx = 2;
        chkReinicioAutomatico = new JCheckBox("Reinicio Automático", true);
        panel.add(chkReinicioAutomatico, gbc);

        // Tiempo de reinicio
        gbc.gridx = 3;
        panel.add(new JLabel("Tiempo reinicio (ms):"), gbc);

        gbc.gridx = 4;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2000, 500, 10000, 500);
        spinnerTiempoReinicio = new JSpinner(spinnerModel);
        panel.add(spinnerTiempoReinicio, gbc);

        return panel;
    }

    private JPanel crearPanelLog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Registro de Eventos",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLog.setBackground(new Color(40, 40, 40));
        txtLog.setForeground(new Color(0, 255, 0));
        txtLog.setCaretColor(Color.WHITE);

        scrollLog = new JScrollPane(txtLog);
        scrollLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollLog, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Estado del Servidor",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        panel.setPreferredSize(new Dimension(250, 0));

        // Panel de estado
        JPanel estadoPanel = new JPanel(new GridLayout(4, 1, 5, 10));
        estadoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Estado
        JPanel panelEstadoLabel = new JPanel(new BorderLayout());
        panelEstadoLabel.add(new JLabel("Estado:"), BorderLayout.WEST);
        lblEstado = new JLabel("DETENIDO");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstado.setForeground(Color.RED);
        panelEstadoLabel.add(lblEstado, BorderLayout.CENTER);
        estadoPanel.add(panelEstadoLabel);

        // Puerto
        JPanel panelPuertoLabel = new JPanel(new BorderLayout());
        panelPuertoLabel.add(new JLabel("Puerto:"), BorderLayout.WEST);
        lblPuerto = new JLabel("N/A");
        lblPuerto.setFont(new Font("Arial", Font.BOLD, 12));
        panelPuertoLabel.add(lblPuerto, BorderLayout.CENTER);
        estadoPanel.add(panelPuertoLabel);

        // Clientes
        JPanel panelClientesLabel = new JPanel(new BorderLayout());
        panelClientesLabel.add(new JLabel("Clientes:"), BorderLayout.WEST);
        lblClientes = new JLabel("0");
        lblClientes.setFont(new Font("Arial", Font.BOLD, 12));
        panelClientesLabel.add(lblClientes, BorderLayout.CENTER);
        estadoPanel.add(panelClientesLabel);

        // Reinicio automático
        JPanel panelReinicioLabel = new JPanel(new BorderLayout());
        panelReinicioLabel.add(new JLabel("Auto-reinicio:"), BorderLayout.WEST);
        lblReinicioAuto = new JLabel("SÍ");
        lblReinicioAuto.setFont(new Font("Arial", Font.BOLD, 12));
        lblReinicioAuto.setForeground(new Color(0, 150, 0));
        panelReinicioLabel.add(lblReinicioAuto, BorderLayout.CENTER);
        estadoPanel.add(panelReinicioLabel);

        panel.add(estadoPanel);

        return panel;
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnIniciar = new JButton("▶ Iniciar Servidor");
        btnIniciar.setFont(new Font("Arial", Font.BOLD, 12));
        btnIniciar.setBackground(new Color(0, 150, 0));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);
        btnIniciar.setPreferredSize(new Dimension(150, 35));

        btnDetener = new JButton("⏹ Detener Servidor");
        btnDetener.setFont(new Font("Arial", Font.BOLD, 12));
        btnDetener.setBackground(new Color(200, 0, 0));
        btnDetener.setForeground(Color.WHITE);
        btnDetener.setFocusPainted(false);
        btnDetener.setEnabled(false);
        btnDetener.setPreferredSize(new Dimension(150, 35));

        btnReiniciar = new JButton("🔄 Reiniciar");
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 12));
        btnReiniciar.setBackground(new Color(200, 150, 0));
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setFocusPainted(false);
        btnReiniciar.setEnabled(false);
        btnReiniciar.setPreferredSize(new Dimension(120, 35));

        btnLimpiarLog = new JButton("🗑 Limpiar Log");
        btnLimpiarLog.setFont(new Font("Arial", Font.BOLD, 12));
        btnLimpiarLog.setPreferredSize(new Dimension(120, 35));

        panel.add(btnIniciar);
        panel.add(btnDetener);
        panel.add(btnReiniciar);
        panel.add(btnLimpiarLog);

        return panel;
    }

    private void configurarEventos() {
        btnIniciar.addActionListener(e -> iniciarServidor());
        btnDetener.addActionListener(e -> detenerServidor());
        btnReiniciar.addActionListener(e -> reiniciarServidor());
        btnLimpiarLog.addActionListener(e -> txtLog.setText(""));

        // Iniciar monitor de estado
        Timer timer = new Timer(1000, e -> actualizarEstado());
        timer.start();
    }

    private void iniciarServidor() {
        try {
            int puerto = Integer.parseInt(txtPuerto.getText().trim());
            if (puerto < 1024 || puerto > 65535) {
                mostrarError("El puerto debe estar entre 1024 y 65535");
                return;
            }

            config.setReinicioAutomaticoHabilitado(chkReinicioAutomatico.isSelected());
            config.setTiempoEsperaReinicio((Integer) spinnerTiempoReinicio.getValue());

            servidor = new Servidor(puerto, config);

            // Redirigir salida del servidor al log
            redirigirSalidaALog();

            servidor.iniciar();

            if (servidor.isEjecutando()) {
                servidorIniciado = true;
                actualizarEstadoBotones(true);
                actualizarEstadoLabels(true);
                agregarLog("✓ Servidor iniciado en puerto " + puerto);

                // Deshabilitar campos de configuración
                txtPuerto.setEnabled(false);
            } else {
                mostrarError("No se pudo iniciar el servidor");
            }

        } catch (NumberFormatException ex) {
            mostrarError("Puerto inválido. Debe ser un número.");
        } catch (Exception ex) {
            mostrarError("Error al iniciar servidor: " + ex.getMessage());
        }
    }

    private void detenerServidor() {
        if (servidor != null && servidor.isEjecutando()) {
            servidor.detener();
            servidorIniciado = false;
            actualizarEstadoBotones(false);
            actualizarEstadoLabels(false);
            agregarLog("⏹ Servidor detenido");
            txtPuerto.setEnabled(true);
        }
    }

    private void reiniciarServidor() {
        if (servidor != null && servidor.isEjecutando()) {
            agregarLog("🔄 Reiniciando servidor...");
            servidor.reiniciar();
            agregarLog("✓ Servidor reiniciado");
        }
    }

    private void redirigirSalidaALog() {
        // Aquí capturamos los mensajes del servidor
        new Thread(() -> {
            while (servidorIniciado) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private void actualizarEstadoBotones(boolean iniciado) {
        btnIniciar.setEnabled(!iniciado);
        btnDetener.setEnabled(iniciado);
        btnReiniciar.setEnabled(iniciado);
        chkReinicioAutomatico.setEnabled(!iniciado);
        spinnerTiempoReinicio.setEnabled(!iniciado);
    }

    private void actualizarEstadoLabels(boolean iniciado) {
        if (iniciado && servidor != null) {
            lblEstado.setText("EJECUTANDO");
            lblEstado.setForeground(new Color(0, 150, 0));
            lblPuerto.setText(String.valueOf(servidor.getPuerto()));
        } else {
            lblEstado.setText("DETENIDO");
            lblEstado.setForeground(Color.RED);
            lblPuerto.setText("N/A");
            lblClientes.setText("0");
        }

        boolean reinicioAuto = chkReinicioAutomatico.isSelected();
        lblReinicioAuto.setText(reinicioAuto ? "SÍ" : "NO");
        lblReinicioAuto.setForeground(reinicioAuto ? new Color(0, 150, 0) : Color.RED);
    }

    private void actualizarEstado() {
        if (servidor != null && servidor.isEjecutando()) {
            lblClientes.setText(String.valueOf(servidor.getCantidadClientes()));
        }
    }

    private void agregarLog(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());
            txtLog.append("[" + timestamp + "] " + mensaje + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        agregarLog("❌ ERROR: " + mensaje);
    }

    private void cerrarAplicacion() {
        if (servidor != null && servidor.isEjecutando()) {
            int opcion = JOptionPane.showConfirmDialog(
                this,
                "El servidor está ejecutándose. ¿Desea apagarlo y salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION
            );

            if (opcion == JOptionPane.YES_OPTION) {
                servidor.apagar();
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ServidorGUI gui = new ServidorGUI();
            gui.setVisible(true);
            gui.agregarLog("=== Servidor TCP Iniciado ===");
            gui.agregarLog("Configure el puerto y presione 'Iniciar Servidor'");
        });
    }
}
