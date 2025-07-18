
package org.wilsonflorian.model;

import java.time.LocalDateTime;

/**
 *
 * @author Wilson Florian
 */
public class Compra {
    private int idCompra;
    private String estadoCompra;
    private String estadoPago;
    private LocalDateTime fechaCompra;

    public Compra() {
    }

    public Compra(int idCompra, String estadoCompra, String estadoPago, LocalDateTime fechaCompra) {
        this.idCompra = idCompra;
        this.estadoCompra = estadoCompra;
        this.estadoPago = estadoPago;
        this.fechaCompra = fechaCompra;
    }

    public Compra(String estadoCompra, String estadoPago, LocalDateTime fechaCompra) {
        this.estadoCompra = estadoCompra;
        this.estadoPago = estadoPago;
        this.fechaCompra = fechaCompra;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public String getEstadoCompra() {
        return estadoCompra;
    }

    public void setEstadoCompra(String estadoCompra) {
        this.estadoCompra = estadoCompra;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
    
    
}
