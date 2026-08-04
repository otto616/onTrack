package com.otto616.onTrack.controllers;

import com.otto616.onTrack.models.DocumentType;
import com.otto616.onTrack.models.Enums;
import com.otto616.onTrack.repositories.DocumentTypeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CatalogController {

    private final DocumentTypeRepository documentTypeRepository;

    public CatalogController(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @GetMapping("/catalog")
    public String viewCatalog(Model model) {
        model.addAttribute("documents", documentTypeRepository.findAll());
        model.addAttribute("newDocument", new DocumentType());
        model.addAttribute("clientTypes", Enums.ProviderType.values());
        model.addAttribute("categories", Enums.DocumentCategory.values());
        return "catalog";
    }

    @PostMapping("/catalog/new")
    public String saveDocumentType(@ModelAttribute DocumentType documentType) {
        documentTypeRepository.save(documentType);
        return "redirect:/catalog";
    }
}