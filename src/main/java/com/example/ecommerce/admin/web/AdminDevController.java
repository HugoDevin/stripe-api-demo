package com.example.ecommerce.admin.web;

import com.example.ecommerce.admin.repo.AdminEmailOutboxRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("dev-offline")
public class AdminDevController {
  private final AdminEmailOutboxRepository repo;
  public AdminDevController(AdminEmailOutboxRepository repo){this.repo=repo;}
  @GetMapping("/admin/dev/emails")
  String emails(Model model){ model.addAttribute("emails", repo.findTop100ByOrderByCreatedAtDesc()); return "admin/dev/emails"; }
}
