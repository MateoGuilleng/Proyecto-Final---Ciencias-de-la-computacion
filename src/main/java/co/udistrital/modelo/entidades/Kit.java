package co.udistrital.modelo.entidades;

/**
 * Representa un kit de atención rápida de la empresa AutoRescate 24/7.
 * Los kits disponibles se gestionan en una pila LIFO. Cuando se usa en un servicio
 * pasa a EN_USO, al terminar el servicio pasa a una pila de revisión (EN_REVISION),
 * y tras la revisión vuelve a LISTO o se repone con un kit nuevo.
 *
 * @author AutoRescate 24/7
 */
public class Kit {

    /** Identificador único del kit (número simple: 1, 2, 3...). */
    private String id;

    /** Estado actual del kit. */
    private EstadoKit estado;

    /**
     * Enumeración de los posibles estados de un kit.
     */
    public enum EstadoKit {
        /** El kit está disponible en la pila para ser despachado. */
        LISTO,
        /** El kit está siendo utilizado en un servicio activo. */
        EN_USO,
        /** El kit regresó de un servicio y está pendiente de revisión. */
        EN_REVISION
    }

    /**
     * Construye un nuevo kit con estado inicial LISTO.
     *
     * @param id Identificador único del kit.
     */
    public Kit(String id) {
        this.id = id;
        this.estado = EstadoKit.LISTO;
    }

    /**
     * Obtiene el identificador del kit.
     *
     * @return El id del kit.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el estado del kit.
     *
     * @return El estado del kit.
     */
    public EstadoKit getEstado() {
        return estado;
    }

    /**
     * Establece el estado del kit.
     *
     * @param estado El nuevo estado del kit.
     */
    public void setEstado(EstadoKit estado) {
        this.estado = estado;
    }

    /**
     * Devuelve una representación textual del kit.
     *
     * @return Cadena con los datos del kit.
     */
    @Override
    public String toString() {
        return "Kit-" + id + " | " + estado;
    }
}
