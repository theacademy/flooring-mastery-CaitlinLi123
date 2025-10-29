package com.mthree.dao;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderDao {
    List<Order> getOrdersForDate(LocalDate date) throws PersistenceException;

    void writeToFile() throws PersistenceException;

    void loadFromFile() throws PersistenceException;

    int getNextOrderNumber();

    Order addOrder(LocalDate date, Order order);

    Order getOrder(LocalDate date, int orderNumber) throws PersistenceException;

    Order editOrder(LocalDate date, Order order) throws PersistenceException;

    Map<LocalDate, Map<Integer,Order>> getAllOrders();

    Order removeOrder(LocalDate date, int orderNumber) throws PersistenceException;
}
