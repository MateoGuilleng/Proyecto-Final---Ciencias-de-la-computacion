package co.udistrital.control;

import co.udistrital.modelo.entidades.Cliente;
import co.udistrital.modelo.entidades.Kit;
import co.udistrital.modelo.entidades.SolicitudServicio;
import co.udistrital.modelo.entidades.Tecnico;
import co.udistrital.modelo.entidades.Tecnico.EstadoTecnico;
import co.udistrital.modelo.entidades.UnidadServicio;
import co.udistrital.modelo.entidades.UnidadServicio.EstadoUnidad;
import co.udistrital.modelo.entidades.UnidadServicio.TipoUnidad;
import co.udistrital.modelo.estructuras.ListaEnlazadaSimple;
import co.udistrital.vista.VistaPrincipal;

/**
 * Controlador de vista de AutoRescate 24/7. Intermediario entre
 * {@link VistaPrincipal} y {@link ControlPrincipal}. La vista nunca manipula
 * directamente el modelo.
 *
 * @author AutoRescate 24/7
 */
public class ControlVista {

    private ControlPrincipal cp;
    private VistaPrincipal vista;

    /**
     * Construye el controlador de vista.
     *
     * @param cp    Controlador principal.
     * @param vista Vista principal.
     */
    public ControlVista(ControlPrincipal cp, VistaPrincipal vista) {
        this.cp = cp;
        this.vista = vista;
    }

    // ---- TÉCNICOS ----
    /**
     * Nombres de las especialidades para combos en la vista.
     */
    public String[] obtenerNombresEspecialidad() {
        Tecnico.Especialidad[] valores = Tecnico.Especialidad.values();
        String[] nombres = new String[valores.length];
        for (int i = 0; i < valores.length; i++) {
            nombres[i] = valores[i].name();
        }
        return nombres;
    }

    /**
     * Registra técnico.
     */
    public void accionRegistrarTecnico(String nombre, String especialidad) {
        if (nombre == null || nombre.isBlank() || especialidad == null || especialidad.isBlank()) {
            vista.mostrarMensaje("Error: Nombre y especialidad son obligatorios.");
            return;
        }
        try {
            Tecnico.Especialidad esp = Tecnico.Especialidad.valueOf(especialidad.toUpperCase().replace(" ", "_"));
            Tecnico t = cp.registrarTecnico(nombre.trim(), esp);
            vista.mostrarMensaje("Técnico registrado: " + t);
            vista.actualizarAreaTexto(listarTecnicos());
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje("Especialidad inválida: " + especialidad);
        }
    }

    /**
     * Consulta todos los técnicos.
     */
    public void accionConsultarTecnicos() {
        vista.actualizarAreaTexto(listarTecnicos());
    }

    /**
     * Busca técnico por id.
     */
    public void accionBuscarTecnico(String id) {
        Tecnico t = cp.buscarTecnico(id);
        vista.actualizarAreaTexto(t == null ? "Técnico no encontrado: " + id : "Técnico encontrado:\n  " + t);
    }

    /**
     * Cambia estado de técnico.
     */
    public void accionCambiarEstadoTecnico(String id, String estado) {
        try {
            boolean ok = cp.cambiarEstadoTecnico(id, EstadoTecnico.valueOf(estado.toUpperCase()));
            vista.mostrarMensaje(ok ? "Estado actualizado." : "Técnico no encontrado.");
            if (ok) {
                vista.actualizarAreaTexto(listarTecnicos());
            }
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje("Estado inválido: " + estado);
        }
    }

    /**
     * Elimina un técnico por id (solo si está DISPONIBLE).
     */
    public void accionEliminarTecnico(String id) {
        Tecnico t = cp.buscarTecnico(id);
        if (t == null) {
            vista.mostrarMensaje("Técnico no encontrado.");
        } else if (t.getEstado() == EstadoTecnico.OCUPADO) {
            vista.mostrarMensaje("No se puede eliminar: el técnico está OCUPADO.");
        } else {
            boolean ok = cp.eliminarTecnico(id);
            vista.mostrarMensaje(ok ? "Técnico eliminado." : "No se pudo eliminar el técnico.");
            if (ok) {
                vista.actualizarAreaTexto(listarTecnicos());
            }
        }
    }

    // ---- UNIDADES ----
    /**
     * Registra unidad.
     */
    public void accionRegistrarUnidad(String tipo, String zona) {
        if (zona == null || zona.isBlank()) {
            vista.mostrarMensaje("Error: La zona es obligatoria.");
            return;
        }
        try {
            UnidadServicio u = cp.registrarUnidad(TipoUnidad.valueOf(tipo.toUpperCase()), zona.trim());
            vista.mostrarMensaje("Unidad registrada: " + u);
            vista.actualizarAreaTexto(listarUnidades());
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje("Tipo inválido: " + tipo);
        }
    }

    /**
     * Consulta todas las unidades.
     */
    public void accionConsultarUnidades() {
        vista.actualizarAreaTexto(listarUnidades());
    }

    /**
     * Cambia estado de unidad.
     */
    public void accionCambiarEstadoUnidad(String id, String estado) {
        try {
            boolean ok = cp.cambiarEstadoUnidad(id, EstadoUnidad.valueOf(estado.toUpperCase()));
            vista.mostrarMensaje(ok ? "Estado actualizado." : "Unidad no encontrada.");
            if (ok) {
                vista.actualizarAreaTexto(listarUnidades());
            }
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje("Estado inválido: " + estado);
        }
    }

    /**
     * Elimina una unidad por id (no se permite si está OCUPADA).
     */
    public void accionEliminarUnidad(String id) {
        UnidadServicio u = cp.buscarUnidad(id);
        if (u == null) {
            vista.mostrarMensaje("Unidad no encontrada.");
        } else if (u.getEstado() == EstadoUnidad.OCUPADO) {
            vista.mostrarMensaje("No se puede eliminar: la unidad está OCUPADA.");
        } else {
            boolean ok = cp.eliminarUnidad(id);
            vista.mostrarMensaje(ok ? "Unidad eliminada." : "No se pudo eliminar la unidad.");
            if (ok) {
                vista.actualizarAreaTexto(listarUnidades());
            }
        }
    }

    // ---- CLIENTES ----
    /**
     * Registra cliente.
     */
    public void accionRegistrarCliente(String nombre, String telefono, String tipo) {
        if (nombre == null || nombre.isBlank()) {
            vista.mostrarMensaje("Error: El nombre es obligatorio.");
            return;
        }
        try {
            Cliente.TipoCliente tc = Cliente.TipoCliente.valueOf(tipo.toUpperCase());
            Cliente c = cp.registrarCliente(nombre.trim(), telefono == null ? "" : telefono.trim(), tc);
            vista.mostrarMensaje("Cliente registrado: " + c);
            vista.actualizarAreaTexto(listarClientes());
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje("Tipo de cliente inválido.");
        }
    }

    /**
     * Busca cliente por id.
     */
    public void accionBuscarCliente(String id) {
        Cliente c = cp.buscarCliente(id);
        vista.actualizarAreaTexto(c == null ? "Cliente no encontrado: " + id : "Cliente encontrado:\n  " + c);
    }

    /**
     * Consulta todos los clientes.
     */
    public void accionConsultarClientes() {
        vista.actualizarAreaTexto(listarClientes());
    }

    /**
     * Elimina un cliente por id (solo si no tiene solicitudes registradas).
     */
    public void accionEliminarCliente(String id) {
        Cliente c = cp.buscarCliente(id);
        if (c == null) {
            vista.mostrarMensaje("Cliente no encontrado.");
        } else {
            boolean ok = cp.eliminarCliente(id);
            if (!ok) {
                vista.mostrarMensaje("No se puede eliminar: el cliente tiene solicitudes registradas.");
            } else {
                vista.mostrarMensaje("Cliente eliminado.");
                vista.actualizarAreaTexto(listarClientes());
            }
        }
    }

    // ---- SOLICITUDES ----
    /**
     * Registra solicitud. La prioridad se calcula automáticamente.
     */
    public void accionRegistrarSolicitud(String clienteId, String tipoServicio, String zona) {
        if (tipoServicio == null || tipoServicio.isBlank()) {
            vista.mostrarMensaje("Error: El tipo de servicio es obligatorio.");
            return;
        }
        try {
            Tecnico.TipoServicio ts = Tecnico.TipoServicio.valueOf(tipoServicio.toUpperCase());
            SolicitudServicio.Zona z = SolicitudServicio.Zona.valueOf(zona.toUpperCase());
            SolicitudServicio sol = cp.registrarSolicitud(clienteId, ts, z);
            if (sol == null) {
                vista.mostrarMensaje("Error: Cliente no encontrado: " + clienteId);
            } else {
                if (cp.pilaKitsDisponiblesVacia()) {
                    vista.mostrarMensaje("Solicitud registrada: " + sol
                            + "\nNo hay kits disponibles. La solicitud queda en cola y se atenderá automáticamente cuando haya kits.");
                } else {
                    vista.mostrarMensaje("Solicitud registrada: " + sol);
                }
                vista.actualizarAreaTexto(listarSolicitudes());
            }
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje("Tipo de servicio o zona inválidos.");
        }
    }

    /**
     * Consulta solicitudes organizadas por estado.
     */
    public void accionConsultarSolicitudes() {
        vista.actualizarAreaTexto(listarSolicitudes());
    }

    /**
     * Elimina una solicitud por id (solo si está PENDIENTE).
     */
    public void accionEliminarSolicitud(String id) {
        SolicitudServicio s = cp.buscarSolicitud(id);
        if (s == null) {
            vista.mostrarMensaje("Solicitud no encontrada.");
        } else if (s.getEstado() != SolicitudServicio.EstadoSolicitud.PENDIENTE) {
            vista.mostrarMensaje("Solo se pueden eliminar solicitudes en estado PENDIENTE.");
        } else {
            boolean ok = cp.eliminarSolicitud(id);
            vista.mostrarMensaje(ok ? "Solicitud eliminada." : "No se pudo eliminar la solicitud.");
            if (ok) {
                vista.actualizarAreaTexto(listarSolicitudes());
            }
        }
    }

    /**
     * Recibe notificaciones del modelo (timers) y actualiza la vista de
     * solicitudes.
     *
     * @param mensaje Mensaje informativo del evento.
     */
    public void notificarActualizacion(String mensaje) {
        notificarActualizacion(mensaje, false);
    }

    /**
     * Recibe notificaciones del modelo y actualiza la vista indicada.
     *
     * @param mensaje   Mensaje informativo del evento.
     * @param vistaKits true para mostrar el estado de las pilas de kits.
     */
    public void notificarActualizacion(String mensaje, boolean vistaKits) {
        vista.mostrarMensaje(mensaje);
        if (vistaKits) {
            vista.actualizarAreaTexto("▶ " + mensaje + "\n\n" + listarKits());
        } else {
            vista.actualizarAreaTexto(listarSolicitudes());
        }
    }

    // ---- KITS ----
    /**
     * Agrega un kit a la pila de disponibles.
     */
    public void accionAgregarKit() {
        Kit kit = cp.agregarKit();
        vista.mostrarMensaje("Kit agregado: " + kit);
        vista.actualizarAreaTexto(listarKits());
    }

    /**
     * Muestra estado de las pilas de kits.
     */
    public void accionConsultarKits() {
        vista.actualizarAreaTexto(listarKits());
    }

    // ---- OPERACIONES ----
    /**
     * Deshace la última operación.
     */
    public void accionDeshacerUltimaOperacion() {
        String r = cp.deshacerUltimaOperacion();
        vista.mostrarMensaje(r);
        vista.actualizarAreaTexto(cp.generarReporte());
    }

    // ---- REPORTES ----
    /**
     * Muestra reporte general.
     */
    public void accionVerReporte() {
        vista.actualizarAreaTexto(cp.generarReporte());
    }

    /**
     * Exporta CSV.
     */
    public void accionExportarCSV() {
        vista.mostrarMensaje(cp.exportarCSV());
    }

    /**
     * Importa datos de prueba.
     */
    public void accionImportarDatosPrueba(String ruta) {
        String r = cp.importarDatosPrueba(ruta);
        vista.mostrarMensaje(r);
        vista.actualizarAreaTexto(cp.generarReporte());
    }

    // ---- HELPERS ----
    private String listarTecnicos() {
        StringBuilder sb = new StringBuilder("TÉCNICOS:\n");
        ListaEnlazadaSimple.Iterador<Tecnico> it = cp.getTecnicos().iterador();
        if (!it.tieneSiguiente()) {
            sb.append("  (Sin técnicos registrados)\n");
        } else {
            while (it.tieneSiguiente()) {
                sb.append("  ").append(it.siguiente()).append("\n");
            }
        }
        return sb.toString();
    }

    private String listarUnidades() {
        StringBuilder sb = new StringBuilder("UNIDADES DE SERVICIO:\n");
        ListaEnlazadaSimple.Iterador<UnidadServicio> it = cp.getUnidades().iterador();
        if (!it.tieneSiguiente()) {
            sb.append("  (Sin unidades registradas)\n");
        } else {
            while (it.tieneSiguiente()) {
                sb.append("  ").append(it.siguiente()).append("\n");
            }
        }
        return sb.toString();
    }

    private String listarClientes() {
        StringBuilder sb = new StringBuilder("CLIENTES:\n");
        ListaEnlazadaSimple.Iterador<Cliente> it = cp.getClientes().iterador();
        if (!it.tieneSiguiente()) {
            sb.append("  (Sin clientes registrados)\n");
        } else {
            while (it.tieneSiguiente()) {
                sb.append("  ").append(it.siguiente()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Solicitudes organizadas en tres secciones.
     */
    public String listarSolicitudes() {
        StringBuilder sb = new StringBuilder();
        sb.append("SOLICITUDES PENDIENTES\n");

        boolean hay = false;
        ListaEnlazadaSimple.Iterador<SolicitudServicio> it = cp.getTodasLasSolicitudes().iterador();
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            if (s.getEstado() == SolicitudServicio.EstadoSolicitud.PENDIENTE
                    && s.getPrioridad() == SolicitudServicio.Prioridad.CRITICA) {
                Tecnico.Especialidad esp = s.getTipoServicio().getEspecialidadRequerida();
                sb.append("  [").append(s.getId()).append("] ")
                        .append(s.getCliente().getNombre()).append(" | ")
                        .append(s.getTipoServicio().getNombre()).append(" | ")
                        .append(s.getPrioridad()).append(" | ")
                        .append(esp.getDuracionMinMin()).append("-")
                        .append(esp.getDuracionMaxMin()).append(" min\n");
                hay = true;
            }
        }
        it = cp.getTodasLasSolicitudes().iterador();
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            if (s.getEstado() == SolicitudServicio.EstadoSolicitud.PENDIENTE
                    && s.getPrioridad() == SolicitudServicio.Prioridad.ORDINARIA) {
                Tecnico.Especialidad esp = s.getTipoServicio().getEspecialidadRequerida();
                sb.append("  [").append(s.getId()).append("] ")
                        .append(s.getCliente().getNombre()).append(" | ")
                        .append(s.getTipoServicio().getNombre()).append(" | ")
                        .append(s.getPrioridad()).append(" | ")
                        .append(esp.getDuracionMinMin()).append("-")
                        .append(esp.getDuracionMaxMin()).append(" min\n");
                hay = true;
            }
        }
        if (!hay) {
            sb.append("  (ninguna)\n");
        }
        sb.append("\n");
        sb.append("SOLICITUDES EN PROCESO\n");

        it = cp.getTodasLasSolicitudes().iterador();
        hay = false;
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            if (s.getEstado() == SolicitudServicio.EstadoSolicitud.EN_PROCESO
                    && s.getPrioridad() == SolicitudServicio.Prioridad.CRITICA) {
                sb.append("  [").append(s.getId()).append("] ")
                        .append(s.getCliente().getNombre()).append(" | ")
                        .append(s.getTipoServicio().getNombre());
                if (s.getTecnicoAsignado() != null) {
                    sb.append(" | Técnico: ").append(s.getTecnicoAsignado().getNombre())
                            .append(" (~").append(s.getDuracionMs() / 1000L * 10L).append(" min)");
                }
                if (s.getUnidadAsignada() != null) {
                    sb.append(" | Unidad: [").append(s.getUnidadAsignada().getId()).append("]");
                }
                if (s.getKitAsignado() != null) {
                    sb.append(" | ").append(s.getKitAsignado());
                }
                sb.append("\n");
                hay = true;
            }
        }
        it = cp.getTodasLasSolicitudes().iterador();
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            if (s.getEstado() == SolicitudServicio.EstadoSolicitud.EN_PROCESO
                    && s.getPrioridad() == SolicitudServicio.Prioridad.ORDINARIA) {
                sb.append("  [").append(s.getId()).append("] ")
                        .append(s.getCliente().getNombre()).append(" | ")
                        .append(s.getTipoServicio().getNombre());
                if (s.getTecnicoAsignado() != null) {
                    sb.append(" | Técnico: ").append(s.getTecnicoAsignado().getNombre())
                            .append(" (~").append(s.getDuracionMs() / 1000L * 10L).append(" min)");
                }
                if (s.getUnidadAsignada() != null) {
                    sb.append(" | Unidad: [").append(s.getUnidadAsignada().getId()).append("]");
                }
                if (s.getKitAsignado() != null) {
                    sb.append(" | ").append(s.getKitAsignado());
                }
                sb.append("\n");
                hay = true;
            }
        }
        if (!hay) {
            sb.append("  (ninguna)\n");
        }
        sb.append("\n");
        sb.append("SOLICITUDES COMPLETADAS\n");

        it = cp.getTodasLasSolicitudes().iterador();
        hay = false;
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            if (s.getEstado() == SolicitudServicio.EstadoSolicitud.COMPLETADA
                    && s.getPrioridad() == SolicitudServicio.Prioridad.CRITICA) {
                sb.append("  [").append(s.getId()).append("] ")
                        .append(s.getCliente().getNombre()).append(" | ")
                        .append(s.getTipoServicio().getNombre()).append("\n");
                hay = true;
            }
        }
        it = cp.getTodasLasSolicitudes().iterador();
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            if (s.getEstado() == SolicitudServicio.EstadoSolicitud.COMPLETADA
                    && s.getPrioridad() == SolicitudServicio.Prioridad.ORDINARIA) {
                sb.append("  [").append(s.getId()).append("] ")
                        .append(s.getCliente().getNombre()).append(" | ")
                        .append(s.getTipoServicio().getNombre()).append("\n");
                hay = true;
            }
        }
        if (!hay) {
            sb.append("  (ninguna)\n");
        }
        return sb.toString();
    }

    private String listarKits() {
        StringBuilder sb = new StringBuilder();
        int disp = cp.getPilaKitsDisponibles().getTamanno();
        int rev = cp.getPilaKitsRevision().getTamanno();
        sb.append("ESTADO DE KITS:");
        sb.append("\n");

        sb.append("  Disponibles: ").append(disp).append("  |  En revisión: ").append(rev).append("\n");

        sb.append("\n");
        sb.append("KITS DISPONIBLES\n");

        if (cp.pilaKitsDisponiblesVacia()) {
            sb.append("  (Sin kits disponibles)\n");
        } else {
            co.udistrital.modelo.estructuras.Pila.Iterador<Kit> it = cp.getPilaKitsDisponibles().iterador();
            int pos = 1;
            while (it.tieneSiguiente()) {
                Kit k = it.siguiente();
                sb.append("  [").append(pos++).append("] ").append(k);
                if (pos == 2) {
                    sb.append("  ← CIMA (próximo a despachar)");
                }
                sb.append("\n");
            }
        }
        sb.append("\n");
        sb.append("KITS EN REVISIÓN\n");
        if (cp.pilaKitsRevisionVacia()) {
            sb.append("  (Sin kits en revisión)\n");
        } else {
            co.udistrital.modelo.estructuras.Pila.Iterador<Kit> it2 = cp.getPilaKitsRevision().iterador();
            int pos2 = 1;
            while (it2.tieneSiguiente()) {
                Kit k = it2.siguiente();
                sb.append("  [").append(pos2++).append("] ").append(k);
                if (pos2 == 2) {
                    sb.append("  ← CIMA (en proceso por operario)");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
