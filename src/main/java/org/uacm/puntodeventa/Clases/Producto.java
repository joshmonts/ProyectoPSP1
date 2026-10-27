package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;

/**
 * Clase que representa un producto.
 *
 * @author Jose
 */
public class Producto {

    private static final double PORCENTAJE_GANANCIA = 0.20;

    private int idProducto;
    private String nombre;
    private String tipo;
    private int stock;
    private double precioProveedor;
    private double precioVenta;
    private String rutaImagen;
    private LocalDate fechaCaducidad;

    /**
     * Constructor para productos sin fecha de caducidad.
     */
    public Producto(int id,
                    String nombre,
                    String tipo,
                    int stock,
                    double precioProveedor) {

        this.idProducto = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.stock = stock;
        this.precioProveedor = precioProveedor;
        this.precioVenta = calcularPrecioVenta(precioProveedor);
        this.rutaImagen = null;
        this.fechaCaducidad = null;
    }

    /**
     * Constructor para productos perecederos.
     */
    public Producto(int id,
                    String nombre,
                    String tipo,
                    int stock,
                    double precioProveedor,
                    String rutaImagen,
                    LocalDate fechaCaducidad) {

        this(id, nombre, tipo, stock, precioProveedor);
        this.rutaImagen = rutaImagen;
        this.fechaCaducidad = fechaCaducidad;
    }

    /**
     * Calcula el precio de venta.
     *
     * @param precio precio proveedor
     * @return precio de venta
     */
    public double calcularPrecioVenta(double precio) {
        return Math.ceil(precio + (precio * PORCENTAJE_GANANCIA));
    }

    /**
     * Reduce el stock disponible.
     *
     * @param cantidad cantidad a descontar
     * @return true si existe stock suficiente
     */
    public boolean reducirStock(int cantidad) {

        if (stock >= cantidad) {
            stock -= cantidad;
            return true;
        }

        return false;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void agregarStock(int cantidad) {
        this.stock += cantidad;
    }

    public double getPrecioProveedor() {
        return precioProveedor;
    }

    public void setPrecioProveedor(double precioProveedor) {
        this.precioProveedor = precioProveedor;
        this.precioVenta = calcularPrecioVenta(precioProveedor);
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    /**
     * Muestra la información del producto.
     *
     * @return información formateada
     */
    public String mostrarDetalles() {

        StringBuilder datos = new StringBuilder();

        datos.append("\nID: ").append(idProducto);
        datos.append("\nNombre: ").append(nombre);
        datos.append("\nTipo: ").append(tipo);
        datos.append("\nStock: ").append(stock);
        datos.append("\nPrecio proveedor: ").append(precioProveedor);
        datos.append("\nPrecio venta: ").append(precioVenta);

        if (fechaCaducidad != null) {
            datos.append("\nCaducidad: ").append(fechaCaducidad);
        }

        return datos.toString();
    }
}