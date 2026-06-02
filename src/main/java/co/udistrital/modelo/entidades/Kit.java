package co.udistrital.modelo.entidades;

import java.util.Random;

/**
 * Representa un kit de atención rápida de la empresa AutoRescate 24/7.
 * Al regresar de un servicio se le asigna automáticamente una decisión de revisión
 * (REPARAR, REPONER o NADA), que el operario aplicará al procesarlo.
 *
 * @author AutoRescate 24/7
 */
public class Kit {

    private static final Random RANDOM = new Random();

    /** Identificador único del kit (1, 2, 3...). */
    private String id;

    /** Estado actual del kit. */
    private EstadoKit estado;

    /**
     * Decisión de revisión asignada al regresar del servicio.
     * Determina qué hará el operario automáticamente.
     */
    private DecisionRevision decision;

    /** Estados posibles del kit. */
    public enum EstadoKit {
        /** Disponible en la pila para ser despachado. */
        LISTO,
        /** Siendo utilizado en un servicio activo. */
        EN_USO,
        /** Regresó de un servicio, pendiente de revisión por el operario. */
        EN_REVISION
    }

    /** Decisiones de revisión posibles al inspeccionar el kit. */
    public enum DecisionRevision {
        /** El kit está bien, vuelve a LISTO sin cambios. */
        NADA,
        /** El kit necesita reparación, vuelve a LISTO tras arreglo. */
        REPARAR,
        /** El kit debe reponerse por uno nuevo. */
        REPONER
    }

    /**
     * Construye un nuevo kit con estado inicial LISTO y sin decisión asignada.
     * @param id Identificador único del kit.
     */
    public Kit(String id) {
        this.id = id;
        this.estado = EstadoKit.LISTO;
        this.decision = null;
    }

    /** @return El id del kit. */
    public String getId() { return id; }

    /** @return El estado del kit. */
    public EstadoKit getEstado() { return estado; }

    /** @param estado El nuevo estado del kit. */
    public void setEstado(EstadoKit estado) { this.estado = estado; }

    /** @return La decisión de revisión asignada, o null si no ha sido revisado. */
    public DecisionRevision getDecision() { return decision; }

    /** @param decision La decisión de revisión a asignar. */
    public void setDecision(DecisionRevision decision) { this.decision = decision; }

    /**
     * Asigna aleatoriamente una decisión de revisión al kit cuando regresa de un servicio.
     * Probabilidades: 50% NADA, 30% REPARAR, 20% REPONER.
     */
    public void asignarDecisionAleatoria() {
        int r = RANDOM.nextInt(10);
        if (r < 5) this.decision = DecisionRevision.NADA;
        else if (r < 8) this.decision = DecisionRevision.REPARAR;
        else this.decision = DecisionRevision.REPONER;
    }

    @Override
    public String toString() {
        String dec = decision != null ? " [" + decision + "]" : "";
        return "Kit-" + id + " | " + estado + dec;
    }
}
