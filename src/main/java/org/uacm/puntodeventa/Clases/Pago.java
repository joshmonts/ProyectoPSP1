package org.uacm.puntodeventa.Clases;

/**
 * Representa un pago realizado durante una venta.
 * Permite calcular el cambio cuando el método de
 * pago es efectivo.
 *
 * @author Jose
 */
public class Pago {

    /**
     * Métodos de pago disponibles.
     */
    public enum Metodo {
        EFECTIVO,
        TARJETA,
        TRANSFERENCIA,
        SALDO
    }

    private Metodo metodo;
    private double recibido;

    /**
     * Constructor de la clase Pago.
     *
     * @param metodo método de pago seleccionado
     * @param recibido cantidad recibida
     */
    public Pago(Metodo metodo, double recibido) {
        this.metodo = metodo;
        this.recibido = recibido;
    }

    /**
     * Calcula el cambio de una compra.
     *
     * @param subtotal total de la compra
     * @return cambio correspondiente
     */
    public double calcularCambio(double subtotal) {

        return (metodo == Metodo.EFECTIVO)
                ? recibido - subtotal
                : 0;
    }

    /**
     * Obtiene el método de pago.
     *
     * @return método seleccionado
     */
    public Metodo getMetodo() {
        return metodo;
    }

    /**
     * Establece el método de pago.
     *
     * @param metodo nuevo método de pago
     */
    public void setMetodo(Metodo metodo) {
        this.metodo = metodo;
    }

    /**
     * Obtiene la cantidad recibida.
     *
     * @return monto recibido
     */
    public double getRecibido() {
        return recibido;
    }

    /**
     * Establece la cantidad recibida.
     *
     * @param recibido nuevo monto recibido
     */
    public void setRecibido(double recibido) {
        this.recibido = recibido;
    }
}