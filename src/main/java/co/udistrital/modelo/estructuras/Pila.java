/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.udistrital.modelo.estructuras;

import co.udistrital.modelo.estructuras.Nodo;

public class Pila<T> {

    private Nodo<T> tope;

    public Pila() {
        this.tope = null;
    }

    public void push(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        nuevoNodo.setSiguiente(this.tope);
        this.tope = nuevoNodo;
    }

    public T pop() {
        if (estaVacia()) {
            return null;
        } else {
            T dato = this.tope.getDato();
            this.tope = this.tope.getSiguiente();
            return dato;
        }
    }

    public T cima() {
        if (estaVacia()) {
            return null;
        } else {
            return this.tope.getDato();
        }
    }

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
         * @return true si hay más elementos.
         */
        public boolean tieneSiguiente() {
            return actual != null;
        }

        /**
         * @return El dato del nodo actual y avanza.
         */
        public I siguiente() {
            I dato = actual.getDato();
            actual = actual.getSiguiente();
            return dato;
        }
    }
}
