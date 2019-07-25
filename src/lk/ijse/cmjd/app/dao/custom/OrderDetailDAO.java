package lk.ijse.cmjd.app.dao.custom;

import lk.ijse.cmjd.app.entity.OrderDetail;
import lk.ijse.cmjd.app.entity.OrderDetailPK;
import lk.ijse.cmjd.app.dao.CrudDAO;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailDAO extends CrudDAO<OrderDetail, OrderDetailPK> {

    List<OrderDetail> find(String orderId) throws SQLException;

}
