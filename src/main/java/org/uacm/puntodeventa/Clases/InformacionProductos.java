package org.uacm.puntodeventa.Clases;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Gestiona el catálogo de productos del sistema.
 * Implementa el patrón Singleton para mantener
 * una única instancia de la información.
 *
 * @author Jose
 */
public class InformacionProductos {

    private static InformacionProductos instancia;

    private ObservableList<Producto> productos;

    /**
     * Constructor privado.
     */
    private InformacionProductos() {

        this.productos = FXCollections.observableArrayList();

        this.productos.addAll(
            new Producto(1, "Agua", "Normal", 5, 20),
            new Producto(2, "Cereal", "Normal", 15, 29),
            new Producto(3, "Caja de donas", "Normal", 50, 200),
            new Producto(4, "Coca Cola", "Normal", 30, 20)
                
        );
    }

    /**
     * Obtiene la única instancia de la clase.
     *
     * @return instancia de InformacionProductos
     */
    public static InformacionProductos getInstance() {

        if (instancia == null) {
            instancia = new InformacionProductos();
        }

        return instancia;
    }

    /**
     * Devuelve la lista observable de productos.
     *
     * @return catálogo de productos
     */
    public ObservableList<Producto> getProductos() {
        return productos;
    }
}