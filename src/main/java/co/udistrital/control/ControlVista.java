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
import co.udistrital.modelo.estructuras.Pila;
import co.udistrital.vista.VistaPrincipal;

/**
 * Controlador de vista de AutoRescate 24/7. Intermediario entre
 * {@link VistaPrincipal} y {@link ControlPrincipal}. La vista nunca manipula
 * directamente el modelo.
 *
 * @author AutoRescate 24/7
 */
public class ControlVista {

    /**
     * Referencia al controlador principal de la lógica de negocio.
     */
    private ControlPrincipal cp;

    /**
     * Referencia a la vista principal de la aplicación.
     */
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
                            + "\nNo hay kits disponibles. Use «Atender siguiente solicitud» cuando haya recursos.");
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
     * Asigna recursos a la siguiente solicitud pendiente atendible en cola.
     */
    public void accionAtenderSiguienteSolicitud() {
        String r = cp.atenderSiguienteSolicitud();
        vista.mostrarMensaje(r);
        if (r.startsWith("Servicio asignado")) {
            vista.actualizarAreaTexto(listarSolicitudes());
        }
    }

    /**
     * Completa manualmente una solicitud en proceso.
     */
    public void accionCompletarServicio(String solicitudId) {
        if (solicitudId == null || solicitudId.isBlank()) {
            vista.mostrarMensaje("Error: Indique el ID de la solicitud.");
            return;
        }
        String r = cp.completarServicio(solicitudId.trim());
        vista.mostrarMensaje(r);
        if (!r.startsWith("Error:")) {
            vista.actualizarAreaTexto(listarSolicitudes());
        }
    }

    /**
     * Revisa el kit en la cima de la pila de kits en revisión.
     */
    public void accionRevisarKit() {
        String r = cp.revisarKitEnRevision();
        vista.mostrarMensaje(r);
        if (!r.startsWith("Error:")) {
            vista.actualizarAreaTexto(listarKits());
        }
    }

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

    /**
     * Deshace la última operación.
     */
    public void accionDeshacerUltimaOperacion() {
        String r = cp.deshacerUltimaOperacion();
        vista.mostrarMensaje(r);
    }

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

    /**
     * Genera texto con la lista de técnicos registrados.
     *
     * @return Representación textual de los técnicos.
     */
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

    /**
     * Genera texto con la lista de unidades registradas.
     *
     * @return Representación textual de las unidades.
     */
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

    /**
     * Genera texto con la lista de clientes registrados.
     *
     * @return Representación textual de los clientes.
     */
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
                        .append(esp.getNombre()).append("\n");
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
                        .append(esp.getNombre()).append("\n");
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
                    sb.append(" | Técnico: ").append(s.getTecnicoAsignado().getNombre());
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
                    sb.append(" | Técnico: ").append(s.getTecnicoAsignado().getNombre());
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

    /**
     * Genera texto con el estado de las pilas de kits.
     *
     * @return Representación textual de los kits.
     */
    private String listarKits() {
        StringBuilder sb = new StringBuilder();
        int disp = cp.getPilaKitsDisponibles().getTamanno();
        int rev = cp.getPilaKitsRevision().getTamanno();
        int enUso = contarKitsEnUso();
        sb.append("ESTADO DE KITS:");
        sb.append("\n");

        sb.append("  Disponibles: ").append(disp).append("  |  En revisión: ").append(rev)
                .append("  |  En uso: ").append(enUso).append("\n");

        sb.append("\n");
        sb.append("KITS DISPONIBLES\n");

        if (cp.pilaKitsDisponiblesVacia()) {
            sb.append("  (Sin kits disponibles)\n");
        } else {
            Pila.Iterador<Kit> it = cp.getPilaKitsDisponibles().iterador();
            int pos = 1;
            while (it.tieneSiguiente()) {
                Kit k = it.siguiente();
                sb.append("  [").append(pos++).append("] ").append(k);
                
                sb.append("\n");
            }
        }
        sb.append("\n");
        sb.append("KITS EN REVISIÓN\n");
        if (cp.pilaKitsRevisionVacia()) {
            sb.append("  (Sin kits en revisión)\n");
        } else {
            Pila.Iterador<Kit> it2 = cp.getPilaKitsRevision().iterador();
            int pos2 = 1;
            while (it2.tieneSiguiente()) {
                Kit k = it2.siguiente();
                sb.append("  [").append(pos2++).append("] ").append(k);
                
                sb.append("\n");
            }
        }
        sb.append("\n");
        sb.append("KITS EN USO \n");
        if (enUso == 0) {
            sb.append("  (Ninguno)\n");
        } else {
            ListaEnlazadaSimple.Iterador<SolicitudServicio> itSol = cp.getTodasLasSolicitudes().iterador();
            int pos3 = 1;
            while (itSol.tieneSiguiente()) {
                SolicitudServicio s = itSol.siguiente();
                Kit k = s.getKitAsignado();
                if (k != null && k.getEstado() == Kit.EstadoKit.EN_USO) {
                    sb.append("  [").append(pos3++).append("] ").append(k)
                            .append(" → solicitud ").append(s.getId()).append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Cuenta los kits actualmente en uso en solicitudes activas.
     *
     * @return Cantidad de kits en estado {@link Kit.EstadoKit#EN_USO}.
     */
    private int contarKitsEnUso() {
        int n = 0;
        ListaEnlazadaSimple.Iterador<SolicitudServicio> it = cp.getTodasLasSolicitudes().iterador();
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            Kit k = s.getKitAsignado();
            if (k != null && k.getEstado() == Kit.EstadoKit.EN_USO) {
                n++;
            }
        }
        return n;
    }
}
