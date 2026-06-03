package co.udistrital.modelo.entidades;

/**
 * Representa una operación registrada en el sistema AutoRescate 24/7. Se
 * almacena en una Pila para permitir deshacer las operaciones más recientes.
 *
 * @author AutoRescate 24/7
 */
public class Movimiento {

    /**
     * Tipo de operación realizada.
     */
    private TipoOperacion tipoOperacion;

    /**
     * Descripción legible de la operación.
     */
    private String descripcion;

    /**
     * Solicitud involucrada (puede ser null).
     */
    private SolicitudServicio solicitud;

    /**
     * Técnico involucrado (puede ser null).
     */
    private Tecnico tecnico;

    /**
     * Unidad involucrada (puede ser null).
     */
    private UnidadServicio unidad;

    /**
     * Kit involucrado (puede ser null).
     */
    private Kit kit;

    /**
     * Estado anterior del técnico para revertir.
     */
    private Tecnico.EstadoTecnico estadoAnteriorTecnico;

    /**
     * Estado anterior de la unidad para revertir.
     */
    private UnidadServicio.EstadoUnidad estadoAnteriorUnidad;

    /**
     * Estado anterior de la solicitud para revertir.
     */
    private SolicitudServicio.EstadoSolicitud estadoAnteriorSolicitud;

    /**
     * Enumeración de los tipos de operaciones registrables.
     */
    public enum TipoOperacion {
        /** Asignación de recursos para atender un servicio. */
        ATENDER_SERVICIO,
        /** Finalización de un servicio en proceso. */
        COMPLETAR_SERVICIO,
        /** Cambio manual del estado de una unidad. */
        CAMBIAR_ESTADO_UNIDAD,
        /** Cambio manual del estado de un técnico. */
        CAMBIAR_ESTADO_TECNICO
    }

    /**
     * Construye un nuevo movimiento.
     *
     * @param tipoOperacion Tipo de operación.
     * @param descripcion Descripción legible.
     */
    public Movimiento(TipoOperacion tipoOperacion, String descripcion) {
        this.tipoOperacion = tipoOperacion;
        this.descripcion = descripcion;
    }

    /**
     * @return El tipo de operación.
     */
    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * @return La descripción.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @return La solicitud.
     */
    public SolicitudServicio getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud La solicitud a asociar.
     */
    public void setSolicitud(SolicitudServicio solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * @return El técnico.
     */
    public Tecnico getTecnico() {
        return tecnico;
    }

    /**
     * @param tecnico El técnico a asociar.
     */
    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    /**
     * @return La unidad.
     */
    public UnidadServicio getUnidad() {
        return unidad;
    }

    /**
     * @param unidad La unidad a asociar.
     */
    public void setUnidad(UnidadServicio unidad) {
        this.unidad = unidad;
    }

    /**
     * @return El kit.
     */
    public Kit getKit() {
        return kit;
    }

    /**
     * @param kit El kit a asociar.
     */
    public void setKit(Kit kit) {
        this.kit = kit;
    }

    /**
     * @return Estado anterior del técnico.
     */
    public Tecnico.EstadoTecnico getEstadoAnteriorTecnico() {
        return estadoAnteriorTecnico;
    }

    /**
     * @param e Estado previo del técnico.
     */
    public void setEstadoAnteriorTecnico(Tecnico.EstadoTecnico e) {
        this.estadoAnteriorTecnico = e;
    }

    /**
     * @return Estado anterior de la unidad.
     */
    public UnidadServicio.EstadoUnidad getEstadoAnteriorUnidad() {
        return estadoAnteriorUnidad;
    }

    /**
     * @param e Estado previo de la unidad.
     */
    public void setEstadoAnteriorUnidad(UnidadServicio.EstadoUnidad e) {
        this.estadoAnteriorUnidad = e;
    }

    /**
     * @return Estado anterior de la solicitud.
     */
    public SolicitudServicio.EstadoSolicitud getEstadoAnteriorSolicitud() {
        return estadoAnteriorSolicitud;
    }

    /**
     * @param e Estado previo de la solicitud.
     */
    public void setEstadoAnteriorSolicitud(SolicitudServicio.EstadoSolicitud e) {
        this.estadoAnteriorSolicitud = e;
    }

    @Override
    public String toString() {
        return "[" + tipoOperacion + "] " + descripcion;
    }
}
