package co.udistrital.control;

import co.udistrital.modelo.entidades.*;
import co.udistrital.modelo.entidades.SolicitudServicio.EstadoSolicitud;
import co.udistrital.modelo.entidades.SolicitudServicio.Prioridad;
import co.udistrital.modelo.entidades.Tecnico.EstadoTecnico;
import co.udistrital.modelo.entidades.UnidadServicio.EstadoUnidad;
import co.udistrital.modelo.entidades.Kit.EstadoKit;
import co.udistrital.modelo.entidades.Kit.DecisionRevision;
import co.udistrital.modelo.estructuras.ListaEnlazadaSimple;
import co.udistrital.modelo.estructuras.Pila;
import co.udistrital.modelo.estructuras.Cola;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

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

    private ControlVista controlVista;
    private final Random random = new Random();

    /**
     * Construye el controlador principal, inicializa estructuras y arranca la GUI.
     */
    public ControlPrincipal() {
        tecnicos            = new ListaEnlazadaSimple<>();
        unidades            = new ListaEnlazadaSimple<>();
        clientes            = new ListaEnlazadaSimple<>();
        todasLasSolicitudes = new ListaEnlazadaSimple<>();
        colaOrdinarias      = new Cola<>();
        colaCriticas        = new Cola<>();
        pilaKitsDisponibles = new Pila<>();
        pilaKitsRevision    = new Pila<>();
        pilaMovimientos     = new Pila<>();

        SwingUtilities.invokeLater(() -> {
            co.udistrital.vista.VistaPrincipal vista = new co.udistrital.vista.VistaPrincipal();
            controlVista = new ControlVista(this, vista);
            vista.setControlVista(controlVista);
            vista.mostrar();
        });
    }

    // =========================================================================
    // TÉCNICOS
    // =========================================================================

    /** Registra un nuevo técnico. */
    public Tecnico registrarTecnico(String nombre, Tecnico.Especialidad especialidad) {
        Tecnico t = new Tecnico(String.valueOf(tecnicos.getTamanno() + 1), nombre, especialidad);
        tecnicos.agregar(t);
        intentarAtenderAutomaticamente();
        return t;
    }

    /** Busca técnico por id. */
    public Tecnico buscarTecnico(String id) {
        ListaEnlazadaSimple.Iterador<Tecnico> it = tecnicos.iterador();
        while (it.tieneSiguiente()) {
            Tecnico t = it.siguiente();
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    /**
     * Elimina un técnico por id.
     * Solo se permite si existe y está disponible.
     * @param id Id del técnico.
     * @return true si fue eliminado.
     */
    public boolean eliminarTecnico(String id) {
        Tecnico t = buscarTecnico(id);
        if (t == null) return false;
        if (t.getEstado() == EstadoTecnico.OCUPADO) return false;
        return tecnicos.eliminar(t);
    }

    /**
     * Busca el primer técnico disponible cuya especialidad coincida con la requerida.
     * @param especialidad Especialidad requerida.
     * @return El técnico disponible, o null si no hay ninguno.
     */
    public Tecnico buscarTecnicoDisponiblePorEspecialidad(Tecnico.Especialidad especialidad) {
        ListaEnlazadaSimple.Iterador<Tecnico> it = tecnicos.iterador();
        while (it.tieneSiguiente()) {
            Tecnico t = it.siguiente();
            if (t.isDisponible() && t.getEspecialidad() == especialidad) return t;
        }
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
        if (estado == EstadoTecnico.DISPONIBLE) intentarAtenderAutomaticamente();
        return true;
    }

    // =========================================================================
    // UNIDADES
    // =========================================================================

    /** Registra una nueva unidad. */
    public UnidadServicio registrarUnidad(UnidadServicio.TipoUnidad tipo, String zona) {
        UnidadServicio u = new UnidadServicio(String.valueOf(unidades.getTamanno() + 1), tipo, zona);
        unidades.agregar(u);
        return u;
    }

    /** Busca unidad por id. */
    public UnidadServicio buscarUnidad(String id) {
        ListaEnlazadaSimple.Iterador<UnidadServicio> it = unidades.iterador();
        while (it.tieneSiguiente()) {
            UnidadServicio u = it.siguiente();
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    /** Busca la primera unidad disponible. */
    public UnidadServicio buscarUnidadDisponible() {
        ListaEnlazadaSimple.Iterador<UnidadServicio> it = unidades.iterador();
        while (it.tieneSiguiente()) {
            UnidadServicio u = it.siguiente();
            if (u.isDisponible()) return u;
        }
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

    /**
     * Elimina una unidad por id.
     * Solo se permite si existe y no está ocupada.
     * @param id Id de la unidad.
     * @return true si fue eliminada.
     */
    public boolean eliminarUnidad(String id) {
        UnidadServicio u = buscarUnidad(id);
        if (u == null) return false;
        if (u.getEstado() == EstadoUnidad.OCUPADO) return false;
        return unidades.eliminar(u);
    }

    // =========================================================================
    // CLIENTES
    // =========================================================================

    /** Registra un nuevo cliente. */
    public Cliente registrarCliente(String nombre, String telefono, Cliente.TipoCliente tipo) {
        Cliente c = new Cliente(String.valueOf(clientes.getTamanno() + 1), nombre, telefono, tipo);
        clientes.agregar(c);
        return c;
    }

    /** Busca cliente por id. */
    public Cliente buscarCliente(String id) {
        ListaEnlazadaSimple.Iterador<Cliente> it = clientes.iterador();
        while (it.tieneSiguiente()) {
            Cliente c = it.siguiente();
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    /** @return Lista de clientes. */
    public ListaEnlazadaSimple<Cliente> getClientes() { return clientes; }

    /**
     * Elimina un cliente por id.
     * Solo se permite si no tiene solicitudes registradas.
     * @param id Id del cliente.
     * @return true si fue eliminado.
     */
    public boolean eliminarCliente(String id) {
        Cliente c = buscarCliente(id);
        if (c == null) return false;
        ListaEnlazadaSimple.Iterador<SolicitudServicio> it = todasLasSolicitudes.iterador();
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            if (s.getCliente() != null && s.getCliente().getId().equals(id)) return false;
        }
        return clientes.eliminar(c);
    }

    // =========================================================================
    // SOLICITUDES
    // =========================================================================

    /**
     * Registra una nueva solicitud. La prioridad se calcula automáticamente:
     * puntos = cliente.tipo.puntos + zona.puntos + tipoServicio.puntos
     * Si puntos >= 3 → CRITICA, si no → ORDINARIA.
     *
     * @return La solicitud creada, o null si el cliente no existe.
     */
    public SolicitudServicio registrarSolicitud(String clienteId,
                                                Tecnico.TipoServicio tipoServicio,
                                                SolicitudServicio.Zona zona) {
        Cliente cliente = buscarCliente(clienteId);
        if (cliente == null) return null;
        SolicitudServicio sol = new SolicitudServicio(
                String.valueOf(todasLasSolicitudes.getTamanno() + 1),
                cliente, tipoServicio, zona);
        todasLasSolicitudes.agregar(sol);
        if (sol.getPrioridad() == SolicitudServicio.Prioridad.CRITICA) colaCriticas.encolar(sol);
        else colaOrdinarias.encolar(sol);
        intentarAtenderAutomaticamente();
        return sol;
    }

    /** Busca solicitud por id. */
    public SolicitudServicio buscarSolicitud(String id) {
        ListaEnlazadaSimple.Iterador<SolicitudServicio> it = todasLasSolicitudes.iterador();
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    /** @return Lista completa de solicitudes. */
    public ListaEnlazadaSimple<SolicitudServicio> getTodasLasSolicitudes() { return todasLasSolicitudes; }

    /**
     * Elimina una solicitud por id.
     * Solo se permite si está en estado PENDIENTE.
     * @param id Id de la solicitud.
     * @return true si fue eliminada.
     */
    public boolean eliminarSolicitud(String id) {
        SolicitudServicio s = buscarSolicitud(id);
        if (s == null) return false;
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) return false;
        boolean eliminada = todasLasSolicitudes.eliminar(s);
        if (eliminada) quitarSolicitudDeColas(s);
        return eliminada;
    }

    /**
     * Obtiene la siguiente solicitud a atender SIN desencolarla.
     * @return La solicitud en el frente, o null si no hay.
     */
    public SolicitudServicio verSiguienteSolicitud() {
        if (!colaCriticas.estaVacia())   return colaCriticas.verFrente();
        if (!colaOrdinarias.estaVacia()) return colaOrdinarias.verFrente();
        return null;
    }

    /**
     * Evalúa si una solicitud puede ser atendida ahora mismo
     * según técnico disponible por especialidad, unidad y kit.
     */
    private boolean esAtendibleAhora(SolicitudServicio sol) {
        if (sol == null) return false;
        if (buscarTecnicoDisponiblePorEspecialidad(sol.getTipoServicio().getEspecialidadRequerida()) == null) return false;
        if (buscarUnidadDisponible() == null) return false;
        return !pilaKitsDisponibles.estaVacia();
    }

    /**
     * Desencola y retorna la primera solicitud atendible de la cola dada.
     * Si no hay atendibles, deja la cola exactamente en el mismo orden.
     */
    private SolicitudServicio desencolarPrimeraAtendible(Cola<SolicitudServicio> cola) {
        Cola<SolicitudServicio> temporal = new Cola<>();
        SolicitudServicio seleccionada = null;

        while (!cola.estaVacia()) {
            SolicitudServicio actual = cola.desencolar();
            if (seleccionada == null && esAtendibleAhora(actual)) {
                seleccionada = actual;
            } else {
                temporal.encolar(actual);
            }
        }
        while (!temporal.estaVacia()) {
            cola.encolar(temporal.desencolar());
        }
        return seleccionada;
    }

    /**
     * Reconstruye las colas excluyendo la solicitud indicada.
     */
    private void quitarSolicitudDeColas(SolicitudServicio solObjetivo) {
        Cola<SolicitudServicio> nuevasCriticas = new Cola<>();
        while (!colaCriticas.estaVacia()) {
            SolicitudServicio actual = colaCriticas.desencolar();
            if (actual != solObjetivo) nuevasCriticas.encolar(actual);
        }
        colaCriticas = nuevasCriticas;

        Cola<SolicitudServicio> nuevasOrdinarias = new Cola<>();
        while (!colaOrdinarias.estaVacia()) {
            SolicitudServicio actual = colaOrdinarias.desencolar();
            if (actual != solObjetivo) nuevasOrdinarias.encolar(actual);
        }
        colaOrdinarias = nuevasOrdinarias;
    }

    /**
     * Asigna automáticamente técnico (por especialidad) y unidad disponible
     * a la siguiente solicitud en cola. Inicia el timer de servicio automáticamente.
     * Opcionalmente asigna un kit de la pila.
     *
     * @param usarKit true si se debe asignar un kit de atención rápida.
     * @return Mensaje de resultado.
     */
    public String atenderSiguienteAutomatico() {
        if (pilaKitsDisponibles.estaVacia())
            return "Error: No hay kits disponibles. Agregue kits antes de atender.";
        if (buscarUnidadDisponible() == null)
            return "Error: No hay unidades de servicio disponibles.";

        SolicitudServicio sol = desencolarPrimeraAtendible(colaCriticas);
        if (sol == null) sol = desencolarPrimeraAtendible(colaOrdinarias);
        if (sol == null)
            return "Error: No hay solicitudes atendibles en este momento (faltan técnicos disponibles por especialidad).";

        Tecnico.TipoServicio tipo = sol.getTipoServicio();

        // Buscar técnico disponible con la especialidad requerida
        Tecnico tec = buscarTecnicoDisponiblePorEspecialidad(tipo.getEspecialidadRequerida());
        if (tec == null)
            return "Error: No hay técnico disponible con especialidad '"
                    + tipo.getEspecialidadRequerida().getNombre() + "' para este servicio.";

        // Buscar unidad disponible
        UnidadServicio uni = buscarUnidadDisponible();
        if (uni == null) return "Error: No hay unidades de servicio disponibles.";

        // Kit OBLIGATORIO — no se puede atender sin kit disponible
        if (pilaKitsDisponibles.estaVacia())
            return "Error: No hay kits disponibles. Agregue kits antes de atender.";
        Kit kit = pilaKitsDisponibles.pop();
        kit.setEstado(EstadoKit.EN_USO);

        // Guardar estados para Undo
        EstadoTecnico estadoAntTec = tec.getEstado();
        EstadoUnidad estadoAntUni  = uni.getEstado();
        EstadoSolicitud estadoAntSol = sol.getEstado();

        // Calcular duración aleatoria según el rango del TÉCNICO asignado
        long durMin = tec.getEspecialidad().getDuracionMinMs();
        long durMax = tec.getEspecialidad().getDuracionMaxMs();
        long duracionMs = durMin + (long)(random.nextDouble() * (durMax - durMin));

        // Asignar recursos
        tec.setEstado(EstadoTecnico.OCUPADO);
        uni.setEstado(EstadoUnidad.OCUPADO);
        sol.setTecnicoAsignado(tec);
        sol.setUnidadAsignada(uni);
        sol.setKitAsignado(kit);
        sol.setDuracionMs(duracionMs);
        sol.setEstado(EstadoSolicitud.EN_PROCESO);

        // Registrar movimiento
        Movimiento mov = new Movimiento(Movimiento.TipoOperacion.ASIGNAR_RECURSOS,
                "Asignación: Sol." + sol.getId() + " -> " + tec.getNombre()
                        + " + Unidad [" + uni.getId() + "]"
                        + (kit != null ? " + " + kit : ""));
        mov.setSolicitud(sol); mov.setTecnico(tec); mov.setUnidad(uni); mov.setKit(kit);
        mov.setEstadoAnteriorTecnico(estadoAntTec);
        mov.setEstadoAnteriorUnidad(estadoAntUni);
        mov.setEstadoAnteriorSolicitud(estadoAntSol);
        pilaMovimientos.push(mov);

        // Iniciar timer para completar el servicio automáticamente
        iniciarTimerServicio(sol, duracionMs);

        long minSimulados = duracionMs / 1000L * 10L;
        return "Servicio asignado:\n  Técnico: " + tec.getNombre()
                + " (" + tec.getEspecialidad().getDuracionMinMin()
                + "-" + tec.getEspecialidad().getDuracionMaxMin() + " min)"
                + "\n  Unidad: [" + uni.getId() + "] " + uni
                + "\n  Duración estimada: ~" + minSimulados + " min"
                + (kit != null ? "\n  Kit: " + kit : "");
    }

    /**
     * Inicia un timer de Swing que completa el servicio automáticamente
     * tras la duración indicada.
     */
    private void iniciarTimerServicio(SolicitudServicio sol, long duracionMs) {
        Timer timer = new Timer((int) duracionMs, e -> {
            completarServicioAutomatico(sol);
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Completa automáticamente un servicio al vencer su timer.
     * Libera técnico y unidad, envía el kit a revisión con decisión aleatoria.
     */
    private void completarServicioAutomatico(SolicitudServicio sol) {
        if (sol.getEstado() != EstadoSolicitud.EN_PROCESO) return;

        sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
        sol.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
        sol.setEstado(EstadoSolicitud.COMPLETADA);

        if (sol.getKitAsignado() != null) {
            Kit kit = sol.getKitAsignado();
            kit.asignarDecisionAleatoria();
            kit.setEstado(EstadoKit.EN_REVISION);
            pilaKitsRevision.push(kit);
            iniciarProcesadoAutomaticoKit(kit);
        }

        Movimiento mov = new Movimiento(Movimiento.TipoOperacion.COMPLETAR_SERVICIO,
                "Servicio completado automáticamente: Solicitud " + sol.getId());
        mov.setSolicitud(sol);
        pilaMovimientos.push(mov);

        SwingUtilities.invokeLater(() -> {
            controlVista.notificarActualizacion(
                "Servicio " + sol.getId() + " completado. Técnico "
                + sol.getTecnicoAsignado().getNombre() + " disponible.");
            intentarAtenderAutomaticamente();
        });
    }

    /**
     * Asigna automáticamente técnico y unidad a la siguiente solicitud pendiente en cola.
     */
    private void intentarAtenderAutomaticamente() {
        if (verSiguienteSolicitud() == null) return;
        String r = atenderSiguienteAutomatico();
        if (r.startsWith("Servicio asignado")) {
            if (controlVista != null) controlVista.notificarActualizacion(r);
        } else if (r.startsWith("Error:")) {
            if (controlVista != null) controlVista.notificarActualizacion("Solicitud en cola. " + r.substring(7));
        }
    }

    /**
     * Inicia el procesado automático de un kit en revisión.
     * El operario tarda 2 segundos reales (20 min simulados).
     */
    private void iniciarProcesadoAutomaticoKit(Kit kit) {
        Timer timer = new Timer(2000, e -> procesarKitAutomatico(kit));
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Procesa automáticamente un kit según su decisión de revisión.
     */
    private void procesarKitAutomatico(Kit kit) {
        quitarKitDeRevision(kit);
        DecisionRevision dec = kit.getDecision();
        String msg;
        if (dec == DecisionRevision.REPONER) {
            Kit nuevo = agregarKit();
            msg = "Operario: Kit-" + kit.getId() + " repuesto → nuevo " + nuevo;
        } else {
            kit.setEstado(EstadoKit.LISTO);
            kit.setDecision(null);
            pilaKitsDisponibles.push(kit);
            msg = "Operario: Kit-" + kit.getId() + " (" + dec + ") → devuelto a disponibles.";
        }
        final String finalMsg = msg;
        SwingUtilities.invokeLater(() -> {
            controlVista.notificarActualizacion(finalMsg);
            intentarAtenderAutomaticamente();
        });
    }

    /**
     * Retira un kit específico de la pila de revisión preservando
     * el orden relativo del resto de elementos.
     */
    private void quitarKitDeRevision(Kit kitObjetivo) {
        if (kitObjetivo == null || pilaKitsRevision.estaVacia()) return;
        Pila<Kit> temporal = new Pila<>();
        boolean removido = false;

        while (!pilaKitsRevision.estaVacia()) {
            Kit actual = pilaKitsRevision.pop();
            if (!removido && actual == kitObjetivo) {
                removido = true;
            } else {
                temporal.push(actual);
            }
        }
        while (!temporal.estaVacia()) {
            pilaKitsRevision.push(temporal.pop());
        }
    }

    // =========================================================================
    // KITS
    // =========================================================================

    /** Agrega un nuevo kit a la pila de disponibles. */
    public Kit agregarKit() {
        Kit kit = new Kit(String.valueOf(
                pilaKitsDisponibles.getTamanno() + pilaKitsRevision.getTamanno() + 1));
        pilaKitsDisponibles.push(kit);
        intentarAtenderAutomaticamente();
        return kit;
    }

    /**
     * Completa manualmente un servicio EN_PROCESO.
     * @param solicitudId Id de la solicitud.
     * @return Mensaje de resultado.
     */
    public String completarServicio(String solicitudId) {
        SolicitudServicio sol = buscarSolicitud(solicitudId);
        if (sol == null) return "Error: Solicitud no encontrada.";
        if (sol.getEstado() != SolicitudServicio.EstadoSolicitud.EN_PROCESO)
            return "Error: La solicitud no está en proceso.";
        completarServicioAutomatico(sol);
        return "Servicio " + solicitudId + " completado.";
    }

    /**
     * Revisa manualmente el kit en la cima de la pila de revisión.
     * @param decision REPONER, REPARAR o NADA.
     * @return Mensaje de resultado.
     */
    public String revisarKitEnCima(String decision) {
        if (pilaKitsRevision.estaVacia()) return "No hay kits en revisión.";
        Kit kit = pilaKitsRevision.pop();
        if ("REPONER".equalsIgnoreCase(decision)) {
            Kit nuevo = agregarKit();
            return "Kit-" + kit.getId() + " repuesto. Nuevo kit: " + nuevo;
        }
        kit.setEstado(EstadoKit.LISTO);
        kit.setDecision(null);
        pilaKitsDisponibles.push(kit);
        intentarAtenderAutomaticamente();
        return "Kit-" + kit.getId() + " devuelto a disponibles.";
    }

    /** @return true si no hay kits disponibles. */
    public boolean pilaKitsDisponiblesVacia() { return pilaKitsDisponibles.estaVacia(); }

    /** @return La pila de kits disponibles para iteración. */
    public Pila<Kit> getPilaKitsDisponibles() { return pilaKitsDisponibles; }

    /** @return true si no hay kits en revisión. */
    public boolean pilaKitsRevisionVacia() { return pilaKitsRevision.estaVacia(); }

    /** @return La pila de kits en revisión para iteración. */
    public Pila<Kit> getPilaKitsRevision() { return pilaKitsRevision; }

    // =========================================================================
    // UNDO
    // =========================================================================

    /** Deshace la última operación registrada. */
    public String deshacerUltimaOperacion() {
        if (pilaMovimientos.estaVacia()) return "No hay operaciones para deshacer.";
        Movimiento mov = pilaMovimientos.pop();
        switch (mov.getTipoOperacion()) {
            case ASIGNAR_RECURSOS:
                if (mov.getTecnico() != null) mov.getTecnico().setEstado(mov.getEstadoAnteriorTecnico());
                if (mov.getUnidad() != null)  mov.getUnidad().setEstado(mov.getEstadoAnteriorUnidad());
                if (mov.getSolicitud() != null) {
                    mov.getSolicitud().setEstado(mov.getEstadoAnteriorSolicitud());
                    mov.getSolicitud().setTecnicoAsignado(null);
                    mov.getSolicitud().setUnidadAsignada(null);
                    if (mov.getKit() != null) {
                        mov.getKit().setEstado(EstadoKit.LISTO);
                        pilaKitsDisponibles.push(mov.getKit());
                        mov.getSolicitud().setKitAsignado(null);
                    }
                    if (mov.getSolicitud().getPrioridad() == Prioridad.CRITICA)
                        colaCriticas.encolar(mov.getSolicitud());
                    else colaOrdinarias.encolar(mov.getSolicitud());
                }
                break;
            case COMPLETAR_SERVICIO:
                if (mov.getTecnico() != null) mov.getTecnico().setEstado(mov.getEstadoAnteriorTecnico());
                if (mov.getUnidad() != null)  mov.getUnidad().setEstado(mov.getEstadoAnteriorUnidad());
                if (mov.getSolicitud() != null) mov.getSolicitud().setEstado(mov.getEstadoAnteriorSolicitud());
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

    /** @return Descripción del último movimiento. */
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
            if (s.getEstado() == EstadoSolicitud.EN_PROCESO) sb.append("  ").append(s)
                .append(" (~").append(s.getDuracionMs() / 1000L * 10L).append(" min)\n"); }

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
            fw.write("id,cliente,tipoServicio,prioridad,estado\n");
            int count = 0;
            ListaEnlazadaSimple.Iterador<SolicitudServicio> it = todasLasSolicitudes.iterador();
            while (it.tieneSiguiente()) {
                SolicitudServicio s = it.siguiente();
                if (s.getEstado() == EstadoSolicitud.COMPLETADA) {
                    fw.write(s.getId() + "," + s.getCliente().getNombre().replace(",", ";")
                            + "," + s.getTipoServicio().getNombre().replace(",", ";")
                            + "," + s.getPrioridad() + "," + s.getEstado() + "\n");
                    count++;
                }
            }
            return "CSV exportado: servicios_atendidos.csv (" + count + " registros).";
        } catch (IOException e) { return "Error al exportar CSV: " + e.getMessage(); }
    }

    /** Importa datos de prueba desde CSV. */
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
                        case "CLIENTE":  if (p.length >= 4) { registrarCliente(p[1].trim(), p[2].trim(), Cliente.TipoCliente.valueOf(p[3].trim().toUpperCase())); cli++; } else if (p.length >= 3) { registrarCliente(p[1].trim(), p[2].trim(), Cliente.TipoCliente.ORDINARIO); cli++; } else err++; break;
                        case "TECNICO":  if (p.length >= 3) { registrarTecnico(p[1].trim(), Tecnico.Especialidad.valueOf(p[2].trim().toUpperCase().replace(" ", "_"))); tec++; } else err++; break;
                        case "UNIDAD":   if (p.length >= 3) { registrarUnidad(UnidadServicio.TipoUnidad.valueOf(p[1].trim().toUpperCase()), p[2].trim()); uni++; } else err++; break;
                        case "KIT":      agregarKit(); kit++; break;
                        case "SOLICITUD":
                            if (p.length >= 4) {
                                Tecnico.TipoServicio ts = Tecnico.TipoServicio.valueOf(p[2].trim().toUpperCase());
                                SolicitudServicio.Zona z = p.length >= 5
                                        ? SolicitudServicio.Zona.valueOf(p[4].trim().toUpperCase())
                                        : SolicitudServicio.Zona.PARQUEADERO;
                                SolicitudServicio s = registrarSolicitud(p[1].trim(), ts, z);
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
