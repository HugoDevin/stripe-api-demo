package com.example.stripedemo.service;

import com.example.stripedemo.model.CatalogProduct;
import com.example.stripedemo.repository.ProductCatalogRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductCatalogService {

    private final ProductCatalogRepository productCatalogRepository;

    public ProductCatalogService(ProductCatalogRepository productCatalogRepository) {
        this.productCatalogRepository = productCatalogRepository;
    }

    @Cacheable(value = "product-catalog", key = "'all-products'")
    public Map<String, CatalogProduct> getAllProductsForAdmin() {
        return productCatalogRepository.findAllProducts();
    }

    public Map<String, Long> getAllProducts() {
        return getAllProductsForAdmin().values().stream()
                .filter(CatalogProduct::isActive)
                .collect(java.util.stream.Collectors.toMap(
                        CatalogProduct::getName,
                        CatalogProduct::getPrice,
                        (left, right) -> right,
                        java.util.LinkedHashMap::new
                ));
    }

    @Cacheable(value = "product-price", key = "#productName")
    public Long getProductPrice(String productName) {
        CatalogProduct product = productCatalogRepository.findByProductName(productName);
        if (product == null || !product.isActive()) {
            return null;
        }
        return product.getPrice();
    }

    public List<CatalogProduct> getAdminProductList() {
        return getAllProductsForAdmin().values().stream().toList();
    }

    @CacheEvict(value = {"product-catalog", "product-price"}, allEntries = true)
    public void updatePrice(String productName, long price) {
        productCatalogRepository.updatePrice(productName, price);
    }

    @CacheEvict(value = {"product-catalog", "product-price"}, allEntries = true)
    public void updateActiveStatus(String productName, boolean active) {
        productCatalogRepository.updateActiveStatus(productName, active);
    }
}
