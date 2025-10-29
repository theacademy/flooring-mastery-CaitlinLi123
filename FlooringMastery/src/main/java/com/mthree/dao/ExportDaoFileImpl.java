package com.mthree.dao;

import com.mthree.model.Order;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class ExportDaoFileImpl implements ExportDao{
    private List<Order> allOrders;
    private final static String ORDER_DIRECTORY = "Orders/";
    private final static String DELIMETER = ",";

    @Override
    public void exportData(Map<LocalDate, Map<Integer, Order>> orders) {
        // for export data (optional)
    }

    @Override
    public void writeToFile() {

    }
}
