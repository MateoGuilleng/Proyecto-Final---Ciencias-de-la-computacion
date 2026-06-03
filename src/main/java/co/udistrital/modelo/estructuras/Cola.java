package co.udistrital.modelo.estructuras;

/**
 * Implementación de una cola (FIFO) basada en nodos enlazados.
 *
 * @param <T> Tipo de los elementos almacenados.
 * @author AutoRescate 24/7
 */
public class Cola<T> {

    /**
     * Nodo del frente de la cola (primer elemento en salir).
     */
    private Nodo<T> frente;

    /**
     * Nodo del fondo de la cola (último elemento en entrar).
     */
    private Nodo<T> fondo;

    /**
     * Construye una cola vacía.
     */
    public Cola() {
        this.frente = null;
        this.fondo = null;
    }

    /**
     * Inserta un elemento al final de la cola.
     *
     * @param dato Elemento a encolar.
     */
    public void encolar(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (estaVacia()) {
            this.frente = nuevoNodo;
            this.fondo = nuevoNodo;
        } else {
            this.fondo.setSiguiente(nuevoNodo);
            this.fondo = nuevoNodo;
        }
    }

    /**
     * Retira y devuelve el elemento del frente de la cola.
     *
     * @return El dato desencolado, o {@code null} si la cola está vacía.
     */
    public T desencolar() {
        if (estaVacia()) {
            return null;
        } else {
            T dato = this.frente.getDato();
            this.frente = this.frente.getSiguiente();
            if (this.frente == null) {
                this.fondo = null;
            }
            return dato;
        }
    }

    /**
     * @return {@code true} si la cola no contiene elementos.
     */
    public boolean estaVacia() {
        return this.frente == null;
    }

    /**
     * Devuelve el dato del frente de la cola sin desencolar.
     *
     * @return El dato del frente, o {@code null} si está vacía.
     */
    public T verFrente() {
        if (estaVacia()) {
            return null;
        } else {
            return this.frente.getDato();
        }
    }
}
