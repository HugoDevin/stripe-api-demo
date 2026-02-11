package com.example.stripedemo.service;

import com.example.stripedemo.repository.ProductCatalogRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductCatalogService {

    private final ProductCatalogRepository productCatalogRepository;

    public ProductCatalogService(ProductCatalogRepository productCatalogRepository) {
        this.productCatalogRepository = productCatalogRepository;
    }

    @Cacheable(value = "product-catalog", key = "'all-products'")
    public Map<String, Long> getAllProducts() {
        return productCatalogRepository.findAllProducts();
    }

    @Cacheable(value = "product-price", key = "#productName")
    public Long getProductPrice(String productName) {
        return productCatalogRepository.findPriceByProductName(productName);
    }
}
