package com.example.ecommerce.admin.web;

import com.example.ecommerce.admin.service.AdminUserService;
import com.example.ecommerce.catalog.*;
import com.example.ecommerce.inventory.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminConsoleController {
  private final AdminUserService adminUserService;
  private final ProductRepository productRepository;
  private final InventoryRepository inventoryRepository;
  public AdminConsoleController(AdminUserService a, ProductRepository p, InventoryRepository i){adminUserService=a;productRepository=p;inventoryRepository=i;}

  @GetMapping("/admin/login") String login(){ return "admin/login"; }
  @GetMapping("/admin/register") String register(){ return "admin/register"; }
  @PostMapping("/admin/register") String registerPost(@RequestParam @Email String email,@RequestParam @Size(min=8) String password,@RequestParam String name, Model model){
    adminUserService.register(email,password,name); model.addAttribute("message","Registration submitted. Please verify email then wait super admin enablement."); return "admin/register";
  }
  @GetMapping("/admin/verify-email") String verify(@RequestParam String token, Model model){ model.addAttribute("ok", adminUserService.verifyEmail(token)); return "admin/verify-result"; }

  @GetMapping("/admin") String root(){ return "redirect:/admin/dashboard"; }
  @GetMapping("/admin/dashboard") String dashboard(){ return "admin/dashboard"; }

  @GetMapping("/admin/products") String products(Model m){ m.addAttribute("products", productRepository.findAll()); return "admin/products/list"; }
  @GetMapping("/admin/products/new") String newProduct(Model m){ m.addAttribute("p", new Product()); return "admin/products/edit"; }
  @GetMapping("/admin/products/{sku}") String editProduct(@PathVariable String sku, Model m){ m.addAttribute("p", productRepository.findById(sku).orElseThrow()); return "admin/products/edit"; }
  @PostMapping("/admin/products/save")
  String saveProduct(@RequestParam String sku,
                     @RequestParam String name,
                     @RequestParam BigDecimal price,
                     @RequestParam String currency,
                     @RequestParam(required = false, defaultValue = "false") boolean active){
    if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku is required");
    Product p = productRepository.findById(sku).orElseGet(Product::new);
    if (p.createdAt == null) p.createdAt = Instant.now();
    p.sku = sku.trim();
    p.name = name;
    p.price = price;
    p.currency = currency;
    p.active = active;
    p.updatedAt = Instant.now();
    productRepository.save(p);
    return "redirect:/admin/products";
  }
  @PostMapping("/admin/products/{sku}/delete") String deleteProduct(@PathVariable String sku){ productRepository.deleteById(sku); return "redirect:/admin/products"; }

  @GetMapping("/admin/inventory") String inventory(Model m){ m.addAttribute("items", inventoryRepository.findAll()); return "admin/inventory/list"; }
  @PostMapping("/admin/inventory/{sku}/adjust") String adjust(@PathVariable String sku, @RequestParam int qty){
    Inventory i = inventoryRepository.findById(sku).orElseGet(() -> {var n=new Inventory();n.sku=sku;return n;});
    i.availableQty = Math.max(0, i.availableQty + qty); i.updatedAt = Instant.now(); inventoryRepository.save(i); return "redirect:/admin/inventory";
  }

  @GetMapping("/admin/users") String users(Model m){ m.addAttribute("users", adminUserService.listUsers()); return "admin/users/list"; }
  @GetMapping("/admin/users/{id}") String user(@PathVariable UUID id, Model m){ m.addAttribute("u", adminUserService.get(id)); return "admin/users/detail"; }
  @PostMapping("/admin/users/{id}/enable") String enable(@PathVariable UUID id, @RequestParam boolean enabled){ adminUserService.setEnabled(id, enabled); return "redirect:/admin/users/"+id; }

}
