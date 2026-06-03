package co.udistrital.modelo.estructuras;

/**
 * Implementación de una pila (LIFO) basada en nodos enlazados.
 *
 * @param <T> Tipo de los elementos almacenados.
 * @author AutoRescate 24/7
 */
public class Pila<T> {

    /**
     * Nodo en la cima de la pila (último elemento insertado).
     */
    private Nodo<T> tope;

    /**
     * Construye una pila vacía.
     */
    public Pila() {
        this.tope = null;
    }

    /**
     * Inserta un elemento en la cima de la pila.
     *
     * @param dato Elemento a apilar.
     */
    public void push(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        nuevoNodo.setSiguiente(this.tope);
        this.tope = nuevoNodo;
    }

    /**
     * Retira y devuelve el elemento de la cima de la pila.
     *
     * @return El dato desapilado, o {@code null} si la pila está vacía.
     */
    public T pop() {
        if (estaVacia()) {
            return null;
        } else {
            T dato = this.tope.getDato();
            this.tope = this.tope.getSiguiente();
            return dato;
        }
    }

    /**
     * Devuelve el elemento de la cima sin retirarlo.
     *
     * @return El dato en la cima, o {@code null} si la pila está vacía.
     */
    public T cima() {
        if (estaVacia()) {
            return null;
        } else {
            return this.tope.getDato();
        }
    }

    /**
     * @return {@code true} si la pila no contiene elementos.
     */
    public boolean estaVacia() {
        return this.tope == null;
    }

    /**
     * Devuelve el número de elementos en la pila.
     *
     * @return Cantidad de elementos.
     */
    public int getTamanno() {
        int count = 0;
        Nodo<T> actual = this.tope;
        while (actual != null) {
            count++;
            actual = actual.getSiguiente();
        }
        return count;
    }

    /**
     * Devuelve un iterador que recorre la pila desde el tope hacia el fondo. El
     * primer elemento devuelto es el que está en la cima (último en entrar).
     *
     * @return Iterador de la pila.
     */
    public Iterador<T> iterador() {
        return new Iterador<>(this.tope);
    }

    /**
     * Iterador simple para recorrer la pila sin modificarla. Recorre desde el
     * tope (cima) hacia el fondo.
     *
     * @param <I> Tipo de los elementos.
     */
    public static class Iterador<I> {

        private Nodo<I> actual;

        /**
         * Construye el iterador desde el nodo tope.
         *
         * @param tope Nodo en la cima de la pila.
         */
        public Iterador(Nodo<I> tope) {
            this.actual = tope;
        }

        /**
         * @return {@code true} si hay más elementos.
         */
        public boolean tieneSiguiente() {
            return actual != null;
        }

        /**
         * Devuelve el dato del nodo actual y avanza al siguiente.
         *
         * @return El dato del nodo actual.
         */
        public I siguiente() {
            I dato = actual.getDato();
            actual = actual.getSiguiente();
            return dato;
        }
    }
}
