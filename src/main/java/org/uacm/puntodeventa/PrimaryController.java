package org.uacm.puntodeventa;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PrimaryController {

    @FXML
    private Button btnIniciarSesion;
    @FXML
    private TextField TxtFieldUsuario;
    @FXML
    private PasswordField PasswordField;
    @FXML
    private Label lblErrores;
    @FXML
    private Label lblUsuario;
    @FXML
    private Label lblContra;


    private void abrirSistemaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/pantallas/PantallaPrincipal.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Sistema de Ventas");
            stage.setScene(new Scene(root, 917, 660));
            stage.show();
            Stage actual = (Stage) btnIniciarSesion.getScene().getWindow();
            actual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}