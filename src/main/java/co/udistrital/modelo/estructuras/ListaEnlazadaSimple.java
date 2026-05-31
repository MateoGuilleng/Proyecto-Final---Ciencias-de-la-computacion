package co.udistrital.modelo.estructuras;


import co.udistrital.modelo.estructuras.Nodo;
import java.util.Objects;
import java.util.NoSuchElementException;

/**
 * Implementación de una lista enlazada simple genérica.
 * Permite almacenar una secuencia ordenada de elementos de tipo {@code T}.
 * Soporta operaciones básicas de inserción, eliminación, búsqueda y clonación.
 * Mantiene referencias a la cabeza y la cola para optimizar algunas operaciones.
 * Puede ser ordenada usando el patrón Strategy si los elementos son {@link Comparable}.
 */
public class ListaEnlazadaSimple<T> {

    /** Referencia al primer nodo de la lista (cabeza). {@code null} si la lista está vacía. */
    private Nodo<T> cabeza;
    /** Referencia al último nodo de la lista (cola). {@code null} si la lista está vacía. Optimiza inserción al final. */
    private Nodo<T> cola;
    /** Número actual de elementos en la lista. */
    private int tamanno;

    /**
     * Construye una lista enlazada simple vacía.
     * La cabeza, la cola y el tamaño se inicializan adecuadamente.
     */
    public ListaEnlazadaSimple() {
        this.cabeza = null;
        this.cola = null;
        this.tamanno = 0;
    }

    // --- Información Básica y Acceso Interno ---

    /**
     * Comprueba si la lista no contiene elementos.
     * @return {@code true} si el tamaño es 0, {@code false} en caso contrario.
     */
    public boolean estaVacia() {
        return this.tamanno == 0;
    }

    /**
     * Devuelve el número de elementos actualmente en la lista.
     * @return El tamaño (entero no negativo).
     */
    public int getTamanno() {
        return this.tamanno;
    }

    /**
     * Obtiene el nodo cabeza de la lista.
     * Utilizado internamente por algunas estrategias de ordenación.
     * ¡Precaución! Modificar el nodo devuelto externamente puede romper la lista.
     * @return El primer {@link Nodo}, o {@code null} si la lista está vacía.
     */
    public Nodo<T> getCabeza() {
        return this.cabeza;
    }

    /**
     * Establece el nodo cabeza de la lista.
     * Utilizado internamente por estrategias de ordenación que reestructuran la lista.
     * Este método también se encarga de actualizar la referencia a la cola.
     * @param cabeza El nuevo nodo que será la cabeza de la lista.
     */
    public void setCabeza(Nodo<T> cabeza) {
        this.cabeza = cabeza;
        // Si la nueva cabeza es null, la lista está vacía, cola también es null.
        if (this.cabeza == null) {
            this.cola = null;
        } else {
            // Si no es null, encontrar la nueva cola recorriendo desde la nueva cabeza.
            Nodo<T> actual = this.cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            this.cola = actual; // El último nodo encontrado es la nueva cola.
        }
        // Nota: El tamaño no se modifica aquí, se asume que la estrategia
        // de ordenación no cambia el número de elementos.
    }

    // --- Métodos de Inserción ---

    /**
     * Inserta un elemento al principio de la lista (nueva cabeza).
     * Operación de tiempo constante O(1).
     * @param dato El dato a insertar.
     */
    public void insertarAlInicio(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato, this.cabeza);
        this.cabeza = nuevoNodo;
        if (this.cola == null) { // Si la lista estaba vacía
            this.cola = this.cabeza; // El nuevo nodo es también la cola
        }
        this.tamanno++;
    }

    /**
     * Inserta un elemento al final de la lista (nueva cola).
     * Operación de tiempo constante O(1) gracias a la referencia {@code cola}.
     * @param dato El dato a insertar.
     */
    public void insertarAlFinal(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (estaVacia()) {
            this.cabeza = nuevoNodo;
            this.cola = nuevoNodo;
        } else {
            this.cola.setSiguiente(nuevoNodo); // Enlazar desde la antigua cola
            this.cola = nuevoNodo;            // Actualizar la referencia de la cola
        }
        this.tamanno++;
    }

    /**
     * Alias conveniente para {@link #insertarAlFinal(Object)}.
     * Agrega un elemento al final de la lista.
     * @param dato El dato a agregar.
     */
    public void agregar(T dato) {
        insertarAlFinal(dato);
    }

    /**
     * Inserta un nuevo elemento {@code datoNuevo} inmediatamente después de la primera
     * ocurrencia del nodo que contiene {@code datoExistente}.
     * Si {@code datoExistente} no se encuentra, la lista no se modifica.
     * La búsqueda es O(n), la inserción es O(1) una vez encontrado el nodo.
     *
     * @param datoExistente El dato del nodo después del cual se realizará la inserción. Se compara usando {@code equals()}.
     * @param datoNuevo El dato del nuevo nodo a insertar.
     * @return {@code true} si la inserción se realizó con éxito, {@code false} si {@code datoExistente} no fue encontrado.
     */
    public boolean insertarDespuesDe(T datoExistente, T datoNuevo) {
        Nodo<T> nodoExistente = buscarNodo(datoExistente);
        if (nodoExistente == null) {
            return false; // No se encontró el nodo de referencia
        }

        Nodo<T> nuevoNodo = new Nodo<>(datoNuevo, nodoExistente.getSiguiente());
        nodoExistente.setSiguiente(nuevoNodo);

        // Si se insertó después de la que era la cola, el nuevo nodo es la nueva cola.
        if (nodoExistente == this.cola) {
            this.cola = nuevoNodo;
        }
        this.tamanno++;
        return true;
    }

    /**
     * Inserta un nuevo elemento {@code datoNuevo} inmediatamente antes de la primera
     * ocurrencia del nodo que contiene {@code datoExistente}.
     * Si {@code datoExistente} es la cabeza, equivale a {@link #insertarAlInicio(Object)}.
     * Si {@code datoExistente} no se encuentra (y no es la cabeza), la lista no se modifica.
     * La búsqueda es O(n), la inserción es O(1).
     *
     * @param datoExistente El dato del nodo antes del cual se realizará la inserción. Se compara usando {@code equals()}.
     * @param datoNuevo El dato del nuevo nodo a insertar.
     * @return {@code true} si la inserción se realizó con éxito, {@code false} si {@code datoExistente} no fue encontrado (y no era la cabeza).
     */
    public boolean insertarAntesDe(T datoExistente, T datoNuevo) {
        if (estaVacia()) {
            return false; // No se puede insertar antes de nada en lista vacía
        }

        // Caso especial: insertar antes de la cabeza
        if (Objects.equals(this.cabeza.getDato(), datoExistente)) {
            insertarAlInicio(datoNuevo);
            return true;
        }

        // Buscar el nodo *anterior* al que contiene datoExistente
        Nodo<T> nodoAnterior = buscarNodoAnterior(datoExistente);
        if (nodoAnterior == null) {
            // No se encontró el nodo anterior (datoExistente no existe o es la cabeza ya tratada)
            return false;
        }

        // Insertar el nuevo nodo entre nodoAnterior y su siguiente original
        Nodo<T> nuevoNodo = new Nodo<>(datoNuevo, nodoAnterior.getSiguiente());
        nodoAnterior.setSiguiente(nuevoNodo);
        this.tamanno++;
        return true;
    }

    // --- Métodos de Eliminación ---

    /**
     * Elimina y devuelve el elemento que se encuentra al principio de la lista (la cabeza).
     * Operación de tiempo constante O(1).
     *
     * @return El dato del elemento eliminado.
     * @throws NoSuchElementException si la lista está vacía.
     */
    public T eliminarAlInicio() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista está vacía, no se puede eliminar al inicio.");
        }
        T datoEliminado = this.cabeza.getDato();
        this.cabeza = this.cabeza.getSiguiente();
        this.tamanno--;
        // Si la lista quedó vacía después de eliminar
        if (estaVacia()) {
            this.cola = null; // La cola también debe ser null
        }
        return datoEliminado;
    }

    /**
     * Elimina y devuelve el elemento que se encuentra al final de la lista (la cola).
     * Requiere recorrer la lista para encontrar el penúltimo nodo, por lo que es O(n).
     *
     * @return El dato del elemento eliminado.
     * @throws NoSuchElementException si la lista está vacía.
     */
    public T eliminarAlFinal() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista está vacía, no se puede eliminar al final.");
        }
        // Si solo hay un elemento, es lo mismo que eliminar al inicio
        if (this.tamanno == 1) {
            return eliminarAlInicio();
        }

        // Encontrar el penúltimo nodo
        Nodo<T> penultimo = this.cabeza;
        // Avanza hasta que el *siguiente* del actual sea la cola
        while (penultimo.getSiguiente() != this.cola) {
            penultimo = penultimo.getSiguiente();
        }

        T datoEliminado = this.cola.getDato();
        penultimo.setSiguiente(null); // El penúltimo ahora es el último
        this.cola = penultimo;       // Actualizar la referencia de la cola
        this.tamanno--;
        return datoEliminado;
    }

    /**
     * Elimina la primera ocurrencia del elemento especificado {@code dato} de la lista.
     * Utiliza {@code equals()} para la comparación. La búsqueda es O(n).
     *
     * @param dato El dato del elemento a eliminar.
     * @return {@code true} si el elemento fue encontrado y eliminado, {@code false} en caso contrario.
     */
    public boolean eliminar(T dato) {
        if (estaVacia()) {
            return false;
        }

        // Caso 1: El dato a eliminar está en la cabeza
        if (Objects.equals(this.cabeza.getDato(), dato)) {
            eliminarAlInicio(); // Reutiliza el método que maneja cabeza y cola
            return true;
        }

        // Caso 2: El dato está en otra parte de la lista. Buscar el nodo anterior.
        Nodo<T> nodoAnterior = buscarNodoAnterior(dato);
        if (nodoAnterior == null) {
            // No se encontró el nodo anterior (el dato no existe o era la cabeza ya tratada)
            return false;
        }

        // Si se encontró el nodo anterior, su siguiente es el que hay que eliminar
        Nodo<T> nodoAEliminar = nodoAnterior.getSiguiente();
        // Hacer que nodoAnterior salte sobre nodoAEliminar
        nodoAnterior.setSiguiente(nodoAEliminar.getSiguiente());

        // Importante: si el nodo a eliminar era la cola, actualizar la cola
        if (nodoAEliminar == this.cola) {
            this.cola = nodoAnterior;
        }

        this.tamanno--;
        // nodoAEliminar queda desconectado y será eliminado por el GC.
        return true;
    }

    /**
     * Elimina y devuelve el elemento que se encuentra inmediatamente después de la primera
     * ocurrencia del nodo que contiene {@code datoExistente}.
     * Si {@code datoExistente} no se encuentra, o si es la cola, no se elimina nada.
     * La búsqueda es O(n).
     *
     * @param datoExistente El dato del nodo después del cual se eliminará. Se compara usando {@code equals()}.
     * @return El dato del nodo eliminado, o {@code null} si no se pudo realizar la eliminación.
     */
    public T eliminarDespuesDe(T datoExistente) {
        Nodo<T> nodoExistente = buscarNodo(datoExistente);

        // No se puede eliminar si no se encuentra, o si es la cola (no hay nada después)
        if (nodoExistente == null || nodoExistente == this.cola) {
            return null;
        }

        Nodo<T> nodoAEliminar = nodoExistente.getSiguiente();
        T datoEliminado = nodoAEliminar.getDato();

        // Hacer que nodoExistente salte sobre nodoAEliminar
        nodoExistente.setSiguiente(nodoAEliminar.getSiguiente());

        // Si el nodo eliminado era la cola, el nodoExistente es la nueva cola
        if (nodoAEliminar == this.cola) {
            this.cola = nodoExistente;
        }

        this.tamanno--;
        return datoEliminado;
    }

    /**
     * Elimina y devuelve el elemento que se encuentra inmediatamente antes de la primera
     * ocurrencia del nodo que contiene {@code datoExistente}.
     * No se puede eliminar antes de la cabeza. Requiere al menos dos nodos en la lista
     * para poder eliminar algo "antes" de un nodo que no sea la cabeza.
     * La búsqueda es O(n).
     *
     * @param datoExistente El dato del nodo antes del cual se eliminará. Se compara usando {@code equals()}.
     * @return El dato del nodo eliminado, o {@code null} si no se pudo realizar la eliminación.
     */
    public T eliminarAntesDe(T datoExistente) {
        // Casos borde: imposible eliminar antes si hay 0 o 1 nodo, o si datoExistente es la cabeza.
        if (estaVacia() || this.tamanno == 1 || Objects.equals(this.cabeza.getDato(), datoExistente)) {
            return null;
        }

        // Caso: eliminar la cabeza porque datoExistente está en el segundo nodo
        if (this.cabeza.getSiguiente() != null && Objects.equals(this.cabeza.getSiguiente().getDato(), datoExistente)) {
            return eliminarAlInicio();
        }

        // Buscar el nodo X tal que: X -> (nodo_a_eliminar) -> (nodo_con_datoExistente)
        Nodo<T> nodoAnteriorAlPrevio = this.cabeza;
        while (nodoAnteriorAlPrevio.getSiguiente() != null && nodoAnteriorAlPrevio.getSiguiente().getSiguiente() != null) {
            // Comprobar si el nodo 2 posiciones adelante contiene datoExistente
            if (Objects.equals(nodoAnteriorAlPrevio.getSiguiente().getSiguiente().getDato(), datoExistente)) {
                // Encontrado! El nodo a eliminar es nodoAnteriorAlPrevio.getSiguiente()
                Nodo<T> nodoAEliminar = nodoAnteriorAlPrevio.getSiguiente();
                T datoEliminado = nodoAEliminar.getDato();
                // Hacer que nodoAnteriorAlPrevio salte sobre nodoAEliminar
                nodoAnteriorAlPrevio.setSiguiente(nodoAEliminar.getSiguiente());
                this.tamanno--;
                // La cola no puede ser el nodo eliminado en este caso, no hace falta verificarla.
                return datoEliminado;
            }
            nodoAnteriorAlPrevio = nodoAnteriorAlPrevio.getSiguiente(); // Avanzar
        }

        // Si el bucle termina, no se encontró la secuencia X -> Y -> datoExistente
        return null;
    }

    // --- Otras Utilidades ---

    /**
     * Elimina todos los elementos de la lista, dejándola en su estado inicial (vacía).
     * Las referencias a cabeza y cola se anulan, y el tamaño se pone a 0.
     * Los nodos anteriores serán elegibles para recolección de basura.
     */
    public void borrarLista() {
        this.cabeza = null;
        this.cola = null;
        this.tamanno = 0;
    }

    /**
     * Crea y devuelve una copia superficial (shallow copy) de esta lista.
     * Se crean nuevos nodos, pero estos contienen referencias a los mismos
     * objetos de datos que la lista original. Las modificaciones a los objetos
     * de datos se reflejarán en ambas listas. Modificaciones a la estructura
     * de una lista (añadir/quitar nodos) no afectan a la otra.
     *
     * @return Una nueva instancia de {@code ListaEnlazadaSimple} con los mismos datos.
     */
    public ListaEnlazadaSimple<T> clonarLista() {
        ListaEnlazadaSimple<T> clon = new ListaEnlazadaSimple<>();
        Nodo<T> actual = this.cabeza;
        while (actual != null) {
            clon.insertarAlFinal(actual.getDato()); // Más eficiente usar add/insertarAlFinal
            actual = actual.getSiguiente();
        }
        return clon;
    }

    /**
     * Imprime una representación textual de la lista en la consola estándar.
     * Muestra los elementos desde la cabeza hasta la cola, separados por " -> ",
     * indicando la cabeza, el final (NULL) y el dato de la cola.
     */
    public void imprimir() {
        if (estaVacia()) {
            System.out.println("Lista Simple Vacía");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("HEAD -> ");
        Nodo<T> actual = this.cabeza;
        while (actual != null) {
            sb.append(actual); // Usa Nodo.toString() -> dato.toString()
            actual = actual.getSiguiente();
            if (actual != null) {
                sb.append(" -> ");
            } else {
                sb.append(" -> NULL");
            }
        }
        // Añadir información de la cola para depuración/verificación
        sb.append(" (TAIL: ").append(this.cola != null ? this.cola : "null").append(")");
        System.out.println(sb.toString());
    }

    // --- Búsqueda ---

    /**
     * Comprueba si la lista contiene al menos una ocurrencia del elemento especificado.
     * Utiliza {@code equals()} para la comparación. La búsqueda es O(n).
     *
     * @param dato El dato a buscar en la lista.
     * @return {@code true} si el dato se encuentra en la lista, {@code false} en caso contrario.
     */
    public boolean contiene(T dato) {
        return buscarNodo(dato) != null;
    }

    // --- Iterador ---

    /**
     * Devuelve un iterador simple para recorrer los elementos de la lista desde la cabeza hasta la cola.
     * Este iterador no soporta la operación {@code remove()}.
     *
     * @return Una instancia de {@link Iterador} para esta lista.
     */
    public Iterador<T> iterador() {
        return new Iterador<>(this.cabeza);
    }

    /**
     * Implementación de un iterador básico para {@link ListaEnlazadaSimple}.
     * Permite recorrer la lista secuencialmente usando los métodos {@code hasNext()} y {@code next()}.
     * No implementa {@code remove()}. Es una clase estática anidada.
     *
     * @param <I> El tipo de elementos sobre los que itera (debe coincidir con T de la lista).
     */
    public static class Iterador<I> {
        /** El nodo actual al que apunta el iterador durante el recorrido. */
        private Nodo<I> actual;

        /**
         * Construye un iterador comenzando desde el nodo cabeza proporcionado.
         * @param cabeza El primer nodo de la secuencia a iterar.
         */
        public Iterador(Nodo<I> cabeza) {
            this.actual = cabeza;
        }

        /**
         * Comprueba si quedan más elementos por iterar en la secuencia.
         * @return {@code true} si {@code actual} no es {@code null}, {@code false} en caso contrario.
         */
        public boolean tieneSiguiente() {
            return actual != null;
        }

        /**
         * Devuelve el dato del nodo actual y avanza el iterador al siguiente nodo.
         *
         * @return El dato del nodo actual.
         * @throws NoSuchElementException si se llama cuando {@link #tieneSiguiente()} es {@code false}.
         */
        public I siguiente() {
            if (!tieneSiguiente()) {
                throw new NoSuchElementException("No hay más elementos en la iteración.");
            }
            I dato = actual.getDato();
            actual = actual.getSiguiente(); // Avanzar
            return dato;
        }
    }

    // --- Métodos Auxiliares Privados ---

    /**
     * Busca el primer nodo en la lista que contiene el {@code datoBusqueda}.
     * Utiliza {@code Objects.equals()} para manejar {@code null} de forma segura.
     *
     * @param datoBusqueda El dato a buscar.
     * @return El {@link Nodo} que contiene el dato, o {@code null} si no se encuentra.
     */
    private Nodo<T> buscarNodo(T datoBusqueda) {
        Nodo<T> actual = this.cabeza;
        while (actual != null) {
            if (Objects.equals(actual.getDato(), datoBusqueda)) {
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null; // No encontrado
    }

    /**
     * Busca el nodo que se encuentra inmediatamente *antes* del primer nodo
     * que contiene {@code datoBusqueda}.
     * Utiliza {@code Objects.equals()} para manejar {@code null} de forma segura.
     *
     * @param datoBusqueda El dato contenido en el nodo objetivo (el nodo *después* del que buscamos).
     * @return El nodo predecesor al nodo que contiene {@code datoBusqueda},
     *         o {@code null} si {@code datoBusqueda} está en la cabeza, no se encuentra, o la lista tiene menos de 2 elementos.
     */
    private Nodo<T> buscarNodoAnterior(T datoBusqueda) {
        // No hay nodo anterior si la lista es vacía, tiene 1 elemento, o si el dato está en la cabeza.
        if (estaVacia() || this.tamanno == 1 || Objects.equals(this.cabeza.getDato(), datoBusqueda)) {
            return null;
        }

        Nodo<T> actual = this.cabeza;
        // Recorre mientras haya un nodo siguiente para comprobar
        while (actual.getSiguiente() != null) {
            // Comprueba si el *siguiente* nodo contiene el dato buscado
            if (Objects.equals(actual.getSiguiente().getDato(), datoBusqueda)) {
                return actual; // 'actual' es el nodo anterior al que contiene el dato
            }
            actual = actual.getSiguiente(); // Avanza
        }
        return null; // No se encontró el dato en ningún nodo (excepto quizás la cabeza)
    }
}