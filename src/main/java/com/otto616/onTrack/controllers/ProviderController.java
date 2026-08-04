package com.otto616.onTrack.controllers;

import com.otto616.onTrack.dto.ChecklistForm;
import com.otto616.onTrack.models.*;
import com.otto616.onTrack.repositories.ChecklistDocumentRepository;
import com.otto616.onTrack.repositories.ProviderRepository;
import com.otto616.onTrack.repositories.DocumentTypeRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProviderController {

    private final ProviderRepository providerRepository;
    private final ChecklistDocumentRepository checklistRepository;
    private final DocumentTypeRepository documentTypeRepository;

    public ProviderController(ProviderRepository providerRepository, ChecklistDocumentRepository checklistRepository, DocumentTypeRepository documentTypeRepository) {
        this.providerRepository = providerRepository;
        this.checklistRepository = checklistRepository;
        this.documentTypeRepository = documentTypeRepository;
    }

    @GetMapping("/")
    public String listProviders(@RequestParam(value = "search", required = false) String search, Model model) {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);

        List<ChecklistDocument> alerts = checklistRepository.findByExpirationDateLessThanEqual(thirtyDaysFromNow);

        long totalProviders = providerRepository.count();
        long pendingDocs = checklistRepository.countByReceivedFalse();
        long expiredDocs = checklistRepository.countByExpirationDateLessThan(today);

        List<Provider> providers;
        if (search != null && !search.isEmpty()) {
            providers = providerRepository.findByNameContainingIgnoreCaseOrTaxIdContainingIgnoreCase(search, search);
        } else {
            providers = providerRepository.findAll();
        }

        Map<Long, Long> pendingMap = new HashMap<>();
        Map<Long, Long> expiredMap = new HashMap<>();

        for (Provider p : providers) {
            pendingMap.put(p.getId(), checklistRepository.countByProviderIdAndReceivedFalse(p.getId()));
            expiredMap.put(p.getId(), checklistRepository.countByProviderIdAndExpirationDateLessThan(p.getId(), today));
        }

        model.addAttribute("providers", providers);
        model.addAttribute("alerts", alerts);
        model.addAttribute("totalProviders", totalProviders);
        model.addAttribute("pendingDocs", pendingDocs);
        model.addAttribute("expiredDocs", expiredDocs);
        model.addAttribute("searchQuery", search);
        model.addAttribute("pendingMap", pendingMap);
        model.addAttribute("expiredMap", expiredMap);

        return "index";
    }

    @GetMapping("/provider/new")
    public String newProviderForm(Model model) {
        model.addAttribute("provider", new Provider());
        model.addAttribute("providerTypes", Enums.ProviderType.values());
        return "provider-form";
    }

    @PostMapping("/provider/new")
    public String saveProvider(@ModelAttribute Provider provider) {
        providerRepository.save(provider);
        return "redirect:/";
    }

    @GetMapping("/provider/{id}/checklist")
    public String viewChecklist(@PathVariable Long id, Model model) {
        Provider provider = providerRepository.findById(id).orElseThrow();
        List<ChecklistDocument> checklist = checklistRepository.findByProviderId(id);
        List<DocumentType> requiredDocs = documentTypeRepository.findByProviderType(provider.getProviderType());

        for (DocumentType docType : requiredDocs) {
            boolean exists = checklist.stream().anyMatch(c -> c.getDocumentType().getId().equals(docType.getId()));
            if (!exists) {
                ChecklistDocument chk = new ChecklistDocument();
                chk.setProvider(provider);
                chk.setDocumentType(docType);
                chk.setReceived(false);
                checklistRepository.save(chk);
                checklist.add(chk);
            }
        }

        ChecklistForm form = new ChecklistForm();
        form.setDocuments(checklist);

        model.addAttribute("provider", provider);
        model.addAttribute("form", form);
        return "checklist";
    }

    @PostMapping("/provider/{id}/checklist")
    public String saveChecklist(@PathVariable Long id, @ModelAttribute ChecklistForm form) {
        for (ChecklistDocument doc : form.getDocuments()) {
            ChecklistDocument existingDoc = checklistRepository.findById(doc.getId()).orElseThrow();
            existingDoc.setReceived(doc.isReceived());
            existingDoc.setExpirationDate(doc.getExpirationDate());
            checklistRepository.save(existingDoc);
        }
        return "redirect:/provider/" + id + "/checklist";
    }

    @GetMapping("/document/{id}/upload")
    public String uploadForm(@PathVariable Long id, Model model) {
        ChecklistDocument doc = checklistRepository.findById(id).orElseThrow();
        model.addAttribute("document", doc);
        return "upload";
    }

    @PostMapping("/document/{id}/upload")
    public String saveFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        ChecklistDocument doc = checklistRepository.findById(id).orElseThrow();

        if (!file.isEmpty()) {
            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));
            String storedFileName = id + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + storedFileName);
            Files.write(path, file.getBytes());

            doc.setFileName(storedFileName);
            doc.setReceived(true);
            checklistRepository.save(doc);
        }
        return "redirect:/provider/" + doc.getProvider().getId() + "/checklist";
    }

    @GetMapping("/document/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws MalformedURLException {
        ChecklistDocument doc = checklistRepository.findById(id).orElseThrow();
        Path path = Paths.get("uploads/" + doc.getFileName());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }
}