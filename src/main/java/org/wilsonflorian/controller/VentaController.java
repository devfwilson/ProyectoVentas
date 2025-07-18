package org.wilsonflorian.controller;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.wilsonflorian.database.Conexion;
import org.wilsonflorian.model.Producto;
import org.wilsonflorian.system.Main;

public class VentaController implements Initializable {

    @FXML
    private Button btnRegresar, btnAgregar, btnEliminar, btnFinalizar;

    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, Integer> colId;
    @FXML
    private TableColumn<Producto, String> colProducto;
    @FXML
    private TableColumn<Producto, Double> colPrecioUnitario;
    @FXML
    private TableColumn<Producto, Integer> colCantidad;
    @FXML
    private TableColumn<Producto, Double> colSubtotal;

    @FXML
    private ComboBox<Producto> cbProducto;
    @FXML
    private Spinner<Integer> spCantidad;

    private Main principal;
    private ObservableList<Producto> listaProductos;
    private ObservableList<Producto> listaProductosDisponibles;
    private int idCompraActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarSpinnerCantidad();
        configurarComboBoxProductos();
        configurarTablaProductos();
        cargarProductosDisponibles();
        deshabilitarControles();
    }

    private void configurarSpinnerCantidad() {
        SpinnerValueFactory<Integer> valueFactory
                = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spCantidad.setValueFactory(valueFactory);
    }

    private void configurarComboBoxProductos() {
        cbProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                spCantidad.setDisable(false);
            }
        });

        cbProducto.setCellFactory(param -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getIdProducto() + " | " + item.getNombreProducto() + " | Q" + item.getPrecioProducto());
                }
            }
        });

        cbProducto.setButtonCell(new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombreProducto());
                }
            }
        });
    }

    private void configurarTablaProductos() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void cargarProductosDisponibles() {
        listaProductosDisponibles = FXCollections.observableArrayList(obtenerProductosDisponibles());
        cbProducto.setItems(listaProductosDisponibles);
    }

    private ArrayList<Producto> obtenerProductosDisponibles() {
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT idProducto, nombreProducto, precioProducto FROM Productos WHERE stockProducto > 0";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                productos.add(new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getDouble("precioProducto")
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar productos disponibles: " + e.getMessage());
        }
        return productos;
    }

    private void crearNuevaCompra() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            if (idCompraActual == 0) {
                CallableStatement procedimiento = conexion.prepareCall(
                        "{call sp_agregarCompra(?, ?, ?)}");

                procedimiento.setString(1, "Pendiente");
                procedimiento.setString(2, "Pendiente");

                procedimiento.registerOutParameter(3, Types.INTEGER);

                procedimiento.execute();

                idCompraActual = procedimiento.getInt(3);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al crear nueva compra: " + e.getMessage());
        }
    }

    @FXML
    private void agregarProductoAVenta() {
        Producto productoSeleccionado = cbProducto.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            try {
                if (idCompraActual == 0) {
                    crearNuevaCompra();
                    if (idCompraActual == 0) {
                        mostrarAlerta("No se pudo crear la compra");
                        return;
                    }
                }
                int cantidad = spCantidad.getValue();

                Connection conexion = Conexion.getInstancia().getConexion();
                CallableStatement procedimiento = conexion.prepareCall("{call sp_agregarDetalleCompra(?, ?, ?, ?)}");
                procedimiento.setInt(1, idCompraActual);
                procedimiento.setInt(2, productoSeleccionado.getIdProducto());
                procedimiento.setInt(3, cantidad);
                procedimiento.setDouble(4, productoSeleccionado.getPrecioProducto() * cantidad);
                procedimiento.execute();

                actualizarTablaProductos();
                limpiarSeleccion();
            } catch (SQLException e) {
                mostrarAlerta("Error al agregar producto a la venta: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Seleccione un producto para agregar");
        }
    }

    private void actualizarTablaProductos() {
        listaProductos = FXCollections.observableArrayList(obtenerProductosEnVenta());
        tablaProductos.setItems(listaProductos);
    }

    private ArrayList<Producto> obtenerProductosEnVenta() {
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT p.idProducto, p.nombreProducto, p.precioProducto, "
                    + "dc.cantidad, dc.subtotal "
                    + "FROM DetalleCompras dc "
                    + "JOIN Productos p ON dc.idProducto = p.idProducto "
                    + "WHERE dc.idCompra = ?";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idCompraActual);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getDouble("precioProducto")
                );
                producto.setCantidad(rs.getInt("cantidad"));
                producto.setSubtotal(rs.getDouble("subtotal"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar productos de la venta: " + e.getMessage());
        }
        return productos;
    }

    @FXML
    private void eliminarProductoDeVenta() {
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            try {
                Connection conexion = Conexion.getInstancia().getConexion();
                CallableStatement procedimiento = conexion.prepareCall("{call sp_eliminarDetalleCompra(?, ?)}");
                procedimiento.setInt(1, idCompraActual);
                procedimiento.setInt(2, productoSeleccionado.getIdProducto());
                procedimiento.execute();

                actualizarTablaProductos();
            } catch (SQLException e) {
                mostrarAlerta("Error al eliminar producto: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Seleccione un producto de la tabla para eliminar");
        }
    }

    @FXML
    private void finalizarVenta() {
        if (idCompraActual != 0 && !tablaProductos.getItems().isEmpty()) {
            double subtotal = calcularSubtotal();
            principal.getVenta2View(idCompraActual, subtotal);
        } else {
            mostrarAlerta("No hay productos en la venta para finalizar");
        }
    }

    private double calcularSubtotal() {
        double subtotal = 0;
        for (Producto producto : tablaProductos.getItems()) {
            subtotal += producto.getSubtotal();
        }
        return subtotal;
    }

    private void limpiarSeleccion() {
        cbProducto.getSelectionModel().clearSelection();
        spCantidad.getValueFactory().setValue(1);
        spCantidad.setDisable(true);
    }

    private void deshabilitarControles() {
        spCantidad.setDisable(true);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void regresarAMenu() {
        if (idCompraActual != 0 && !tablaProductos.getItems().isEmpty()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación");
            confirmacion.setHeaderText("Venta en progreso");
            confirmacion.setContentText("¿Desea cancelar la venta actual y regresar al menú?");

            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                cancelarVentaActual();
                principal.getMenuView();
            }
        } else {
            principal.getMenuView();
        }
    }

    private void cancelarVentaActual() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement procedimiento = conexion.prepareCall("{call sp_eliminarCompra(?)}");
            procedimiento.setInt(1, idCompraActual);
            procedimiento.execute();
        } catch (SQLException e) {
            mostrarAlerta("Error al cancelar la venta: " + e.getMessage());
        }
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}
