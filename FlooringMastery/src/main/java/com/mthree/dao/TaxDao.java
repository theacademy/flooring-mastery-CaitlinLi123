package com.mthree.dao;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Tax;

import java.util.List;

public interface TaxDao {
    void loadFile() throws PersistenceException;
    List<Tax> getAllTaxes() throws PersistenceException;
}
