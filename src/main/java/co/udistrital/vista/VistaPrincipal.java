package co.udistrital.vista;

import co.udistrital.control.ControlVista;
import co.udistrital.modelo.entidades.SolicitudServicio;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Vista principal de AutoRescate 24/7 (Swing, patrón MVC).
 * Solo captura eventos y delega al {@link ControlVista}.
 *
 * @author AutoRescate 24/7
 */
public class VistaPrincipal extends JFrame {

    private ControlVista cv;
    private JTextArea areaTexto;
    private JMenuBar menuBar;

    /** Construye la ventana sin hacerla visible. */
    public VistaPrincipal() {
        super("AutoRescate 24/7 - Sistema de Gestión");
        inicializarComponentes();
    }

    /** Asigna el controlador de vista. */
    public void setControlVista(ControlVista cv) { this.cv = cv; }

    /** Hace visible la ventana. */
    public void mostrar() { setVisible(true); }

    /** Muestra un diálogo de información. */
    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "AutoRescate 24/7", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Actualiza el área de texto principal. */
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
        addItem(m, "Registrar",       () -> dialogoRegistrarTecnico());
        addItem(m, "Consultar todos", () -> cv.accionConsultarTecnicos());
        addItem(m, "Buscar por ID",   () -> dialogoBuscarTecnico());
        addItem(m, "Cambiar Estado",  () -> dialogoCambiarEstadoTecnico());
        menuBar.add(m);
    }

    private void construirMenuUnidades() {
        JMenu m = new JMenu("Unidades");
        addItem(m, "Registrar",       () -> dialogoRegistrarUnidad());
        addItem(m, "Consultar todas", () -> cv.accionConsultarUnidades());
        addItem(m, "Cambiar Estado",  () -> dialogoCambiarEstadoUnidad());
        menuBar.add(m);
    }

    private void construirMenuClientes() {
        JMenu m = new JMenu("Clientes");
        addItem(m, "Registrar",       () -> dialogoRegistrarCliente());
        addItem(m, "Buscar por ID",   () -> dialogoBuscarCliente());
        menuBar.add(m);
    }

    private void construirMenuSolicitudes() {
        JMenu m = new JMenu("Solicitudes");
        addItem(m, "Registrar",           () -> dialogoRegistrarSolicitud());
        addItem(m, "Consultar todas",     () -> cv.accionConsultarSolicitudes());
        m.addSeparator();
        addItem(m, "Atender siguiente",   () -> dialogoAtenderSiguiente());
        addItem(m, "Completar servicio",  () -> dialogoCompletarServicio());
        menuBar.add(m);
    }

    private void construirMenuKits() {
        JMenu m = new JMenu("Kits");
        addItem(m, "Agregar kit",          () -> cv.accionAgregarKit());
        addItem(m, "Consultar kits",       () -> cv.accionConsultarKits());
        addItem(m, "Revisar kit en cima",  () -> dialogoRevisarKit());
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
        addItem(m, "Exportar CSV",        () -> cv.accionExportarCSV());
        menuBar.add(m);
    }

    /** Utilidad para agregar ítems de menú con lambda. */
    private void addItem(JMenu menu, String texto, Runnable accion) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> accion.run());
        menu.add(item);
    }

    // =========================================================================
    // DIÁLOGOS - TÉCNICOS
    // =========================================================================
    private void dialogoRegistrarTecnico() {
        JTextField fNombre = new JTextField(), fEsp = new JTextField();
        if (JOptionPane.showConfirmDialog(this, new Object[]{"Nombre:", fNombre, "Especialidad:", fEsp},
                "Registrar Técnico", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            cv.accionRegistrarTecnico(fNombre.getText(), fEsp.getText());
    }

    private void dialogoBuscarTecnico() {
        String id = JOptionPane.showInputDialog(this, "ID del técnico:", "Buscar Técnico", JOptionPane.QUESTION_MESSAGE);
        if (id != null && !id.isBlank()) cv.accionBuscarTecnico(id.trim());
    }

    private void dialogoCambiarEstadoTecnico() {
        JTextField fId = new JTextField();
        JComboBox<String> combo = new JComboBox<>(new String[]{"DISPONIBLE", "OCUPADO"});
        if (JOptionPane.showConfirmDialog(this, new Object[]{"ID del Técnico:", fId, "Nuevo Estado:", combo},
                "Cambiar Estado Técnico", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            cv.accionCambiarEstadoTecnico(fId.getText().trim(), (String) combo.getSelectedItem());
    }

    // =========================================================================
    // DIÁLOGOS - UNIDADES
    // =========================================================================
    private void dialogoRegistrarUnidad() {
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"GRUA", "MOTO", "CAMIONETA", "VEHICULO_LIVIANO"});
        JTextField fZona = new JTextField();
        if (JOptionPane.showConfirmDialog(this, new Object[]{"Tipo:", comboTipo, "Zona:", fZona},
                "Registrar Unidad", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            cv.accionRegistrarUnidad((String) comboTipo.getSelectedItem(), fZona.getText());
    }

    private void dialogoCambiarEstadoUnidad() {
        JTextField fId = new JTextField();
        JComboBox<String> combo = new JComboBox<>(new String[]{"DISPONIBLE", "OCUPADO", "MANTENIMIENTO"});
        if (JOptionPane.showConfirmDialog(this, new Object[]{"ID de la Unidad:", fId, "Nuevo Estado:", combo},
                "Cambiar Estado Unidad", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            cv.accionCambiarEstadoUnidad(fId.getText().trim(), (String) combo.getSelectedItem());
    }

    // =========================================================================
    // DIÁLOGOS - CLIENTES
    // =========================================================================
    private void dialogoRegistrarCliente() {
        JTextField fNombre = new JTextField(), fTel = new JTextField();
        if (JOptionPane.showConfirmDialog(this, new Object[]{"Nombre:", fNombre, "Teléfono:", fTel},
                "Registrar Cliente", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            cv.accionRegistrarCliente(fNombre.getText(), fTel.getText());
    }

    private void dialogoBuscarCliente() {
        String id = JOptionPane.showInputDialog(this, "ID del cliente:", "Buscar Cliente", JOptionPane.QUESTION_MESSAGE);
        if (id != null && !id.isBlank()) cv.accionBuscarCliente(id.trim());
    }

    // =========================================================================
    // DIÁLOGOS - SOLICITUDES
    // =========================================================================
    private void dialogoRegistrarSolicitud() {
        JTextField fClienteId = new JTextField(), fDesc = new JTextField();
        JComboBox<String> comboPrio = new JComboBox<>(new String[]{"ORDINARIA", "CRITICA"});
        if (JOptionPane.showConfirmDialog(this,
                new Object[]{"ID del Cliente:", fClienteId, "Descripción:", fDesc, "Prioridad:", comboPrio},
                "Registrar Solicitud", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            cv.accionRegistrarSolicitud(fClienteId.getText().trim(), fDesc.getText(), (String) comboPrio.getSelectedItem());
    }

    /**
     * Muestra la siguiente solicitud en cola y, si el usuario confirma,
     * pide técnico, unidad y si desea kit. Solo desencola si se asignan recursos.
     */
    private void dialogoAtenderSiguiente() {
        SolicitudServicio sol = cv.accionVerSiguienteSolicitud();
        if (sol == null) return;

        // Mostrar info de la solicitud
        String info = "Solicitud a atender:\n"
                + "  ID: " + sol.getId() + "\n"
                + "  Cliente: " + sol.getCliente().getNombre() + "\n"
                + "  Descripción: " + sol.getDescripcion() + "\n"
                + "  Prioridad: " + sol.getPrioridad() + "\n\n"
                + "¿Desea asignar recursos ahora?";

        int confirmar = JOptionPane.showConfirmDialog(this, info, "Atender Siguiente", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;

        // Pedir técnico y unidad
        JTextField fTecnico = new JTextField(), fUnidad = new JTextField();
        JComboBox<String> comboKit = new JComboBox<>(new String[]{"No", "Sí"});
        int res = JOptionPane.showConfirmDialog(this,
                new Object[]{
                    "ID del Técnico:", fTecnico,
                    "ID de la Unidad:", fUnidad,
                    "¿Agregar kit de atención rápida?", comboKit
                },
                "Asignar Recursos - Sol. " + sol.getId(), JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            boolean usarKit = "Sí".equals(comboKit.getSelectedItem());
            cv.accionAsignarRecursosASiguiente(fTecnico.getText().trim(), fUnidad.getText().trim(), usarKit);
        }
    }

    private void dialogoCompletarServicio() {
        String id = JOptionPane.showInputDialog(this, "ID de la solicitud a completar:", "Completar Servicio", JOptionPane.QUESTION_MESSAGE);
        if (id != null && !id.isBlank()) cv.accionCompletarServicio(id.trim());
    }

    // =========================================================================
    // DIÁLOGOS - KITS
    // =========================================================================
    private void dialogoRevisarKit() {
        String[] opciones = {"Reparar (vuelve a LISTO)", "Reponer (nuevo kit)", "Sin novedad (vuelve a LISTO)"};
        int sel = JOptionPane.showOptionDialog(this,
                "¿Qué se hace con el kit en revisión?", "Revisar Kit",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (sel == 0) cv.accionRevisarKitEnCima("REPARAR");
        else if (sel == 1) cv.accionRevisarKitEnCima("REPONER");
        else if (sel == 2) cv.accionRevisarKitEnCima("NADA");
    }

    // =========================================================================
    // DIÁLOGO - IMPORTAR DATOS DE PRUEBA
    // =========================================================================
    private void dialogoImportarDatosPrueba() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setDialogTitle("Seleccionar archivo de datos de prueba");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            cv.accionImportarDatosPrueba(fc.getSelectedFile().getAbsolutePath());
    }
}
