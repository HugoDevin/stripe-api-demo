package com.example.stripedemo.repository;

import com.example.stripedemo.model.CatalogProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCatalogRepository extends JpaRepository<CatalogProduct, String> {
    List<CatalogProduct> findAllByOrderByNameAsc();
}
