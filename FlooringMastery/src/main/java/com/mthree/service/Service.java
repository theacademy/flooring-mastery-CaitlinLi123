package com.mthree.service;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface Service {
    List<Order> getAllOrders(LocalDate date) throws PersistenceException;
}
