package lk.ijse.cmjd.app.dao.custom.impl;

import lk.ijse.cmjd.app.entity.Customer;
import lk.ijse.cmjd.app.dao.custom.CustomerDAO;
import lk.ijse.cmjd.app.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public Customer find(String customerId) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer WHERE id=?");
        pstm.setObject(1, customerId);
        ResultSet rst = pstm.executeQuery();
        if (rst.next()){
            return  new Customer(rst.getString("id"),
                    rst.getString("name"),
                    rst.getString("address"));
        }
        return null;
    }

    public List<Customer> findAll() throws SQLException {
        ArrayList<Customer> alCustomerS = new ArrayList<>();
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer");
        ResultSet rst = pstm.executeQuery();
        while (rst.next()) {
            String id = rst.getString(1);
            String name = rst.getString(2);
            String address = rst.getString(3);
            Customer customer = new Customer(id, name, address);
            alCustomerS.add(customer);
        }
        return alCustomerS;
    }

    public boolean save(Customer customer) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?)");
        pstm.setObject(1, customer.getId());
        pstm.setObject(2, customer.getName());
        pstm.setObject(3, customer.getAddress());
        return pstm.executeUpdate() > 0;
    }

    public boolean update(Customer customer) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("UPDATE Customer SET name=?,address=? WHERE id=?");
        pstm.setObject(3, customer.getId());
        pstm.setObject(1, customer.getName());
        pstm.setObject(2, customer.getAddress());
        return pstm.executeUpdate() > 0;
    }

    public boolean delete(String customerId) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("DELETE FROM Customer WHERE id=?");
        pstm.setObject(1, customerId);
        return pstm.executeUpdate() > 0;
    }

}
