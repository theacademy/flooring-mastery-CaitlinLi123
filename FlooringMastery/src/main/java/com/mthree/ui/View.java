package com.mthree.ui;

import com.mthree.model.Order;
import com.mthree.model.Product;
import com.mthree.model.Tax;
import jdk.jshell.execution.LoaderDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class View {
    private UserIO io;
    private final String DELIMITER = ",";
    private static int nextAvailableOrderNum;

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

    public LocalDate getDateInput() {
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

    public void displayOrderInfo(Order order){
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

    public Order getAddOrderInput(int orderNumber, List<Tax> taxes, List<Product> products){

        // create a new Order object
        Order order = new Order(orderNumber);

        // prompt the user for customerName
        String customerName = getAndValidateNameInput("Please enter the customer name[only letters and numbers]: ");
        order.setCustomerName(customerName);

        // prompt the user for state and get the Tax information
        Tax tax = getAndValidateStateInput(taxes);
        order.setTaxRate(tax.getTaxRate());
        order.setState(tax.getStateAbbreviation());

        // display available products
        displayAllProducts(products);
        // prompt user for product type
        Product product = getAndValidateProductInput(products);
        order.setProductType(product.getProductType());
        order.setCostPerSquareFoot(product.getCostPerSquareFoot());
        order.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());

        // prompt the user for area
        BigDecimal area = getAndValidateAreaInput();
        order.setArea(area);

        // calculate the materialCost
        order.setMaterialCost(order.getCostPerSquareFoot().multiply(order.getArea()));

        // calculate the laborCost
        order.setLaborCost(order.getLaborCostPerSquareFoot().multiply(order.getArea()));

        // calculate the tax
        order.setTax((order.getMaterialCost().add(order.getLaborCost())).multiply(order.getTaxRate().divide(new BigDecimal("100"))));

        // calculate total
        order.setTotal(order.getMaterialCost().add(order.getLaborCost()).add(order.getTax()));

        return order;
    }

    public void displayAllProducts(List<Product> products){
        io.print("------Here's all the available products------");
        for(Product product: products){
            // ProductType,CostPerSquareFoot,LaborCostPerSquareFoot
            String info = product.getProductType() + DELIMITER
                        + product.getCostPerSquareFoot() + DELIMITER
                    + product.getLaborCostPerSquareFoot();

            io.print(info);
        }
        io.print("----------------------------------------------");
    };

    public void displayAddOrderSuccess(){
        io.print("You have added the order in the memory.");
        io.print("========================================");
    }

    public void displayEditOrderBanner(){
        io.print("=== Edit Order ===");
    }

    public Order getEditOrderInput(Order order, List<Tax> taxes, List<Product> products){
        // customer name
        String customerName = getAndValidateNameInput("Please enter the customer name[only letters and numbers]: ");
        order.setCustomerName(customerName);

        boolean change = false;

        // state
        Tax tax = getAndValidateStateInput(taxes);

        if(!tax.getStateAbbreviation().equals(order.getState())){
            // reset the new tax info
            order.setTaxRate(tax.getTaxRate());
            order.setState(tax.getStateAbbreviation());
            change = true;
        }

        // product type
        Product product = getAndValidateProductInput(products);
        if(!product.getProductType().equals(order.getProductType())){
            order.setProductType(product.getProductType());
            order.setCostPerSquareFoot(product.getCostPerSquareFoot());
            order.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());
            change = true;
        }

        // area
        BigDecimal area = getAndValidateAreaInput();
        if(!area.equals(order.getArea())){
            order.setArea(area);
            change = true;
        }

        if(change){
            // calculate the materialCost
            order.setMaterialCost(order.getCostPerSquareFoot().multiply(order.getArea()));

            // calculate the laborCost
            order.setLaborCost(order.getLaborCostPerSquareFoot().multiply(order.getArea()));

            // calculate the tax
            order.setTax((order.getMaterialCost().add(order.getLaborCost())).multiply(order.getTaxRate().divide(new BigDecimal("100"))));

            // calculate total
            order.setTotal(order.getMaterialCost().add(order.getLaborCost()).add(order.getTax()));
        }

        return order;
    }

    public void displayEditOrderSuccess(){
        io.print("You have edited the order and saved it to the file.");
        io.print("========================================");
    }

    public void displayRemoveOrderBanner(){
        io.print("=== Remove Order ===");
    }

    public boolean getConfirmation(String message){

        while (true){
            String selecion = io.readString(message);
            if(selecion.toUpperCase().equals("Y")){
                return true;
            }else if(selecion.toUpperCase().equals("N")){
                return false;
            }else{
                io.print("Please try again and type either Y or N");
            }
        }

    }

    public void displayRemoveOrderSuccess(){

    }

    public void displayExportDataSuccess(){

    }

    public void displayExitMessage(){
        io.print("===========GOOD BYE!==============");
    }

    public void displayUnknownCommandMessage(){
        io.print("🚫Unknown command.");
    }

    public LocalDate getFutureDate(){
        while (true){
            io.print("Please enter a future date for the order below.");
            LocalDate date = getDateInput();
            LocalDate today = LocalDate.now();

            if(date.isAfter(today)){
                return date;
            }else{
                io.print("Please try again and give a future date!");
            }

        }
    }

    public String getAndValidateNameInput(String message){
        String customerName;

        while(true){
            customerName = io.readString(message);
            if(customerName.matches("^[a-zA-Z0-9 ]+$")){
                break;
            }else{
                io.print("🚫Invalid input. Please try again");
            }
        }

        return customerName;
    }

    public Tax getAndValidateStateInput(List<Tax> taxes){
        String state;

        while(true){
            state = io.readString("Please enter the state name in either full or abbreviated version: ");

            for(Tax tax: taxes){
                if(tax.getStateAbbreviation().equals(state) || tax.getStateName().equals(state)){
                    return tax;
                }
            }

            io.print("Unfound tax information from input state. Please try again");
        }
    }

    public Product getAndValidateProductInput(List<Product> products){
        String productType;

        while (true){
            productType = io.readString("Please enter the productType: ");

            for(Product product: products){
                if(product.getProductType().equals(productType)){
                    return product;
                }
            }

            io.print("Unfound product type. Please try again");
        }
    }

    public BigDecimal getAndValidateAreaInput(){
        String areaInput;
        BigDecimal area = new BigDecimal("0");

        while(true){

            try{
                areaInput = io.readString("Please enter the area in sq ft(min:100 sq ft): ");
                area = new BigDecimal(areaInput);
            }catch (NumberFormatException e){
                displayErrorMessage("Please try again and enter a number!");
            }

            if(area.compareTo(new BigDecimal(100)) == -1){
                displayErrorMessage("Please try again and enter a number not smaller than 100!");
            }else{
                return area;
            }

        }
    }

    public int getOrderNumber() {
        int orderNumber = io.readInt("Please give the order number");
        return orderNumber;
    }
}
