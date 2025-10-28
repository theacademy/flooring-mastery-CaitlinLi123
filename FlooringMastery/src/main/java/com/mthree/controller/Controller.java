package com.mthree.controller;

import com.mthree.dao.OrderDao;
import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
import com.mthree.model.Product;
import com.mthree.model.Tax;
import com.mthree.service.Service;
import com.mthree.ui.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class Controller {
    private View view;
    private Service service;

    @Autowired
    public Controller(View view, Service service){
        this.view = view;
        this.service = service;
    }

    public void run(){
        boolean keepGoing = true;
        int menuSelection = 0;

            while (keepGoing){
                menuSelection = getMenuSelection();
                try{
                switch (menuSelection){
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addAnOrder();
                        break;
                    case 3:
                        editAnOrder();
                        break;
                    case 4:
                        removeAnOrder();
                        break;
                    case 5:
                        exportAllData();
                        break;
                    case 6:
                        keepGoing = false;
                        exitMessage();
                        break;
                    default:
                        unknownCommand();
                }}
                catch (Exception e){
                        view.displayErrorMessage(e.getMessage());
                    }
            }

    }

    private void exitMessage() {
        view.displayExitMessage();
    }

    private void unknownCommand() {
        view.displayUnknownCommandMessage();
    }

    private void exportAllData() throws PersistenceException {
        service.exportData();
    }

    private void removeAnOrder() throws PersistenceException {
        // display the banner
        view.displayRemoveOrderBanner();

        // Prompt the user for date and order
        Order order;
        LocalDate date;
        int orderNumber;

        while (true){
            // Prompt the user for date
            date = view.getDateInput();

            // Prompt the user for order numberl
            orderNumber = view.getOrderNumber();

            // validate the ordernumber in a given date orders
            order = service.validateOrderNumber(date,orderNumber);

            if(order == null){
                view.displayErrorMessage("Cannot find the order! Please try again.");
            }else{
                break;
            }
        }

        // Display the order info
        view.displayOrderInfo(order);

        // prompt the user if they are sure to delete the order
        boolean remove = view.getConfirmation("Are you sure you want to delete this order? [Y/N]: ");

        // If yes, then remove the order
        if(remove){
            service.removeOrder(date,orderNumber);
            view.displayRemoveOrderSuccess();
        }
    }

    private void editAnOrder() throws PersistenceException {
        // display the banner
        view.displayEditOrderBanner();

        Order order;
        LocalDate date;
        int orderNumber;

        while (true){
            // Prompt the user for date
            date = view.getDateInput();

            // Prompt the user for order numberl
            orderNumber = view.getOrderNumber();

            // validate the ordernumber in a given date orders
            order = service.validateOrderNumber(date,orderNumber);

            if(order == null){
                view.displayErrorMessage("Cannot find the order! Please try again.");
            }else{
                break;
            }
        }

        // Get the taxes and products
        List<Tax> taxes = service.getTaxes();
        List<Product> products = service.getProducts();

        // Prompt the user to edit each editable data
        order = view.getEditOrderInput(order,taxes, products);

        // Display the editted order
        view.displayOrderInfo(order);

        // Prompt the user for whether the edit should be saved
        boolean save = view.getConfirmation("Do you want to save the data? [Y/N]: ");

        if(save){
            service.editOrder(date, order);
            view.displayEditOrderSuccess();
        }
    }

    private void addAnOrder() throws PersistenceException {
        view.displayAddOrderBanner();

        // Prompt the user for the date
        LocalDate date = view.getFutureDate();

        // Get the next order input
        int orderNumber = service.getNextOrderNumber();

        // Get the taxes and products
        List<Tax> taxes = service.getTaxes();
        List<Product> products = service.getProducts();

        // Prompt the user for add order info
        Order order = view.getAddOrderInput(orderNumber,taxes,products);

        // display order details
        view.displayOrderInfo(order);

        // confirm the order
        boolean confirm = view.getConfirmation("Would you like to confirm your order? [Y/N]: ");

        if(confirm){
            Order orderAdded = service.addOrder(date,order);
            if(orderAdded != null){
                view.displayAddOrderSuccess();
            }
        }
    }

    private void displayOrders() throws PersistenceException {
        view.displayDisplayAllBanner();
        LocalDate date = view.getDateInput();
        List<Order> orders = service.getOrdersForDate(date);
        view.displayOrderList(orders);
    }

    public int getMenuSelection(){
        return view.printMenuAndGetSelection();
    }
}
