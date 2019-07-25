package lk.ijse.cmjd.app.dao.custom.impl;

import lk.ijse.cmjd.app.entity.CustomEntity;
import lk.ijse.cmjd.app.dao.custom.QueryDAO;
import lk.ijse.cmjd.app.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryDAOImpl implements QueryDAO {

    @Override
    public List<CustomEntity> findOrderDetailsWithItemDescriptions(String orderId) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT itemCode,qty,OrderDetail.unitPrice,description FROM OrderDetail\n" +
                "    INNER JOIN Item I on OrderDetail.itemCode = I.code WHERE orderId=?");
        pstm.setObject(1,orderId);
        ResultSet rst = pstm.executeQuery();
        List<CustomEntity> al = new ArrayList<>();

        while(rst.next()){
            CustomEntity customEntity = new CustomEntity(rst.getString(1),
                    rst.getInt(2),
                    rst.getDouble(3),
                    rst.getString(4));
            al.add(customEntity);
        }
        return al;
    }

    @Override
    public List<CustomEntity> findAllOrdersWithCustomerNameAndTotal() throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT o.id, o.date, o.customerId, C.name,\n" +
                "       SUM(Detail.qty * Detail.unitPrice) AS Total FROM Orders AS o\n" +
                " INNER JOIN Customer C on o.customerId = C.id\n" +
                "INNER JOIN OrderDetail Detail on o.id = Detail.orderId GROUP BY o.id");
        ResultSet rst = pstm.executeQuery();

        List<CustomEntity> al = new ArrayList<>();

        while(rst.next()){
            CustomEntity customEntity = new CustomEntity(rst.getString(1),
                    rst.getDate(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getDouble(5));
            al.add(customEntity);
        }
        return al;
    }
}
