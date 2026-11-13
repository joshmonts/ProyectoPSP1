package org.uacm.puntodeventa;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.uacm.puntodeventa.Clases.Producto;
import org.uacm.puntodeventa.Clases.InformacionProductos;
import org.uacm.puntodeventa.Clases.ProductoCarrito;

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
    @FXML private Button btnNuevaCliente;
    @FXML private Button btnNuevaVenta;
    @FXML private Button btnRecarga;

    // Botones de método de pago
    @FXML private Button btnMonedero1;  // Efectivo
    @FXML private Button btnMonedero11; // Tarjeta
    @FXML private Button btnMonedero12; // Transferencia
    @FXML private Button btnMonedero13; // Saldo

    // Campos de texto de la interfaz (Asegúrate de vincularlos en tu archivo FXML)
    @FXML private TextField txtEfectivoRecibido;
    @FXML private TextField txtCambio;

    ObservableList<ProductoCarrito> carritoList = FXCollections.observableArrayList();
    private FilteredList<Producto> filteredProducts;
    private Popup suggestionPopup;
    private ListView<Producto> suggestionList;

    // Variable para almacenar el método seleccionado
    private String metodoPagoSeleccionado = "";

    @Override
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
        configurarBuscador();
        configurarEventosMetodosPago();
        configurarCalculoCambio();
    }

    private void configurarBuscador() {
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
                agregarProductoDirecto(filteredProducts.get(0));
                busquedaCarrito.clear();
                suggestionPopup.hide();
            }
        });
    }

    // 1. SELECCIÓN DE MÉTODO DE COBRO
    private void configurarEventosMetodosPago() {
        btnMonedero1.setOnAction(e -> seleccionarMetodoPago("Efectivo", btnMonedero1));
        btnMonedero11.setOnAction(e -> seleccionarMetodoPago("Tarjeta", btnMonedero11));
        btnMonedero12.setOnAction(e -> seleccionarMetodoPago("Transferencia", btnMonedero12));
        btnMonedero13.setOnAction(e -> seleccionarMetodoPago("Saldo", btnMonedero13));
    }

    private void seleccionarMetodoPago(String metodo, Button botonActivo) {
        this.metodoPagoSeleccionado = metodo;

        // Restablecer estilo de todos los botones
        Button[] botones = {btnMonedero1, btnMonedero11, btnMonedero12, btnMonedero13};
        for (Button b : botones) {
            b.setStyle("-fx-background-color: #ffffff; -fx-border-color: #7f8c8d; -fx-border-radius: 5;");
        }

        // Resaltar el botón seleccionado en azul
        botonActivo.setStyle("-fx-background-color: #d6eaf8; -fx-border-color: #1a66ff; -fx-border-width: 2; -fx-border-radius: 5; -fx-font-weight: bold;");

        // Habilitar o deshabilitar el campo de Efectivo Recibido según el método
        if (txtEfectivoRecibido != null) {
            boolean esEfectivo = metodo.equals("Efectivo");
            txtEfectivoRecibido.setDisable(!esEfectivo);
            if (!esEfectivo) {
                txtEfectivoRecibido.clear();
                if (txtCambio != null) txtCambio.setText("$0.00");
            }
        }
    }

    // Cálculo automático de cambio mientras se escribe el efectivo
    private void configurarCalculoCambio() {
        if (txtEfectivoRecibido != null) {
            txtEfectivoRecibido.textProperty().addListener((obs, oldVal, newVal) -> calcularCambio());
        }
    }

    private void calcularCambio() {
        if (txtEfectivoRecibido == null || txtCambio == null) return;
        try {
            double recibido = Double.parseDouble(txtEfectivoRecibido.getText().trim());
            double total = obtenerTotalNumerico();
            double cambio = recibido - total;
            txtCambio.setText(cambio >= 0 ? String.format("$%.2f", cambio) : "Falta dinero");
        } catch (NumberFormatException e) {
            txtCambio.setText("$0.00");
        }
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
        carritoList.add(new ProductoCarrito(seleccionado, 1));
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

    
    private double obtenerTotalNumerico() {
        return carritoList.stream().mapToDouble(ProductoCarrito::getTotal).sum();
    }

    private void actualizarTotales() {
        int totalArticulos = carritoList.stream().mapToInt(ProductoCarrito::getCantidad).sum();
        double totalPagar = obtenerTotalNumerico();

        lblArticulosCount.setText(String.valueOf(totalArticulos));
        lblTotal.setText(String.format("$%.2f", totalPagar));
        calcularCambio();
    }

    // 2. PROCESAR COBRO, GENERAR TICKET Y VACIAR CARRITO
    @FXML
    private void cobrar(ActionEvent event) {
        if (carritoList.isEmpty()) {
            mostrarAlerta("No hay productos en el carrito.");
            return;
        }

        if (metodoPagoSeleccionado.isEmpty()) {
            mostrarAlerta("Por favor selecciona un método de pago (Efectivo, Tarjeta, Transferencia o Saldo).");
            return;
        }

        double total = obtenerTotalNumerico();
        double efectivoRecibido = total;
        double cambio = 0.0;

        // Validar monto si se eligió Efectivo
        if (metodoPagoSeleccionado.equals("Efectivo") && txtEfectivoRecibido != null) {
            try {
                efectivoRecibido = Double.parseDouble(txtEfectivoRecibido.getText().trim());
                if (efectivoRecibido < total) {
                    mostrarAlerta("El efectivo recibido es menor al total a pagar.");
                    return;
                }
                cambio = efectivoRecibido - total;
            } catch (NumberFormatException e) {
                mostrarAlerta("Ingresa una cantidad válida en Efectivo Recibido.");
                return;
            }
        }

        // Guardar copia de los productos vendidos antes de limpiar el carrito
        List<ProductoCarrito> productosVendidos = new ArrayList<>(carritoList);

        // Descontar stock en el inventario
        for (ProductoCarrito item : productosVendidos) {
            for (Producto p : InformacionProductos.getInstance().getProductos()) {
                if (p.getIdProducto() == item.getId()) {
                    p.setStock(p.getStock() - item.getCantidad());
                    break;
                }
            }
        }

        // Mostrar ticket en ventana independiente
        mostrarVentanaTicket(productosVendidos, total, metodoPagoSeleccionado, efectivoRecibido, cambio);

        // Limpiar interfaz y vaciar carrito
        limpiarVenta();
    }

    // Método para vaciar el carrito y reiniciar controles
    @FXML
    private void nuevaVenta(ActionEvent event) {
        limpiarVenta();
    }

    private void limpiarVenta() {
        carritoList.clear();
        tableViewCarrito.refresh();
        actualizarTotales();

        metodoPagoSeleccionado = "";
        Button[] botones = {btnMonedero1, btnMonedero11, btnMonedero12, btnMonedero13};
        for (Button b : botones) {
            b.setStyle("");
        }

        if (txtEfectivoRecibido != null) {
            txtEfectivoRecibido.clear();
            txtEfectivoRecibido.setDisable(false);
        }
        if (txtCambio != null) {
            txtCambio.clear();
        }
    }

    // 3. VENTANA INDEPENDIENTE DEL TICKET
    private void mostrarVentanaTicket(List<ProductoCarrito> items, double total, String metodo, double recibido, double cambio) {
        Stage ticketStage = new Stage();
        ticketStage.setTitle("Ticket de Venta - NOVA");
        ticketStage.initModality(Modality.APPLICATION_MODAL);

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        sb.append("========================================\n");
        sb.append("              PUNTO DE VENTA            \n");
        sb.append("                   NOVA                 \n");
        sb.append("========================================\n");
        sb.append("Fecha: ").append(dtf.format(LocalDateTime.now())).append("\n");
        sb.append("Método de Pago: ").append(metodo).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-4s %-18s %7s %8s\n", "Cant", "Producto", "Precio", "Total"));
        sb.append("----------------------------------------\n");

        for (ProductoCarrito item : items) {
            String nombreCorto = item.getNombre().length() > 18 
                ? item.getNombre().substring(0, 15) + "..." 
                : item.getNombre();
            sb.append(String.format("%-4d %-18s $%6.2f $%7.2f\n",
                    item.getCantidad(), nombreCorto, item.getPrecio(), item.getTotal()));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("TOTAL A PAGAR:                 $%8.2f\n", total));
        if (metodo.equals("Efectivo")) {
            sb.append(String.format("EFECTIVO RECIBIDO:             $%8.2f\n", recibido));
            sb.append(String.format("CAMBIO:                        $%8.2f\n", cambio));
        }
        sb.append("========================================\n");
        sb.append("         ¡Gracias por su compra!        \n");

        TextArea areaTicket = new TextArea(sb.toString());
        areaTicket.setEditable(false);
        areaTicket.setFont(Font.font("Monospaced", 13));
        areaTicket.setPrefSize(380, 420);

        Button btnCerrar = new Button("Cerrar Ticket");
        btnCerrar.setMaxWidth(Double.MAX_VALUE);
        btnCerrar.setStyle("-fx-background-color: #1a66ff; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCerrar.setOnAction(e -> ticketStage.close());

        VBox layout = new VBox(10, areaTicket, btnCerrar);
        layout.setPadding(new Insets(15));

        ticketStage.setScene(new Scene(layout));
        ticketStage.showAndWait();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso - NOVA");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}