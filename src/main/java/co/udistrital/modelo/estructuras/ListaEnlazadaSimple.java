package co.udistrital.modelo.estructuras;

import java.util.NoSuchElementException;

/**
 * Implementación de una lista enlazada simple genérica. Soporta inserción al
 * final, eliminación por valor, consulta de tamaño e iteración.
 *
 * @param <T> Tipo de los elementos almacenados.
 * @author AutoRescate 24/7
 */
public class ListaEnlazadaSimple<T> {

    /**
     * Referencia al primer nodo de la lista.
     */
    private Nodo<T> cabeza;
    /**
     * Referencia al último nodo de la lista. Optimiza inserción al final.
     */
    private Nodo<T> cola;
    /**
     * Número actual de elementos en la lista.
     */
    private int tamanno;

    /**
     * Construye una lista enlazada simple vacía.
     */
    public ListaEnlazadaSimple() {
        this.cabeza = null;
        this.cola = null;
        this.tamanno = 0;
    }

    /**
     * Comprueba si la lista no contiene elementos.
     *
     * @return {@code true} si el tamaño es 0.
     */
    public boolean estaVacia() {
        return this.tamanno == 0;
    }

    /**
     * Devuelve el número de elementos en la lista.
     *
     * @return El tamaño de la lista.
     */
    public int getTamanno() {
        return this.tamanno;
    }

    /**
     * Agrega un elemento al final de la lista. O(1).
     *
     * @param dato El dato a agregar.
     */
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            this.cabeza = nuevo;
            this.cola = nuevo;
        } else {
            this.cola.setSiguiente(nuevo);
            this.cola = nuevo;
        }
        this.tamanno++;
    }

    /**
     * Elimina la primera ocurrencia del elemento especificado. O(n). Usa
     * {@code equals()} para la comparación.
     *
     * @param dato El dato a eliminar.
     * @return {@code true} si fue encontrado y eliminado.
     */
    public boolean eliminar(T dato) {
        if (estaVacia()) {
            return false;
        } else if (this.cabeza.getDato().equals(dato)) {
            this.cabeza = this.cabeza.getSiguiente();
            this.tamanno--;
            if (estaVacia()) {
                this.cola = null;
            }
            return true;
        } else {
            Nodo<T> actual = this.cabeza;
            while (actual.getSiguiente() != null) {
                if (actual.getSiguiente().getDato().equals(dato)) {
                    if (actual.getSiguiente() == this.cola) {
                        actual.setSiguiente(null);
                        this.cola = actual;
                    } else {
                        actual.setSiguiente(actual.getSiguiente().getSiguiente());
                    }
                    this.tamanno--;
                    return true;
                }
                actual = actual.getSiguiente();
            }
            return false;
        }
    }

    /**
     * Devuelve un iterador para recorrer la lista de cabeza a cola.
     *
     * @return Una instancia de {@link Iterador}.
     */
    public Iterador<T> iterador() {
        return new Iterador<>(this.cabeza);
    }

    /**
     * Iterador secuencial para {@link ListaEnlazadaSimple}.
     *
     * @param <I> Tipo de los elementos.
     */
    public static class Iterador<I> {

        private Nodo<I> actual;

        /**
         * Construye el iterador desde el nodo cabeza.
         *
         * @param cabeza El primer nodo de la lista.
         */
        public Iterador(Nodo<I> cabeza) {
            this.actual = cabeza;
        }

        /**
         * @return {@code true} si hay más elementos por recorrer.
         */
        public boolean tieneSiguiente() {
            return actual != null;
        }

        /**
         * Devuelve el dato del nodo actual y avanza al siguiente.
         *
         * @return El dato del nodo actual.
         * @throws NoSuchElementException si no hay más elementos.
         */
        public I siguiente() {
            if (!tieneSiguiente()) {
                throw new NoSuchElementException("No hay más elementos.");
            }
            I dato = actual.getDato();
            actual = actual.getSiguiente();
            return dato;
        }
    }
}
