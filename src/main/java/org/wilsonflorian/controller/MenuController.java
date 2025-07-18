package org.wilsonflorian.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.wilsonflorian.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class MenuController implements Initializable {

    private Main principal;

    @FXML
    private Button btnCompras, btnProductos, btnVentas, btnCerrar;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public void clickManejadorEventos(ActionEvent evento) {
        if (evento.getSource() == btnVentas) {
            principal.getVentaView();

        } else if (evento.getSource() == btnProductos) {
            principal.getProductoView();
            
        } else if (evento.getSource() == btnCompras) {
            principal.getComprasView();
            
        } else if (evento.getSource() == btnCerrar) {
            principal.getLoginView();
        }
    }
}
