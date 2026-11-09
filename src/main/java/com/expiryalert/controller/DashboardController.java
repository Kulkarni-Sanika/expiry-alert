package com.expiryalert.controller;

import com.expiryalert.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles the home page (the dashboard).
 * A controller receives a browser request, asks the service for data,
 * puts the data into the Model, and returns the name of a template.
 */
@Controller
public class DashboardController {

    private final DocumentService documentService;

    public DashboardController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stats", documentService.getDashboardStats());
        model.addAttribute("alerts", documentService.getAlertDocuments());
        return "dashboard"; // templates/dashboard.html
    }
}
