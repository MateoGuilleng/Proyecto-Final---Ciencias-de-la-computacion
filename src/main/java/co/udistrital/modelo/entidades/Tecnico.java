package co.udistrital.modelo.entidades;

/**
 * Representa un técnico de la empresa AutoRescate 24/7.
 * Cada técnico tiene un identificador numérico simple, nombre, especialidad y estado.
 * Un técnico no puede atender dos servicios simultáneamente.
 *
 * @author AutoRescate 24/7
 */
public class Tecnico {

    /** Identificador único del técnico (1, 2, 3...). */
    private String id;

    /** Nombre completo del técnico. */
    private String nombre;

    /** Especialidad del técnico. */
    private String especialidad;

    /** Estado actual del técnico. */
    private EstadoTecnico estado;

    /** Enumeración de los posibles estados de un técnico. */
    public enum EstadoTecnico {
        /** El técnico está libre y puede ser asignado. */
        DISPONIBLE,
        /** El técnico está atendiendo un servicio actualmente. */
        OCUPADO
    }

    /**
     * Construye un nuevo técnico con estado inicial DISPONIBLE.
     *
     * @param id           Identificador único del técnico.
     * @param nombre       Nombre completo del técnico.
     * @param especialidad Especialidad del técnico.
     */
    public Tecnico(String id, String nombre, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.estado = EstadoTecnico.DISPONIBLE;
    }

    /** @return El id del técnico. */
    public String getId() { return id; }

    /** @return El nombre del técnico. */
    public String getNombre() { return nombre; }

    /** @param nombre Nuevo nombre. */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return La especialidad del técnico. */
    public String getEspecialidad() { return especialidad; }

    /** @param especialidad Nueva especialidad. */
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    /** @return El estado del técnico. */
    public EstadoTecnico getEstado() { return estado; }

    /** @param estado El nuevo estado del técnico. */
    public void setEstado(EstadoTecnico estado) { this.estado = estado; }

    /** @return {@code true} si el estado es DISPONIBLE. */
    public boolean isDisponible() { return this.estado == EstadoTecnico.DISPONIBLE; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " | " + especialidad + " | " + estado;
    }
}
