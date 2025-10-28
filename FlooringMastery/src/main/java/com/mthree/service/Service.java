package com.mthree.service;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
import com.mthree.model.Product;
import com.mthree.model.Tax;

import java.time.LocalDate;
import java.util.List;

public interface Service {
    List<Order> getOrdersForDate(LocalDate date) throws PersistenceException;

    int getNextOrderNumber();

    Order addOrder(LocalDate date, Order order);

    Order getOrder(LocalDate date, int orderNumber);

    Order editOrder(LocalDate date, Order order) throws PersistenceException;

    Order removeOrder(LocalDate date, int orderNumber);

    void exportData() throws PersistenceException;

    List<Tax> getTaxes();

    List<Product> getProducts();

    void writeToAudit(String message) throws PersistenceException;

    Order validateOrderNumber(LocalDate date, int orderNumber) throws PersistenceException;
}
