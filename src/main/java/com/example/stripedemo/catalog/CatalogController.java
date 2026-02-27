package com.example.stripedemo.catalog;

import com.example.stripedemo.common.error.ApiException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class CatalogController {
    private final ProductRepository repo;
    public CatalogController(ProductRepository repo) { this.repo = repo; }

    @PostMapping
    public Product create(@Valid @RequestBody CreateProductRequest req) {
        Product p = new Product();
        p.setSku(req.sku()); p.setName(req.name()); p.setPrice(req.price()); p.setCurrency(req.currency().toUpperCase());
        p.setActive(true); p.setCreatedAt(OffsetDateTime.now()); p.setUpdatedAt(OffsetDateTime.now());
        return repo.save(p);
    }

    @GetMapping public List<Product> all() { return repo.findAll(); }
    @GetMapping("/{sku}") public Product one(@PathVariable String sku) { return repo.findById(sku).orElseThrow(() -> new ApiException(404, "product not found")); }

    public record CreateProductRequest(@NotBlank String sku, @NotBlank String name, @NotNull @Min(1) Long price, @NotBlank String currency) {}
}
