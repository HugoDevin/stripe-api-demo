package com.example.stripedemo.controller.web;

import com.example.stripedemo.service.ProductCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductCatalogService productCatalogService;

    public AdminController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @GetMapping
    public String adminPage(org.springframework.ui.Model model) {
        model.addAttribute("products", productCatalogService.getAdminProductList());
        return "admin";
    }

    @PostMapping("/products/price")
    public String updatePrice(
            @RequestParam String productName,
            @RequestParam long price,
            RedirectAttributes redirectAttributes
    ) {
        productCatalogService.updatePrice(productName, price);
        redirectAttributes.addFlashAttribute("message", "價格已更新：" + productName);
        return "redirect:/admin";
    }

    @PostMapping("/products/status")
    public String updateStatus(
            @RequestParam String productName,
            @RequestParam boolean active,
            RedirectAttributes redirectAttributes
    ) {
        productCatalogService.updateActiveStatus(productName, active);
        redirectAttributes.addFlashAttribute("message", "商品狀態已更新：" + productName);
        return "redirect:/admin";
    }
}
