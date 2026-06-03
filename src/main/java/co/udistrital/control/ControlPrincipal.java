package co.udistrital.control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.SwingUtilities;

import co.udistrital.modelo.entidades.Cliente;
import co.udistrital.modelo.entidades.Kit;
import co.udistrital.modelo.entidades.Kit.DecisionRevision;
import co.udistrital.modelo.entidades.Kit.EstadoKit;
import co.udistrital.modelo.entidades.Movimiento;
import co.udistrital.modelo.entidades.SolicitudServicio;
import co.udistrital.modelo.entidades.SolicitudServicio.EstadoSolicitud;
import co.udistrital.modelo.entidades.SolicitudServicio.Prioridad;
import co.udistrital.modelo.entidades.Tecnico;
import co.udistrital.modelo.entidades.Tecnico.EstadoTecnico;
import co.udistrital.modelo.entidades.UnidadServicio;
import co.udistrital.modelo.entidades.UnidadServicio.EstadoUnidad;
import co.udistrital.modelo.estructuras.Cola;
import co.udistrital.modelo.estructuras.ListaEnlazadaSimple;
import co.udistrital.modelo.estructuras.Pila;

/**
 * Controlador principal de la lógica de negocio de AutoRescate 24/7. Gestiona
 * técnicos, unidades, clientes, solicitudes, kits y movimientos. No usa ninguna
 * clase del Collections Framework de Java.
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

    /**
     * Construye el controlador principal, inicializa estructuras y arranca la
     * GUI.
     */
    public ControlPrincipal() {
        tecnicos = new ListaEnlazadaSimple<>();
        unidades = new ListaEnlazadaSimple<>();
        clientes = new ListaEnlazadaSimple<>();
        todasLasSolicitudes = new ListaEnlazadaSimple<>();
        colaOrdinarias = new Cola<>();
        colaCriticas = new Cola<>();
        pilaKitsDisponibles = new Pila<>();
        pilaKitsRevision = new Pila<>();
        pilaMovimientos = new Pila<>();

        SwingUtilities.invokeLater(() -> {
            co.udistrital.vista.VistaPrincipal vista = new co.udistrital.vista.VistaPrincipal();
            ControlVista cv = new ControlVista(this, vista);
            vista.setControlVista(cv);
            vista.mostrar();
        });
    }

    // =========================================================================
    // TÉCNICOS
    // =========================================================================
    /**
     * Registra un nuevo técnico.
     */
    public Tecnico registrarTecnico(String nombre, Tecnico.Especialidad especialidad) {
        Tecnico t = new Tecnico(String.valueOf(tecnicos.getTamanno() + 1), nombre, especialidad);
        tecnicos.agregar(t);
        return t;
    }

    /**
     * Busca técnico por id.
     */
    public Tecnico buscarTecnico(String id) {
        ListaEnlazadaSimple.Iterador<Tecnico> it = tecnicos.iterador();
        while (it.tieneSiguiente()) {
            Tecnico t = it.siguiente();
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Elimina un técnico por id. Solo se permite si existe y está disponible.
     *
     * @param id Id del técnico.
     * @return true si fue eliminado.
     */
    public boolean eliminarTecnico(String id) {
        Tecnico t = buscarTecnico(id);
        if (t == null) {
            return false;
        } else if (t.getEstado() == EstadoTecnico.OCUPADO) {
            return false;
        } else {
            return tecnicos.eliminar(t);
        }
    }

    /**
     * Busca el primer técnico disponible cuya especialidad coincida con la
     * requerida.
     *
     * @param especialidad Especialidad requerida.
     * @return El técnico disponible, o null si no hay ninguno.
     */
    private Tecnico buscarTecnicoDisponiblePorEspecialidad(Tecnico.Especialidad especialidad) {
        ListaEnlazadaSimple.Iterador<Tecnico> it = tecnicos.iterador();
        while (it.tieneSiguiente()) {
            Tecnico t = it.siguiente();
            if (t.isDisponible() && t.getEspecialidad() == especialidad) {
                return t;
            }
        }
        return null;
    }

    /**
     * @return Lista de técnicos.
     */
    public ListaEnlazadaSimple<Tecnico> getTecnicos() {
        return tecnicos;
    }

    /**
     * Cambia estado de un técnico y registra movimiento.
     */
    public boolean cambiarEstadoTecnico(String id, EstadoTecnico estado) {
        Tecnico t = buscarTecnico(id);
        if (t == null) {
            return false;
        }
        EstadoTecnico anterior = t.getEstado();
        t.setEstado(estado);
        Movimiento mov = new Movimiento(Movimiento.TipoOperacion.CAMBIAR_ESTADO_TECNICO,
                "Estado técnico " + t.getNombre() + ": " + anterior + " -> " + estado);
        mov.setTecnico(t);
        mov.setEstadoAnteriorTecnico(anterior);
        pilaMovimientos.push(mov);
        return true;
    }

    // =========================================================================
    // UNIDADES
    // =========================================================================
    /**
     * Registra una nueva unidad.
     */
    public UnidadServicio registrarUnidad(UnidadServicio.TipoUnidad tipo, String zona) {
        UnidadServicio u = new UnidadServicio(String.valueOf(unidades.getTamanno() + 1), tipo, zona);
        unidades.agregar(u);
        return u;
    }

    /**
     * Busca unidad por id.
     */
    public UnidadServicio buscarUnidad(String id) {
        ListaEnlazadaSimple.Iterador<UnidadServicio> it = unidades.iterador();
        while (it.tieneSiguiente()) {
            UnidadServicio u = it.siguiente();
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Busca la primera unidad disponible.
     */
    public UnidadServicio buscarUnidadDisponible() {
        ListaEnlazadaSimple.Iterador<UnidadServicio> it = unidades.iterador();
        while (it.tieneSiguiente()) {
            UnidadServicio u = it.siguiente();
            if (u.isDisponible()) {
                return u;
            }
        }
        return null;
    }

    /**
     * @return Lista de unidades.
     */
    public ListaEnlazadaSimple<UnidadServicio> getUnidades() {
        return unidades;
    }

    /**
     * Cambia estado de una unidad y registra movimiento.
     */
    public boolean cambiarEstadoUnidad(String id, EstadoUnidad estado) {
        UnidadServicio u = buscarUnidad(id);
        if (u == null) {
            return false;
        }
        EstadoUnidad anterior = u.getEstado();
        u.setEstado(estado);
        Movimiento mov = new Movimiento(Movimiento.TipoOperacion.CAMBIAR_ESTADO_UNIDAD,
                "Estado unidad [" + id + "]: " + anterior + " -> " + estado);
        mov.setUnidad(u);
        mov.setEstadoAnteriorUnidad(anterior);
        pilaMovimientos.push(mov);
        return true;
    }

    /**
     * Elimina una unidad por id. Solo se permite si existe y no está ocupada.
     *
     * @param id Id de la unidad.
     * @return true si fue eliminada.
     */
    public boolean eliminarUnidad(String id) {
        UnidadServicio u = buscarUnidad(id);
        if (u == null) {
            return false;
        } else if (u.getEstado() == EstadoUnidad.OCUPADO) {
            return false;
        } else {
            return unidades.eliminar(u);
        }
    }

    // =========================================================================
    // CLIENTES
    // =========================================================================
    /**
     * Registra un nuevo cliente.
     */
    public Cliente registrarCliente(String nombre, String telefono, Cliente.TipoCliente tipo) {
        Cliente c = new Cliente(String.valueOf(clientes.getTamanno() + 1), nombre, telefono, tipo);
        clientes.agregar(c);
        return c;
    }

    /**
     * Busca cliente por id.
     */
    public Cliente buscarCliente(String id) {
        ListaEnlazadaSimple.Iterador<Cliente> it = clientes.iterador();
        while (it.tieneSiguiente()) {
            Cliente c = it.siguiente();
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    /**
     * @return Lista de clientes.
     */
    public ListaEnlazadaSimple<Cliente> getClientes() {
        return clientes;
    }

    /**
     * Elimina un cliente por id. Solo se permite si no tiene solicitudes
     * registradas.
     *
     * @param id Id del cliente.
     * @return true si fue eliminado.
     */
    public boolean eliminarCliente(String id) {
        Cliente c = buscarCliente(id);
        if (c == null) {
            return false;
        } else {
            ListaEnlazadaSimple.Iterador<SolicitudServicio> it = todasLasSolicitudes.iterador();
            while (it.tieneSiguiente()) {
                SolicitudServicio s = it.siguiente();
                if (s.getCliente() != null && s.getCliente().getId().equals(id)) {
                    return false;
                }
            }
            return clientes.eliminar(c);
        }
    }

    // =========================================================================
    // SOLICITUDES
    // =========================================================================
    /**
     * Registra una nueva solicitud. La prioridad se calcula automáticamente:
     * puntos = cliente.tipo.puntos + zona.puntos + tipoServicio.puntos Si
     * puntos >= 3 → CRITICA, si no → ORDINARIA.
     *
     * @return La solicitud creada, o null si el cliente no existe.
     */
    public SolicitudServicio registrarSolicitud(String clienteId,
            Tecnico.TipoServicio tipoServicio,
            SolicitudServicio.Zona zona) {
        Cliente cliente = buscarCliente(clienteId);
        if (cliente == null) {
            return null;
        } else {
            SolicitudServicio sol = new SolicitudServicio(
                    String.valueOf(todasLasSolicitudes.getTamanno() + 1),
                    cliente, tipoServicio, zona);
            todasLasSolicitudes.agregar(sol);
            if (sol.getPrioridad() == SolicitudServicio.Prioridad.CRITICA) {
                colaCriticas.encolar(sol);
            } else {
                colaOrdinarias.encolar(sol);
            }
            return sol;
        }
    }

    /**
     * Busca solicitud por id.
     */
    public SolicitudServicio buscarSolicitud(String id) {
        ListaEnlazadaSimple.Iterador<SolicitudServicio> it = todasLasSolicitudes.iterador();
        while (it.tieneSiguiente()) {
            SolicitudServicio s = it.siguiente();
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return Lista completa de solicitudes.
     */
    public ListaEnlazadaSimple<SolicitudServicio> getTodasLasSolicitudes() {
        return todasLasSolicitudes;
    }

    /**
     * Elimina una solicitud por id. Solo se permite si está en estado
     * PENDIENTE.
     *
     * @param id Id de la solicitud.
     * @return true si fue eliminada.
     */
    public boolean eliminarSolicitud(String id) {
        SolicitudServicio s = buscarSolicitud(id);
        if (s == null) {
            return false;
        } else if (s.getEstado() != EstadoSolicitud.PENDIENTE) {
            return false;
        } else {
            boolean eliminada = todasLasSolicitudes.eliminar(s);
            if (eliminada) {
                quitarSolicitudDeColas(s);
            }
            return eliminada;
        }
    }

    /**
     * Evalúa si una solicitud puede ser atendida ahora mismo según técnico
     * disponible por especialidad, unidad y kit.
     */
    private boolean esAtendibleAhora(SolicitudServicio sol) {
        if (sol == null) {
            return false;
        } else if (buscarTecnicoDisponiblePorEspecialidad(sol.getTipoServicio().getEspecialidadRequerida()) == null) {
            return false;
        } else if (buscarUnidadDisponible() == null) {
            return false;
        } else {
            return !pilaKitsDisponibles.estaVacia();
        }
    }

    /**
     * Retira el kit de las pilas y lo deja en estado EN_USO (asignado a una
     * solicitud).
     */
    private void dejarKitEnUsoFueraDePilas(Kit kit) {
        if (kit == null) {
            return;
        }
        quitarKitDeRevision(kit);
        quitarKitDeDisponibles(kit);
        kit.setEstado(EstadoKit.EN_USO);
        kit.setDecision(null);
    }

    /**
     * Desencola y retorna la primera solicitud atendible de la cola dada. Si no
     * hay atendibles, deja la cola exactamente en el mismo orden.
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
            if (actual != solObjetivo) {
                nuevasCriticas.encolar(actual);
            }
        }
        colaCriticas = nuevasCriticas;

        Cola<SolicitudServicio> nuevasOrdinarias = new Cola<>();
        while (!colaOrdinarias.estaVacia()) {
            SolicitudServicio actual = colaOrdinarias.desencolar();
            if (actual != solObjetivo) {
                nuevasOrdinarias.encolar(actual);
            }
        }
        colaOrdinarias = nuevasOrdinarias;
    }

    /**
     * Asigna técnico (por especialidad), unidad y kit a la siguiente solicitud
     * atendible en cola (manual).
     *
     * @return Mensaje de resultado.
     */
    public String atenderSiguienteSolicitud() {
        if (buscarUnidadDisponible() == null) {
            return "Error: No hay unidades de servicio disponibles.";
        } else {
            SolicitudServicio sol = desencolarPrimeraAtendible(colaCriticas);
            if (sol == null) {
                sol = desencolarPrimeraAtendible(colaOrdinarias);
            }
            if (sol == null) {
                return "Error: No hay solicitudes atendibles en este momento (faltan técnicos, unidades o kits).";
            } else {
                Tecnico.TipoServicio tipo = sol.getTipoServicio();

                // Buscar técnico disponible con la especialidad requerida
                Tecnico tec = buscarTecnicoDisponiblePorEspecialidad(tipo.getEspecialidadRequerida());
                if (tec == null) {
                    return "Error: No hay técnico disponible con especialidad '"
                            + tipo.getEspecialidadRequerida().getNombre() + "' para este servicio.";
                } else {
                    // Buscar unidad disponible
                    UnidadServicio uni = buscarUnidadDisponible();
                    if (uni == null) {
                        return "Error: No hay unidades de servicio disponibles.";
                    } else {
                        if (pilaKitsDisponibles.estaVacia()) {
                            return "Error: No hay kits disponibles. Agregue kits antes de atender.";
                        }
                        Kit kit = pilaKitsDisponibles.pop();
                        kit.setEstado(EstadoKit.EN_USO);
                        sol.setKitAsignado(kit);

                        // Guardar estados para Undo
                        EstadoTecnico estadoAntTec = tec.getEstado();
                        EstadoUnidad estadoAntUni = uni.getEstado();
                        EstadoSolicitud estadoAntSol = sol.getEstado();

                        // Asignar recursos
                        tec.setEstado(EstadoTecnico.OCUPADO);
                        uni.setEstado(EstadoUnidad.OCUPADO);
                        sol.setTecnicoAsignado(tec);
                        sol.setUnidadAsignada(uni);
                        sol.setKitAsignado(kit);
                        sol.setEstado(EstadoSolicitud.EN_PROCESO);

                // Registrar movimiento con descripción simple
                Movimiento mov = new Movimiento(Movimiento.TipoOperacion.ATENDER_SERVICIO,
                        "Servicio atendido: solicitud " + sol.getId());
                mov.setSolicitud(sol);
                mov.setTecnico(tec);
                mov.setUnidad(uni);
                mov.setKit(kit);
                mov.setEstadoAnteriorTecnico(estadoAntTec);
                mov.setEstadoAnteriorUnidad(estadoAntUni);
                mov.setEstadoAnteriorSolicitud(estadoAntSol);
                pilaMovimientos.push(mov);

                        return "Servicio asignado:\n  Técnico: " + tec.getNombre()
                                + " (" + tec.getEspecialidad().getNombre() + ")"
                                + "\n  Unidad: [" + uni.getId() + "] " + uni
                                + (kit != null ? "\n  Kit: " + kit : "");
                    }
                }
            }
        }
    }

    /**
     * Completa un servicio en proceso. Libera técnico y unidad, envía el kit a
     * revisión con decisión aleatoria.
     */
    private void ejecutarCompletarServicio(SolicitudServicio sol) {
        if (sol.getEstado() != EstadoSolicitud.EN_PROCESO) {
            return;
        } else {
            sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
            sol.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
            sol.setEstado(EstadoSolicitud.COMPLETADA);

            if (sol.getKitAsignado() != null) {
                Kit kit = sol.getKitAsignado();
                kit.asignarDecisionAleatoria();
                kit.setEstado(EstadoKit.EN_REVISION);
                pilaKitsRevision.push(kit);
            }

            Movimiento movCompletar = new Movimiento(Movimiento.TipoOperacion.COMPLETAR_SERVICIO,
                    "Servicio completado: solicitud " + sol.getId());
            movCompletar.setSolicitud(sol);
            movCompletar.setTecnico(sol.getTecnicoAsignado());
            movCompletar.setUnidad(sol.getUnidadAsignada());
            movCompletar.setKit(sol.getKitAsignado());
            movCompletar.setEstadoAnteriorTecnico(EstadoTecnico.OCUPADO);
            movCompletar.setEstadoAnteriorUnidad(EstadoUnidad.OCUPADO);
            movCompletar.setEstadoAnteriorSolicitud(EstadoSolicitud.EN_PROCESO);
            pilaMovimientos.push(movCompletar);
        }
    }

    /**
     * Procesa el kit en la cima de la pila de revisión según su decisión.
     *
     * @return Mensaje de resultado.
     */
    public String revisarKitEnRevision() {
        if (pilaKitsRevision.estaVacia()) {
            return "Error: No hay kits en revisión.";
        }
        Kit kit = pilaKitsRevision.cima();
        return aplicarRevisionKit(kit);
    }

    /**
     * Aplica la decisión de revisión de un kit y lo retira de la pila.
     */
    private String aplicarRevisionKit(Kit kit) {
        quitarKitDeRevision(kit);
        DecisionRevision dec = kit.getDecision();
        if (dec == DecisionRevision.REPONER) {
            Kit nuevo = agregarKit();
            return "Kit-" + kit.getId() + " repuesto → nuevo " + nuevo;
        }
        kit.setEstado(EstadoKit.LISTO);
        kit.setDecision(null);
        pilaKitsDisponibles.push(kit);
        return "Kit-" + kit.getId() + " (" + dec + ") → devuelto a disponibles.";

    }

    /**
     * Retira un kit específico de la pila de revisión preservando el orden
     * relativo del resto de elementos.
     */
    private void quitarKitDeRevision(Kit kitObjetivo) {
        quitarKitDePila(pilaKitsRevision, kitObjetivo);
    }

    /**
     * Retira un kit de la pila de disponibles si está presente.
     */
    private void quitarKitDeDisponibles(Kit kitObjetivo) {
        quitarKitDePila(pilaKitsDisponibles, kitObjetivo);
    }

    private void quitarKitDePila(Pila<Kit> pila, Kit kitObjetivo) {
        if (kitObjetivo == null || pila.estaVacia()) {
            return;
        }
        Pila<Kit> temporal = new Pila<>();
        boolean removido = false;
        while (!pila.estaVacia()) {
            Kit actual = pila.pop();
            if (!removido && actual == kitObjetivo) {
                removido = true;
            } else {
                temporal.push(actual);
            }
        }
        while (!temporal.estaVacia()) {
            pila.push(temporal.pop());
        }
    }

    /**
     * Encola una solicitud pendiente evitando duplicados en las colas.
     */
    private void encolarSolicitud(SolicitudServicio sol) {
        if (sol == null || sol.getEstado() != EstadoSolicitud.PENDIENTE) {
            return;
        }
        quitarSolicitudDeColas(sol);
        if (sol.getPrioridad() == Prioridad.CRITICA) {
            colaCriticas.encolar(sol);
        } else {
            colaOrdinarias.encolar(sol);
        }
    }

    /**
     * Devuelve un kit a la pila de disponibles en estado LISTO.
     */
    private void devolverKitADisponibles(Kit kit) {
        if (kit == null) {
            return;
        }
        quitarKitDeRevision(kit);
        quitarKitDeDisponibles(kit);
        kit.setEstado(EstadoKit.LISTO);
        kit.setDecision(null);
        pilaKitsDisponibles.push(kit);
    }

    // =========================================================================
    // KITS
    // =========================================================================
    /**
     * Agrega un nuevo kit a la pila de disponibles.
     */
    public Kit agregarKit() {
        Kit kit = new Kit(String.valueOf(
                pilaKitsDisponibles.getTamanno() + pilaKitsRevision.getTamanno() + 1));
        pilaKitsDisponibles.push(kit);
        return kit;
    }

    /**
     * Completa manualmente un servicio EN_PROCESO.
     *
     * @param solicitudId Id de la solicitud.
     * @return Mensaje de resultado.
     */
    public String completarServicio(String solicitudId) {
        SolicitudServicio sol = buscarSolicitud(solicitudId);
        if (sol == null) {
            return "Error: Solicitud no encontrada.";
        } else if (sol.getEstado() != SolicitudServicio.EstadoSolicitud.EN_PROCESO) {
            return "Error: La solicitud no está en proceso.";
        } else {
            ejecutarCompletarServicio(sol);
            StringBuilder sb = new StringBuilder("Servicio ").append(solicitudId).append(" completado.");
            Kit kit = sol.getKitAsignado();
            if (kit != null && kit.getEstado() == EstadoKit.EN_REVISION) {
                sb.append("\nKit-").append(kit.getId())
                        .append(" enviado a revisión (").append(kit.getDecision()).append(").");
            }
            return sb.toString();
        }
    }

    /**
     * @return true si no hay kits disponibles.
     */
    public boolean pilaKitsDisponiblesVacia() {
        return pilaKitsDisponibles.estaVacia();
    }

    /**
     * @return La pila de kits disponibles para iteración.
     */
    public Pila<Kit> getPilaKitsDisponibles() {
        return pilaKitsDisponibles;
    }

    /**
     * @return true si no hay kits en revisión.
     */
    public boolean pilaKitsRevisionVacia() {
        return pilaKitsRevision.estaVacia();
    }

    /**
     * @return La pila de kits en revisión para iteración.
     */
    public Pila<Kit> getPilaKitsRevision() {
        return pilaKitsRevision;
    }

    // =========================================================================
    // UNDO
    // =========================================================================
    /**
    *

    Deshace la
    última operación

    registrada (inverso exacto según el estado actual).
     */

    public String deshacerUltimaOperacion() {
        if (pilaMovimientos.estaVacia()) {
            return "No hay operaciones para deshacer.";
        }
        Movimiento mov = pilaMovimientos.pop();
        String detalle;
        switch (mov.getTipoOperacion()) {
            case ATENDER_SERVICIO:
                detalle = deshacerAtenderServicio(mov);
                break;
            case COMPLETAR_SERVICIO:
                detalle = deshacerCompletarServicio(mov);
                break;
            case CAMBIAR_ESTADO_TECNICO:
                if (mov.getTecnico() != null) {
                    mov.getTecnico().setEstado(mov.getEstadoAnteriorTecnico());
                }
                detalle = "Estado del técnico restaurado.";
                break;
            case CAMBIAR_ESTADO_UNIDAD:
                if (mov.getUnidad() != null) {
                    mov.getUnidad().setEstado(mov.getEstadoAnteriorUnidad());
                }
                detalle = "Estado de la unidad restaurado.";
                break;
            default:
                detalle = "Operación revertida.";
                break;
        }
        return "Operación deshecha: " + mov.getDescripcion() + "\n" + detalle;
    }

    /**
     * Deshace atender servicio (solicitud debe estar EN_PROCESO).
     */
    private String deshacerAtenderServicio(Movimiento mov) {
        SolicitudServicio sol = mov.getSolicitud();
        if (sol == null) {
            return "Sin solicitud asociada.";
        }
        if (sol.getEstado() == EstadoSolicitud.COMPLETADA) {
            pilaMovimientos.push(mov);
            return "Primero deshaga el movimiento de completar servicio (es el último en la pila).";
        }
        revertirAtencion(sol, mov);
        return "Solicitud " + sol.getId() + " devuelta a la cola como PENDIENTE.";
    }

    /**
     * Deshace completar servicio: vuelve a EN_PROCESO con recursos ocupados.
     */
    private String deshacerCompletarServicio(Movimiento mov) {
        SolicitudServicio sol = mov.getSolicitud();
        if (sol == null) {
            return "Sin solicitud asociada.";
        }
        revertirCompletado(sol, mov);
        return "Solicitud " + sol.getId() + " reanudada en proceso. El kit sigue en uso (no está en disponibles).";
    }

    /**
     * Inverso de completar servicio: restaura EN_PROCESO, técnico/unidad ocupados y
     * kit en uso.
     */
    private void revertirCompletado(SolicitudServicio sol, Movimiento mov) {
        Tecnico tec = mov.getTecnico();
        UnidadServicio uni = mov.getUnidad();
        Kit kit = mov.getKit();

        if (tec != null) {
            tec.setEstado(EstadoTecnico.OCUPADO);
            sol.setTecnicoAsignado(tec);
        }
        if (uni != null) {
            uni.setEstado(EstadoUnidad.OCUPADO);
            sol.setUnidadAsignada(uni);
        }
        if (kit != null) {
            dejarKitEnUsoFueraDePilas(kit);
            sol.setKitAsignado(kit);
        }
        quitarSolicitudDeColas(sol);
        sol.setEstado(EstadoSolicitud.EN_PROCESO);
    }

    /**
     * Inverso de atender servicio: libera recursos y reencola la solicitud.
     */
    private void revertirAtencion(SolicitudServicio sol, Movimiento mov) {
        if (mov.getTecnico() != null) {
            mov.getTecnico().setEstado(mov.getEstadoAnteriorTecnico());
        }
        if (mov.getUnidad() != null) {
            mov.getUnidad().setEstado(mov.getEstadoAnteriorUnidad());
        }
        if (mov.getKit() != null) {
            devolverKitADisponibles(mov.getKit());
        }
        sol.setTecnicoAsignado(null);
        sol.setUnidadAsignada(null);
        sol.setKitAsignado(null);
        sol.setEstado(mov.getEstadoAnteriorSolicitud() != null
                ? mov.getEstadoAnteriorSolicitud()
                : EstadoSolicitud.PENDIENTE);
        encolarSolicitud(sol);

    }

    /**
     * @return Descripción del último movimiento.
     */
    public String verUltimaOperacion() {
        if (pilaMovimientos.estaVacia()) {
            return "No hay operaciones registradas.";
        } else {
            return pilaMovimientos.cima().toString();
        }
    }

    // =========================================================================
    // REPORTES Y CSV
    // =========================================================================
    /**
     * Genera reporte general del sistema.
     */
    public String generarReporte() {
        StringBuilder sb = new StringBuilder("REPORTE AUTORESCATE 24/7:\n\n");

        sb.append("TÉCNICOS: (").append(tecnicos.getTamanno()).append(")\n");
        ListaEnlazadaSimple.Iterador<Tecnico> itT = tecnicos.iterador();
        while (itT.tieneSiguiente()) {
            sb.append("  ").append(itT.siguiente()).append("\n");
        }

        sb.append("\nUNIDADES:  (").append(unidades.getTamanno()).append(")\n");
        ListaEnlazadaSimple.Iterador<UnidadServicio> itU = unidades.iterador();
        while (itU.tieneSiguiente()) {
            sb.append("  ").append(itU.siguiente()).append("\n");
        }

        sb.append("\nSOLICITUDES PENDIENTES:\n");
        ListaEnlazadaSimple.Iterador<SolicitudServicio> itS = todasLasSolicitudes.iterador();
        while (itS.tieneSiguiente()) {
            SolicitudServicio s = itS.siguiente();
            if (s.getEstado() == EstadoSolicitud.PENDIENTE) {
                sb.append("  ").append(s).append("\n");
            }
        }

        sb.append("\nSOLICITUDES EN PROCESO:\n");
        itS = todasLasSolicitudes.iterador();
        while (itS.tieneSiguiente()) {
            SolicitudServicio s = itS.siguiente();
            if (s.getEstado() == EstadoSolicitud.EN_PROCESO) {
                sb.append("  ").append(s).append("\n");
            }
        }

        sb.append("\nSOLICITUDES COMPLETADAS:\n");
        itS = todasLasSolicitudes.iterador();
        while (itS.tieneSiguiente()) {
            SolicitudServicio s = itS.siguiente();
            if (s.getEstado() == EstadoSolicitud.COMPLETADA) {
                sb.append("  ").append(s).append("\n");
            }
        }

        sb.append("\nKITS DISPONIBLES: ");
        sb.append(pilaKitsDisponibles.estaVacia() ? "ninguno" : pilaKitsDisponibles.cima()).append("\n");
        sb.append("KITS EN REVISIÓN: ");
        sb.append(pilaKitsRevision.estaVacia() ? "ninguno" : pilaKitsRevision.cima()).append("\n");

        sb.append("\nÚLTIMO MOVIMIENTO\n  ").append(verUltimaOperacion()).append("\n");
        return sb.toString();
    }

    /**
     * Exporta solicitudes COMPLETADAS a servicios_atendidos.csv.
     */
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
        } catch (IOException e) {
            return "Error al exportar CSV: " + e.getMessage();
        }
    }

    /**
     * Importa datos de prueba desde CSV.
     */
    public String importarDatosPrueba(String ruta) {
        int cli = 0, tec = 0, uni = 0, kit = 0, sol = 0, err = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] p = linea.split(",", -1);
                try {
                    switch (p[0].trim().toUpperCase()) {
                        case "CLIENTE":
                            if (p.length >= 4) {
                                registrarCliente(p[1].trim(), p[2].trim(),
                                        Cliente.TipoCliente.valueOf(p[3].trim().toUpperCase()));
                                cli++;
                            } else if (p.length >= 3) {
                                registrarCliente(p[1].trim(), p[2].trim(), Cliente.TipoCliente.ORDINARIO);
                                cli++;
                            } else {
                                err++;
                            }
                            break;
                        case "TECNICO":
                            if (p.length >= 3) {
                                registrarTecnico(p[1].trim(),
                                        Tecnico.Especialidad.valueOf(p[2].trim().toUpperCase().replace(" ", "_")));
                                tec++;
                            } else {
                                err++;
                            }
                            break;
                        case "UNIDAD":
                            if (p.length >= 3) {
                                registrarUnidad(UnidadServicio.TipoUnidad.valueOf(p[1].trim().toUpperCase()),
                                        p[2].trim());
                                uni++;
                            } else {
                                err++;
                            }
                            break;
                        case "KIT":
                            agregarKit();
                            kit++;
                            break;
                        case "SOLICITUD":
                            if (p.length >= 4) {
                                Tecnico.TipoServicio ts = Tecnico.TipoServicio.valueOf(p[2].trim().toUpperCase());
                                SolicitudServicio.Zona z = p.length >= 5
                                        ? SolicitudServicio.Zona.valueOf(p[4].trim().toUpperCase())
                                        : SolicitudServicio.Zona.PARQUEADERO;
                                SolicitudServicio s = registrarSolicitud(p[1].trim(), ts, z);
                                if (s != null) {
                                    sol++;
                                } else {
                                    err++;
                                }
                            } else {
                                err++;
                            }
                            break;
                        default:
                            err++;
                    }
                } catch (Exception ex) {
                    err++;
                }
            }
        } catch (IOException e) {
            return "Error al leer el archivo: " + e.getMessage();
        }
        return String.format(
                "Importación completada:\n  Clientes: %d\n  Técnicos: %d\n  Unidades: %d\n  Kits: %d\n  Solicitudes: %d\n",
                cli, tec, uni, kit, sol);
    }

}
