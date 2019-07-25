package lk.ijse.cmjd.app.business;

import lk.ijse.cmjd.app.dao.DAOFactory;
import lk.ijse.cmjd.app.dao.custom.ItemDAO;
import lk.ijse.cmjd.app.dao.custom.OrderDAO;
import lk.ijse.cmjd.app.dao.custom.OrderDetailDAO;
import lk.ijse.cmjd.app.dao.custom.QueryDAO;
import lk.ijse.cmjd.app.db.DBConnection;
import lk.ijse.cmjd.app.dto.OrderDTO;
import lk.ijse.cmjd.app.dto.OrderDTO2;
import lk.ijse.cmjd.app.dto.OrderDetailDTO;
import lk.ijse.cmjd.app.entity.CustomEntity;
import lk.ijse.cmjd.app.entity.Item;
import lk.ijse.cmjd.app.entity.Order;
import lk.ijse.cmjd.app.entity.OrderDetail;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageOrdersBusiness {

    private static OrderDAO orderDAO = (OrderDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER);
    private static OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER_DETAIL);
    private static ItemDAO itemDAO = (ItemDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ITEM);
    private static QueryDAO queryDAO = (QueryDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.QUERY);

    public static List<OrderDTO2> getOrdersWithCustomerNamesAndTotals() throws SQLException {

        List<CustomEntity> orders = queryDAO.findAllOrdersWithCustomerNameAndTotal();
        List<OrderDTO2> tmpDTOs = new ArrayList<>();

        for (CustomEntity order : orders) {
            tmpDTOs.add(new OrderDTO2(order.getOrderId(),
                    order.getOrderDate().toLocalDate(),
                    order.getCustomerId(),
                    order.getCustomerName(),
                    order.getTotal()));
        }

        return tmpDTOs;

    }

    public static List<OrderDTO> getOrders() throws SQLException {

        List<Order> orders = orderDAO.findAll();
        ArrayList<OrderDTO> tmpDTOs = new ArrayList<>();

        for (Order order : orders) {
            List<CustomEntity> orderDetails = queryDAO.findOrderDetailsWithItemDescriptions(order.getId());
            List<OrderDetailDTO> tmpOrderDetailsDtos = new ArrayList<>();

            for (CustomEntity ce : orderDetails) {
                tmpOrderDetailsDtos.add(new OrderDetailDTO(ce.getItemCode(),
                        ce.getDescription(),
                        ce.getQty(),
                        ce.getUnitPrice()));

            }

            OrderDTO dto = new OrderDTO(order.getId(),
                    order.getDate().toLocalDate(),
                    order.getCustomerId(), tmpOrderDetailsDtos);
            tmpDTOs.add(dto);
        }

        return tmpDTOs;
    }

    public static String generateOrderId() throws SQLException {
        return orderDAO.count() + 1 + "";
    }

    public static void createOrder(OrderDTO dto) throws SQLException {

        DBConnection.getConnection().setAutoCommit(false);

        try {

            boolean result = orderDAO.save(new Order(dto.getId(), Date.valueOf(dto.getDate()), dto.getCustomerId()));

            if (!result) {
                return;
            }

            for (OrderDetailDTO detailDTO : dto.getOrderDetailDTOS()) {
                result = orderDetailDAO.save(new OrderDetail(dto.getId(),
                        detailDTO.getCode(), detailDTO.getQty(), detailDTO.getUnitPrice()));

                if (!result) {
                    DBConnection.getConnection().rollback();
                    return;
                }

                Item item = itemDAO.find(detailDTO.getCode());
                int qty = item.getQtyOnHand() - detailDTO.getQty();
                item.setQtyOnHand(qty);
                itemDAO.update(item);

            }

            DBConnection.getConnection().commit();

        } catch (Exception ex) {
            DBConnection.getConnection().rollback();
            ex.printStackTrace();
        } finally {
            DBConnection.getConnection().setAutoCommit(true);
        }

    }

    public static OrderDTO findOrder(String orderId) throws SQLException {
        Order order = orderDAO.find(orderId);

        List<CustomEntity> orderDetails = queryDAO.findOrderDetailsWithItemDescriptions(order.getId());
        List<OrderDetailDTO> tmpOrderDetailsDtos = new ArrayList<>();

        for (CustomEntity ce : orderDetails) {
            tmpOrderDetailsDtos.add(new OrderDetailDTO(ce.getItemCode(),
                    ce.getDescription(),
                    ce.getQty(),
                    ce.getUnitPrice()));

        }

        return new OrderDTO(order.getId(), order.getDate().toLocalDate(), order.getCustomerId(), tmpOrderDetailsDtos);
    }
}
