package co.udistrital.modelo.entidades;

/**
 * Representa un cliente de la empresa AutoRescate 24/7.
 * Toda solicitud de servicio debe quedar asociada a un cliente registrado.
 *
 * @author AutoRescate 24/7
 */
public class Cliente {

    /** Identificador único del cliente (1, 2, 3...). */
    private String id;

    /** Nombre completo del cliente. */
    private String nombre;

    /** Número de teléfono de contacto del cliente. */
    private String telefono;

    /**
     * Construye un nuevo cliente.
     *
     * @param id       Identificador único del cliente.
     * @param nombre   Nombre completo del cliente.
     * @param telefono Teléfono de contacto del cliente.
     */
    public Cliente(String id, String nombre, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    /** @return El id del cliente. */
    public String getId() { return id; }

    /** @return El nombre del cliente. */
    public String getNombre() { return nombre; }

    /** @param nombre Nuevo nombre. */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return El teléfono del cliente. */
    public String getTelefono() { return telefono; }

    /** @param telefono Nuevo teléfono. */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " | Tel: " + telefono;
    }
}
