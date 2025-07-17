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
    private Button btnCompras;
    @FXML
    private Button btnProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public void clickManejadorEventos(ActionEvent evento) {
        if (evento.getSource() == btnCompras) {
            principal.getVentaView();

        } else if (evento.getSource() == btnProductos) {
            principal.getProductoView();
        }
    }
}
