
package org.wilsonflorian.model;

import java.time.LocalDateTime;

/**
 *
 * @author Wilson Florian
 */
public class Factura {
    
    private int idFactura;
    private LocalDateTime fecha;
    private double total;
    private int idCompra;

    public Factura() {
    }

    public Factura(int idFactura, LocalDateTime fecha, double total, int idCompra) {
        this.idFactura = idFactura;
        this.fecha = fecha;
        this.total = total;
        this.idCompra = idCompra;
    }

    public Factura(LocalDateTime fecha, double total, int idCompra) {
        this.fecha = fecha;
        this.total = total;
        this.idCompra = idCompra;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }
}
