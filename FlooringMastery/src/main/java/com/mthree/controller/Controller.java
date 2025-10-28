package com.mthree.controller;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Order;
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
        try{
            while (keepGoing){
                menuSelection = getMenuSelection();

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
                        break;
                    default:
                        unknownCommand();
                }
                exitMessage();
            }
        }catch (Exception e){
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void exitMessage() {
    }

    private void unknownCommand() {
    }

    private void exportAllData() {
    }

    private void removeAnOrder() {
    }

    private void editAnOrder() {
    }

    private void addAnOrder() {

    }

    private void displayOrders() throws PersistenceException {
        view.displayDisplayAllBanner();
        LocalDate date = view.getDate();
        List<Order> orders = service.getAllOrders(date);
        view.displayOrderList(orders);
    }

    public int getMenuSelection(){
        return view.printMenuAndGetSelection();
    }
}
