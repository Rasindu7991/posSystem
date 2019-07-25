package lk.ijse.cmjd.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.cmjd.app.db.DBConnection;
import lk.ijse.cmjd.app.dto.OrderDTO;
import lk.ijse.cmjd.app.dto.OrderDetailDTO;
import lk.ijse.cmjd.app.business.ManageCustomersBusiness;
import lk.ijse.cmjd.app.business.ManageItemsBusiness;
import lk.ijse.cmjd.app.business.ManageOrdersBusiness;
import lk.ijse.cmjd.app.view.util.OrderDetailTM;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOrderFormController {

    @FXML
    private JFXButton billBtn;

    @FXML
    private JFXDatePicker txtOrderDate;
    @FXML
    private JFXTextField txtOrderID;
    @FXML
    private JFXTextField txtCustomerID;
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
    private Label lblTotal;

    private String orderId;

    @FXML
    private void navigateToMain(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/cmjd/app/view/SearchOrderForm.fxml"));
        Scene mainScene = new Scene(root);
        Stage mainStage = (Stage) lblTotal.getScene().getWindow();
        mainStage.setScene(mainScene);

        TranslateTransition tt1 = new TranslateTransition(Duration.millis(300), root.lookup("AnchorPane"));
        tt1.setToX(0);
        tt1.setFromX(-mainScene.getWidth());
        tt1.play();

        mainStage.centerOnScreen();
    }

    public void initialize() {
        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrderDetailTM>() {
            @Override
            public void changed(ObservableValue<? extends OrderDetailTM> observable, OrderDetailTM oldValue, OrderDetailTM selectedOrderDetail) {

                if (selectedOrderDetail == null) {
                    // Clear Selection
                    return;
                }

                txtItemCode.setText(selectedOrderDetail.getCode());
                txtDescription.setText(selectedOrderDetail.getDescription());
                txtUnitPrice.setText(selectedOrderDetail.getUnitPrice() + "");
                txtQty.setText(selectedOrderDetail.getQty() + "");
                try {
                    txtQtyOnHand.setText(ManageItemsBusiness.findItem(txtItemCode.getText()).getQtyOnHand() + "");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void setInitData(String orderId, double orderTotal) {
        this.orderId = orderId;
        lblTotal.setText("Total : " + orderTotal);
        fillData();
    }

    public void fillData() {
        OrderDTO orderDTO = null;
        try {
            orderDTO = ManageOrdersBusiness.findOrder(this.orderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        txtCustomerID.setText(orderDTO.getId());
        txtOrderID.setText(orderDTO.getId());
        txtOrderDate.setValue(orderDTO.getDate());
        try {
            txtCustomerName.setText(ManageCustomersBusiness.findCustomer(orderDTO.getCustomerId()).getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<OrderDetailDTO> orderDetailDTOS = orderDTO.getOrderDetailDTOS();
        ObservableList<OrderDetailTM> details = FXCollections.observableArrayList();

        for (OrderDetailDTO orderDetailDTO : orderDetailDTOS) {
            details.add(new OrderDetailTM(orderDetailDTO.getCode(),
                    orderDetailDTO.getDescription(),
                    orderDetailDTO.getQty(),
                    orderDetailDTO.getUnitPrice(),
                    orderDetailDTO.getQty() * orderDetailDTO.getUnitPrice()));
        }
        tblOrderDetails.setItems(details);
    }

    @FXML
    void generateBillOnAction(ActionEvent event) throws JRException, SQLException {
        Connection connection = DBConnection.getConnection();

        File file = new File("Reports/orderD.jasper");
        JasperReport compiledReport = (JasperReport)
                JRLoader.loadObject(file);

        Map<String,Object> parms=new HashMap<>();
        parms.put("orderID",txtOrderID.getText());
        parms.put("customerID",txtCustomerID.getText());
        parms.put("customerName",txtCustomerName.getText());
        parms.put("total",lblTotal.getText());


        JasperPrint filledReport = JasperFillManager
                .fillReport(compiledReport, parms,
                        connection
                );

        JasperViewer.viewReport(filledReport,false);
    }

}
