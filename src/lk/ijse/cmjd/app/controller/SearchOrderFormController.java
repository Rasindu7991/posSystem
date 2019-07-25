package lk.ijse.cmjd.app.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.cmjd.app.dto.OrderDTO2;
import lk.ijse.cmjd.app.main.AppInitializer;
import lk.ijse.cmjd.app.business.ManageOrdersBusiness;
import lk.ijse.cmjd.app.view.util.OrderTM;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SearchOrderFormController {

    @FXML
    private JFXTextField txtSearchOrder;
    @FXML
    private AnchorPane root;
    @FXML
    private TableView<OrderTM> tblOrders;

    private ObservableList<OrderTM> olOrders;

    public void initialize() throws SQLException {
        tblOrders.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderId"));
        tblOrders.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tblOrders.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tblOrders.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tblOrders.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        List<OrderDTO2> ordersDB = ManageOrdersBusiness.getOrdersWithCustomerNamesAndTotals();
        olOrders = FXCollections.observableArrayList();
        for (OrderDTO2 orderDTO : ordersDB) {
            olOrders.add(new OrderTM(orderDTO.getOrderId(),
                    orderDTO.getOrderDate(),
                    orderDTO.getCustomerId(),
                    orderDTO.getCustomerName(),
                    orderDTO.getTotal()));
        }

        tblOrders.setItems(olOrders);
    }

    @FXML
    private void txtOrderId_OnKeyReleased(KeyEvent keyEvent) {

        ObservableList<OrderTM> tempList = FXCollections.observableArrayList();
//        System.out.println("TEST : " + olOrders);
        for (OrderTM olOrder : olOrders) {
            if (olOrder.getOrderId().startsWith(txtSearchOrder.getText())){
                tempList.add(olOrder);
            }
        }

        tblOrders.setItems(tempList);
        
    }

    @FXML
    private void navigateToHome(MouseEvent mouseEvent) throws IOException {
        AppInitializer.navigateToHome(root, (Stage) this.root.getScene().getWindow());
    }

    @FXML
    private void tblOrders_OnClick(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2){

            OrderTM selectedItem = tblOrders.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/lk/ijse/cmjd/app/view/ViewOrderForm.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            ViewOrderFormController controller = fxmlLoader.getController();
            controller.setInitData(selectedItem.getOrderId(), selectedItem.getTotal());
            Scene scene = new Scene(root);
            ((Stage)tblOrders.getScene().getWindow()).setScene(scene);
        }
    }
}
