package com.example.stripedemo.repository;

import com.example.stripedemo.model.CatalogProduct;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductCatalogRepository {

    private final Map<String, CatalogProduct> productCatalogTable = new ConcurrentHashMap<>();

    public ProductCatalogRepository() {
        productCatalogTable.put("Book", new CatalogProduct("Book", 1000L, true));
        productCatalogTable.put("Pen", new CatalogProduct("Pen", 500L, true));
        productCatalogTable.put("Notebook", new CatalogProduct("Notebook", 750L, true));
    }

    public Map<String, CatalogProduct> findAllProducts() {
        return new LinkedHashMap<>(productCatalogTable);
    }

    public CatalogProduct findByProductName(String productName) {
        return productCatalogTable.get(productName);
    }

    public void updatePrice(String productName, long price) {
        productCatalogTable.computeIfPresent(productName, (name, product) -> product.withPrice(price));
    }

    public void updateActiveStatus(String productName, boolean active) {
        productCatalogTable.computeIfPresent(productName, (name, product) -> product.withActive(active));
    }
}
