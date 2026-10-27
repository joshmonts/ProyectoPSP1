package org.uacm.puntodeventa.Clases;

/**
 * Representa un producto agregado al carrito
 * de compras junto con la cantidad seleccionada.
 *
 * @author Jose
 */
public class ProductoCarrito {

    private final int id;
    private final String nombre;
    private final double precio;
    private int cantidad;

    /**
     * Constructor de la clase.
     *
     * @param producto producto agregado al carrito
     * @param cantidad cantidad seleccionada
     */
    public ProductoCarrito(Producto producto, int cantidad) {

        this.id = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecioVenta();
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el identificador del producto.
     *
     * @return identificador
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el precio unitario.
     *
     * @return precio
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Obtiene la cantidad seleccionada.
     *
     * @return cantidad
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Actualiza la cantidad.
     *
     * @param cantidad nueva cantidad
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Calcula el total del producto en el carrito.
     *
     * @return subtotal del producto
     */
    public double getTotal() {
        return precio * cantidad;
    }
}