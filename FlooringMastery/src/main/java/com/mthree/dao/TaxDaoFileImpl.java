package com.mthree.dao;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Tax;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

@Component
public class TaxDaoFileImpl implements TaxDao{
    private Map<String, Tax> allTaxes;
    private final static String TAX_FILE = "Data/Taxes.txt";
    private final static String DELIMETER = ",";

    public TaxDaoFileImpl(){
        this.allTaxes = new HashMap<>();
    }

    @Override
    public void loadFile() throws PersistenceException {
        Scanner sc;

        // load the file reader
        try{
            sc = new Scanner(new BufferedReader(new FileReader(TAX_FILE)));

        } catch (FileNotFoundException e) {
            throw new PersistenceException("-_- Could not load order data into memory.",e);
        }

        int count = 0;
        String currentLine;
        Tax currTax;

        while(sc.hasNextLine()){
            // read a line
            currentLine = sc.nextLine();

            // unmarshall data and put into the list
            if(count != 0){
                currTax = unmarshallTax(currentLine);
                allTaxes.put(currTax.getStateAbbreviation(),currTax);
            }

            count++;
        }

        // close the file reader
        sc.close();
    }

    private Tax unmarshallTax(String currentLine) {
        String[] taxTokens = currentLine.split(DELIMETER);

        Tax tax = new Tax();
        tax.setStateAbbreviation(taxTokens[0]);
        tax.setStateName(taxTokens[1]);
        tax.setTaxRate(new BigDecimal(taxTokens[2]));

        return tax;
    }


    @Override
    public List<Tax> getAllTaxes() throws PersistenceException {
        loadFile();
        return new ArrayList<>(allTaxes.values());
    }
}
