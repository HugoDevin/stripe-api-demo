package com.example.ecommerce.catalog;

import com.example.ecommerce.common.error.AppException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
  private final ProductRepository repo;
  public ProductService(ProductRepository repo){this.repo=repo;}
  public Product create(Product p){return repo.save(p);}  public List<Product> list(){return repo.findAll();}
  public Product get(String sku){return repo.findById(sku).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,"product not found"));}
}
