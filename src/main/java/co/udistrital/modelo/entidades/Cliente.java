package co.udistrital.modelo.entidades;

/**
 * Representa un cliente de la empresa AutoRescate 24/7.
 * El tipo de cliente (ORDINARIO o PRIORITARIO) aporta puntos al cálculo
 * automático de prioridad de sus solicitudes.
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

    /** Tipo de cliente que determina su peso en el cálculo de prioridad. */
    private TipoCliente tipo;

    /**
     * Clasificación del cliente según su importancia operativa.
     * Aporta puntos al sistema de prioridad automática de solicitudes.
     */
    public enum TipoCliente {
        /** Cliente regular, aporta 0 puntos de prioridad. */
        ORDINARIO(0, "Ordinario"),
        /** Cliente con contrato especial o alta criticidad, aporta 2 puntos. */
        PRIORITARIO(2, "Prioritario");

        /** Puntos que este tipo de cliente aporta al cálculo de prioridad. */
        private final int puntos;
        private final String nombre;

        TipoCliente(int puntos, String nombre) {
            this.puntos = puntos;
            this.nombre = nombre;
        }

        /** @return Puntos de prioridad que aporta este tipo de cliente. */
        public int getPuntos() { return puntos; }

        @Override
        public String toString() { return nombre; }
    }

    /**
     * Construye un nuevo cliente.
     *
     * @param id       Identificador único.
     * @param nombre   Nombre completo.
     * @param telefono Teléfono de contacto.
     * @param tipo     Tipo de cliente (ORDINARIO o PRIORITARIO).
     */
    public Cliente(String id, String nombre, String telefono, TipoCliente tipo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipo = tipo;
    }

    /** @return El id del cliente. */
    public String getId() { return id; }

    /** @return El nombre del cliente. */
    public String getNombre() { return nombre; }

    /** @return El tipo de cliente. */
    public TipoCliente getTipo() { return tipo; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " | Tel: " + telefono + " | " + tipo;
    }
}
