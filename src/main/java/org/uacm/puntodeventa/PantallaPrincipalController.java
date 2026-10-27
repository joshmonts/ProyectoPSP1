package org.uacm.puntodeventa;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.uacm.puntodeventa.Clases.Producto;
import org.uacm.puntodeventa.Clases.InformacionProductos;

public class PantallaPrincipalController implements Initializable {
    @FXML private TextField busquedaCarrito;
    @FXML private Button btnAgregarCarrito;
    @FXML private TableView<ProductoCarrito> tableViewCarrito;
    @FXML private TableColumn<ProductoCarrito, Integer> colId;
    @FXML private TableColumn<ProductoCarrito, String> colNombre;
    @FXML private TableColumn<ProductoCarrito, Integer> colCantidad;
    @FXML private TableColumn<ProductoCarrito, Double> colPrecio;
    @FXML private TableColumn<ProductoCarrito, Double> colTotal;
    @FXML private Label lblArticulosCount;
    @FXML private Label lblTotal;
    @FXML private Button btnCobrar;

    ObservableList<ProductoCarrito> carritoList = FXCollections.observableArrayList();
    private FilteredList<Producto> filteredProducts;
    private Popup suggestionPopup;
    private ListView<Producto> suggestionList;
    @FXML
    private Button btnNuevaCliente;
    @FXML
    private Button btnNuevaVenta;
    @FXML
    private Button btnRecarga;
    @FXML
    private Button btnMonedero1;
    @FXML
    private Button btnMonedero11;
    @FXML
    private Button btnMonedero12;
    @FXML
    private Button btnMonedero13;

    @Override
    /**
    * Controlador principal del sistema de Punto de Venta.
    * Gestiona las operaciones del carrito, cobro e inventario.
    * Inicializa la lista en blanco.
    * @author Jose
    * @version 1.0
    */
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colCantidad.setOnEditCommit(event -> {
            ProductoCarrito item = event.getRowValue();
            int nuevaCantidad = event.getNewValue();
            if (nuevaCantidad > 0) {
                item.setCantidad(nuevaCantidad);
                actualizarTotales();
                tableViewCarrito.refresh();
            } else {
                carritoList.remove(item);
                actualizarTotales();
            }
        });
        tableViewCarrito.setItems(carritoList);
        ObservableList<Producto> productos = InformacionProductos.getInstance().getProductos();
        filteredProducts = new FilteredList<>(productos, p -> true);
        suggestionList = new ListView<>(filteredProducts);
        suggestionList.setCellFactory(lv -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getIdProducto() + " - " + item.getNombre() + " (Stock: " + item.getStock() + ")");
                }
            }
        });
        suggestionList.setPrefWidth(busquedaCarrito.getPrefWidth());
        suggestionList.setMaxHeight(150);
        suggestionList.getStyleClass().add("suggestion-list");
        suggestionPopup = new Popup();
        suggestionPopup.getContent().add(suggestionList);
        suggestionPopup.setAutoHide(true);
        busquedaCarrito.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = (newVal == null) ? "" : newVal.toLowerCase();
            filteredProducts.setPredicate(p -> {
                if (lower.isEmpty()) return true;
                return p.getNombre().toLowerCase().contains(lower) ||
                       String.valueOf(p.getIdProducto()).contains(lower);
            });
            if (!filteredProducts.isEmpty() && !newVal.isEmpty()) {
                Bounds bounds = busquedaCarrito.localToScreen(busquedaCarrito.getBoundsInLocal());
                suggestionPopup.show(busquedaCarrito, bounds.getMinX(), bounds.getMaxY());
            } else {
                suggestionPopup.hide();
            }
        });
        suggestionList.setOnMouseClicked(e -> {
            Producto selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                busquedaCarrito.setText(selected.getNombre());
                suggestionPopup.hide();
            }
        });
        busquedaCarrito.setOnAction(e -> {
            if (!filteredProducts.isEmpty()) {
                Producto seleccionado = filteredProducts.get(0);
                agregarProductoDirecto(seleccionado);
                busquedaCarrito.clear();
                suggestionPopup.hide();
            }
        });
    }
    private void agregarProductoDirecto(Producto seleccionado) {
        if (seleccionado.getStock() < 1) {
            mostrarAlerta("Stock insuficiente para " + seleccionado.getNombre());
            return;
        }
        for (ProductoCarrito item : carritoList) {
            if (item.getId() == seleccionado.getIdProducto()) {
                item.setCantidad(item.getCantidad() + 1);
                actualizarTotales();
                tableViewCarrito.refresh();
                return;
            }
        }
        ProductoCarrito nuevo = new ProductoCarrito(seleccionado, 1);
        carritoList.add(nuevo);
        actualizarTotales();
    }

    @FXML
    private void agregarProductoAlCarrito(ActionEvent event) {
        String texto = busquedaCarrito.getText().trim();
        if (texto.isEmpty()) {
            mostrarAlerta("Escribe o selecciona un producto");
            return;
        }
        Producto seleccionado = null;
        for (Producto p : InformacionProductos.getInstance().getProductos()) {
            if (p.getNombre().equalsIgnoreCase(texto) || String.valueOf(p.getIdProducto()).equals(texto)) {
                seleccionado = p;
                break;
            }
        }
        if (seleccionado == null && !filteredProducts.isEmpty()) {
            seleccionado = filteredProducts.get(0);
        }
        if (seleccionado == null) {
            mostrarAlerta("Producto no encontrado");
            return;
        }

        agregarProductoDirecto(seleccionado);
        busquedaCarrito.clear();
        suggestionPopup.hide();
    }

    private void actualizarTotales() {
        int totalArticulos = carritoList.stream().mapToInt(ProductoCarrito::getCantidad).sum();
        double totalPagar = carritoList.stream().mapToDouble(ProductoCarrito::getTotal).sum();
        lblArticulosCount.setText(String.valueOf(totalArticulos));
        lblTotal.setText(String.format("$%.2f", totalPagar));
    }

    @FXML
    private void cobrar(ActionEvent event) throws IOException {
        if (carritoList.isEmpty()) {
            mostrarAlerta("No hay productos en el carrito");
            return;
        }
        for (ProductoCarrito item : carritoList) {
            Producto original = InformacionProductos.getInstance().getProductos().stream()
                    .filter(p -> p.getIdProducto() == item.getId())
                    .findFirst().orElse(null);
            if (original != null) {
                if (original.getStock() >= item.getCantidad()) {
                    original.reducirStock(item.getCantidad());
                } else {
                    mostrarAlerta("Stock insuficiente para " + original.getNombre());
                    return;
                }
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Venta realizada");
        alert.setHeaderText(null);
        alert.setContentText("Total a pagar: " + lblTotal.getText() + "\nGracias por su compra.");
        alert.showAndWait();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/ticket.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ticket");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        carritoList.clear();
        actualizarTotales();
        
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atencion");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    public static class ProductoCarrito {
        private final int id;
        private final String nombre;
        private final double precio;
        private int cantidad;

        public ProductoCarrito(Producto p, int cantidad) {
            this.id = p.getIdProducto();
            this.nombre = p.getNombre();
            this.precio = p.getPrecioVenta();
            this.cantidad = cantidad;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public double getPrecio() { return precio; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public double getTotal() { return precio * cantidad; }
    }
    
    private void NuevaVenta(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/ticket.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ticket");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        carritoList.clear();
        actualizarTotales();
        
    }

}