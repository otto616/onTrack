package com.otto616.onTrack.controllers;

import com.otto616.onTrack.dto.ChecklistForm;
import com.otto616.onTrack.models.*;
import com.otto616.onTrack.repositories.ChecklistDocumentRepository;
import com.otto616.onTrack.repositories.DocumentVersionRepository;
import com.otto616.onTrack.repositories.ProviderRepository;
import com.otto616.onTrack.repositories.DocumentTypeRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private final DocumentVersionRepository documentVersionRepository;

    public ProviderController(ProviderRepository providerRepository, ChecklistDocumentRepository checklistRepository, DocumentTypeRepository documentTypeRepository, DocumentVersionRepository documentVersionRepository) {
        this.providerRepository = providerRepository;
        this.checklistRepository = checklistRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.documentVersionRepository = documentVersionRepository;
    }

    @GetMapping("/")
    public String listProviders(@RequestParam(value = "search", required = false) String search, Model model) {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);

        List<ChecklistDocument> alerts = checklistRepository.findByExpirationDateLessThanEqual(thirtyDaysFromNow);

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
        List<ChecklistDocument> checklist = checklistRepository.findByProviderId(id).stream()
                .filter(c -> c.getWorker() == null && c.getMachinery() == null).toList();

        List<DocumentType> requiredDocs = documentTypeRepository.findByProviderType(provider.getProviderType())
                .stream().filter(d -> d.getCategory() == Enums.DocumentCategory.COMPANY).toList();

        for (DocumentType docType : requiredDocs) {
            boolean exists = checklist.stream().anyMatch(c -> c.getDocumentType().getId().equals(docType.getId()));
            if (!exists) {
                ChecklistDocument chk = new ChecklistDocument();
                chk.setProvider(provider);
                chk.setDocumentType(docType);
                chk.setReceived(false);
                checklistRepository.save(chk);
            }
        }

        List<ChecklistDocument> updatedChecklist = checklistRepository.findByProviderId(id).stream()
                .filter(c -> c.getWorker() == null && c.getMachinery() == null).toList();

        ChecklistForm form = new ChecklistForm();
        form.setDocuments(updatedChecklist);

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

    @GetMapping("/document/{id}/toggle-exempt")
    public String toggleExempt(@PathVariable Long id) {
        ChecklistDocument doc = checklistRepository.findById(id).orElseThrow();
        doc.setExempt(!doc.isExempt());
        checklistRepository.save(doc);

        if (doc.getWorker() != null) return "redirect:/provider/" + doc.getProvider().getId() + "/workers/" + doc.getWorker().getId() + "/checklist";
        if (doc.getMachinery() != null) return "redirect:/provider/" + doc.getProvider().getId() + "/machinery/" + doc.getMachinery().getId() + "/checklist";
        return "redirect:/provider/" + doc.getProvider().getId() + "/checklist";
    }

    @GetMapping("/document/{id}/history")
    public String viewHistory(@PathVariable Long id, Model model) {
        ChecklistDocument doc = checklistRepository.findById(id).orElseThrow();
        model.addAttribute("document", doc);
        return "document-history";
    }

    @PostMapping("/document/{id}/history/upload")
    public String saveVersion(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam(value = "expirationDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expirationDate) throws IOException {
        ChecklistDocument doc = checklistRepository.findById(id).orElseThrow();

        if (!file.isEmpty()) {
            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));
            String uniquePrefix = System.currentTimeMillis() + "_";
            String storedFileName = id + "_" + uniquePrefix + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + storedFileName);
            Files.write(path, file.getBytes());

            DocumentVersion version = new DocumentVersion();
            version.setChecklistDocument(doc);
            version.setFileName(storedFileName);
            version.setOriginalFileName(file.getOriginalFilename());
            version.setUploadDate(LocalDate.now());
            version.setExpirationDate(expirationDate);

            documentVersionRepository.save(version);

            doc.setFileName(storedFileName);
            doc.setReceived(true);
            if (expirationDate != null) {
                doc.setExpirationDate(expirationDate);
            }
            checklistRepository.save(doc);
        }
        return "redirect:/document/" + id + "/history";
    }

    @GetMapping("/document/version/{versionId}/download")
    public ResponseEntity<Resource> downloadVersion(@PathVariable Long versionId) throws MalformedURLException {
        DocumentVersion version = documentVersionRepository.findById(versionId).orElseThrow();
        Path path = Paths.get("uploads/" + version.getFileName());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + version.getOriginalFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/document/version/{versionId}/view")
    public ResponseEntity<Resource> viewVersion(@PathVariable Long versionId) throws IOException {
        DocumentVersion version = documentVersionRepository.findById(versionId).orElseThrow();
        Path path = Paths.get("uploads/" + version.getFileName());
        Resource resource = new UrlResource(path.toUri());

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + version.getOriginalFileName() + "\"")
                .body(resource);
    }
}