package co.udistrital.modelo.entidades;

/**
 * Representa una solicitud de servicio de asistencia vehicular. La prioridad se
 * calcula automáticamente sumando los puntos del tipo de cliente, la zona del
 * incidente y el tipo de servicio solicitado. Si la suma es mayor o igual a 3,
 * la solicitud es CRITICA; si no, ORDINARIA.
 *
 * @author AutoRescate 24/7
 */
public class SolicitudServicio {

    /**
     * Identificador único (1, 2, 3...).
     */
    private String id;

    /**
     * Cliente que generó la solicitud.
     */
    private Cliente cliente;

    /**
     * Tipo de servicio requerido.
     */
    private Tecnico.TipoServicio tipoServicio;

    /**
     * Zona donde ocurre el incidente.
     */
    private Zona zona;

    /**
     * Prioridad calculada automáticamente.
     */
    private Prioridad prioridad;

    /**
     * Estado actual.
     */
    private EstadoSolicitud estado;

    /**
     * Técnico asignado.
     */
    private Tecnico tecnicoAsignado;

    /**
     * Unidad de servicio asignada.
     */
    private UnidadServicio unidadAsignada;

    /**
     * Kit de atención rápida asignado (siempre requerido).
     */
    private Kit kitAsignado;

    // =========================================================================
    // ENUMS
    // =========================================================================
    /**
     * Zona donde ocurre el incidente. Aporta puntos al cálculo de prioridad.
     */
    public enum Zona {
        /**
         * Parqueadero o zona privada, sin riesgo vial. 0 puntos.
         */
        PARQUEADERO("Parqueadero", 0),
        /**
         * Calle pública con tráfico moderado. 1 punto.
         */
        CALLE_PUBLICA("Calle pública", 1),
        /**
         * Carretera concurrida o vía principal. 2 puntos.
         */
        CARRETERA_CONCURRIDA("Carretera concurrida", 2);

        private final String nombre;
        private final int puntos;

        Zona(String nombre, int puntos) {
            this.nombre = nombre;
            this.puntos = puntos;
        }

        /**
         * @return Nombre descriptivo de la zona.
         */
        public String getNombre() {
            return nombre;
        }

        /**
         * @return Puntos de prioridad que aporta esta zona.
         */
        public int getPuntos() {
            return puntos;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    /**
     * Prioridad de la solicitud, calculada automáticamente.
     */
    public enum Prioridad {
        /**
         * Suma de puntos menor a 3. Atención en orden de llegada.
         */
        ORDINARIA,
        /**
         * Suma de puntos mayor o igual a 3. Atención preferente.
         */
        CRITICA
    }

    /**
     * Estado de la solicitud.
     */
    public enum EstadoSolicitud {
        PENDIENTE, EN_PROCESO, COMPLETADA
    }

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================
    /**
     * Construye una nueva solicitud con estado PENDIENTE. La prioridad se
     * calcula automáticamente con la fórmula: puntos = cliente.tipo.puntos +
     * zona.puntos + tipoServicio.puntos Si puntos >= 3 → CRITICA, si no →
     * ORDINARIA.
     *
     * @param id Identificador único.
     * @param cliente Cliente solicitante.
     * @param tipoServicio Tipo de servicio requerido.
     * @param zona Zona donde ocurre el incidente.
     */
    public SolicitudServicio(String id, Cliente cliente,
            Tecnico.TipoServicio tipoServicio, Zona zona) {
        this.id = id;
        this.cliente = cliente;
        this.tipoServicio = tipoServicio;
        this.zona = zona;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.tecnicoAsignado = null;
        this.unidadAsignada = null;
        this.kitAsignado = null;

        // Calcular prioridad automáticamente
        int puntos = cliente.getTipo().getPuntos()
                + zona.getPuntos()
                + tipoServicio.getPuntos();
        this.prioridad = (puntos >= 4) ? Prioridad.CRITICA : Prioridad.ORDINARIA;
    }

    // =========================================================================
    // GETTERS / SETTERS
    // =========================================================================
    /**
     * @return El id.
     */
    public String getId() {
        return id;
    }

    /**
     * @return El cliente.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * @return El tipo de servicio.
     */
    public Tecnico.TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    /**
     * @return La zona del incidente.
     */
    public Zona getZona() {
        return zona;
    }

    /**
     * @return La prioridad calculada.
     */
    public Prioridad getPrioridad() {
        return prioridad;
    }

    /**
     * @return El estado.
     */
    public EstadoSolicitud getEstado() {
        return estado;
    }

    /**
     * @param estado El nuevo estado.
     */
    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    /**
     * @return El técnico asignado o null.
     */
    public Tecnico getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    /**
     * @param tecnico El técnico a asignar.
     */
    public void setTecnicoAsignado(Tecnico tecnico) {
        this.tecnicoAsignado = tecnico;
    }

    /**
     * @return La unidad asignada o null.
     */
    public UnidadServicio getUnidadAsignada() {
        return unidadAsignada;
    }

    /**
     * @param unidad La unidad a asignar.
     */
    public void setUnidadAsignada(UnidadServicio unidad) {
        this.unidadAsignada = unidad;
    }

    /**
     * @return El kit asignado (siempre requerido para iniciar servicio).
     */
    public Kit getKitAsignado() {
        return kitAsignado;
    }

    /**
     * @param kit El kit a asignar.
     */
    public void setKitAsignado(Kit kit) {
        this.kitAsignado = kit;
    }

    /**
     * @return true si tiene técnico, unidad y kit asignados.
     */
    public boolean tieneRecursosAsignados() {
        return tecnicoAsignado != null && unidadAsignada != null && kitAsignado != null;
    }

    @Override
    public String toString() {
        int puntos = cliente.getTipo().getPuntos() + zona.getPuntos() + tipoServicio.getPuntos();
        return "[" + id + "] " + cliente.getNombre()
                + " | " + tipoServicio.getNombre()
                + " | " + zona.getNombre()
                + " | " + prioridad + " (" + puntos + "pts)"
                + " | " + estado;
    }
}
