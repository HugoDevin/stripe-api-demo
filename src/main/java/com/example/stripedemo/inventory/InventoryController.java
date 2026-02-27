package com.example.stripedemo.inventory;

import com.example.stripedemo.common.error.ApiException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryRepository repo;
    public InventoryController(InventoryRepository repo) { this.repo = repo; }

    @GetMapping("/{sku}")
    public Inventory get(@PathVariable String sku) { return repo.findById(sku).orElseThrow(() -> new ApiException(404, "inventory not found")); }

    @PostMapping("/{sku}/adjust") @Transactional
    public Inventory adjust(@PathVariable String sku, @Valid @RequestBody AdjustRequest req) {
        Inventory i = repo.findById(sku).orElseGet(() -> { Inventory n = new Inventory(); n.setSku(sku); n.setAvailableQty(0L); n.setReservedQty(0L); n.setVersion(0L); return n; });
        long next = i.getAvailableQty() + req.delta();
        if (next < 0) throw new ApiException(409, "negative inventory");
        i.setAvailableQty(next); i.setUpdatedAt(OffsetDateTime.now());
        return repo.save(i);
    }
    public record AdjustRequest(@NotNull Long delta) {}
}
