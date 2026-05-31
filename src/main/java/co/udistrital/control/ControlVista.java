package co.udistrital.control;

import co.udistrital.modelo.entidades.*;
import co.udistrital.modelo.entidades.SolicitudServicio.Prioridad;
import co.udistrital.modelo.entidades.UnidadServicio.EstadoUnidad;
import co.udistrital.modelo.entidades.UnidadServicio.TipoUnidad;
import co.udistrital.modelo.entidades.Tecnico.EstadoTecnico;
import co.udistrital.modelo.estructuras.ListaEnlazadaSimple;
import co.udistrital.vista.VistaPrincipal;

/**
 * Controlador de vista de AutoRescate 24/7.
 * Intermediario entre {@link VistaPrincipal} y {@link ControlPrincipal}.
 * La vista nunca manipula directamente el modelo.
 *
 * @author AutoRescate 24/7
 */
public class ControlVista {

    private ControlPrincipal cp;
    private VistaPrincipal vista;

    /**
     * Construye el controlador de vista.
     * @param cp    Controlador principal.
     * @param vista Vista principal.
     */
    public ControlVista(ControlPrincipal cp, VistaPrincipal vista) {
        this.cp = cp;
        this.vista = vista;
    }

    // ---- TÉCNICOS ----

    /** Registra técnico. */
    public void accionRegistrarTecnico(String nombre, String especialidad) {
        if (nombre == null || nombre.isBlank() || especialidad == null || especialidad.isBlank()) {
            vista.mostrarMensaje("Error: Nombre y especialidad son obligatorios."); return;
        }
        Tecnico t = cp.registrarTecnico(nombre.trim(), especialidad.trim());
        vista.mostrarMensaje("Técnico registrado: " + t);
        vista.actualizarAreaTexto(listarTecnicos());
    }

    /** Consulta todos los técnicos. */
    public void accionConsultarTecnicos() { vista.actualizarAreaTexto(listarTecnicos()); }

    /** Busca técnico por id. */
    public void accionBuscarTecnico(String id) {
        Tecnico t = cp.buscarTecnico(id);
        vista.actualizarAreaTexto(t == null ? "Técnico no encontrado: " + id : "Técnico encontrado:\n  " + t);
    }

    /** Cambia estado de técnico. */
    public void accionCambiarEstadoTecnico(String id, String estado) {
        try {
            boolean ok = cp.cambiarEstadoTecnico(id, EstadoTecnico.valueOf(estado.toUpperCase()));
            vista.mostrarMensaje(ok ? "Estado actualizado." : "Técnico no encontrado.");
            if (ok) vista.actualizarAreaTexto(listarTecnicos());
        } catch (IllegalArgumentException e) { vista.mostrarMensaje("Estado inválido: " + estado); }
    }

    // ---- UNIDADES ----

    /** Registra unidad. */
    public void accionRegistrarUnidad(String tipo, String zona) {
        if (zona == null || zona.isBlank()) { vista.mostrarMensaje("Error: La zona es obligatoria."); return; }
        try {
            UnidadServicio u = cp.registrarUnidad(TipoUnidad.valueOf(tipo.toUpperCase()), zona.trim());
            vista.mostrarMensaje("Unidad registrada: " + u);
            vista.actualizarAreaTexto(listarUnidades());
        } catch (IllegalArgumentException e) { vista.mostrarMensaje("Tipo inválido: " + tipo); }
    }

    /** Consulta todas las unidades. */
    public void accionConsultarUnidades() { vista.actualizarAreaTexto(listarUnidades()); }

    /** Cambia estado de unidad. */
    public void accionCambiarEstadoUnidad(String id, String estado) {
        try {
            boolean ok = cp.cambiarEstadoUnidad(id, EstadoUnidad.valueOf(estado.toUpperCase()));
            vista.mostrarMensaje(ok ? "Estado actualizado." : "Unidad no encontrada.");
            if (ok) vista.actualizarAreaTexto(listarUnidades());
        } catch (IllegalArgumentException e) { vista.mostrarMensaje("Estado inválido: " + estado); }
    }

    // ---- CLIENTES ----

    /** Registra cliente. */
    public void accionRegistrarCliente(String nombre, String telefono) {
        if (nombre == null || nombre.isBlank()) { vista.mostrarMensaje("Error: El nombre es obligatorio."); return; }
        Cliente c = cp.registrarCliente(nombre.trim(), telefono == null ? "" : telefono.trim());
        vista.mostrarMensaje("Cliente registrado: " + c);
        vista.actualizarAreaTexto(listarClientes());
    }

    /** Busca cliente por id. */
    public void accionBuscarCliente(String id) {
        Cliente c = cp.buscarCliente(id);
        vista.actualizarAreaTexto(c == null ? "Cliente no encontrado: " + id : "Cliente encontrado:\n  " + c);
    }

    // ---- SOLICITUDES ----

    /** Registra solicitud. */
    public void accionRegistrarSolicitud(String clienteId, String descripcion, String prioridad) {
        if (descripcion == null || descripcion.isBlank()) { vista.mostrarMensaje("Error: La descripción es obligatoria."); return; }
        try {
            SolicitudServicio sol = cp.registrarSolicitud(clienteId, descripcion.trim(), Prioridad.valueOf(prioridad.toUpperCase()));
            if (sol == null) vista.mostrarMensaje("Error: Cliente no encontrado: " + clienteId);
            else { vista.mostrarMensaje("Solicitud registrada: " + sol); vista.actualizarAreaTexto(listarSolicitudes()); }
        } catch (IllegalArgumentException e) { vista.mostrarMensaje("Prioridad inválida: " + prioridad); }
    }

    /** Consulta solicitudes organizadas por estado. */
    public void accionConsultarSolicitudes() { vista.actualizarAreaTexto(listarSolicitudes()); }

    /**
     * Muestra la siguiente solicitud en cola y retorna sus datos para que la vista
     * pueda pedir los recursos al usuario.
     * @return La solicitud, o null si no hay pendientes.
     */
    public SolicitudServicio accionVerSiguienteSolicitud() {
        SolicitudServicio sol = cp.verSiguienteSolicitud();
        if (sol == null) vista.mostrarMensaje("No hay solicitudes pendientes en cola.");
        return sol;
    }

    /**
     * Asigna recursos a la siguiente solicitud y la desencola.
     * @param tecnicoId Id del técnico.
     * @param unidadId  Id de la unidad.
     * @param usarKit   true si se asigna kit.
     */
    public void accionAsignarRecursosASiguiente(String tecnicoId, String unidadId, boolean usarKit) {
        String r = cp.asignarRecursosASiguiente(tecnicoId, unidadId, usarKit);
        vista.mostrarMensaje(r);
        vista.actualizarAreaTexto(listarSolicitudes());
    }

    /** Completa un servicio. */
    public void accionCompletarServicio(String solicitudId) {
        String r = cp.completarServicio(solicitudId);
        vista.mostrarMensaje(r);
        vista.actualizarAreaTexto(listarSolicitudes());
    }

    // ---- KITS ----

    /** Agrega un kit a la pila de disponibles. */
    public void accionAgregarKit() {
        Kit kit = cp.agregarKit();
        vista.mostrarMensaje("Kit agregado: " + kit);
        vista.actualizarAreaTexto(listarKits());
    }

    /** Revisa el kit en la cima de la pila de revisión. */
    public void accionRevisarKitEnCima(String decision) {
        String r = cp.revisarKitEnCima(decision);
        vista.mostrarMensaje(r);
        vista.actualizarAreaTexto(listarKits());
    }

    /** Muestra estado de las pilas de kits. */
    public void accionConsultarKits() { vista.actualizarAreaTexto(listarKits()); }

    // ---- OPERACIONES ----

    /** Deshace la última operación. */
    public void accionDeshacerUltimaOperacion() {
        String r = cp.deshacerUltimaOperacion();
        vista.mostrarMensaje(r);
        vista.actualizarAreaTexto(cp.generarReporte());
    }

    // ---- REPORTES ----

    /** Muestra reporte general. */
    public void accionVerReporte() { vista.actualizarAreaTexto(cp.generarReporte()); }

    /** Exporta CSV. */
    public void accionExportarCSV() { vista.mostrarMensaje(cp.exportarCSV()); }

    /** Importa datos de prueba. */
    public void accionImportarDatosPrueba(String ruta) {
        String r = cp.importarDatosPrueba(ruta);
        vista.mostrarMensaje(r);
        vista.actualizarAreaTexto(cp.generarReporte());
    }

    // ---- HELPERS ----

    private String listarTecnicos() {
        StringBuilder sb = new StringBuilder("=== TÉCNICOS ===\n");
        ListaEnlazadaSimple.Iterador<Tecnico> it = cp.getTecnicos().iterador();
        if (!it.tieneSiguiente()) { sb.append("  (Sin técnicos registrados)\n"); return sb.toString(); }
        while (it.tieneSiguiente()) sb.append("  ").append(it.siguiente()).append("\n");
        return sb.toString();
    }

    private String listarUnidades() {
        StringBuilder sb = new StringBuilder("=== UNIDADES DE SERVICIO ===\n");
        ListaEnlazadaSimple.Iterador<UnidadServicio> it = cp.getUnidades().iterador();
        if (!it.tieneSiguiente()) { sb.append("  (Sin unidades registradas)\n"); return sb.toString(); }
        while (it.tieneSiguiente()) sb.append("  ").append(it.siguiente()).append("\n");
        return sb.toString();
    }

    private String listarClientes() {
        StringBuilder sb = new StringBuilder("=== CLIENTES ===\n");
        ListaEnlazadaSimple.Iterador<Cliente> it = cp.getClientes().iterador();
        if (!it.tieneSiguiente()) { sb.append("  (Sin clientes registrados)\n"); return sb.toString(); }
        while (it.tieneSiguiente()) sb.append("  ").append(it.siguiente()).append("\n");
        return sb.toString();
    }

    /** Solicitudes organizadas en tres secciones. */
    public String listarSolicitudes() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║         SOLICITUDES PENDIENTES       ║\n");
        sb.append("╚══════════════════════════════════════╝\n");
        ListaEnlazadaSimple.Iterador<SolicitudServicio> it = cp.getTodasLasSolicitudes().iterador();
        boolean hay = false;
        while (it.tieneSiguiente()) { SolicitudServicio s = it.siguiente();
            if (s.getEstado() == SolicitudServicio.EstadoSolicitud.PENDIENTE) { sb.append("  ").append(s).append("\n"); hay = true; } }
        if (!hay) sb.append("  (ninguna)\n");

        sb.append("\n╔══════════════════════════════════════╗\n");
        sb.append("║         SOLICITUDES EN PROCESO       ║\n");
        sb.append("╚══════════════════════════════════════╝\n");
        it = cp.getTodasLasSolicitudes().iterador(); hay = false;
        while (it.tieneSiguiente()) { SolicitudServicio s = it.siguiente();
            if (s.getEstado() == SolicitudServicio.EstadoSolicitud.EN_PROCESO) {
                sb.append("  ").append(s);
                if (s.getTecnicoAsignado() != null) sb.append(" | Técnico: ").append(s.getTecnicoAsignado().getNombre());
                if (s.getUnidadAsignada() != null) sb.append(" | Unidad: [").append(s.getUnidadAsignada().getId()).append("]");
                if (s.getKitAsignado() != null) sb.append(" | ").append(s.getKitAsignado());
                sb.append("\n"); hay = true; } }
        if (!hay) sb.append("  (ninguna)\n");

        sb.append("\n╔══════════════════════════════════════╗\n");
        sb.append("║        SOLICITUDES COMPLETADAS       ║\n");
        sb.append("╚══════════════════════════════════════╝\n");
        it = cp.getTodasLasSolicitudes().iterador(); hay = false;
        while (it.tieneSiguiente()) { SolicitudServicio s = it.siguiente();
            if (s.getEstado() == SolicitudServicio.EstadoSolicitud.COMPLETADA) { sb.append("  ").append(s).append("\n"); hay = true; } }
        if (!hay) sb.append("  (ninguna)\n");
        return sb.toString();
    }

    private String listarKits() {
        StringBuilder sb = new StringBuilder();

        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║        KITS DISPONIBLES (PILA)       ║\n");
        sb.append("╚══════════════════════════════════════╝\n");
        if (cp.pilaKitsDisponiblesVacia()) {
            sb.append("  (pila vacía)\n");
        } else {
            sb.append("  ↑ TOPE (se usa primero)\n");
            co.udistrital.modelo.estructuras.Pila.Iterador<Kit> it = cp.getPilaKitsDisponibles().iterador();
            int pos = 1;
            while (it.tieneSiguiente()) {
                Kit k = it.siguiente();
                sb.append("  [").append(pos++).append("] ").append(k).append("\n");
            }
            sb.append("  ↓ FONDO\n");
        }

        sb.append("\n╔══════════════════════════════════════╗\n");
        sb.append("║        KITS EN REVISIÓN (PILA)       ║\n");
        sb.append("╚══════════════════════════════════════╝\n");
        if (cp.pilaKitsRevisionVacia()) {
            sb.append("  (pila vacía)\n");
        } else {
            sb.append("  ↑ TOPE (se revisa primero)\n");
            co.udistrital.modelo.estructuras.Pila.Iterador<Kit> it2 = cp.getPilaKitsRevision().iterador();
            int pos2 = 1;
            while (it2.tieneSiguiente()) {
                Kit k = it2.siguiente();
                sb.append("  [").append(pos2++).append("] ").append(k).append("\n");
            }
            sb.append("  ↓ FONDO\n");
        }
        return sb.toString();
    }
}
