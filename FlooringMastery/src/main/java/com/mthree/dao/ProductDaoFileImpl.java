package com.mthree.dao;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Product;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

@Component
public class ProductDaoFileImpl implements ProductDao{
    private Map<String,Product> allProducts;
    private final static String PRODUCT_FILE = "Data/Products.txt";
    private final static String DELIMETER = ",";

    public ProductDaoFileImpl(){
        this.allProducts = new HashMap<>();
    }

    @Override
    public void loadFile() throws PersistenceException {
        Scanner sc;

        // load the file reader
        try{
           sc = new Scanner(new BufferedReader(new FileReader(PRODUCT_FILE)));

        } catch (FileNotFoundException e) {
            throw new PersistenceException("-_- Could not load order data into memory.",e);
        }

        int count = 0;
        String currentLine;
        Product currProduct;

        while(sc.hasNextLine()){
            // read a line
            currentLine = sc.nextLine();

            // unmarshall data and put into the list
            if(count != 0){
                currProduct = unmarshallProduct(currentLine);
                allProducts.put(currProduct.getProductType(),currProduct);
            }

            count++;
        }

        // close the file reader
        sc.close();
    }

    private Product unmarshallProduct(String currentLine) {
        String[] productTokens = currentLine.split(DELIMETER);

        // extract information and record into the product object
        Product product = new Product();
        product.setProductType(productTokens[0]);
        product.setCostPerSquareFoot(new BigDecimal(productTokens[1]));
        product.setLaborCostPerSquareFoot(new BigDecimal(productTokens[2]));

        return product;
    }

    @Override
    public List<Product> getAllProducts() throws PersistenceException {
        loadFile();
        return new ArrayList<>(allProducts.values());
    }
}
