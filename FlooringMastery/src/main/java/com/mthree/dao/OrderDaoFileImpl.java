package com.mthree.dao;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class OrderDaoFileImpl implements OrderDao {
    private Map<Integer,Order> orders = new HashMap<>();
    private String HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";
    public static final String DELIMITER = ",";
    public static final String DIRECTORYLOC = "Orders/";

    @Override
    public List<Order> findOrdersByDate(LocalDate date) throws PersistenceException {
        loadOrdersByDate(date);
        return new ArrayList<>(orders.values());
    }

    @Override
    public String getFileHeader() {
        return HEADER;
    }

    private void loadOrdersByDate(LocalDate date) throws PersistenceException {
        Scanner sc;

        int year = date.getYear(), month = date.getMonthValue(), day = date.getDayOfMonth();
        String dateStr = ""+(month < 10 ? "0"+month : month) + (day < 10 ? "0"+day : day) + year;
        String file = DIRECTORYLOC+"Orders_"+dateStr+".txt";

        // load the order file into memory
        try{
            sc = new Scanner(new BufferedReader(new FileReader(file)));

        }catch (FileNotFoundException e){
            throw new PersistenceException("-_- Could not load order data into memory.",e);
        }

        // load the file content into hashmap object
        String currLine;
        Order currOrder;

        // skip the headers
        int count = 0;

        while(sc.hasNextLine()){
            currLine = sc.nextLine();
            if(count != 0){
                currOrder = unmarshallOrder(currLine);
                orders.put(currOrder.getOrderNumber(),currOrder);
            }
            count++;
        }

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
