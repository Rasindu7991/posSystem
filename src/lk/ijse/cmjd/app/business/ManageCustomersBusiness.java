package lk.ijse.cmjd.app.business;

import lk.ijse.cmjd.app.dao.custom.CustomerDAO;
import lk.ijse.cmjd.app.dao.DAOFactory;
import lk.ijse.cmjd.app.dto.CustomerDTO;
import lk.ijse.cmjd.app.entity.Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageCustomersBusiness {

    private static CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.CUSTOMER);

    public static List<CustomerDTO> getCustomers() throws SQLException {
        List<Customer> allCustomers = customerDAO.findAll();
        List<CustomerDTO> tmpDTOs = new ArrayList<>();
        for (Customer customer : allCustomers) {
            CustomerDTO dto = new CustomerDTO(customer.getId(), customer.getName(), customer.getAddress());
            tmpDTOs.add(dto);
        }
        return tmpDTOs;
    }

    public static boolean createCustomer(CustomerDTO dto) throws SQLException {
        Customer customer = new Customer(dto.getId(), dto.getName(), dto.getAddress());
        return customerDAO.save(customer);
    }

    public static boolean updateCustomer(CustomerDTO dto) throws SQLException {
        Customer customer = new Customer(dto.getId(), dto.getName(), dto.getAddress());
        return customerDAO.update(customer);
    }

    public static boolean deleteCustomer(String customerID) throws SQLException {
        return customerDAO.delete(customerID);
    }

    public static CustomerDTO findCustomer(String id) throws SQLException {
        Customer customer = customerDAO.find(id);
        if (customer != null) {
            return new CustomerDTO(customer.getId(),
                    customer.getName(),
                    customer.getAddress());
        }
        return null;
    }

}
