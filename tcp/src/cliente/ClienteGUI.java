package cliente;

import comun.ConfiguracionPoliticas;
import comun.EstadoConexion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Interfaz gráfica para el Cliente TCP
 */
public class ClienteGUI extends JFrame {
    private Cliente cliente;
    private ConfiguracionPoliticas config;
    private ConfiguracionTimeout configTimeout;

    // Componentes de configuración
    private JTextField txtNombre;
    private JTextField txtHost;
    private JTextField txtPuerto;
    private JCheckBox chkReconexion;
    private JSpinner spinnerReintentos;

    // Componentes de control
    private JButton btnConectar;
    private JButton btnDesconectar;
    private JButton btnEnviar;
    private JButton btnLimpiarChat;
    private JButton btnLimpiarLog;

    // Componentes de chat
    private JTextArea txtChat;
    private JTextArea txtLog;
    private JTextField txtMensaje;
    private JScrollPane scrollChat;
    private JScrollPane scrollLog;

    // Componentes de estado
    private JLabel lblEstado;
    private JLabel lblServidor;
    private JLabel lblReconexion;
    private JPanel panelEstadoConexion;

    // Estado
    private boolean conectado = false;
    private Thread hiloLectura;

    public ClienteGUI() {
        setTitle("Cliente TCP - Interfaz Gráfica");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        config = new ConfiguracionPoliticas();
        config.setMaxReintentos(5);
        config.setTiempoEsperaEntreReintentos(3000);

        configTimeout = new ConfiguracionTimeout();

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

        // Panel central - Chat y Log
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(crearPanelChat());
        splitPane.setBottomComponent(crearPanelLog());
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.6);
        panelPrincipal.add(splitPane, BorderLayout.CENTER);

        // Panel derecho - Estado
        panelPrincipal.add(crearPanelEstado(), BorderLayout.EAST);

        add(panelPrincipal);
    }

    private JPanel crearPanelConfiguracion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Configuración de Conexión",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        txtNombre = new JTextField("Cliente1", 12);
        panel.add(txtNombre, gbc);

        // Host
        gbc.gridx = 2;
        panel.add(new JLabel("Host:"), gbc);

        gbc.gridx = 3;
        txtHost = new JTextField("localhost", 12);
        panel.add(txtHost, gbc);

        // Puerto
        gbc.gridx = 4;
        panel.add(new JLabel("Puerto:"), gbc);

        gbc.gridx = 5;
        txtPuerto = new JTextField("8080", 6);
        panel.add(txtPuerto, gbc);

        // Segunda fila - Políticas
        gbc.gridx = 0;
        gbc.gridy = 1;
        chkReconexion = new JCheckBox("Reconexión Automática", true);
        panel.add(chkReconexion, gbc);

        gbc.gridx = 1;
        gbc.gridx = 2;
        panel.add(new JLabel("Max Reintentos:"), gbc);

        gbc.gridx = 3;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(5, 1, 20, 1);
        spinnerReintentos = new JSpinner(spinnerModel);
        panel.add(spinnerReintentos, gbc);

        // Botones de conexión
        gbc.gridx = 4;
        gbc.gridwidth = 2;
        JPanel panelBotonesConexion = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        btnConectar = new JButton("🔌 Conectar");
        btnConectar.setFont(new Font("Arial", Font.BOLD, 11));
        btnConectar.setBackground(new Color(0, 150, 0));
        btnConectar.setForeground(Color.WHITE);
        btnConectar.setFocusPainted(false);

        btnDesconectar = new JButton("❌ Desconectar");
        btnDesconectar.setFont(new Font("Arial", Font.BOLD, 11));
        btnDesconectar.setBackground(new Color(200, 0, 0));
        btnDesconectar.setForeground(Color.WHITE);
        btnDesconectar.setFocusPainted(false);
        btnDesconectar.setEnabled(false);

        panelBotonesConexion.add(btnConectar);
        panelBotonesConexion.add(btnDesconectar);
        panel.add(panelBotonesConexion, gbc);

        return panel;
    }

    private JPanel crearPanelChat() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Chat - Mensajes",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        // Área de chat
        txtChat = new JTextArea();
        txtChat.setEditable(false);
        txtChat.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtChat.setLineWrap(true);
        txtChat.setWrapStyleWord(true);

        scrollChat = new JScrollPane(txtChat);
        scrollChat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollChat, BorderLayout.CENTER);

        // Panel de envío
        JPanel panelEnvio = new JPanel(new BorderLayout(5, 0));
        panelEnvio.setBorder(new EmptyBorder(5, 0, 0, 0));

        txtMensaje = new JTextField();
        txtMensaje.setFont(new Font("Arial", Font.PLAIN, 13));
        txtMensaje.setEnabled(false);

        btnEnviar = new JButton("📤 Enviar");
        btnEnviar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEnviar.setBackground(new Color(0, 120, 215));
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setEnabled(false);
        btnEnviar.setPreferredSize(new Dimension(100, 30));

        btnLimpiarChat = new JButton("🗑");
        btnLimpiarChat.setToolTipText("Limpiar chat");
        btnLimpiarChat.setPreferredSize(new Dimension(50, 30));

        panelEnvio.add(txtMensaje, BorderLayout.CENTER);
        panelEnvio.add(btnEnviar, BorderLayout.EAST);
        panelEnvio.add(btnLimpiarChat, BorderLayout.WEST);

        panel.add(panelEnvio, BorderLayout.SOUTH);

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
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtLog.setBackground(new Color(40, 40, 40));
        txtLog.setForeground(new Color(0, 255, 0));

        scrollLog = new JScrollPane(txtLog);
        scrollLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollLog, BorderLayout.CENTER);

        // Botón limpiar log
        JPanel panelBotonLog = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLimpiarLog = new JButton("🗑 Limpiar Log");
        panelBotonLog.add(btnLimpiarLog);
        panel.add(panelBotonLog, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Estado de Conexión",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        panel.setPreferredSize(new Dimension(220, 0));

        // Indicador visual de estado
        panelEstadoConexion = new JPanel();
        panelEstadoConexion.setPreferredSize(new Dimension(200, 80));
        panelEstadoConexion.setMaximumSize(new Dimension(200, 80));
        panelEstadoConexion.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        panelEstadoConexion.setBackground(new Color(255, 100, 100));

        JLabel lblIndicador = new JLabel("●", SwingConstants.CENTER);
        lblIndicador.setFont(new Font("Arial", Font.BOLD, 60));
        lblIndicador.setForeground(Color.RED);
        panelEstadoConexion.add(lblIndicador);

        panel.add(panelEstadoConexion);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Información de estado
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 10));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.setMaximumSize(new Dimension(200, 120));

        // Estado
        JPanel panelEstadoLabel = new JPanel(new BorderLayout());
        panelEstadoLabel.add(new JLabel("Estado:"), BorderLayout.NORTH);
        lblEstado = new JLabel("DESCONECTADO");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 12));
        lblEstado.setForeground(Color.RED);
        panelEstadoLabel.add(lblEstado, BorderLayout.CENTER);
        infoPanel.add(panelEstadoLabel);

        // Servidor
        JPanel panelServidorLabel = new JPanel(new BorderLayout());
        panelServidorLabel.add(new JLabel("Servidor:"), BorderLayout.NORTH);
        lblServidor = new JLabel("N/A");
        lblServidor.setFont(new Font("Arial", Font.PLAIN, 11));
        panelServidorLabel.add(lblServidor, BorderLayout.CENTER);
        infoPanel.add(panelServidorLabel);

        // Reconexión
        JPanel panelReconexionLabel = new JPanel(new BorderLayout());
        panelReconexionLabel.add(new JLabel("Reconexión:"), BorderLayout.NORTH);
        lblReconexion = new JLabel("HABILITADA");
        lblReconexion.setFont(new Font("Arial", Font.BOLD, 11));
        lblReconexion.setForeground(new Color(0, 150, 0));
        panelReconexionLabel.add(lblReconexion, BorderLayout.CENTER);
        infoPanel.add(panelReconexionLabel);

        panel.add(infoPanel);

        return panel;
    }

    private void configurarEventos() {
        btnConectar.addActionListener(e -> conectarServidor());
        btnDesconectar.addActionListener(e -> desconectarServidor());
        btnEnviar.addActionListener(e -> enviarMensaje());
        btnLimpiarChat.addActionListener(e -> txtChat.setText(""));
        btnLimpiarLog.addActionListener(e -> txtLog.setText(""));

        // Enter en campo de mensaje para enviar
        txtMensaje.addActionListener(e -> enviarMensaje());

        // Actualizar estado de reconexión
        chkReconexion.addActionListener(e -> {
            boolean habilitada = chkReconexion.isSelected();
            lblReconexion.setText(habilitada ? "HABILITADA" : "DESHABILITADA");
            lblReconexion.setForeground(habilitada ? new Color(0, 150, 0) : Color.RED);
            if (cliente != null) {
                cliente.setReconexionHabilitada(habilitada);
            }
        });

        // Monitor de estado
        Timer timer = new Timer(500, e -> actualizarEstado());
        timer.start();
    }

    private void conectarServidor() {
        try {
            String nombre = txtNombre.getText().trim();
            String host = txtHost.getText().trim();
            int puerto = Integer.parseInt(txtPuerto.getText().trim());

            if (nombre.isEmpty() || host.isEmpty()) {
                mostrarError("El nombre y el host no pueden estar vacíos");
                return;
            }

            if (puerto < 1024 || puerto > 65535) {
                mostrarError("El puerto debe estar entre 1024 y 65535");
                return;
            }

            config.setMaxReintentos((Integer) spinnerReintentos.getValue());

            agregarLog("🔄 Intentando conectar a " + host + ":" + puerto + "...");

            // Conectar en un hilo separado para no bloquear la UI
            new Thread(() -> {
                cliente = new Cliente(nombre, host, puerto, config, configTimeout);
                cliente.setReconexionHabilitada(chkReconexion.isSelected());
                cliente.setReintentosHabilitados(true);

                if (cliente.conectar()) {
                    SwingUtilities.invokeLater(() -> {
                        conectado = true;
                        actualizarEstadoConexion(true);
                        agregarLog("✓ Conectado exitosamente");
                        agregarChat("=== Conectado al servidor ===");
                        iniciarHiloLectura();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        agregarLog("❌ No se pudo conectar al servidor");
                        mostrarError("No se pudo conectar al servidor después de " +
                                   config.getMaxReintentos() + " intentos");
                    });
                }
            }).start();

        } catch (NumberFormatException ex) {
            mostrarError("Puerto inválido. Debe ser un número.");
        } catch (Exception ex) {
            mostrarError("Error al conectar: " + ex.getMessage());
        }
    }

    private void desconectarServidor() {
        if (cliente != null && cliente.isConectado()) {
            cliente.desconectar();
            conectado = false;
            actualizarEstadoConexion(false);
            agregarLog("❌ Desconectado del servidor");
            agregarChat("=== Desconectado del servidor ===");

            if (hiloLectura != null && hiloLectura.isAlive()) {
                hiloLectura.interrupt();
            }
        }
    }

    private void enviarMensaje() {
        if (!conectado || cliente == null || !cliente.isConectado()) {
            mostrarError("No está conectado al servidor");
            return;
        }

        String mensaje = txtMensaje.getText().trim();
        if (mensaje.isEmpty()) {
            return;
        }

        cliente.enviarMensaje(mensaje);
        agregarChat("TÚ: " + mensaje);
        txtMensaje.setText("");
    }

    private void iniciarHiloLectura() {
        hiloLectura = new Thread(() -> {
            try {
                // Obtener flujo de entrada directamente del socket del cliente
                while (conectado && cliente.isConectado()) {
                    // Simular recepción de mensajes
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        hiloLectura.setDaemon(true);
        hiloLectura.start();
    }

    private void actualizarEstadoConexion(boolean conectado) {
        SwingUtilities.invokeLater(() -> {
            btnConectar.setEnabled(!conectado);
            btnDesconectar.setEnabled(conectado);
            btnEnviar.setEnabled(conectado);
            txtMensaje.setEnabled(conectado);

            txtNombre.setEnabled(!conectado);
            txtHost.setEnabled(!conectado);
            txtPuerto.setEnabled(!conectado);
            chkReconexion.setEnabled(!conectado);
            spinnerReintentos.setEnabled(!conectado);

            if (conectado) {
                lblEstado.setText("CONECTADO");
                lblEstado.setForeground(new Color(0, 150, 0));
                lblServidor.setText(txtHost.getText() + ":" + txtPuerto.getText());
                panelEstadoConexion.setBackground(new Color(100, 255, 100));
                // Cambiar color del indicador
                Component[] components = panelEstadoConexion.getComponents();
                if (components.length > 0 && components[0] instanceof JLabel) {
                    ((JLabel) components[0]).setForeground(new Color(0, 200, 0));
                }
            } else {
                lblEstado.setText("DESCONECTADO");
                lblEstado.setForeground(Color.RED);
                lblServidor.setText("N/A");
                panelEstadoConexion.setBackground(new Color(255, 100, 100));
                // Cambiar color del indicador
                Component[] components = panelEstadoConexion.getComponents();
                if (components.length > 0 && components[0] instanceof JLabel) {
                    ((JLabel) components[0]).setForeground(Color.RED);
                }
            }
        });
    }

    private void actualizarEstado() {
        if (cliente != null && cliente.isConectado() != conectado) {
            conectado = cliente.isConectado();
            actualizarEstadoConexion(conectado);

            if (!conectado) {
                agregarLog("⚠ Conexión perdida");
            }
        }
    }

    private void agregarChat(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());
            txtChat.append("[" + timestamp + "] " + mensaje + "\n");
            txtChat.setCaretPosition(txtChat.getDocument().getLength());
        });
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
        if (cliente != null && cliente.isConectado()) {
            cliente.desconectar();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ClienteGUI gui = new ClienteGUI();
            gui.setVisible(true);
            gui.agregarLog("=== Cliente TCP Iniciado ===");
            gui.agregarLog("Configure la conexión y presione 'Conectar'");
        });
    }
}
