package org.wilsonflorian.system;

import java.io.InputStream;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.wilsonflorian.controller.CompraController;
import org.wilsonflorian.controller.InicioController;
import org.wilsonflorian.controller.MenuController;
import org.wilsonflorian.controller.ProductoController;
import org.wilsonflorian.controller.Venta2Controller;
import org.wilsonflorian.controller.VentaController;


/**
 *
 * @author Wilson Florian
 */
public class Main extends Application {

    private Stage escenarioPrincipal;
    private Scene siguienteEscena;
    private static String URL = "/view/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        this.escenarioPrincipal = escenarioPrincipal;
        escenarioPrincipal.setTitle("Joyería 'Los Brothers'");
        Image icono = new Image ("/image/JOYERÍA1.png");
        escenarioPrincipal.getIcons().add(icono);
        getLoginView();
        escenarioPrincipal.show();
    }

    public Initializable cambiarEscena(String fxml) throws Exception {
        Initializable interfazDeCambio = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivoFXML = Main.class.getResourceAsStream(URL + fxml);

        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Main.class.getResource(URL + fxml));

        siguienteEscena = new Scene(cargadorFXML.load(archivoFXML));
        escenarioPrincipal.setScene(siguienteEscena);
        escenarioPrincipal.sizeToScene();

        interfazDeCambio = cargadorFXML.getController();
        return interfazDeCambio;
    }

    public void getLoginView() {
        try {
            InicioController control
                    = (InicioController) cambiarEscena("InicioView.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir al login: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getMenuView() {
        try {
            MenuController control
                    = (MenuController) cambiarEscena("MenuView.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir al menu: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getProductoView() {
        try {
            ProductoController control
                    = (ProductoController) cambiarEscena("ProductoView.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a productos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getComprasView() {
        try {
            CompraController control
                    = (CompraController) cambiarEscena("CompraView.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a compras: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getVentaView() {
        try {
            VentaController control
                    = (VentaController) cambiarEscena("VentaView.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir al menu de ventas: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getVenta2View(int idCompra, double subtotal) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(URL + "Venta2View.fxml"));
        Parent root = loader.load();
        
        Venta2Controller controller = loader.getController();
        controller.setPrincipal(this);
        controller.initData(idCompra, subtotal);
        
        escenarioPrincipal.setScene(new Scene(root));
        escenarioPrincipal.sizeToScene();
    } catch (Exception ex) {
        System.out.println("Error al ir al menu 2 de ventas: " + ex.getMessage());
        ex.printStackTrace();
    }
}
    
    public void getVenta2View() {
        try {
            Venta2Controller control
                    = (Venta2Controller) cambiarEscena("Venta2View.fxml");
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir al menu 2 de ventas: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}