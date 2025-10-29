package com.mthree.dao;

import com.mthree.exception.NoSuchOrderException;
import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderDaoFileImpl implements OrderDao {
    private Map<LocalDate, Map<Integer, Order>> orders = new HashMap<>();
    public static final String DELIMITER = ",";
    public static final String ORDER_FOLDER = "Orders/";
    private static final String HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";
    private int largestOrderNumber;

    public OrderDaoFileImpl() throws PersistenceException {
        loadFromFile();
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) throws PersistenceException {
        loadOrdersByDate(date);
        return new ArrayList<>(orders.get(date).values());
    }

    @Override
    public void writeToFile() throws PersistenceException {
        PrintWriter out;

        for (LocalDate date : orders.keySet()) {
            writeToFile(date);
        }
    }

    public void writeToFile(LocalDate date) throws PersistenceException {
        PrintWriter out;

        // load the order file
        String filename = generateOrderFilename(date);

        try {
            out = new PrintWriter(new FileWriter(filename));
        } catch (IOException e) {
            throw new PersistenceException("Could not save student data.", e);
        }

        // get the corresponding order by date
        String orderAsText;
        Map<Integer, Order> orderMap = orders.get(date);

        // write the header
        out.println(HEADER);
        out.flush();

        // write to flie
        for (Order order : orderMap.values()) {
            orderAsText = marshallOrder(order);
            out.println(orderAsText);
            out.flush();
        }

        out.close();

    }

    private String marshallOrder(Order order) {
        String orderAsText = order.getOrderNumber() + DELIMITER
                + order.getCustomerName() + DELIMITER
                + order.getState() + DELIMITER
                + order.getTaxRate() + DELIMITER
                + order.getProductType() + DELIMITER
                + order.getArea() + DELIMITER
                + order.getCostPerSquareFoot() + DELIMITER
                + order.getLaborCostPerSquareFoot() + DELIMITER
                + order.getMaterialCost() + DELIMITER
                + order.getLaborCost() + DELIMITER
                + order.getTax() + DELIMITER
                + order.getTotal();

        return orderAsText;
    }

    @Override
    public void loadFromFile() throws PersistenceException {
        // load all the order into memory

        // search for all the files under /Orders
        File folder = new File(ORDER_FOLDER);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    String filename = listOfFiles[i].getName();
                    LocalDate date = getDateFromOrderFilename(filename);
                    loadOrdersByDate(date);
                }
            }
        }

    }

    public LocalDate getDateFromOrderFilename(String filename) {
        // extract the date from the filename
        StringBuilder sb = new StringBuilder();

        for (char c : filename.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }

        String dateStr = sb.toString();

        // the dateStr is in MM/dd/yyyy format
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MMddyyyy"));
        return date;
    }

    @Override
    public int getNextOrderNumber() {

        for (LocalDate date : orders.keySet()) {
            for (Integer orderNumber : orders.get(date).keySet()) {
                if (largestOrderNumber < orderNumber) {
                    largestOrderNumber = orderNumber;
                }
            }

        }

        return largestOrderNumber + 1;
    }

    @Override
    public Order addOrder(LocalDate date, Order order) {
        Map<Integer, Order> orderByDate = orders.getOrDefault(date, new HashMap<>());

        orderByDate.put(order.getOrderNumber(), order);

        // load into in-memory storage;
        orders.put(date, orderByDate);

        return order;
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) throws NoSuchOrderException {
        try {
            return orders.get(date).get(orderNumber);
        } catch (NullPointerException e) {
            throw new NoSuchOrderException(
                    "Order with date: " + date.toString() + " and order number: " + orderNumber + " not found", e);
        }
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws PersistenceException {
        // update in memory storage
        Map<Integer, Order> map = orders.get(date);
        map.put(order.getOrderNumber(), order);
        orders.put(date, map);

        // write to file
        writeToFile(date);

        return order;
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() {
        return orders;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) {
        // delete the order from the in memory storage
        Map<Integer, Order> map = orders.get(date);
        Order order = map.get(orderNumber);
        map.remove(orderNumber);
        orders.put(date, map);
        return order;
    }

    private String generateOrderFilename(LocalDate date) {
        int year = date.getYear(), month = date.getMonthValue(), day = date.getDayOfMonth();
        String dateStr = "" + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day) + year;
        String file = ORDER_FOLDER + "Orders_" + dateStr + ".txt";
        return file;
    }

    private void loadOrdersByDate(LocalDate date) throws PersistenceException {
        Scanner sc;

        String file = generateOrderFilename(date);

        // load the order file into memory
        try {
            sc = new Scanner(new BufferedReader(new FileReader(file)));

        } catch (FileNotFoundException e) {
            throw new PersistenceException("-_- File: " + file + " is not found.", e);
        }

        // load the file content into hashmap object
        String currLine;
        Order currOrder;

        // skip the headers
        int count = 0;

        Map<Integer, Order> allOrders = new HashMap<>();
        while (sc.hasNextLine()) {
            currLine = sc.nextLine();
            if (count != 0) {
                currOrder = unmarshallOrder(currLine);
                allOrders.put(currOrder.getOrderNumber(), currOrder);
            }
            count++;
        }
        orders.put(date, allOrders);

        // close the scanner
        sc.close();
    }

    private Order unmarshallOrder(String currLine) {
        // Example line:
        // 1,Ada Lovelace,CA,25.00,Tile,249.00,3.50,4.15,871.50,1033.35,476.21,2381.06
        String[] orderTokens = currLine.split(DELIMITER);

        Order orderFromFile = new Order(Integer.parseInt(orderTokens[0]));

        orderFromFile.setCustomerName(orderTokens[1]);

        orderFromFile.setState(orderTokens[2]);

        orderFromFile.setTaxRate(new BigDecimal(orderTokens[3]));

        orderFromFile.setProductType(orderTokens[4]);

        orderFromFile.setArea(new BigDecimal(orderTokens[5]));

        orderFromFile.setCostPerSquareFoot(new BigDecimal(orderTokens[6]));

        orderFromFile.setLaborCostPerSquareFoot(new BigDecimal(orderTokens[7]));

        orderFromFile.setMaterialCost(new BigDecimal(orderTokens[8]));

        orderFromFile.setLaborCost(new BigDecimal(orderTokens[9]));

        orderFromFile.setTax(new BigDecimal(orderTokens[10]));

        orderFromFile.setTotal(new BigDecimal(orderTokens[11]));

        return orderFromFile;
    }
}
