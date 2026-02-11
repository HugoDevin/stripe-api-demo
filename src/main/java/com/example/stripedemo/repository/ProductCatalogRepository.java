package com.example.stripedemo.repository;

import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class ProductCatalogRepository {

    private static final Map<String, Long> PRODUCT_CATALOG_TABLE = Map.of(
            "Book", 1000L,
            "Pen", 500L,
            "Notebook", 750L
    );

    public Map<String, Long> findAllProducts() {
        return new LinkedHashMap<>(PRODUCT_CATALOG_TABLE);
    }

    public Long findPriceByProductName(String productName) {
        return PRODUCT_CATALOG_TABLE.get(productName);
    }
}
