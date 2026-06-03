package co.udistrital.modelo.estructuras;

/**
 * Nodo genérico para estructuras de datos enlazadas (listas, pilas y colas).
 *
 * @param <T> Tipo del dato almacenado en el nodo.
 * @author AutoRescate 24/7
 */
public class Nodo<T> {

    /**
     * Dato almacenado en el nodo.
     */
    private T dato;

    /**
     * Referencia al siguiente nodo en la estructura.
     */
    private Nodo<T> siguiente;

    /**
     * Construye un nodo con el dato indicado y sin siguiente.
     *
     * @param dato Valor a almacenar.
     */
    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    /**
     * Construye un nodo con dato y referencia al siguiente nodo.
     *
     * @param dato      Valor a almacenar.
     * @param siguiente Nodo siguiente en la cadena.
     */
    public Nodo(T dato, Nodo<T> siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }

    /**
     * @return El dato almacenado.
     */
    public T getDato() {
        return dato;
    }

    /**
     * @return El nodo siguiente, o {@code null} si es el último.
     */
    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Establece el nodo siguiente.
     *
     * @param siguiente Nuevo nodo siguiente.
     */
    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}
