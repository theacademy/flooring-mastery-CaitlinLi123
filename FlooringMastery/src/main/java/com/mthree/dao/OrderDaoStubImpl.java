package com.mthree.dao;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDaoStubImpl implements OrderDao{
    public Order onlyOrder;
    public int largestOrderNumber;
    private Map<LocalDate, Map<Integer,Order>> orders = new HashMap<>();

    public OrderDaoStubImpl(){
        onlyOrder = new Order(1);
        onlyOrder.setCustomerName("Ada Lovelace");
        onlyOrder.setState("CA");
        onlyOrder.setTaxRate(new BigDecimal("25.00"));
        onlyOrder.setProductType("Tile");
        onlyOrder.setArea(new BigDecimal("249.00"));
        onlyOrder.setCostPerSquareFoot(new BigDecimal("3.50"));
        onlyOrder.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        onlyOrder.setMaterialCost(onlyOrder.getCostPerSquareFoot().multiply(onlyOrder.getArea()));
        onlyOrder.setLaborCost(onlyOrder.getLaborCostPerSquareFoot().multiply(onlyOrder.getArea()));
        onlyOrder.setTax(onlyOrder.getMaterialCost().add(onlyOrder.getLaborCost()).multiply(onlyOrder.getTaxRate().divide(new BigDecimal("100"))));
        onlyOrder.setTotal(onlyOrder.getMaterialCost().add(onlyOrder.getLaborCost()).add(onlyOrder.getTax()));
        largestOrderNumber = 1;

        // initialize all orders hashmap in memory
        Map<Integer, Order> map = new HashMap<>();
        map.put(1,onlyOrder);
        orders.put(LocalDate.parse("06012013", DateTimeFormatter.ofPattern("MMddyyyy")), map);
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) throws NullPointerException {
        return new ArrayList<>(orders.get(date).values());
    }

    @Override
    public void writeToFile() throws PersistenceException {
    }

    @Override
    public void loadFromFile() throws PersistenceException {
    }

    @Override
    public int getNextOrderNumber() {
        return largestOrderNumber+1;
    }

    @Override
    public Order addOrder(LocalDate date, Order order) {
        Map<Integer, Order> orderByDate = orders.getOrDefault(date, new HashMap<>());

        orderByDate.put(order.getOrderNumber(),order);

        // load into in-memory storage;
        orders.put(date,orderByDate);

        return order;
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) throws  NullPointerException{
        return orders.get(date).get(orderNumber);
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws PersistenceException {
        Map<Integer,Order> ordersByDate = orders.getOrDefault(date,new HashMap<>());

        if(ordersByDate.keySet().isEmpty() || !ordersByDate.containsKey(order.getOrderNumber())){
            throw new PersistenceException("No orders found by given date or order number");
        }else{
            ordersByDate.put(order.getOrderNumber(),order);
        }

        return order;
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() {
        return orders;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws PersistenceException {
        Order order;
        Map<Integer,Order> ordersByDate = orders.getOrDefault(date,new HashMap<>());

        if(ordersByDate.keySet().isEmpty() || !ordersByDate.containsKey(orderNumber)){
            throw new PersistenceException("No orders found by given date or order number");
        }else{
            order = ordersByDate.get(orderNumber);
            ordersByDate.remove(orderNumber);
        }

        return order;
    }
}
