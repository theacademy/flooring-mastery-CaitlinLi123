package com.mthree.dao;

import com.mthree.exception.PersistenceException;
import com.mthree.model.Product;

import java.util.List;

public interface ProductDao {
   void loadFile() throws PersistenceException;
   List<Product> getAllProducts() throws PersistenceException;
}
