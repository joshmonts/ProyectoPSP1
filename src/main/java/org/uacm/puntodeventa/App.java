package org.uacm.puntodeventa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import org.uacm.puntodeventa.Clases.Producto;
import org.uacm.puntodeventa.Clases.Venta;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("/pantallas/PantallaPrincipal.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("Venta");
        stage.show();
       
    }

    public static void main(String[] args) {
        launch();   
    }       
}