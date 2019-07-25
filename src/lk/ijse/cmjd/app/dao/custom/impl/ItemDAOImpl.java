package lk.ijse.cmjd.app.dao.custom.impl;

import lk.ijse.cmjd.app.entity.Item;
import lk.ijse.cmjd.app.dao.custom.ItemDAO;
import lk.ijse.cmjd.app.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOImpl implements ItemDAO {

    public List<Item> findAll() throws SQLException {
        ArrayList<Item> alItemS = new ArrayList<>();
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Item");
        ResultSet rst = pstm.executeQuery();
        while (rst.next()) {
            String code = rst.getString(1);
            String description = rst.getString(2);
            double unitPrice = rst.getDouble(3);
            int qty = rst.getInt(4);
            Item item = new Item(code, description, unitPrice, qty);
            alItemS.add(item);
        }
        return alItemS;
    }

    public boolean save(Item item) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("INSERT INTO Item VALUES (?,?,?,?)");
        pstm.setObject(1, item.getCode());
        pstm.setObject(2, item.getDescription());
        pstm.setObject(3, item.getUnitPrice());
        pstm.setObject(4, item.getQtyOnHand());
        return pstm.executeUpdate() > 0;
    }

    public boolean update(Item item) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("UPDATE Item SET description=?,unitPrice=?,qtyOnHand=? WHERE code=?");
        pstm.setObject(4, item.getCode());
        pstm.setObject(1, item.getDescription());
        pstm.setObject(2, item.getUnitPrice());
        pstm.setObject(3, item.getQtyOnHand());
        return pstm.executeUpdate() > 0;
    }

    public boolean delete(String code) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("DELETE FROM Item WHERE code=?");
        pstm.setObject(1, code);
        return pstm.executeUpdate() > 0;
    }

    @Override
    public Item find(String itemCode) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Item WHERE code=?");
        pstm.setObject(1,itemCode);
        ResultSet rst = pstm.executeQuery();
        if (rst.next()){
            return  new Item(rst.getString("code"),
                    rst.getString("description"),
                    rst.getDouble("unitPrice"),
                    rst.getInt("qtyOnHand"));
        }
        return null;
    }
}
