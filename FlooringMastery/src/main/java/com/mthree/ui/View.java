package com.mthree.ui;

import com.mthree.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class View {
    private UserIO io;
    private final String DELIMITER = ",";

    @Autowired
    public View(UserIO io){
        this.io = io;
    }

    public int printMenuAndGetSelection() {
        // display the menu
        System.out.println(
                "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n"+
                "* <<Flooring Program>>\n"+
                "* 1. Display Orders\n"+
                "* 2. Add an Order\n"+
                "* 3. Edit an Order\n"+
                "* 4. Remove an Order\n"+
                "* 5. Export All Data\n"+
                "* 6. Quit\n"+
                "*\n"+
                "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *"
        );

        return io.readInt("Please select from the above choices",1,6);
    }

    public void displayErrorMessage(String message) {
        io.print("🚫"+message);
    }

    public void displayDisplayAllBanner() {
        io.print("=== Display All Orders ===");
    }

    public LocalDate getDate() {
        return io.readDate("Please enter a date [dd/MM/yyyy]:");
    }

    public void displayOrderList(List<Order> orders) {
        for(Order order: orders){
            String orderInfo = order.getOrderNumber() + DELIMITER
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
            io.print(orderInfo);
        }
    }
}
