package lk.ijse.cmjd.app.dao.custom;

import lk.ijse.cmjd.app.entity.CustomEntity;
import lk.ijse.cmjd.app.dao.SuperDAO;

import java.sql.SQLException;
import java.util.List;

public interface QueryDAO extends SuperDAO {

    List<CustomEntity> findOrderDetailsWithItemDescriptions(String orderId)throws SQLException;

    List<CustomEntity> findAllOrdersWithCustomerNameAndTotal() throws SQLException;

}
