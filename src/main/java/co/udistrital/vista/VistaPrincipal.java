package co.udistrital.vista;

import co.udistrital.control.ControlVista;
import co.udistrital.modelo.entidades.Tecnico;
import co.udistrital.modelo.entidades.SolicitudServicio;
import co.udistrital.modelo.entidades.Cliente;

import javax.swing.*;
import java.awt.*;

/**
 * Vista principal de AutoRescate 24/7 (Swing, patrón MVC). Solo captura eventos
 * y delega al {@link ControlVista}.
 *
 * @author AutoRescate 24/7
 */
public class VistaPrincipal extends JFrame {

    private ControlVista cv;
    private JTextArea areaTexto;
    private JMenuBar menuBar;

    /**
     * Construye la ventana sin hacerla visible.
     */
    public VistaPrincipal() {
        super("AutoRescate 24/7 - Sistema de Gestión");
        inicializarComponentes();
    }

    /**
     * Asigna el controlador de vista.
     */
    public void setControlVista(ControlVista cv) {
        this.cv = cv;
    }

    /**
     * Hace visible la ventana.
     */
    public void mostrar() {
        setVisible(true);
    }

    /**
     * Muestra un diálogo de información.
     */
    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "AutoRescate 24/7", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Actualiza el área de texto principal.
     */
    public void actualizarAreaTexto(String texto) {
        areaTexto.setText(texto);
        areaTexto.setCaretPosition(0);
    }

    // =========================================================================
    // INICIALIZACIÓN
    // =========================================================================
    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel norte: título + botón de prueba
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JLabel titulo = new JLabel("AutoRescate 24/7 - Centro de Operaciones", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(new Color(0, 70, 140));
        panelNorte.add(titulo, BorderLayout.CENTER);
        JButton btnPrueba = new JButton("Importar Datos de Prueba");
        btnPrueba.setFont(new Font("Arial", Font.PLAIN, 11));
        btnPrueba.setToolTipText("Carga datos de prueba desde un archivo CSV");
        btnPrueba.addActionListener(e -> dialogoImportarDatosPrueba());
        panelNorte.add(btnPrueba, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        // Área de texto central
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaTexto.setText("Bienvenido al sistema AutoRescate 24/7.\nUse el menú para gestionar técnicos, unidades, solicitudes y kits.");
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(BorderFactory.createTitledBorder("Información del Sistema"));
        add(scroll, BorderLayout.CENTER);

        // Barra de menú
        menuBar = new JMenuBar();
        construirMenuTecnicos();
        construirMenuUnidades();
        construirMenuClientes();
        construirMenuSolicitudes();
        construirMenuKits();
        construirMenuOperaciones();
        construirMenuReportes();
        setJMenuBar(menuBar);

        JLabel status = new JLabel("  Sistema listo.");
        status.setBorder(BorderFactory.createEtchedBorder());
        add(status, BorderLayout.SOUTH);
    }

    // =========================================================================
    // MENÚS
    // =========================================================================
    private void construirMenuTecnicos() {
        JMenu m = new JMenu("Técnicos");
        addItem(m, "Registrar", () -> dialogoRegistrarTecnico());
        addItem(m, "Consultar todos", () -> cv.accionConsultarTecnicos());
        addItem(m, "Buscar por ID", () -> dialogoBuscarTecnico());
        addItem(m, "Cambiar Estado", () -> dialogoCambiarEstadoTecnico());
        addItem(m, "Eliminar Técnico", () -> dialogoEliminarTecnico());
        menuBar.add(m);
    }

    private void construirMenuUnidades() {
        JMenu m = new JMenu("Unidades");
        addItem(m, "Registrar", () -> dialogoRegistrarUnidad());
        addItem(m, "Consultar todas", () -> cv.accionConsultarUnidades());
        addItem(m, "Cambiar Estado", () -> dialogoCambiarEstadoUnidad());
        addItem(m, "Eliminar Unidad", () -> dialogoEliminarUnidad());
        menuBar.add(m);
    }

    private void construirMenuClientes() {
        JMenu m = new JMenu("Clientes");
        addItem(m, "Registrar", () -> dialogoRegistrarCliente());
        addItem(m, "Ver clientes", () -> cv.accionConsultarClientes());
        addItem(m, "Buscar por ID", () -> dialogoBuscarCliente());
        addItem(m, "Eliminar Cliente", () -> dialogoEliminarCliente());
        menuBar.add(m);
    }

    private void construirMenuSolicitudes() {
        JMenu m = new JMenu("Solicitudes");
        addItem(m, "Registrar", () -> dialogoRegistrarSolicitud());
        addItem(m, "Consultar todas", () -> cv.accionConsultarSolicitudes());
        addItem(m, "Eliminar solicitud", () -> dialogoEliminarSolicitud());
        menuBar.add(m);
    }

    private void construirMenuKits() {
        JMenu m = new JMenu("Kits");
        addItem(m, "Agregar kit", () -> cv.accionAgregarKit());
        addItem(m, "Consultar kits", () -> cv.accionConsultarKits());
        menuBar.add(m);
    }

    private void construirMenuOperaciones() {
        JMenu m = new JMenu("Operaciones");
        addItem(m, "Deshacer última operación", () -> cv.accionDeshacerUltimaOperacion());
        menuBar.add(m);
    }

    private void construirMenuReportes() {
        JMenu m = new JMenu("Reportes");
        addItem(m, "Ver reporte general", () -> cv.accionVerReporte());
        addItem(m, "Exportar CSV", () -> cv.accionExportarCSV());
        menuBar.add(m);
    }

    /**
     * Utilidad para agregar ítems de menú con lambda.
     */
    private void addItem(JMenu menu, String texto, Runnable accion) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> accion.run());
        menu.add(item);
    }

    // =========================================================================
    // DIÁLOGOS - TÉCNICOS
    // =========================================================================
    private void dialogoRegistrarTecnico() {
        JTextField fNombre = new JTextField();
        JComboBox<String> comboEsp = new JComboBox<>(cv.obtenerNombresEspecialidad());
        if (JOptionPane.showConfirmDialog(this,
                new Object[]{"Nombre:", fNombre, "Especialidad:", comboEsp},
                "Registrar Técnico", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            cv.accionRegistrarTecnico(fNombre.getText(), (String) comboEsp.getSelectedItem());
        }
    }

    private void dialogoBuscarTecnico() {
        String id = JOptionPane.showInputDialog(this, "ID del técnico:", "Buscar Técnico", JOptionPane.QUESTION_MESSAGE);
        if (id != null && !id.isBlank()) {
            cv.accionBuscarTecnico(id.trim());
        }
    }

    private void dialogoCambiarEstadoTecnico() {
        JTextField fId = new JTextField();
        JComboBox<String> combo = new JComboBox<>(new String[]{"DISPONIBLE", "OCUPADO"});
        if (JOptionPane.showConfirmDialog(this, new Object[]{"ID del Técnico:", fId, "Nuevo Estado:", combo},
                "Cambiar Estado Técnico", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            cv.accionCambiarEstadoTecnico(fId.getText().trim(), (String) combo.getSelectedItem());
        }
    }

    private void dialogoEliminarTecnico() {
        String id = JOptionPane.showInputDialog(this, "ID del técnico a eliminar:", "Eliminar Técnico", JOptionPane.QUESTION_MESSAGE);
        if (id != null && !id.isBlank()) {
            cv.accionEliminarTecnico(id.trim());
        }
    }

    // =========================================================================
    // DIÁLOGOS - UNIDADES
    // =========================================================================
    private void dialogoRegistrarUnidad() {
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"GRUA", "MOTO", "CAMIONETA", "VEHICULO_LIVIANO"});
        JTextField fZona = new JTextField();
        if (JOptionPane.showConfirmDialog(this, new Object[]{"Tipo:", comboTipo, "Zona:", fZona},
                "Registrar Unidad", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            cv.accionRegistrarUnidad((String) comboTipo.getSelectedItem(), fZona.getText());
        }
    }

    private void dialogoCambiarEstadoUnidad() {
        JTextField fId = new JTextField();
        JComboBox<String> combo = new JComboBox<>(new String[]{"DISPONIBLE", "OCUPADO", "MANTENIMIENTO"});
        if (JOptionPane.showConfirmDialog(this, new Object[]{"ID de la Unidad:", fId, "Nuevo Estado:", combo},
                "Cambiar Estado Unidad", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            cv.accionCambiarEstadoUnidad(fId.getText().trim(), (String) combo.getSelectedItem());
        }
    }

    private void dialogoEliminarUnidad() {
        String id = JOptionPane.showInputDialog(this, "ID de la unidad a eliminar:", "Eliminar Unidad", JOptionPane.QUESTION_MESSAGE);
        if (id != null && !id.isBlank()) {
            cv.accionEliminarUnidad(id.trim());
        }
    }

    // =========================================================================
    // DIÁLOGOS - CLIENTES
    // =========================================================================
    private void dialogoRegistrarCliente() {
        JTextField fNombre = new JTextField(), fTel = new JTextField();
        JComboBox<Cliente.TipoCliente> comboTipo = new JComboBox<>(Cliente.TipoCliente.values());
        if (JOptionPane.showConfirmDialog(this,
                new Object[]{"Nombre:", fNombre, "Teléfono:", fTel, "Tipo:", comboTipo},
                "Registrar Cliente", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            cv.accionRegistrarCliente(fNombre.getText(), fTel.getText(),
                    ((Cliente.TipoCliente) comboTipo.getSelectedItem()).name());
        }
    }

    private void dialogoBuscarCliente() {
        String id = JOptionPane.showInputDialog(this, "ID del cliente:", "Buscar Cliente", JOptionPane.QUESTION_MESSAGE);
        if (id != null && !id.isBlank()) {
            cv.accionBuscarCliente(id.trim());
        }
    }

    private void dialogoEliminarCliente() {
        String id = JOptionPane.showInputDialog(this, "ID del cliente a eliminar:", "Eliminar Cliente", JOptionPane.QUESTION_MESSAGE);
        if (id != null && !id.isBlank()) {
            cv.accionEliminarCliente(id.trim());
        }
    }

    // =========================================================================
    // DIÁLOGOS - SOLICITUDES
    // =========================================================================
    private void dialogoRegistrarSolicitud() {
        JTextField fClienteId = new JTextField();
        JComboBox<Tecnico.TipoServicio> comboTipo = new JComboBox<>(Tecnico.TipoServicio.values());
        JComboBox<SolicitudServicio.Zona> comboZona = new JComboBox<>(SolicitudServicio.Zona.values());

        JLabel lblInfo = new JLabel(getInfoTipoServicio((Tecnico.TipoServicio) comboTipo.getSelectedItem()));
        comboTipo.addActionListener(e
                -> lblInfo.setText(getInfoTipoServicio((Tecnico.TipoServicio) comboTipo.getSelectedItem()))
        );

        if (JOptionPane.showConfirmDialog(this,
                new Object[]{"ID del Cliente:", fClienteId,
                    "Tipo de servicio:", comboTipo,
                    "Especialidad requerida:", lblInfo,
                    "Zona del incidente:", comboZona},
                "Registrar Solicitud", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Tecnico.TipoServicio ts = (Tecnico.TipoServicio) comboTipo.getSelectedItem();
            SolicitudServicio.Zona z = (SolicitudServicio.Zona) comboZona.getSelectedItem();
            cv.accionRegistrarSolicitud(fClienteId.getText().trim(), ts.name(), z.name());
        }
    }

    private void dialogoEliminarSolicitud() {
        String id = JOptionPane.showInputDialog(this, "ID de la solicitud a eliminar:", "Eliminar Solicitud", JOptionPane.QUESTION_MESSAGE);
        if (id != null && !id.isBlank()) {
            cv.accionEliminarSolicitud(id.trim());
        }
    }

    /**
     * Texto informativo de la especialidad requerida para un tipo de servicio.
     */
    private String getInfoTipoServicio(Tecnico.TipoServicio ts) {
        if (ts == null) {
            return "";
        } else {
            return ts.getEspecialidadRequerida().getNombre() + "  |  "
                    + ts.getEspecialidadRequerida().getDuracionMinMin() + "-"
                    + ts.getEspecialidadRequerida().getDuracionMaxMin() + " min";
        }
    }

    // =========================================================================
    // DIÁLOGO - IMPORTAR DATOS DE PRUEBA
    // =========================================================================
    private void dialogoImportarDatosPrueba() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setDialogTitle("Seleccionar archivo de datos de prueba");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            cv.accionImportarDatosPrueba(fc.getSelectedFile().getAbsolutePath());
        }
    }
}
