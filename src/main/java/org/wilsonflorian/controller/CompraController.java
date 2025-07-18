package org.wilsonflorian.controller;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.wilsonflorian.database.Conexion;
import org.wilsonflorian.model.Compra;
import org.wilsonflorian.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class CompraController implements Initializable {

    @FXML
    private Button btnRegresar, btnAgregar, btnEditar, btnEliminar, btnReporte;

    @FXML
    private TableView<Compra> tablaCompras;

    @FXML
    private TableColumn<Compra, Integer> colIdCompra;
    @FXML
    private TableColumn<Compra, String> colEstadoCompra;
    @FXML
    private TableColumn<Compra, String> colEstadoPago;
    @FXML
    private TableColumn<Compra, LocalDateTime> colFechaCompra;
    
    @FXML
    private TextField txtId;
    @FXML
    private DatePicker dpFechaCompra;
    
    @FXML
    private RadioButton rbPendienteCompra, rbCompletada, rbCancelada;
    @FXML
    private RadioButton rbPendientePago, rbPagado;
    
    @FXML
    private ToggleGroup estadoCompraGroup, estadoPagoGroup;
    
    @FXML
    private HBox hbEstadoCompra, hbEstadoPago;

    private ObservableList<Compra> listaCompras;
    private Main principal;
    private Compra modeloCompra;

    private enum Acciones {
        Agregar, Editar, Eliminar, Ninguna
    };
    private Acciones accion = Acciones.Ninguna;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarDatos();
        tablaCompras.setOnMouseClicked(event -> {
            getCompraTextFiel();
            deshabilitarControles();
        });
        deshabilitarControles();
    }

    public void setFormatoColumnaModelo() {
        colIdCompra.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colEstadoCompra.setCellValueFactory(new PropertyValueFactory<>("estadoCompra"));
        colEstadoPago.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
        colFechaCompra.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
    }

    public void cargarDatos() {
        listaCompras = FXCollections.observableArrayList(listarCompras());
        tablaCompras.setItems(listaCompras);
        if (!listaCompras.isEmpty()) {
            tablaCompras.getSelectionModel().selectFirst();
            getCompraTextFiel();
        }
    }

    public void getCompraTextFiel() {
        Compra compraSeleccionada = tablaCompras.getSelectionModel().getSelectedItem();
        if (compraSeleccionada != null) {
            txtId.setText(String.valueOf(compraSeleccionada.getIdCompra()));
            
            // Configurar RadioButtons para estadoCompra
            switch (compraSeleccionada.getEstadoCompra()) {
                case "Pendiente":
                    rbPendienteCompra.setSelected(true);
                    break;
                case "Completada":
                    rbCompletada.setSelected(true);
                    break;
                case "Cancelada":
                    rbCancelada.setSelected(true);
                    break;
            }
            
            // Configurar RadioButtons para estadoPago
            if (compraSeleccionada.getEstadoPago().equals("Pendiente")) {
                rbPendientePago.setSelected(true);
            } else {
                rbPagado.setSelected(true);
            }
            
            // Configurar DatePicker con la fecha de LocalDateTime
            dpFechaCompra.setValue(compraSeleccionada.getFechaCompra().toLocalDate());
        }
    }

    public ArrayList<Compra> listarCompras() {
        ArrayList<Compra> compras = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT idCompra, estadoCompra, estadoPago, fechaCompra FROM Compras";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Convertir java.sql.Timestamp a LocalDateTime
                Timestamp timestamp = rs.getTimestamp("fechaCompra");
                LocalDateTime fechaCompra = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
                
                compras.add(new Compra(
                        rs.getInt("idCompra"),
                        rs.getString("estadoCompra"),
                        rs.getString("estadoPago"),
                        fechaCompra
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar compras: " + e.getMessage());
        }
        return compras;
    }

    private Compra getModeloCompra() {
        int idCompra = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());
        
        String estadoCompra = "";
        if (rbPendienteCompra.isSelected()) {
            estadoCompra = "Pendiente";
        } else if (rbCompletada.isSelected()) {
            estadoCompra = "Completada";
        } else if (rbCancelada.isSelected()) {
            estadoCompra = "Cancelada";
        }
        
        String estadoPago = rbPendientePago.isSelected() ? "Pendiente" : "Pagado";
        
        // Convertir LocalDate a LocalDateTime (asumiendo hora 00:00)
        LocalDateTime fechaCompra = dpFechaCompra.getValue() != null 
                ? dpFechaCompra.getValue().atStartOfDay() 
                : LocalDateTime.now();

        return new Compra(idCompra, estadoCompra, estadoPago, fechaCompra);
    }

    public void agregarCompra() {
        if (validarCampos()) {
            modeloCompra = getModeloCompra();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_agregarCompra(?, ?, ?)}");
                stmt.setString(1, modeloCompra.getEstadoCompra());
                stmt.setString(2, modeloCompra.getEstadoPago());
                // Convertir LocalDateTime a Timestamp para la base de datos
                stmt.setTimestamp(3, Timestamp.valueOf(modeloCompra.getFechaCompra()));
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al agregar compra: " + e.getMessage());
            }
        }
    }

    public void editarCompra() {
        if (validarCampos()) {
            modeloCompra = getModeloCompra();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_actualizarCompra(?, ?, ?, ?)}");
                stmt.setInt(1, modeloCompra.getIdCompra());
                stmt.setString(2, modeloCompra.getEstadoCompra());
                stmt.setString(3, modeloCompra.getEstadoPago());
                // Convertir LocalDateTime a Timestamp para la base de datos
                stmt.setTimestamp(4, Timestamp.valueOf(modeloCompra.getFechaCompra()));
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al editar compra: " + e.getMessage());
            }
        }
    }

    public void eliminarCompra() {
        modeloCompra = getModeloCompra();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                    .prepareCall("{call sp_eliminarCompra(?)}");
            stmt.setInt(1, modeloCompra.getIdCompra());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarAlerta("Error al eliminar compra: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if ((!rbPendienteCompra.isSelected() && !rbCompletada.isSelected() && !rbCancelada.isSelected())) {
            mostrarAlerta("Debe seleccionar un estado de compra");
            return false;
        }
        
        if ((!rbPendientePago.isSelected() && !rbPagado.isSelected())) {
            mostrarAlerta("Debe seleccionar un estado de pago");
            return false;
        }
        
        if (dpFechaCompra.getValue() == null) {
            mostrarAlerta("Debe seleccionar una fecha");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void limpiarTexto() {
        txtId.clear();
        rbPendienteCompra.setSelected(true);
        rbPendientePago.setSelected(true);
        dpFechaCompra.setValue(LocalDate.now());
    }

    public void btnRegresarActionEvent(ActionEvent evento) {
        principal.getMenuView();
    }

    @FXML
    private void cambiarNuevoCompra() {
        switch (accion) {
            case Ninguna:
                cambiarGuardarEditar();
                accion = Acciones.Agregar;
                limpiarTexto();
                habilitarControles(true);
                break;
            case Agregar:
                agregarCompra();
                break;
            case Editar:
                editarCompra();
                break;
        }
    }

    @FXML
    private void cambiarEdicionCompra() {
        cambiarGuardarEditar();
        accion = Acciones.Editar;
        habilitarControles(true);
    }

    @FXML
    private void cambiarCancelarEliminar() {
        if (accion == Acciones.Ninguna) {
            eliminarCompra();
        } else {
            cambiarOriginal();
        }
    }

    private void cambiarGuardarEditar() {
        btnAgregar.setText("Guardar");
        btnEliminar.setText("Cancelar");
        btnEditar.setDisable(true);
    }

    private void cambiarOriginal() {
        btnAgregar.setText("Agregar");
        btnEliminar.setText("Eliminar");
        btnEditar.setDisable(false);
        accion = Acciones.Ninguna;
        deshabilitarControles();
    }

    private void deshabilitarControles() {
        txtId.setDisable(true);
        hbEstadoCompra.setDisable(true);
        hbEstadoPago.setDisable(true);
        dpFechaCompra.setDisable(true);
        tablaCompras.setDisable(false);
    }

    private void habilitarControles(boolean habilitar) {
        txtId.setDisable(true); 
        hbEstadoCompra.setDisable(!habilitar);
        hbEstadoPago.setDisable(!habilitar);
        dpFechaCompra.setDisable(!habilitar);
        tablaCompras.setDisable(habilitar);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}