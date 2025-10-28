package com.mthree.service;

import com.mthree.dao.*;
import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
import com.mthree.model.Product;
import com.mthree.model.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class ServiceImpl implements Service {
    private OrderDao orderDao;
    private AuditDao auditDao;
    private ExportDao exportDao;
    private TaxDao taxDao;
    private ProductDao productDao;

    @Autowired
    public ServiceImpl(OrderDao orderDao, AuditDao auditDao, ExportDao exportDao, TaxDao taxDao, ProductDao productDao){
        this.orderDao = orderDao;
        this.auditDao = auditDao;
        this.exportDao = exportDao;
        this.taxDao = taxDao;
        this.productDao = productDao;
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) throws PersistenceException {
        return orderDao.getOrdersForDate(date);
    }

    @Override
    public int getNextOrderNumber() {
        return orderDao.getNextOrderNumber();
    }

    @Override
    public Order addOrder(LocalDate date, Order order) {
        return orderDao.addOrder(date,order);
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) {
        return null;
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws PersistenceException {
        return orderDao.editOrder(date,order);
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) {
        return orderDao.removeOrder(date,orderNumber);
    }

    @Override
    public void exportData() throws PersistenceException {
        orderDao.writeToFile();
    }

    @Override
    public List<Tax> getTaxes() {
        return taxDao.getAllTaxes();
    }

    @Override
    public List<Product> getProducts() {
        return productDao.getAllProducts();
    }

    @Override
    public void writeToAudit(String message) throws PersistenceException {
        auditDao.writeAuditEntry(message);
    }

    @Override
    public Order validateOrderNumber(LocalDate date, int orderNumber) throws PersistenceException {
        List<Order> orders = getOrdersForDate(date);

        for(Order order: orders){
            if(order.getOrderNumber() == orderNumber){
                return order;
            }
        }

        return null;
    }
}
