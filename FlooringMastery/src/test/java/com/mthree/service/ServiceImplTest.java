package com.mthree.service;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceImplTest {
    private Service service;
    Order firstOrder;
    LocalDate date;
    Order secondOrder;

    public ServiceImplTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        service = ctx.getBean("service", Service.class);
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
    void testGetOrdersForDate() throws PersistenceException {
        List<Order> orders = service.getOrdersForDate(date);

        assertEquals(orders.get(0), firstOrder);
        assertTrue(orders.size() == 1);
    }

    @Test
    void getNextOrderNumber() {
        assertEquals(service.getNextOrderNumber(), 2);
    }

    @Test
    void testAddOrder() throws PersistenceException {
        LocalDate date2 = LocalDate.parse("10302025", DateTimeFormatter.ofPattern("MMddyyyy"));
        service.addOrder(date2, secondOrder);

        assertEquals(service.getOrder(date2, secondOrder.getOrderNumber()), secondOrder);
    }

    @Test
    void testGetOrder() throws PersistenceException {
        assertEquals(service.getOrder(date, 1), firstOrder);
    }

    @Test
    void testEditOrder() throws PersistenceException {
        Order orderInMemory = service.getOrder(date, 1);

        orderInMemory.setArea(new BigDecimal("200"));

        service.editOrder(date, orderInMemory);

        assertNotEquals(service.getOrder(date, 1), firstOrder);
    }

    @Test
    void testRemoveOrder() throws PersistenceException {
        Order orderInMemory = service.getOrder(date, 1);

        service.removeOrder(date, orderInMemory.getOrderNumber());

        assertTrue(service.getOrdersForDate(date).size() == 0);
    }

    @Test
    void testGetTaxes() throws PersistenceException {
        assertTrue(service.getTaxes().size() == 4);
    }

    @Test
    void testGetProducts() throws PersistenceException {
        assertTrue(service.getProducts().size() == 4);
    }

    @Test
    void testValidateOrderNumber() throws PersistenceException {
        assertEquals(firstOrder, service.validateOrderNumber(date, 1));
    }
}