package lk.ijse.cmjd.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.cmjd.app.business.ManageItemsBusiness;
import lk.ijse.cmjd.app.dto.CustomerDTO;
import lk.ijse.cmjd.app.dto.ItemDTO;
import lk.ijse.cmjd.app.dto.OrderDTO;
import lk.ijse.cmjd.app.dto.OrderDetailDTO;
import lk.ijse.cmjd.app.business.ManageCustomersBusiness;
import lk.ijse.cmjd.app.business.ManageOrdersBusiness;
import lk.ijse.cmjd.app.view.util.OrderDetailTM;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderFormController {
    @FXML
    private JFXButton btnPlaceOrder;
    @FXML
    private JFXTextField txtCustomerId;
    @FXML
    private JFXTextField txtItemCode;
    @FXML
    private JFXTextField txtCustomerName;
    @FXML
    private JFXTextField txtDescription;
    @FXML
    private JFXTextField txtQtyOnHand;
    @FXML
    private JFXTextField txtUnitPrice;
    @FXML
    private JFXTextField txtQty;
    @FXML
    private TableView<OrderDetailTM> tblOrderDetails;
    @FXML
    private JFXButton btnRemove;
    @FXML
    private Label lblTotal;
    @FXML
    private JFXTextField txtOrderID;
    @FXML
    private JFXDatePicker txtOrderDate;

    private ObservableList<ItemDTO> tempItemsDB = FXCollections.observableArrayList();

    public void initialize() throws SQLException {

        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        List<ItemDTO> itemsDB = ManageItemsBusiness.getItems();
        for (ItemDTO itemDTO : itemsDB) {
            tempItemsDB.add(new ItemDTO(itemDTO.getCode(), itemDTO.getDescription(), itemDTO.getUnitPrice(), itemDTO.getQtyOnHand()));
        }

        txtOrderID.setEditable(false);

        txtOrderID.setText(ManageOrdersBusiness.generateOrderId());
        txtOrderDate.setValue(LocalDate.now());

        btnRemove.setDisable(true);
        btnPlaceOrder.setDisable(true);
        calculateTotal();

        Platform.runLater(() -> {
            txtCustomerId.requestFocus();
        });

        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrderDetailTM>() {
            @Override
            public void changed(ObservableValue<? extends OrderDetailTM> observable, OrderDetailTM oldValue, OrderDetailTM selectedOrderDetail) {

                if (selectedOrderDetail == null){
                    // Clear Selection
                    return;
                }

                txtItemCode.setText(selectedOrderDetail.getCode());
                txtDescription.setText(selectedOrderDetail.getDescription());
                txtUnitPrice.setText(selectedOrderDetail.getUnitPrice() + "");
                txtQty.setText(selectedOrderDetail.getQty() + "");
                txtQtyOnHand.setText(getItemFromTempDB(txtItemCode.getText()).getQtyOnHand() + "");

                txtItemCode.setEditable(false);
                btnRemove.setDisable(false);

            }
        });

        tblOrderDetails.getItems().addListener(new ListChangeListener<OrderDetailTM>() {
            @Override
            public void onChanged(Change<? extends OrderDetailTM> c) {
                calculateTotal();

                btnPlaceOrder.setDisable(tblOrderDetails.getItems().size() == 0);
            }
        });

    }

    @FXML
    private void navigateToMain(MouseEvent event) throws IOException {
        Label lblMainNav = (Label) event.getSource();
        Stage primaryStage = (Stage) lblMainNav.getScene().getWindow();

        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/cmjd/app/view/MainForm.fxml"));
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }


    @FXML
    private void btnSaveOnAction(ActionEvent event) {

        if (validateItemCode() == null) {
            return;
        }

        String qty = txtQty.getText();
        if (!isInt(qty)) {
            showInvalidateMsgBox("Qty should be a number");
            return;
        } else if (Integer.parseInt(qty) == 0) {
            showInvalidateMsgBox("Qty can't be zero");
            return;
        } else if (Integer.parseInt(qty) > Integer.parseInt(txtQtyOnHand.getText())) {
            showInvalidateMsgBox("Invalid Qty");
            return;
        }

        if (tblOrderDetails.getSelectionModel().isEmpty()) {
            // New

            OrderDetailTM orderDetailTM = null;

            if ((orderDetailTM = isItemExist(txtItemCode.getText())) == null) {

                OrderDetailTM newOrderDetailTM = new OrderDetailTM(txtItemCode.getText(),
                        txtDescription.getText(),
                        Integer.parseInt(qty),
                        Double.parseDouble(txtUnitPrice.getText()),
                        Integer.parseInt(qty) * Double.parseDouble(txtUnitPrice.getText()));

                tblOrderDetails.getItems().add(newOrderDetailTM);

            } else {
                orderDetailTM.setQty(orderDetailTM.getQty() + Integer.parseInt(qty));
            }



        } else {
            // Update
            OrderDetailTM selectedItem = tblOrderDetails.getSelectionModel().getSelectedItem();
            synchronizeQty(selectedItem.getCode());
            selectedItem.setQty(Integer.parseInt(qty));
        }

        setTempQty(txtItemCode.getText(), Integer.parseInt(qty));
        tblOrderDetails.refresh();
        reset();

//        calculateTotal();
    }

    @FXML
    private void btnRemoveOnAction(ActionEvent actionEvent) {

        OrderDetailTM selectedItem = tblOrderDetails.getSelectionModel().getSelectedItem();
        tblOrderDetails.getItems().remove(selectedItem);

        synchronizeQty(selectedItem.getCode());
        reset();

//        calculateTotal();

    }

    @FXML
    private void btnPlaceOrderOnAction(ActionEvent actionEvent) throws SQLException {

        if (txtCustomerId.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Can't place a order without a customer Id", ButtonType.OK).showAndWait();
            txtCustomerId.requestFocus();
            return;
        }

        ObservableList<OrderDetailTM> items = tblOrderDetails.getItems();
        ArrayList<OrderDetailDTO> orderDetailDTOS = new ArrayList<>();

        for (OrderDetailTM item : items) {
            orderDetailDTOS.add(new OrderDetailDTO(item.getCode(),item.getDescription(),item.getQty(),item.getUnitPrice()));
        }
        ManageOrdersBusiness.createOrder(new OrderDTO(txtOrderID.getText(), txtOrderDate.getValue(),txtCustomerId.getText(), orderDetailDTOS));

        new Alert(Alert.AlertType.CONFIRMATION,"Order has been placed successfully", ButtonType.OK).showAndWait();
        hardReset();

    }

    private void hardReset() {
        reset();
        tblOrderDetails.getItems().removeAll(tblOrderDetails.getItems());
        txtCustomerId.clear();
        txtCustomerName.clear();
        try {
            txtOrderID.setText(ManageOrdersBusiness.generateOrderId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        txtCustomerId.requestFocus();
    }

    public void calculateTotal() {
        ObservableList<OrderDetailTM> items = tblOrderDetails.getItems();

        double total = 0.0;

        for (OrderDetailTM item : items) {
            total += item.getTotal();
        }

        lblTotal.setText("Total : " + total + "");
    }

    @FXML
    private void txtCustomerID_OnAction(ActionEvent actionEvent) throws SQLException {

        String customerID = txtCustomerId.getText();

        CustomerDTO customerDTO = ManageCustomersBusiness.findCustomer(customerID);

        if (customerDTO == null) {
            new Alert(Alert.AlertType.ERROR, "Invalid Customer ID", ButtonType.OK).showAndWait();
            txtCustomerName.clear();
            txtCustomerId.requestFocus();
            txtCustomerId.selectAll();
        } else {
            txtCustomerName.setText(customerDTO.getName());
            txtItemCode.requestFocus();
        }

    }

    @FXML
    private void txtItemCode_OnAction(ActionEvent actionEvent) {

        ItemDTO itemDTO = validateItemCode();

        if (itemDTO != null) {

            txtDescription.setText(itemDTO.getDescription());
            txtQtyOnHand.setText(getItemFromTempDB(itemDTO.getCode()).getQtyOnHand() + "");
            txtUnitPrice.setText(itemDTO.getUnitPrice() + "");
            txtQty.requestFocus();
        }

    }

    @FXML
    private void txtQty_OnAction(ActionEvent actionEvent) {
        btnSaveOnAction(actionEvent);
    }

    private ItemDTO validateItemCode()  {
        String itemCode = txtItemCode.getText();

        ItemDTO itemDTO = null;
        try {
            itemDTO = ManageItemsBusiness.findItem(itemCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (itemDTO == null) {
            new Alert(Alert.AlertType.ERROR, "Invalid Item Code", ButtonType.OK).showAndWait();
            txtDescription.clear();
            txtQtyOnHand.clear();
            txtUnitPrice.clear();
            txtQty.clear();
            txtItemCode.requestFocus();
            txtItemCode.selectAll();
        }
        return itemDTO;
    }

    public boolean isInt(String number) {
        char[] chars = number.toCharArray();
        for (char aChar : chars) {
            if (!Character.isDigit(aChar)) {
                return false;
            }
        }
        return true;
    }

    public ItemDTO getItemFromTempDB(String itemCode) {
        for (ItemDTO itemDTO : tempItemsDB) {
            if (itemDTO.getCode().equals(itemCode)) {
                return itemDTO;
            }
        }
        return null;
    }

    private void showInvalidateMsgBox(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
        txtQty.requestFocus();
        txtQty.selectAll();
    }

    private OrderDetailTM isItemExist(String itemCode) {
        ObservableList<OrderDetailTM> items = tblOrderDetails.getItems();
        for (OrderDetailTM item : items) {
            if (item.getCode().equals(itemCode)) {
                return item;
            }
        }
        return null;
    }

    public void reset() {
        tblOrderDetails.refresh();
        txtItemCode.clear();
        txtDescription.clear();
        txtQty.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
        txtItemCode.setEditable(true);
        btnRemove.setDisable(true);
        tblOrderDetails.getSelectionModel().clearSelection();
        txtItemCode.requestFocus();
    }

    private void setTempQty(String itemCode, int qty) {
        for (ItemDTO itemDTO : tempItemsDB) {
            if (itemDTO.getCode().equals(itemCode)) {
                itemDTO.setQtyOnHand(itemDTO.getQtyOnHand() - qty);
                break;
            }
        }
    }

    private void synchronizeQty(String itemCode){
        int qtyOnHand = 0;
        try {
            qtyOnHand = ManageItemsBusiness.findItem(itemCode).getQtyOnHand();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (ItemDTO itemDTO : tempItemsDB) {
            if (itemDTO.getCode().equals(itemCode)){
                itemDTO.setQtyOnHand(qtyOnHand);
                return;
            }
        }
    }
}
