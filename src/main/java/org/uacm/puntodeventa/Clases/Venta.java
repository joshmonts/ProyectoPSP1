package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una venta.
 *
 * @author Jose
 */
public class Venta {

    private String folio;
    private LocalDate fechaVenta;
    private List<Producto> listaProductos;
    private double total;

    /**
     * Constructor para una venta.
     */
    public Venta() {
        this.fechaVenta = LocalDate.now();
        this.listaProductos = new ArrayList<>();
        this.total = 0;
    }

    /**
     * Calcula el total a pagar.
     *
     * @param lista lista de productos
     * @return total de la venta
     */
    public double totalPagar(List<Producto> lista) {
        double suma = 0;

        for (Producto producto : lista) {
            suma += producto.getPrecioVenta();
        }

        total = suma;
        return suma;
    }

    /**
     * Convierte los productos en texto.
     *
     * @param lista lista de productos
     * @return productos en formato String
     */
    public String mostrarProductos(List<Producto> lista) {

        StringBuilder sb = new StringBuilder();

        for (Producto producto : lista) {
            sb.append(producto.getNombre())
              .append(" --- $")
              .append(producto.getPrecioVenta())
              .append("\n");
        }

        return sb.toString();
    }

    /**
     * Genera el ticket de venta.
     *
     * @return ticket formateado
     */
    public String generarTicket() {

        StringBuilder ticket = new StringBuilder();

        ticket.append("\n*************** TICKET VENTA ***************");
        ticket.append("\nFolio: ").append(folio);
        ticket.append("\nFecha: ").append(fechaVenta);
        ticket.append("\n--------------- PRODUCTOS -----------------");
        ticket.append("\n").append(mostrarProductos(listaProductos));
        ticket.append("\nTOTAL A PAGAR: $").append(total);
        ticket.append("\n--------------------------------------------");

        return ticket.toString();
    }

    /**
     * Agrega productos a la venta.
     *
     * @param producto producto a vender
     * @param cantidad cantidad solicitada
     * @return true si se agregó correctamente
     */
    public boolean agregarProducto(Producto producto, int cantidad) {

        if (producto.reducirStock(cantidad)) {

            for (int i = 0; i < cantidad; i++) {
                listaProductos.add(producto);
            }

            totalPagar(listaProductos);
            return true;
        }

        return false;
    }

    public String getFolio() {
        return folio;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public double getTotal() {
        return total;
    }
}