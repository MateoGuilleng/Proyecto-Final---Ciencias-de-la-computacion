/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.udistrital.modelo.estructuras;

public class Cola<T> {

    private Nodo<T> frente;
    private Nodo<T> fondo;

    public Cola() {
        this.frente = null;
        this.fondo = null;
    }

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
