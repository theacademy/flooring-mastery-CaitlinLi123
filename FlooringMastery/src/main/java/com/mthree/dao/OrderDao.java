package com.mthree.dao;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderDao {
    List<Order> findOrdersByDate(LocalDate date) throws PersistenceException;
    String getFileHeader();
}
