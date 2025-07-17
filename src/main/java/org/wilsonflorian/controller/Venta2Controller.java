package org.wilsonflorian.controller;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.wilsonflorian.database.Conexion;
import org.wilsonflorian.system.Main;

public class Venta2Controller implements Initializable {

    @FXML private TextField txtSubtotal;
    @FXML private TextField txtTotal;
    @FXML private Button btnPagar;
    @FXML private Button btnCancelar;
    
    private Main principal;
    private int idCompraActual;
    private double subtotal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCampos();
    }

    private void configurarCampos() {
        txtSubtotal.setEditable(false);
        txtTotal.setEditable(false);
    }

    public void initData(int idCompra, double subtotal) {
        this.idCompraActual = idCompra;
        this.subtotal = subtotal;
        mostrarDatos();
    }

    private void mostrarDatos() {
        txtSubtotal.setText(String.format("Q%.2f", subtotal));
        double total = subtotal * 1.12; // Aplicar IVA del 12%
        txtTotal.setText(String.format("Q%.2f", total));
    }

    @FXML
    private void completarCompra() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement procedimiento = conexion.prepareCall(
                "{call sp_actualizarCompra(?, 'Completada', 'Pagado')}");
            procedimiento.setInt(1, idCompraActual);
            procedimiento.execute();
            
            mostrarMensaje("Compra completada", "La compra se ha completado exitosamente");
            principal.getVentaView();
        } catch (SQLException e) {
            mostrarAlerta("Error al completar compra: " + e.getMessage());
        }
    }

    @FXML
    private void cancelarPedido() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement procedimiento = conexion.prepareCall(
                "{call sp_actualizarCompra(?, 'Cancelada', 'Pendiente')}");
            procedimiento.setInt(1, idCompraActual);
            procedimiento.execute();
            
            mostrarMensaje("Pedido cancelado", "El pedido ha sido cancelado exitosamente");
            principal.getVentaView();
        } catch (SQLException e) {
            mostrarAlerta("Error al cancelar pedido: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}