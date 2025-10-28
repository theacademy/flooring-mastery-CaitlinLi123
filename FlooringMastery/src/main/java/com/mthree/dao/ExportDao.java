package com.mthree.dao;

import com.mthree.model.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public interface ExportDao {
    void exportData(Map<LocalDate, Map<Integer, Order>> map);

    void writeToFile();
}
