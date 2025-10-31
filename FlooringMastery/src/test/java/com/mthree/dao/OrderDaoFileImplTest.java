package com.mthree.dao;

import com.mthree.exception.NoSuchOrderException;
import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class OrderDaoFileImplTest {
    OrderDao orderDao;
    Order firstOrder;
    LocalDate date;
    Order secondOrder;

    public OrderDaoFileImplTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        orderDao = ctx.getBean("orderDaoStub", OrderDaoStubImpl.class);
    }

    @BeforeEach
    public void setUp() {

        date = LocalDate.parse("06012013", DateTimeFormatter.ofPattern("MMddyyyy"));

        firstOrder = new Order(1);
        firstOrder.setCustomerName("Ada Lovelace");
        firstOrder.setState("CA");
        firstOrder.setTaxRate(new BigDecimal("25.00"));
        firstOrder.setProductType("Tile");
        firstOrder.setArea(new BigDecimal("249.00"));
        firstOrder.setCostPerSquareFoot(new BigDecimal("3.50"));
        firstOrder.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        firstOrder.setMaterialCost(firstOrder.getCostPerSquareFoot().multiply(firstOrder.getArea()));
        firstOrder.setLaborCost(firstOrder.getLaborCostPerSquareFoot().multiply(firstOrder.getArea()));
        firstOrder.setTax(firstOrder.getMaterialCost().add(firstOrder.getLaborCost())
                .multiply(firstOrder.getTaxRate().divide(new BigDecimal("100"))));
        firstOrder.setTotal(firstOrder.getMaterialCost().add(firstOrder.getLaborCost()).add(firstOrder.getTax()));

        secondOrder = new Order(2);
        secondOrder.setCustomerName("Grace Hopper");
        secondOrder.setState("TX");
        secondOrder.setTaxRate(new BigDecimal("4.45"));
        secondOrder.setProductType("Wood");
        secondOrder.setArea(new BigDecimal("300.00"));
        secondOrder.setCostPerSquareFoot(new BigDecimal("5.15"));
        secondOrder.setLaborCostPerSquareFoot(new BigDecimal("4.75"));

        secondOrder.setMaterialCost(
                secondOrder.getCostPerSquareFoot().multiply(secondOrder.getArea()));

        secondOrder.setLaborCost(
                secondOrder.getLaborCostPerSquareFoot().multiply(secondOrder.getArea()));

        secondOrder.setTax(
                (secondOrder.getMaterialCost().add(secondOrder.getLaborCost()))
                        .multiply(secondOrder.getTaxRate().divide(new BigDecimal("100"))));

        secondOrder.setTotal(
                secondOrder.getMaterialCost().add(secondOrder.getLaborCost()).add(secondOrder.getTax()));

    }

    @Test
    void testGetAllOrders() {
        Order orderInMemory = orderDao.getAllOrders().get(date).get(1);

        assertEquals(orderInMemory, firstOrder);

        assertNotEquals(orderInMemory, secondOrder);
    }

    @Test
    void testGetOrdersForDate() throws PersistenceException {
        assertEquals(orderDao.getOrdersForDate(date).get(0), firstOrder);
    }

    @Test
    void testAddOrder() throws PersistenceException, NoSuchOrderException {
        LocalDate date2 = LocalDate.parse("10302025", DateTimeFormatter.ofPattern("MMddyyyy"));

        orderDao.addOrder(date2, secondOrder);

        assertEquals(orderDao.getOrder(date2, secondOrder.getOrderNumber()), secondOrder);
    }

    @Test
    void testGetOrder() throws PersistenceException, NoSuchOrderException {
        assertEquals(orderDao.getOrder(date, 1), firstOrder);
    }

    @Test
    void editOrder() throws PersistenceException, NoSuchOrderException {
        Order orderInMemory = orderDao.getOrder(date, 1);

        orderInMemory.setArea(new BigDecimal("200"));

        orderDao.editOrder(date, orderInMemory);

        assertNotEquals(firstOrder, orderDao.getOrder(date, orderInMemory.getOrderNumber()));
    }

    @Test
    void removeOrder() throws PersistenceException {
        orderDao.removeOrder(date, 1);
        assertTrue(orderDao.getOrdersForDate(date).size() == 0);
    }
}