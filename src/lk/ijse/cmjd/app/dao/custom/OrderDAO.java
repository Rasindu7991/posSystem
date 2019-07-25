package lk.ijse.cmjd.app.dao.custom;

import lk.ijse.cmjd.app.entity.Order;
import lk.ijse.cmjd.app.dao.CrudDAO;

import java.sql.SQLException;

public interface OrderDAO extends CrudDAO<Order, String> {

    int count() throws SQLException;

}
