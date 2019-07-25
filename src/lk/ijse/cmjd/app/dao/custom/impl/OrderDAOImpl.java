package lk.ijse.cmjd.app.dao.custom.impl;

import lk.ijse.cmjd.app.dao.custom.OrderDAO;
import lk.ijse.cmjd.app.entity.Order;
import lk.ijse.cmjd.app.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public Order find(String key) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Orders WHERE id=?");
        pstm.setObject(1,key);
        ResultSet rst = pstm.executeQuery();
        if (rst.next()){
            return new Order(rst.getString("id"),
                    rst.getDate("date"),
                    rst.getString("customerId"));
        }
        return null;
    }

    @Override
    public List<Order> findAll() throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Orders");
        ResultSet rst = pstm.executeQuery();
        List<Order> alOrders = new ArrayList<>();
        while(rst.next()){
            Order order = new Order(rst.getString("id"),
                    rst.getDate("date"),
                    rst.getString("customerId"));
            alOrders.add(order);
        }
        return alOrders;
    }

    @Override
    public boolean save(Order entity) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("INSERT INTO Orders VALUES (?,?,?)");
        pstm.setObject(1,entity.getId());
        pstm.setObject(2,entity.getDate());
        pstm.setObject(3,entity.getCustomerId());
        return pstm.executeUpdate() > 0;
    }

    @Override
    public boolean update(Order entity) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("UPDATE Orders SET date=?, customerId=? WHERE id=?");
        pstm.setObject(3,entity.getId());
        pstm.setObject(1,entity.getDate());
        pstm.setObject(2,entity.getCustomerId());
        return pstm.executeUpdate() > 0;
    }

    @Override
    public boolean delete(String key) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("DELETE FROM Orders WHERE id=?");
        pstm.setObject(1,key);
        return pstm.executeUpdate() > 0;
    }

    @Override
    public int count() throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT COUNT(*) FROM `" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "Orders`");
        ResultSet rst = pstm.executeQuery();
        if (rst.next()){
            return rst.getInt(1);
        }
        return 0;
    }
}
