package com.expiryalert.controller;

import com.expiryalert.entity.Document;
import com.expiryalert.entity.ExpiryStatus;
import com.expiryalert.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * All pages under /documents: list, add, view, edit and delete.
 */
@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // These two values are added to the model of EVERY page in this controller.
    @ModelAttribute("categories")
    public List<String> categories() {
        return documentService.getCategories();
    }

    @ModelAttribute("statuses")
    public ExpiryStatus[] statuses() {
        return ExpiryStatus.values();
    }

    // ---------- List with search, filter and sort ----------

    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q,
                       @RequestParam(name = "category", required = false) String category,
                       @RequestParam(name = "status", required = false) String status,
                       @RequestParam(name = "sort", required = false) String sort,
                       Model model) {
        model.addAttribute("documents", documentService.search(q, category, status, sort));
        model.addAttribute("totalCount", documentService.count());
        // Send the chosen filters back so the form keeps showing them
        model.addAttribute("q", q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSort", sort);
        return "documents/list";
    }

    // ---------- Add ----------

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("document", new Document());
        return "documents/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("document") Document document,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        document.setId(null); // a new document must never carry an id
        checkDateOrder(document, bindingResult);
        if (bindingResult.hasErrors()) {
            return "documents/form"; // show the form again with error messages
        }
        Document saved = documentService.save(document);
        redirectAttributes.addFlashAttribute("successMessage",
                "\"" + saved.getName() + "\" was added.");
        return "redirect:/documents";
    }

    // ---------- View ----------

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("document", documentService.findById(id));
        return "documents/detail";
    }

    // ---------- Edit ----------

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("document", documentService.findById(id));
        return "documents/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("document") Document document,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        documentService.findById(id); // 404 if it does not exist
        document.setId(id);
        checkDateOrder(document, bindingResult);
        if (bindingResult.hasErrors()) {
            return "documents/form";
        }
        Document saved = documentService.save(document);
        redirectAttributes.addFlashAttribute("successMessage",
                "\"" + saved.getName() + "\" was updated.");
        return "redirect:/documents/" + id;
    }

    // ---------- Delete ----------

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Document existing = documentService.findById(id);
        String name = existing.getName();
        documentService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "\"" + name + "\" was deleted.");
        return "redirect:/documents";
    }

    // ---------- Helper ----------

    /** Extra rule that annotations cannot express: expiry must not be before issue date. */
    private void checkDateOrder(Document document, BindingResult bindingResult) {
        if (document.getIssueDate() != null && document.getExpiryDate() != null
                && document.getExpiryDate().isBefore(document.getIssueDate())) {
            bindingResult.rejectValue("expiryDate", "date.order",
                    "Expiry date cannot be before the issue date");
        }
    }
}
