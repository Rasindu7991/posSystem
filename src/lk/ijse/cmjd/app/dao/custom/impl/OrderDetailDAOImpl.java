package lk.ijse.cmjd.app.dao.custom.impl;

import lk.ijse.cmjd.app.entity.OrderDetail;
import lk.ijse.cmjd.app.entity.OrderDetailPK;
import lk.ijse.cmjd.app.dao.custom.OrderDetailDAO;
import lk.ijse.cmjd.app.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAOImpl implements OrderDetailDAO {

    @Override
    public OrderDetail find(OrderDetailPK key) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM OrderDetail WHERE orderId=? AND itemCode=?");
        pstm.setObject(1,key.getOrderId());
        pstm.setObject(2,key.getItemCode());
        ResultSet rst = pstm.executeQuery();
        if (rst.next()){
            String orderId = rst.getString("orderId");
            String itemCode = rst.getString("itemCode");
            int qty = rst.getInt("qty");
            double unitPrice = rst.getDouble("unitPrice");
            return new OrderDetail(orderId,itemCode,qty,unitPrice);
        }
        return null;
    }

    @Override
    public List<OrderDetail> findAll() throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM OrderDetail ");
        ResultSet rst = pstm.executeQuery();
        List<OrderDetail> alOrderDetails = new ArrayList<>();

        if (rst.next()){
            String orderId = rst.getString("orderId");
            String itemCode = rst.getString("itemCode");
            int qty = rst.getInt("qty");
            double unitPrice = rst.getDouble("unitPrice");
            OrderDetail orderDetail = new OrderDetail(orderId, itemCode, qty, unitPrice);
            alOrderDetails.add(orderDetail);
        }

        return alOrderDetails;
    }

    @Override
    public boolean save(OrderDetail entity) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("INSERT INTO OrderDetail VALUES (?,?,?,?)");
        pstm.setObject(1,entity.getOrderDetailPK().getOrderId());
        pstm.setObject(2,entity.getOrderDetailPK().getItemCode());
        pstm.setObject(3,entity.getQty());
        pstm.setObject(4,entity.getUnitPrice());
        return pstm.executeUpdate() > 0;
    }

    @Override
    public boolean update(OrderDetail entity) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("UPDATE OrderDetail SET qty=?, unitPrice=? WHERE orderId=? AND itemCode=?");
        pstm.setObject(3,entity.getOrderDetailPK().getOrderId());
        pstm.setObject(4,entity.getOrderDetailPK().getItemCode());
        pstm.setObject(1,entity.getQty());
        pstm.setObject(2,entity.getUnitPrice());
        return pstm.executeUpdate() > 0;
    }

    @Override
    public boolean delete(OrderDetailPK key) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("DELETE FROM OrderDetail WHERE orderId=? AND itemCode=?");
        pstm.setObject(1,key.getItemCode());
        pstm.setObject(2,key.getItemCode());
        return pstm.executeUpdate() > 0;
    }

    @Override
    public List<OrderDetail> find(String orderId) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM OrderDetail WHERE orderId=?");
        pstm.setObject(1,orderId);
        ResultSet rst = pstm.executeQuery();
        List<OrderDetail> alOrderDetails = new ArrayList<>();
        if (rst.next()){
            String itemCode = rst.getString("itemCode");
            int qty = rst.getInt("qty");
            double unitPrice = rst.getDouble("unitPrice");
            alOrderDetails.add(new OrderDetail(orderId,itemCode,qty,unitPrice));
        }
        return alOrderDetails;
    }
}
