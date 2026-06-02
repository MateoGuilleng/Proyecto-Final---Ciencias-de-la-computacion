package co.udistrital.modelo.entidades;

/**
 * Representa una unidad de servicio de la empresa AutoRescate 24/7. Cada unidad
 * tiene un ID numérico simple, tipo, estado, zona y disponibilidad. Una unidad
 * en mantenimiento no puede ser asignada a ningún caso.
 *
 * @author AutoRescate 24/7
 */
public class UnidadServicio {

    /**
     * Identificador único de la unidad (1, 2, 3...).
     */
    private String id;

    /**
     * Tipo de unidad de servicio.
     */
    private TipoUnidad tipo;

    /**
     * Estado operativo actual de la unidad.
     */
    private EstadoUnidad estado;

    /**
     * Zona geográfica en la que opera la unidad.
     */
    private String zona;

    /**
     * Enumeración de los tipos de unidades de servicio disponibles.
     */
    public enum TipoUnidad {
        GRUA, MOTO, CAMIONETA, VEHICULO_LIVIANO
    }

    /**
     * Enumeración de los posibles estados operativos de una unidad.
     */
    public enum EstadoUnidad {
        /**
         * La unidad está libre y puede ser asignada.
         */
        DISPONIBLE,
        /**
         * La unidad está atendiendo un servicio actualmente.
         */
        OCUPADO,
        /**
         * La unidad está en mantenimiento y no puede ser asignada.
         */
        MANTENIMIENTO
    }

    /**
     * Construye una nueva unidad de servicio con estado inicial DISPONIBLE.
     *
     * @param id Identificador único de la unidad.
     * @param tipo Tipo de la unidad.
     * @param zona Zona de operación de la unidad.
     */
    public UnidadServicio(String id, TipoUnidad tipo, String zona) {
        this.id = id;
        this.tipo = tipo;
        this.zona = zona;
        this.estado = EstadoUnidad.DISPONIBLE;
    }

    /**
     * @return El id de la unidad.
     */
    public String getId() {
        return id;
    }

    /**
     * @return El tipo de la unidad.
     */
    public TipoUnidad getTipo() {
        return tipo;
    }

    /**
     * @return El estado de la unidad.
     */
    public EstadoUnidad getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la unidad.
     *
     * @param estado El nuevo estado.
     */
    public void setEstado(EstadoUnidad estado) {
        this.estado = estado;
    }

    /**
     * @return La zona de la unidad.
     */
    public String getZona() {
        return zona;
    }

    /**
     * @param zona La nueva zona.
     */
    public void setZona(String zona) {
        this.zona = zona;
    }

    /**
     * @return {@code true} si la unidad está disponible.
     */
    public boolean isDisponible() {
        return this.estado == EstadoUnidad.DISPONIBLE;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + tipo + " | " + estado + " | Zona: " + zona;
    }
}
