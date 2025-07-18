package org.wilsonflorian.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.wilsonflorian.database.Conexion;
import org.wilsonflorian.model.Usuario;
import org.wilsonflorian.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */

public class InicioController implements Initializable {

    private Main principal;
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private boolean modoRegistro = false;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @FXML
    private Button btnIngresar, btnCerrar, btnRegistro;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContraseña;
    @FXML
    private Label lblCuenta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        try {
            usuarios.clear();
            Connection conexion = Conexion.getInstancia().getConexion();
            PreparedStatement stmt = conexion.prepareStatement("call sp_ListarUsuarios()");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("correoUsuario"),
                        rs.getString("contraseñaUsuario")
                );
                usuarios.add(usuario);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los usuarios de la base de datos");
        }
    }

    @FXML
    private void clickManejadorEventos(ActionEvent evento) {
        if (evento.getSource() == btnIngresar) {
            if (modoRegistro) {
                registrarUsuario();
            } else {
                iniciarSesion();
            }
        }
        if (evento.getSource() == btnCerrar) {
            System.exit(0);
        }
        if (evento.getSource() == btnRegistro) {
            toggleModoRegistro();
        }
    }

    private void toggleModoRegistro() {
        modoRegistro = !modoRegistro;
        
        if (modoRegistro) {
            lblCuenta.setText("¿Ya tienes una cuenta?");
            btnIngresar.setText("Guardar");
            btnRegistro.setText("Regresar");
        } else {
            lblCuenta.setText("¿No tienes una cuenta?");
            btnIngresar.setText("Ingresar");
            btnRegistro.setText("Regístrate");
            limpiarCampos();
        }
    }

    private void iniciarSesion() {
        String usuario = txtCorreo.getText();
        String contrasena = txtContraseña.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Debe completar ambos campos");
            return;
        }

        for (Usuario u : usuarios) {
            if (u.getCorreoUsuario().equals(usuario) && u.getContraseñaUsuario().equals(contrasena)) {
                principal.getMenuView();
                limpiarCampos();
                return;
            }
        }
        mostrarAlerta("Error", "Correo o contraseña incorrecta");
    }

    private void registrarUsuario() {
        String correo = txtCorreo.getText();
        String contrasena = txtContraseña.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Debe completar ambos campos");
            return;
        }

        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            PreparedStatement stmt = conexion.prepareStatement("call sp_agregarUsuario(?, ?)");
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);
            stmt.execute();

            mostrarAlerta("Éxito", "Usuario registrado correctamente");
            cargarUsuarios();
            toggleModoRegistro();
        } catch (Exception e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo registrar el usuario");
        }
    }

    private void limpiarCampos() {
        txtCorreo.clear();
        txtContraseña.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}