package com.example.stripedemo.service;

import com.example.stripedemo.model.CatalogProduct;
import com.example.stripedemo.repository.ProductCatalogRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductCatalogService {

    private final ProductCatalogRepository productCatalogRepository;

    public ProductCatalogService(ProductCatalogRepository productCatalogRepository) {
        this.productCatalogRepository = productCatalogRepository;
    }

    @Cacheable(value = "product-catalog", key = "'all-products'")
    public Map<String, CatalogProduct> getAllProductsForAdmin() {
        return productCatalogRepository.findAllByOrderByNameAsc().stream()
                .collect(Collectors.toMap(
                        CatalogProduct::getName,
                        Function.identity(),
                        (left, right) -> right,
                        LinkedHashMap::new
                ));
    }

    @Cacheable(value = "storefront-products", key = "'active-products'")
    public Map<String, Long> getAllProducts() {
        return getAllProductsForAdmin().values().stream()
                .filter(CatalogProduct::isActive)
                .collect(Collectors.toMap(
                        CatalogProduct::getName,
                        CatalogProduct::getPrice,
                        (left, right) -> right,
                        LinkedHashMap::new
                ));
    }

    @Cacheable(value = "product-price", key = "#productName")
    public Long getProductPrice(String productName) {
        CatalogProduct product = productCatalogRepository.findById(productName).orElse(null);
        if (product == null || !product.isActive()) {
            return null;
        }
        return product.getPrice();
    }

    public List<CatalogProduct> getAdminProductList() {
        return getAllProductsForAdmin().values().stream().toList();
    }

    @CacheEvict(value = {"product-catalog", "storefront-products", "product-price"}, allEntries = true)
    public void updatePrice(String productName, long price) {
        CatalogProduct product = productCatalogRepository.findById(productName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + productName));
        product.setPrice(price);
        productCatalogRepository.save(product);
    }

    @CacheEvict(value = {"product-catalog", "storefront-products", "product-price"}, allEntries = true)
    public void updateActiveStatus(String productName, boolean active) {
        CatalogProduct product = productCatalogRepository.findById(productName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + productName));
        product.setActive(active);
        productCatalogRepository.save(product);
    }
}
