package com.mthree.service;

import com.mthree.dao.OrderDao;
import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ServiceImpl implements Service {
    private OrderDao orderDao;

    public ServiceImpl(OrderDao orderDao){
        this.orderDao = orderDao;
    }

    @Override
    public List<Order> getAllOrders(LocalDate date) throws PersistenceException {
        return orderDao.findOrdersByDate(date);
    }
}
