package co.udistrital.modelo.entidades;

/**
 * Representa una solicitud de servicio de asistencia vehicular.
 * Toda solicitud debe estar asociada a un cliente registrado.
 * Una solicitud no puede considerarse atendida sin asignación de recursos.
 * Las solicitudes pueden ser ORDINARIAS (FIFO) o CRITICAS (prioridad).
 *
 * @author AutoRescate 24/7
 */
public class SolicitudServicio {

    /** Identificador único de la solicitud (1, 2, 3...). */
    private String id;

    /** Cliente que generó la solicitud. */
    private Cliente cliente;

    /** Descripción del problema o servicio requerido. */
    private String descripcion;

    /** Prioridad de atención de la solicitud. */
    private Prioridad prioridad;

    /** Estado actual de la solicitud. */
    private EstadoSolicitud estado;

    /** Técnico asignado a esta solicitud. */
    private Tecnico tecnicoAsignado;

    /** Unidad de servicio asignada a esta solicitud. */
    private UnidadServicio unidadAsignada;

    /** Kit de atención rápida asignado (opcional, puede ser null). */
    private Kit kitAsignado;

    /** Enumeración de las prioridades de una solicitud. */
    public enum Prioridad {
        /** Solicitud normal, se atiende en orden de llegada. */
        ORDINARIA,
        /** Solicitud urgente, se atiende antes que las ordinarias. */
        CRITICA
    }

    /** Enumeración de los posibles estados de una solicitud. */
    public enum EstadoSolicitud {
        /** La solicitud fue registrada y espera ser atendida. */
        PENDIENTE,
        /** La solicitud tiene recursos asignados y está siendo atendida. */
        EN_PROCESO,
        /** La solicitud fue completada exitosamente. */
        COMPLETADA
    }

    /**
     * Construye una nueva solicitud con estado inicial PENDIENTE.
     *
     * @param id          Identificador único.
     * @param cliente     Cliente que solicita el servicio.
     * @param descripcion Descripción del servicio.
     * @param prioridad   Prioridad de la solicitud.
     */
    public SolicitudServicio(String id, Cliente cliente, String descripcion, Prioridad prioridad) {
        this.id = id;
        this.cliente = cliente;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.tecnicoAsignado = null;
        this.unidadAsignada = null;
        this.kitAsignado = null;
    }

    /** @return El id de la solicitud. */
    public String getId() { return id; }

    /** @return El cliente de la solicitud. */
    public Cliente getCliente() { return cliente; }

    /** @return La descripción del servicio. */
    public String getDescripcion() { return descripcion; }

    /** @param descripcion Nueva descripción. */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /** @return La prioridad de la solicitud. */
    public Prioridad getPrioridad() { return prioridad; }

    /** @param prioridad Nueva prioridad. */
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    /** @return El estado de la solicitud. */
    public EstadoSolicitud getEstado() { return estado; }

    /** @param estado El nuevo estado. */
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    /** @return El técnico asignado, o {@code null} si no hay. */
    public Tecnico getTecnicoAsignado() { return tecnicoAsignado; }

    /** @param tecnico El técnico a asignar. */
    public void setTecnicoAsignado(Tecnico tecnico) { this.tecnicoAsignado = tecnico; }

    /** @return La unidad asignada, o {@code null} si no hay. */
    public UnidadServicio getUnidadAsignada() { return unidadAsignada; }

    /** @param unidad La unidad a asignar. */
    public void setUnidadAsignada(UnidadServicio unidad) { this.unidadAsignada = unidad; }

    /** @return El kit asignado, o {@code null} si no se usó kit. */
    public Kit getKitAsignado() { return kitAsignado; }

    /** @param kit El kit a asignar. */
    public void setKitAsignado(Kit kit) { this.kitAsignado = kit; }

    /** @return {@code true} si tiene técnico y unidad asignados. */
    public boolean tieneRecursosAsignados() {
        return tecnicoAsignado != null && unidadAsignada != null;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + cliente.getNombre() + " | " + descripcion
                + " | " + prioridad + " | " + estado;
    }
}
