package co.udistrital.modelo.entidades;

/**
 * Representa un técnico de la empresa AutoRescate 24/7. Cada técnico tiene una
 * especialidad que determina los servicios que puede atender y un rango de
 * duración propio (en minutos simulados, 1 seg real = 10 min simulados).
 *
 * @author AutoRescate 24/7
 */
public class Tecnico {

    /**
     * Identificador único del técnico (1, 2, 3...).
     */
    private String id;

    /**
     * Nombre completo del técnico.
     */
    private String nombre;

    /**
     * Especialidad del técnico, define qué servicios puede atender y su tiempo.
     */
    private Especialidad especialidad;

    /**
     * Estado actual del técnico.
     */
    private EstadoTecnico estado;

    // =========================================================================
    // ENUMS
    // =========================================================================
    /**
     * Especialidades disponibles para los técnicos. Cada especialidad define
     * los tipos de servicio que puede atender y el rango de duración estimada
     * en minutos simulados.
     */
    public enum Especialidad {

        /**
         * Especialista en sistemas eléctricos del vehículo.
         */
        ELECTRICIDAD_AUTOMOTRIZ("Electricidad Automotriz", 30, 50),
        /**
         * Especialista en mecánica general.
         */
        MECANICA_GENERAL("Mecánica General", 20, 50),
        /**
         * Especialista en operación de grúas y remolque.
         */
        GRUAS_Y_REMOLQUE("Grúas y Remolque", 40, 60),
        /**
         * Especialista en asistencia en carretera.
         */
        ASISTENCIA_EN_CARRETERA("Asistencia en Carretera", 20, 40),
        /**
         * Especialista en motores diésel.
         */
        MECANICA_DIESEL("Mecánica Diésel", 40, 60);

        /**
         * Nombre descriptivo de la especialidad.
         */
        private final String nombre;

        /**
         * Duración mínima en minutos simulados para cualquier tarea de esta
         * especialidad.
         */
        private final int duracionMinMin;

        /**
         * Duración máxima en minutos simulados para cualquier tarea de esta
         * especialidad.
         */
        private final int duracionMaxMin;

        Especialidad(String nombre, int duracionMinMin, int duracionMaxMin) {
            this.nombre = nombre;
            this.duracionMinMin = duracionMinMin;
            this.duracionMaxMin = duracionMaxMin;
        }

        /**
         * @return Nombre descriptivo.
         */
        public String getNombre() {
            return nombre;
        }

        /**
         * @return Duración mínima en minutos simulados.
         */
        public int getDuracionMinMin() {
            return duracionMinMin;
        }

        /**
         * @return Duración máxima en minutos simulados.
         */
        public int getDuracionMaxMin() {
            return duracionMaxMin;
        }

        /**
         * Duración mínima en milisegundos reales (1 seg = 10 min simulados).
         *
         * @return Milisegundos mínimos.
         */
        public long getDuracionMinMs() {
            return (duracionMinMin / 10L) * 1000L;
        }

        /**
         * Duración máxima en milisegundos reales (1 seg = 10 min simulados).
         *
         * @return Milisegundos máximos.
         */
        public long getDuracionMaxMs() {
            return (duracionMaxMin / 10L) * 1000L;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    /**
     * Estados posibles del técnico.
     */
    public enum EstadoTecnico {
        /**
         * El técnico está libre y puede ser asignado.
         */
        DISPONIBLE,
        /**
         * El técnico está atendiendo un servicio actualmente.
         */
        OCUPADO
    }

    /**
     * Tipos de servicio que el sistema puede solicitar. Cada tipo está asociado
     * a una especialidad requerida y aporta puntos de prioridad.
     */
    public enum TipoServicio {
        PASO_CORRIENTE("Paso de corriente", Especialidad.ELECTRICIDAD_AUTOMOTRIZ, 0),
        CAMBIO_LLANTA("Cambio de llanta", Especialidad.MECANICA_GENERAL, 1),
        ENVIO_GRUA("Envío de grúa", Especialidad.GRUAS_Y_REMOLQUE, 1),
        APERTURA_PUERTAS("Apertura de puertas", Especialidad.MECANICA_GENERAL, 0),
        SUMINISTRO_COMBUSTIBLE("Suministro combustible", Especialidad.ASISTENCIA_EN_CARRETERA, 0),
        REVISION_MECANICA("Revisión mecánica básica", Especialidad.MECANICA_GENERAL, 0),
        FALLA_DIESEL("Falla motor diésel", Especialidad.MECANICA_DIESEL, 2),
        VEHICULO_VARADO("Vehículo varado", Especialidad.ASISTENCIA_EN_CARRETERA, 2);

        private final String nombre;
        private final Especialidad especialidadRequerida;
        /**
         * Puntos que este tipo de servicio aporta al cálculo de prioridad.
         */
        private final int puntos;

        TipoServicio(String nombre, Especialidad especialidadRequerida, int puntos) {
            this.nombre = nombre;
            this.especialidadRequerida = especialidadRequerida;
            this.puntos = puntos;
        }

        /**
         * @return Nombre descriptivo del servicio.
         */
        public String getNombre() {
            return nombre;
        }

        /**
         * @return Especialidad que debe tener el técnico asignado.
         */
        public Especialidad getEspecialidadRequerida() {
            return especialidadRequerida;
        }

        /**
         * @return Puntos de prioridad que aporta este tipo de servicio.
         */
        public int getPuntos() {
            return puntos;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    // =========================================================================
    // CONSTRUCTOR Y MÉTODOS
    // =========================================================================
    /**
     * Construye un nuevo técnico con estado inicial DISPONIBLE.
     *
     * @param id Identificador único.
     * @param nombre Nombre completo.
     * @param especialidad Especialidad del técnico.
     */
    public Tecnico(String id, String nombre, Especialidad especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.estado = EstadoTecnico.DISPONIBLE;
    }

    /**
     * @return El id del técnico.
     */
    public String getId() {
        return id;
    }

    /**
     * @return El nombre del técnico.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return La especialidad del técnico.
     */
    public Especialidad getEspecialidad() {
        return especialidad;
    }

    /**
     * @return El estado del técnico.
     */
    public EstadoTecnico getEstado() {
        return estado;
    }

    /**
     * @param estado El nuevo estado.
     */
    public void setEstado(EstadoTecnico estado) {
        this.estado = estado;
    }

    /**
     * @return true si está disponible.
     */
    public boolean isDisponible() {
        return this.estado == EstadoTecnico.DISPONIBLE;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " | " + especialidad.getNombre()
                + " | " + estado
                + " | " + especialidad.getDuracionMinMin() + "-"
                + especialidad.getDuracionMaxMin() + " min";
    }
}
