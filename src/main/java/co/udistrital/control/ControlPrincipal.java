package co.udistrital.control;

import co.udistrital.modelo.entidades.*;
import co.udistrital.modelo.entidades.SolicitudServicio.EstadoSolicitud;
import co.udistrital.modelo.entidades.SolicitudServicio.Prioridad;
import co.udistrital.modelo.entidades.Tecnico.EstadoTecnico;
import co.udistrital.modelo.entidades.UnidadServicio.EstadoUnidad;
import co.udistrital.modelo.entidades.Kit.EstadoKit;
import co.udistrital.modelo.estructuras.ListaEnlazadaSimple;
import co.udistrital.modelo.estructuras.Pila;
import co.udistrital.modelo.estructuras.Cola;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Controlador principal de la lógica de negocio de AutoRescate 24/7.
 * Gestiona técnicos, unidades, clientes, solicitudes, kits y movimientos.
 * No usa ninguna clase del Collections Framework de Java.
 *
 * @author AutoRescate 24/7
 */
public class ControlPrincipal {

    private ListaEnlazadaSimple<Tecnico> tecnicos;
    private ListaEnlazadaSimple<UnidadServicio> unidades;
    private ListaEnlazadaSimple<Cliente> clientes;
    private ListaEnlazadaSimple<SolicitudServicio> todasLasSolicitudes;
    private Cola<SolicitudServicio> colaOrdinarias;
    private Cola<SolicitudServicio> colaCriticas;
    private Pila<Kit> pilaKitsDisponibles;
    private Pila<Kit> pilaKitsRevision;
    private Pila<Movimiento> pilaMovimientos;

    /**     * Construye el controlador principal, inicializa estructuras y arranca la GUI.
     */
    public ControlPrincipal() {
        tecnicos              = new ListaEnlazadaSimple<>();
        unidades              = new ListaEnlazadaSimple<>();
        clientes              = new ListaEnlazadaSimple<>();
        todasLasSolicitudes   = new ListaEnlazadaSimple<>();
        colaOrdinarias        = new Cola<>();
        colaCriticas          = new Cola<>();
        pilaKitsDisponibles   = new Pila<>();
        pilaKitsRevision      = new Pila<>();
        pilaMovimientos       = new Pila<>();

        javax.swing.SwingUtilities.invokeLater(() -> {
            co.udistrital.vista.VistaPrincipal vista = new co.udistrital.vista.VistaPrincipal();
            ControlVista controlVista = new ControlVista(this, vista);
            vista.setControlVista(controlVista);
            vista.mostrar();
        });
    }

    // =========================================================================
    // CRUD TÉCNICOS
    // =========================================================================

    /** Registra un nuevo técnico. @return El técnico creado. */
    public Tecnico registrarTecnico(String nombre, String especialidad) {
        Tecnico t = new Tecnico(String.valueOf(tecnicos.getTamanno() + 1), nombre, especialidad);
        tecnicos.agregar(t);
        return t;
    }

    /** Busca técnico por id. @return El técnico o null. */
    public Tecnico buscarTecnico(String id) {
        ListaEnlazadaSimple.Iterador<Tecnico> it = tecnicos.iterador();
        while (it.tieneSiguiente()) { Tecnico t = it.siguiente(); if (t.getId().equals(id)) return t; }
        return null;
    }

    /** @return Lista de técnicos. */
    public ListaEnlazadaSimple<Tecnico> getTecnicos() { return tecnicos; }

    /** Cambia estado de un técnico y registra movimiento. */
    public boolean cambiarEstadoTecnico(String id, EstadoTecnico estado) {
        Tecnico t = buscarTecnico(id);
        if (t == null) return false;
        EstadoTecnico anterior = t.getEstado();
        t.setEstado(estado);
        Movimiento mov = new Movimiento(Movimiento.TipoOperacion.CAMBIAR_ESTADO_TECNICO,
                "Estado técnico " + t.getNombre() + ": " + anterior + " -> " + estado);
        mov.setTecnico(t); mov.setEstadoAnteriorTecnico(anterior);
        pilaMovimientos.push(mov);
        return true;
    }

    // =========================================================================
    // CRUD UNIDADES
    // =========================================================================

    /** Registra una nueva unidad. @return La unidad creada. */
    public UnidadServicio registrarUnidad(UnidadServicio.TipoUnidad tipo, String zona) {
        UnidadServicio u = new UnidadServicio(String.valueOf(unidades.getTamanno() + 1), tipo, zona);
        unidades.agregar(u);
        return u;
    }

    /** Busca unidad por id. @return La unidad o null. */
    public UnidadServicio buscarUnidad(String id) {
        ListaEnlazadaSimple.Iterador<UnidadServicio> it = unidades.iterador();
        while (it.tieneSiguiente()) { UnidadServicio u = it.siguiente(); if (u.getId().equals(id)) return u; }
        return null;
    }

    /** @return Lista de unidades. */
    public ListaEnlazadaSimple<UnidadServicio> getUnidades() { return unidades; }

    /** Cambia estado de una unidad y registra movimiento. */
    public boolean cambiarEstadoUnidad(String id, EstadoUnidad estado) {
        UnidadServicio u = buscarUnidad(id);
        if (u == null) return false;
        EstadoUnidad anterior = u.getEstado();
        u.setEstado(estado);
        Movimiento mov = new Movimiento(Movimiento.TipoOperacion.CAMBIAR_ESTADO_UNIDAD,
                "Estado unidad [" + id + "]: " + anterior + " -> " + estado);
        mov.setUnidad(u); mov.setEstadoAnteriorUnidad(anterior);
        pilaMovimientos.push(mov);
        return true;
    }

    // =========================================================================
    // CRUD CLIENTES
    // =========================================================================

    /** Registra un nuevo cliente. @return El cliente creado. */
    public Cliente registrarCliente(String nombre, String telefono) {
        Cliente c = new Cliente(String.valueOf(clientes.getTamanno() + 1), nombre, telefono);
        clientes.agregar(c);
        return c;
    }

    /** Busca cliente por id. @return El cliente o null. */
    public Cliente buscarCliente(String id) {
        ListaEnlazadaSimple.Iterador<Cliente> it = clientes.iterador();
        while (it.tieneSiguiente()) { Cliente c = it.siguiente(); if (c.getId().equals(id)) return c; }
        return null;
    }

    /** @return Lista de clientes. */
    public ListaEnlazadaSimple<Cliente> getClientes() { return clientes; }

    // =========================================================================
    // CRUD SOLICITUDES
    // =========================================================================

    /**
     * Registra una nueva solicitud. CRITICAS van a colaCriticas, ORDINARIAS a colaOrdinarias.
     * @return La solicitud creada, o null si el cliente no existe.
     */
    public SolicitudServicio registrarSolicitud(String clienteId, String descripcion, Prioridad prioridad) {
        Cliente cliente = buscarCliente(clienteId);
        if (cliente == null) return null;
        SolicitudServicio sol = new SolicitudServicio(
                String.valueOf(todasLasSolicitudes.getTamanno() + 1), cliente, descripcion, prioridad);
        todasLasSolicitudes.agregar(sol);
        if (prioridad == Prioridad.CRITICA) colaCriticas.encolar(sol);
        else colaOrdinarias.encolar(sol);
        return sol;
    }

    /** Busca solicitud por id. @return La solicitud o null. */
    public SolicitudServicio buscarSolicitud(String id) {
        ListaEnlazadaSimple.Iterador<SolicitudServicio> it = todasLasSolicitudes.iterador();
        while (it.tieneSiguiente()) { SolicitudServicio s = it.siguiente(); if (s.getId().equals(id)) return s; }
        return null;
    }

    /** @return Lista completa de solicitudes. */
    public ListaEnlazadaSimple<SolicitudServicio> getTodasLasSolicitudes() { return todasLasSolicitudes; }

    /**
     * Obtiene la siguiente solicitud a atender SIN desencolarla todavía.
     * Solo se desencola definitivamente al asignar recursos.
     * @return La solicitud en el frente de la cola, o null si no hay.
     */
    public SolicitudServicio verSiguienteSolicitud() {
        // Usamos peek: miramos sin desencolar
        if (!colaCriticas.estaVacia()) return colaCriticas.verFrente();
        if (!colaOrdinarias.estaVacia()) return colaOrdinarias.verFrente();
        return null;
    }

    /**
     * Asigna recursos a la siguiente solicitud en cola y la desencola.
     * Si se usa kit, se toma el de la cima de pilaKitsDisponibles.
     * Registra el movimiento para soporte de Undo.
     *
     * @param tecnicoId  Id del técnico.
     * @param unidadId   Id de la unidad.
     * @param usarKit    true si se debe asignar un kit de atención rápida.
     * @return Mensaje de resultado.
     */
    public String asignarRecursosASiguiente(String tecnicoId, String unidadId, boolean usarKit) {
        SolicitudServicio sol = verSiguienteSolicitud();
        if (sol == null) return "Error: No hay solicitudes pendientes en cola.";

        Tecnico tec = buscarTecnico(tecnicoId);
        if (tec == null) return "Error: Técnico no encontrado.";
        if (!tec.isDisponible()) return "Error: El técnico no está disponible.";

        UnidadServicio uni = buscarUnidad(unidadId);
        if (uni == null) return "Error: Unidad no encontrada.";
        if (!uni.isDisponible()) return "Error: La unidad no está disponible.";
        if (uni.getEstado() == EstadoUnidad.MANTENIMIENTO) return "Error: La unidad está en mantenimiento.";

        Kit kit = null;
        if (usarKit) {
            if (pilaKitsDisponibles.estaVacia()) return "Error: No hay kits disponibles en la pila.";
            kit = pilaKitsDisponibles.pop();
            kit.setEstado(EstadoKit.EN_USO);
        }

        // Guardar estados anteriores para Undo
        EstadoTecnico estadoAntTec = tec.getEstado();
        EstadoUnidad estadoAntUni = uni.getEstado();
        EstadoSolicitud estadoAntSol = sol.getEstado();

        // Desencolar definitivamente
        if (sol.getPrioridad() == Prioridad.CRITICA) colaCriticas.desencolar();
        else colaOrdinarias.desencolar();

        // Asignar
        tec.setEstado(EstadoTecnico.OCUPADO);
        uni.setEstado(EstadoUnidad.OCUPADO);
        sol.setTecnicoAsignado(tec);
        sol.setUnidadAsignada(uni);
        sol.setKitAsignado(kit);
        sol.setEstado(EstadoSolicitud.EN_PROCESO);

        // Registrar movimiento
        Movimiento mov = new Movimiento(Movimiento.TipoOperacion.ASIGNAR_RECURSOS,
                "Asignación: Sol." + sol.getId() + " -> Técnico " + tec.getNombre()
                        + " + Unidad [" + uni.getId() + "]"
                        + (kit != null ? " + " + kit : ""));
        mov.setSolicitud(sol); mov.setTecnico(tec); mov.setUnidad(uni); mov.setKit(kit);
        mov.setEstadoAnteriorTecnico(estadoAntTec);
        mov.setEstadoAnteriorUnidad(estadoAntUni);
        mov.setEstadoAnteriorSolicitud(estadoAntSol);
        pilaMovimientos.push(mov);

        return "Recursos asignados. Solicitud " + sol.getId() + " en proceso.";
    }

    /**
     * Marca una solicitud EN_PROCESO como COMPLETADA y libera sus recursos.
     * Si tenía kit asignado, lo pasa a la pila de revisión.
     *
     * @param solicitudId Id de la solicitud.
     * @return Mensaje de resultado.
     */
    public String completarServicio(String solicitudId) {
        SolicitudServicio sol = buscarSolicitud(solicitudId);
        if (sol == null) return "Error: Solicitud no encontrada.";
        if (sol.getEstado() != EstadoSolicitud.EN_PROCESO) return "Error: La solicitud no está en proceso.";
        if (!sol.tieneRecursosAsignados()) return "Error: La solicitud no tiene recursos asignados.";

        EstadoSolicitud estadoAnt = sol.getEstado();
        EstadoTecnico estadoAntTec = sol.getTecnicoAsignado().getEstado();
        EstadoUnidad estadoAntUni = sol.getUnidadAsignada().getEstado();

        sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
        sol.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
        sol.setEstado(EstadoSolicitud.COMPLETADA);

        // Si tenía kit, pasa a revisión
        if (sol.getKitAsignado() != null) {
            sol.getKitAsignado().setEstado(EstadoKit.EN_REVISION);
            pilaKitsRevision.push(sol.getKitAsignado());
        }

        Movimiento mov = new Movimiento(Movimiento.TipoOperacion.COMPLETAR_SERVICIO,
                "Servicio completado: Solicitud " + solicitudId);
        mov.setSolicitud(sol);
        mov.setTecnico(sol.getTecnicoAsignado());
        mov.setUnidad(sol.getUnidadAsignada());
        mov.setEstadoAnteriorSolicitud(estadoAnt);
        mov.setEstadoAnteriorTecnico(estadoAntTec);
        mov.setEstadoAnteriorUnidad(estadoAntUni);
        pilaMovimientos.push(mov);

        return "Servicio " + solicitudId + " completado exitosamente.";
    }

    // =========================================================================
    // GESTIÓN DE KITS
    // =========================================================================

    /**
     * Agrega un nuevo kit a la pila de kits disponibles.
     * @return El kit creado.
     */
    public Kit agregarKit() {
        Kit kit = new Kit(String.valueOf(
                pilaKitsDisponibles.getTamanno() + pilaKitsRevision.getTamanno() + 1));
        pilaKitsDisponibles.push(kit);
        return kit;
    }

    /** @return El kit en la cima de la pila disponible, o null. */
    public Kit verKitDisponibleEnCima() { return pilaKitsDisponibles.cima(); }

    /** @return true si no hay kits disponibles. */
    public boolean pilaKitsDisponiblesVacia() { return pilaKitsDisponibles.estaVacia(); }

    /** @return La pila de kits disponibles para iteración en la vista. */
    public co.udistrital.modelo.estructuras.Pila<Kit> getPilaKitsDisponibles() { return pilaKitsDisponibles; }

    /** @return El kit en la cima de la pila de revisión, o null. */
    public Kit verKitRevisionEnCima() { return pilaKitsRevision.cima(); }

    /** @return true si no hay kits en revisión. */
    public boolean pilaKitsRevisionVacia() { return pilaKitsRevision.estaVacia(); }

    /** @return La pila de kits en revisión para iteración en la vista. */
    public co.udistrital.modelo.estructuras.Pila<Kit> getPilaKitsRevision() { return pilaKitsRevision; }

    /**
     * Revisa el kit en la cima de la pila de revisión.
     * - "REPONER": se elimina el kit y se agrega uno nuevo a disponibles.
     * - "REPARAR" o "NADA": el kit pasa a LISTO y vuelve a disponibles.
     *
     * @param decision "REPONER", "REPARAR" o "NADA".
     * @return Mensaje de resultado.
     */
    public String revisarKitEnCima(String decision) {
        if (pilaKitsRevision.estaVacia()) return "No hay kits en revisión.";
        Kit kit = pilaKitsRevision.pop();
        switch (decision.toUpperCase()) {
            case "REPONER":
                // El kit viejo se descarta, se agrega uno nuevo
                Kit nuevo = agregarKit();
                return "Kit-" + kit.getId() + " repuesto. Nuevo kit agregado: " + nuevo;
            case "REPARAR":
            case "NADA":
                kit.setEstado(EstadoKit.LISTO);
                pilaKitsDisponibles.push(kit);
                return "Kit-" + kit.getId() + " revisado y devuelto a disponibles (LISTO).";
            default:
                // Decisión inválida: devolver a revisión
                pilaKitsRevision.push(kit);
                return "Decisión inválida. Kit devuelto a revisión.";
        }
    }

    // =========================================================================
    // UNDO
    // =========================================================================

    /**
     * Deshace la última operación registrada en la pila de movimientos.
     * @return Descripción de la operación deshecha.
     */
    public String deshacerUltimaOperacion() {
        if (pilaMovimientos.estaVacia()) return "No hay operaciones para deshacer.";
        Movimiento mov = pilaMovimientos.pop();
        switch (mov.getTipoOperacion()) {
            case ASIGNAR_RECURSOS:
                if (mov.getTecnico() != null) mov.getTecnico().setEstado(mov.getEstadoAnteriorTecnico());
                if (mov.getUnidad() != null) mov.getUnidad().setEstado(mov.getEstadoAnteriorUnidad());
                if (mov.getSolicitud() != null) {
                    mov.getSolicitud().setEstado(mov.getEstadoAnteriorSolicitud());
                    mov.getSolicitud().setTecnicoAsignado(null);
                    mov.getSolicitud().setUnidadAsignada(null);
                    // Devolver kit a disponibles si se usó
                    if (mov.getKit() != null) {
                        mov.getKit().setEstado(EstadoKit.LISTO);
                        pilaKitsDisponibles.push(mov.getKit());
                        mov.getSolicitud().setKitAsignado(null);
                    }
                    // Reencolar
                    if (mov.getSolicitud().getPrioridad() == Prioridad.CRITICA)
                        colaCriticas.encolar(mov.getSolicitud());
                    else colaOrdinarias.encolar(mov.getSolicitud());
                }
                break;
            case COMPLETAR_SERVICIO:
                if (mov.getTecnico() != null) mov.getTecnico().setEstado(mov.getEstadoAnteriorTecnico());
                if (mov.getUnidad() != null) mov.getUnidad().setEstado(mov.getEstadoAnteriorUnidad());
                if (mov.getSolicitud() != null) mov.getSolicitud().setEstado(mov.getEstadoAnteriorSolicitud());
                // Sacar kit de revisión si aplica
                if (mov.getSolicitud() != null && mov.getSolicitud().getKitAsignado() != null) {
                    pilaKitsRevision.pop(); // quitar el kit que se puso al completar
                    mov.getSolicitud().getKitAsignado().setEstado(EstadoKit.EN_USO);
                }
                break;
            case CAMBIAR_ESTADO_TECNICO:
                if (mov.getTecnico() != null) mov.getTecnico().setEstado(mov.getEstadoAnteriorTecnico());
                break;
            case CAMBIAR_ESTADO_UNIDAD:
                if (mov.getUnidad() != null) mov.getUnidad().setEstado(mov.getEstadoAnteriorUnidad());
                break;
        }
        return "Operación deshecha: " + mov.getDescripcion();
    }

    /** @return Descripción del último movimiento sin deshacer. */
    public String verUltimaOperacion() {
        if (pilaMovimientos.estaVacia()) return "No hay operaciones registradas.";
        return pilaMovimientos.cima().toString();
    }

    // =========================================================================
    // REPORTES Y CSV
    // =========================================================================

    /** Genera reporte general del sistema. */
    public String generarReporte() {
        StringBuilder sb = new StringBuilder("===== REPORTE AUTORESCATE 24/7 =====\n\n");

        sb.append("-- TÉCNICOS (").append(tecnicos.getTamanno()).append(") --\n");
        ListaEnlazadaSimple.Iterador<Tecnico> itT = tecnicos.iterador();
        while (itT.tieneSiguiente()) sb.append("  ").append(itT.siguiente()).append("\n");

        sb.append("\n-- UNIDADES (").append(unidades.getTamanno()).append(") --\n");
        ListaEnlazadaSimple.Iterador<UnidadServicio> itU = unidades.iterador();
        while (itU.tieneSiguiente()) sb.append("  ").append(itU.siguiente()).append("\n");

        sb.append("\n-- SOLICITUDES PENDIENTES --\n");
        ListaEnlazadaSimple.Iterador<SolicitudServicio> itS = todasLasSolicitudes.iterador();
        while (itS.tieneSiguiente()) { SolicitudServicio s = itS.siguiente();
            if (s.getEstado() == EstadoSolicitud.PENDIENTE) sb.append("  ").append(s).append("\n"); }

        sb.append("\n-- SOLICITUDES EN PROCESO --\n");
        itS = todasLasSolicitudes.iterador();
        while (itS.tieneSiguiente()) { SolicitudServicio s = itS.siguiente();
            if (s.getEstado() == EstadoSolicitud.EN_PROCESO) sb.append("  ").append(s).append("\n"); }

        sb.append("\n-- SOLICITUDES COMPLETADAS --\n");
        itS = todasLasSolicitudes.iterador();
        while (itS.tieneSiguiente()) { SolicitudServicio s = itS.siguiente();
            if (s.getEstado() == EstadoSolicitud.COMPLETADA) sb.append("  ").append(s).append("\n"); }

        sb.append("\n-- KITS DISPONIBLES (cima): ");
        sb.append(pilaKitsDisponibles.estaVacia() ? "ninguno" : pilaKitsDisponibles.cima()).append("\n");
        sb.append("-- KITS EN REVISIÓN (cima): ");
        sb.append(pilaKitsRevision.estaVacia() ? "ninguno" : pilaKitsRevision.cima()).append("\n");

        sb.append("\n-- ÚLTIMO MOVIMIENTO --\n  ").append(verUltimaOperacion()).append("\n");
        return sb.toString();
    }

    /** Exporta solicitudes COMPLETADAS a servicios_atendidos.csv. */
    public String exportarCSV() {
        try (FileWriter fw = new FileWriter("servicios_atendidos.csv")) {
            fw.write("id,cliente,descripcion,prioridad,estado\n");
            int count = 0;
            ListaEnlazadaSimple.Iterador<SolicitudServicio> it = todasLasSolicitudes.iterador();
            while (it.tieneSiguiente()) {
                SolicitudServicio s = it.siguiente();
                if (s.getEstado() == EstadoSolicitud.COMPLETADA) {
                    fw.write(s.getId() + "," + s.getCliente().getNombre().replace(",", ";")
                            + "," + s.getDescripcion().replace(",", ";")
                            + "," + s.getPrioridad() + "," + s.getEstado() + "\n");
                    count++;
                }
            }
            return "CSV exportado: servicios_atendidos.csv (" + count + " registros).";
        } catch (IOException e) { return "Error al exportar CSV: " + e.getMessage(); }
    }

    /**
     * Importa datos de prueba desde un archivo CSV.
     * Formato: CLIENTE,nombre,tel | TECNICO,nombre,esp | UNIDAD,tipo,zona | KIT | SOLICITUD,clienteId,desc,prioridad
     */
    public String importarDatosPrueba(String ruta) {
        int cli = 0, tec = 0, uni = 0, kit = 0, sol = 0, err = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] p = linea.split(",", -1);
                try {
                    switch (p[0].trim().toUpperCase()) {
                        case "CLIENTE":  if (p.length >= 3) { registrarCliente(p[1].trim(), p[2].trim()); cli++; } else err++; break;
                        case "TECNICO":  if (p.length >= 3) { registrarTecnico(p[1].trim(), p[2].trim()); tec++; } else err++; break;
                        case "UNIDAD":   if (p.length >= 3) { registrarUnidad(UnidadServicio.TipoUnidad.valueOf(p[1].trim().toUpperCase()), p[2].trim()); uni++; } else err++; break;
                        case "KIT":      agregarKit(); kit++; break;
                        case "SOLICITUD":
                            if (p.length >= 4) {
                                SolicitudServicio s = registrarSolicitud(p[1].trim(), p[2].trim(), Prioridad.valueOf(p[3].trim().toUpperCase()));
                                if (s != null) sol++; else err++;
                            } else err++;
                            break;
                        default: err++;
                    }
                } catch (Exception ex) { err++; }
            }
        } catch (IOException e) { return "Error al leer el archivo: " + e.getMessage(); }
        return String.format("Importación completada:\n  Clientes: %d\n  Técnicos: %d\n  Unidades: %d\n  Kits: %d\n  Solicitudes: %d\n  Errores: %d",
                cli, tec, uni, kit, sol, err);
    }
}
