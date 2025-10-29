package com.mthree.ui;

import com.mthree.model.Order;
import com.mthree.model.Product;
import com.mthree.model.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
public class View {
    private final UserIO io;
    private static final String DELIMITER = ",";
    private static final String ORDER_HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";
    private static final String INFO_DISPLAY_SEPERATION_LINE = "<><><><><><><><><><><><><><><><><><><><><><><><><><><>";
    private static final String TAX_HEADER = "State,StateName,TaxRate";
    private static final String PRODUCT_HEADER = "ProductType,CostPerSquareFoot,LaborCostPerSquareFoot";

    @Autowired
    public View(UserIO io) {
        this.io = io;
    }

    public int printMenuAndGetSelection() {
        // display the menu
        System.out.println(
                """
                        * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
                        * <<Flooring Program>>
                        * 1. Display Orders
                        * 2. Add an Order
                        * 3. Edit an Order
                        * 4. Remove an Order
                        * 5. Export All Data
                        * 6. Quit
                        *
                        * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *""");

        return io.readInt("Please select from the above choices", 1, 6);
    }

    public void displayErrorMessage(String message) {
        io.print("🚫" + message);
    }

    public void displayDisplayAllBanner() {
        io.print("=== Display All Orders ===");
    }

    public LocalDate getDateInput() {
        return io.readDate("Please enter a date [dd/MM/yyyy]:");
    }

    public void displayOrderList(List<Order> orders) {
        io.print(INFO_DISPLAY_SEPERATION_LINE);
        io.print(ORDER_HEADER);
        for (Order order : orders) {
            displayOrderInfo(order);
        }
        io.print(INFO_DISPLAY_SEPERATION_LINE);
    }

    public void displayOrderInfo(Order order) {
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

    public void displayAddOrderBanner() {
        io.print("=== Add Order ===");
    }

    public Order getAddOrderInput(int orderNumber, List<Tax> taxes, List<Product> products) {

        // create a new Order object
        Order order = new Order(orderNumber);

        // prompt the user for customerName
        String customerName = getAndValidateNameInput("Please enter the customer name[only letters and numbers]: ",false);
        order.setCustomerName(customerName);

        // prompt the user for state and get the Tax information, the prompt should not be null
        displayAllTaxes(taxes);
        Tax tax = getAndValidateStateInput(taxes,false);
        order = recordOrderTax(tax,order);

        // display available products
        displayAllProducts(products);
        // prompt user for product type
        Product product = getAndValidateProductInput(products, false);
        order = recordOrderProduct(product,order);

        // prompt the user for area
        BigDecimal area = getAndValidateAreaInput(false);
        order.setArea(area);

        // calculate the materialCost
        order = calculateCosts(order);

        return order;
    }

    public boolean hitEnter(String input){

        return input == null || input.isEmpty();
    }

    public void displayAllTaxes(List<Tax> taxes) {
        io.print("------Here's all the available states and corresponding tax information------");
        io.print(INFO_DISPLAY_SEPERATION_LINE);
        io.print(TAX_HEADER);
        for (Tax tax : taxes) {
            // ProductType,CostPerSquareFoot,LaborCostPerSquareFoot
            String info = tax.getStateAbbreviation() + DELIMITER
                    + tax.getStateName() + DELIMITER
                    + tax.getTaxRate();

            io.print(info);
        }
        io.print(INFO_DISPLAY_SEPERATION_LINE);
        io.print("------------------------------------------------------------------------------");
    }

    public void displayAllProducts(List<Product> products) {
        io.print("------Here's all the available products------");
        io.print(INFO_DISPLAY_SEPERATION_LINE);
        io.print(PRODUCT_HEADER);
        for (Product product : products) {
            // ProductType,CostPerSquareFoot,LaborCostPerSquareFoot
            String info = product.getProductType() + DELIMITER
                    + product.getCostPerSquareFoot() + DELIMITER
                    + product.getLaborCostPerSquareFoot();

            io.print(info);
        }
        io.print(INFO_DISPLAY_SEPERATION_LINE);
        io.print("----------------------------------------------");
    }

    public void displayAddOrderSuccess() {
        io.print("You have added the order in the memory.");
        io.print("========================================");
    }

    public void displayEditOrderBanner() {
        io.print("=== Edit Order ===");
    }

    public Order recordOrderTax(Tax tax, Order order){
        order.setTaxRate(tax.getTaxRate());
        order.setState(tax.getStateAbbreviation());
        return order;
    }

    public Order recordOrderProduct(Product product, Order order){
        order.setProductType(product.getProductType());
        order.setCostPerSquareFoot(product.getCostPerSquareFoot());
        order.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());
        return order;
    }

    public Order calculateCosts(Order order){
        // calculate the materialCost
        order.setMaterialCost(order.getCostPerSquareFoot().multiply(order.getArea()));

        // calculate the laborCost
        order.setLaborCost(order.getLaborCostPerSquareFoot().multiply(order.getArea()));

        // calculate the tax
        order.setTax((order.getMaterialCost().add(order.getLaborCost()))
                .multiply(order.getTaxRate().divide(new BigDecimal("100"),RoundingMode.UP)).setScale(2,RoundingMode.HALF_EVEN));

        // calculate total
        order.setTotal(order.getMaterialCost().add(order.getLaborCost()).add(order.getTax()));

        return order;
    }

    public Order getEditOrderInput(Order order, List<Tax> taxes, List<Product> products) {
        // customer name
        String customerName = getAndValidateNameInput("Please enter the customer name[only letters and numbers]: ",true);

        // if user hits the enter key, then preserve the original data
        if(customerName != null && !customerName.equals(order.getCustomerName())){
            order.setCustomerName(customerName);
        }

        boolean change = false;

        // state
        Tax tax = getAndValidateStateInput(taxes,true);

        if (tax != null && !tax.getStateAbbreviation().equals(order.getState())) {
            // reset the new tax info
            order = recordOrderTax(tax,order);
            change = true;
        }

        // product type
        Product product = getAndValidateProductInput(products, true);
        if (product != null && !product.getProductType().equals(order.getProductType())) {
            order = recordOrderProduct(product,order);
            change = true;
        }

        // area
        BigDecimal area = getAndValidateAreaInput(true);
        if (area != null && !area.equals(order.getArea())) {
            order.setArea(area);
            change = true;
        }

        if (change) {
            calculateCosts(order);
        }

        return order;
    }

    public void displayEditOrderSuccess() {
        io.print("You have edited the order and saved it to the file.");
        io.print("========================================");
    }

    public void displayRemoveOrderBanner() {
        io.print("=== Remove Order ===");
    }

    public boolean getConfirmation(String message) {

        while (true) {
            String selecion = io.readString(message);
            if (selecion.equalsIgnoreCase("Y")) {
                return true;
            } else if (selecion.equalsIgnoreCase("N")) {
                return false;
            } else {
                io.print("Please try again and type either Y or N");
            }
        }

    }

    public void displayRemoveOrderSuccess() {
        io.print("Succeed in removing the order.");
    }

//    public void displayExportDataSuccess() {
//        io.print("Succeed in exporting all in memory orders.");
//    }

    public void displayExitMessage() {
        io.print("===========GOOD BYE!==============");
    }

    public void displayUnknownCommandMessage() {
        io.print("🚫Unknown command.");
    }

    public LocalDate getFutureDate() {
        while (true) {
            io.print("Please enter a future date for the order below.");
            LocalDate date = getDateInput();
            LocalDate today = LocalDate.now();

            if (date.isAfter(today)) {
                return date;
            } else {
                io.print("Please try again and give a future date!");
            }

        }
    }

    public String getAndValidateNameInput(String message, boolean skip) {
        String customerName;

        while (true) {
            customerName = io.readString(message);

            if(skip && hitEnter(customerName)){
                return null;
            }

            if (customerName.matches("^[a-zA-Z0-9 ]+$")) {
                break;
            } else {
                io.print("🚫Invalid input. Please try again");
            }
        }

        return customerName;
    }

    public Tax getAndValidateStateInput(List<Tax> taxes, boolean skip) {
        String state;

        while (true) {
            state = io.readString("Please enter the state name in either full or abbreviated version: ");

            // hits enter -> stay the same
            if(skip && hitEnter(state)){
                return null;
            }

            for (Tax tax : taxes) {
                if (tax.getStateAbbreviation().equals(state) || tax.getStateName().equals(state)) {
                    return tax;
                }
            }

            io.print("Unfound tax information from input state. Please try again");
        }
    }

    public Product getAndValidateProductInput(List<Product> products, boolean skip) {
        String productType;

        while (true) {
            productType = io.readString("Please enter the productType: ");

            // hits enter
            if(skip && hitEnter(productType)){
                return null;
            }

            for (Product product : products) {
                if (product.getProductType().toLowerCase().equals(productType)) {
                    return product;
                }
            }

            io.print("Unfound product type. Please try again");
        }
    }

    public BigDecimal getAndValidateAreaInput(boolean skip) {
        String areaInput;
        BigDecimal area = new BigDecimal("0");

        while (true) {

            try {
                areaInput = io.readString("Please enter the area in sq ft(min:100 sq ft): ");

                if(skip && hitEnter(areaInput)){
                    return null;
                }

                area = new BigDecimal(areaInput);
            } catch (NumberFormatException e) {
                displayErrorMessage("Please try again and enter a number!");
            }

            if (area.compareTo(new BigDecimal(100)) < 0) {
                displayErrorMessage("Please try again and enter a number not smaller than 100!");
            } else {
                return area;
            }

        }
    }

    public int getOrderNumber() {
        return io.readInt("Please give the order number");
    }

    public void displayExportDataBanner() {
        io.print("=== Export in-memory data to files ===");
    }

    public void displayExportSuccessBanner() {
        io.print("Succeed in exporting all in-memory storage to files.");
    }
}
