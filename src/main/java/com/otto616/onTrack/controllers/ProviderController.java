package com.otto616.onTrack.controllers;

import com.otto616.onTrack.dto.ChecklistForm;
import com.otto616.onTrack.models.*;
import com.otto616.onTrack.repositories.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProviderController {

    private final ProviderRepository providerRepository;
    private final ChecklistDocumentRepository checklistRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final WorkerRepository workerRepository;
    private final MachineryRepository machineryRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public ProviderController(ProviderRepository providerRepository, ChecklistDocumentRepository checklistRepository, DocumentTypeRepository documentTypeRepository, DocumentVersionRepository documentVersionRepository, WorkerRepository workerRepository, MachineryRepository machineryRepository, ProjectRepository projectRepository, ProjectAssignmentRepository projectAssignmentRepository) {
        this.providerRepository = providerRepository;
        this.checklistRepository = checklistRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.documentVersionRepository = documentVersionRepository;
        this.workerRepository = workerRepository;
        this.machineryRepository = machineryRepository;
        this.projectRepository = projectRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    public static class SearchResult {
        public String type;
        public String name;
        public String detail;
        public String url;
    }

    @GetMapping("/api/search")
    @ResponseBody
    public List<SearchResult> globalSearch(@RequestParam("q") String q) {
        List<SearchResult> results = new ArrayList<>();
        if (q == null || q.isBlank()) {
            return results;
        }

        List<Provider> providers = providerRepository.findByNameContainingIgnoreCaseOrTaxIdContainingIgnoreCase(q, q)
                .stream().filter(Provider::isActive).toList();

        for (Provider p : providers) {
            SearchResult r = new SearchResult();
            r.type = "Proveïdor";
            r.name = p.getName();
            r.detail = p.getTaxId();
            r.url = "/provider/" + p.getId() + "/checklist";
            results.add(r);
        }

        List<Worker> workers = workerRepository.findByNameContainingIgnoreCaseOrDniContainingIgnoreCase(q, q);
        for (Worker w : workers) {
            SearchResult r = new SearchResult();
            r.type = "Treballador";
            r.name = w.getName();
            r.detail = w.getDni() + " (" + w.getProvider().getName() + ")";
            r.url = "/provider/" + w.getProvider().getId() + "/workers/" + w.getId() + "/checklist";
            results.add(r);
        }

        List<Machinery> machines = machineryRepository.findByNameContainingIgnoreCaseOrInternalCodeContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(q, q, q);
        for (Machinery m : machines) {
            SearchResult r = new SearchResult();
            r.type = "Maquinària";
            r.name = m.getName();
            r.detail = m.getInternalCode() + " / " + m.getSerialNumber() + " (" + m.getProvider().getName() + ")";
            r.url = "/provider/" + m.getProvider().getId() + "/machinery/" + m.getId() + "/checklist";
            results.add(r);
        }

        return results;
    }

    @GetMapping("/")
    public String listProviders(@RequestParam(value = "search", required = false) String search, Model model) {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);

        List<ChecklistDocument> alerts = checklistRepository.findByExpirationDateLessThanEqual(thirtyDaysFromNow);

        List<Provider> providers;
        if (search != null && !search.isEmpty()) {
            providers = providerRepository.findByNameContainingIgnoreCaseOrTaxIdContainingIgnoreCase(search, search)
                    .stream().filter(Provider::isActive).toList();
        } else {
            providers = providerRepository.findByIsActiveTrue();
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

        List<DocumentType> requiredDocs = documentTypeRepository.findByProviderType(provider.getProviderType())
                .stream().filter(d -> d.getCategory() == Enums.DocumentCategory.COMPANY).toList();

        for (DocumentType docType : requiredDocs) {
            ChecklistDocument chk = new ChecklistDocument();
            chk.setProvider(provider);
            chk.setDocumentType(docType);
            chk.setReceived(false);
            checklistRepository.save(chk);
        }

        return "redirect:/";
    }

    @GetMapping("/provider/{id}/edit")
    public String editProviderForm(@PathVariable Long id, Model model) {
        model.addAttribute("provider", providerRepository.findById(id).orElseThrow());
        model.addAttribute("providerTypes", Enums.ProviderType.values());
        return "provider-form";
    }

    @PostMapping("/provider/{id}/edit")
    public String updateProvider(@PathVariable Long id, @ModelAttribute Provider providerForm) {
        Provider provider = providerRepository.findById(id).orElseThrow();
        provider.setName(providerForm.getName());
        provider.setTaxId(providerForm.getTaxId());
        provider.setProviderType(providerForm.getProviderType());
        providerRepository.save(provider);
        return "redirect:/";
    }

    @GetMapping("/provider/{id}/delete")
    public String deleteProvider(@PathVariable Long id) {
        Provider provider = providerRepository.findById(id).orElseThrow();
        provider.setActive(false);
        providerRepository.save(provider);
        return "redirect:/";
    }

    @GetMapping("/providers/trash")
    public String viewTrash(Model model) {
        model.addAttribute("providers", providerRepository.findByIsActiveFalse());
        return "provider-trash";
    }

    @GetMapping("/provider/{id}/restore")
    public String restoreProvider(@PathVariable Long id) {
        Provider provider = providerRepository.findById(id).orElseThrow();
        provider.setActive(true);
        providerRepository.save(provider);
        return "redirect:/providers/trash";
    }

    private void performHardDelete(Long providerId) {
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByProviderIdOrderByStartDateDesc(providerId);
        projectAssignmentRepository.deleteAll(assignments);

        List<ChecklistDocument> docs = checklistRepository.findByProviderId(providerId);
        checklistRepository.deleteAll(docs);

        List<Worker> workers = workerRepository.findByProviderId(providerId);
        workerRepository.deleteAll(workers);

        List<Machinery> machineries = machineryRepository.findByProviderId(providerId);
        machineryRepository.deleteAll(machineries);

        providerRepository.deleteById(providerId);
    }

    @GetMapping("/provider/{id}/hard-delete")
    public String hardDeleteProvider(@PathVariable Long id) {
        performHardDelete(id);
        return "redirect:/providers/trash";
    }

    @GetMapping("/providers/trash/empty")
    public String emptyTrash() {
        List<Provider> trash = providerRepository.findByIsActiveFalse();
        for (Provider p : trash) {
            performHardDelete(p.getId());
        }
        return "redirect:/providers/trash";
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
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              @RequestParam(value = "expirationDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expirationDate,
                              @RequestParam(value = "received", defaultValue = "false") boolean received) throws IOException {
        ChecklistDocument doc = checklistRepository.findById(id).orElseThrow();
        DocumentVersion version = new DocumentVersion();

        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));
            String uniquePrefix = System.currentTimeMillis() + "_";
            String storedFileName = id + "_" + uniquePrefix + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + storedFileName);
            Files.write(path, file.getBytes());

            version.setFileName(storedFileName);
            version.setOriginalFileName(file.getOriginalFilename());
            doc.setFileName(storedFileName);
        }

        version.setChecklistDocument(doc);
        version.setUploadDate(LocalDate.now());
        version.setExpirationDate(expirationDate);
        version = documentVersionRepository.save(version);

        if (!doc.getVersions().contains(version)) {
            doc.getVersions().add(version);
        }

        doc.setReceived(received);
        doc.updateExpirationDateFromVersions();
        checklistRepository.save(doc);

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

    @GetMapping("/document/version/{versionId}/edit")
    public String editVersionForm(@PathVariable Long versionId, Model model) {
        DocumentVersion version = documentVersionRepository.findById(versionId).orElseThrow();
        ChecklistDocument doc = version.getChecklistDocument();
        model.addAttribute("document", doc);
        model.addAttribute("editVersion", version);
        return "document-history";
    }

    @PostMapping("/document/version/{versionId}/edit")
    public String updateVersion(@PathVariable Long versionId,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                @RequestParam(value = "expirationDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expirationDate,
                                @RequestParam(value = "received", defaultValue = "false") boolean received) throws IOException {

        DocumentVersion version = documentVersionRepository.findById(versionId).orElseThrow();
        ChecklistDocument doc = version.getChecklistDocument();

        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));
            String uniquePrefix = System.currentTimeMillis() + "_";
            String storedFileName = doc.getId() + "_" + uniquePrefix + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + storedFileName);
            Files.write(path, file.getBytes());

            version.setFileName(storedFileName);
            version.setOriginalFileName(file.getOriginalFilename());
            doc.setFileName(storedFileName);
        }

        version.setExpirationDate(expirationDate);
        documentVersionRepository.save(version);

        doc.setReceived(received);
        doc.updateExpirationDateFromVersions();
        checklistRepository.save(doc);

        return "redirect:/document/" + doc.getId() + "/history";
    }

    @GetMapping("/document/version/{versionId}/delete")
    public String deleteVersion(@PathVariable Long versionId) {
        DocumentVersion version = documentVersionRepository.findById(versionId).orElseThrow();
        ChecklistDocument doc = version.getChecklistDocument();

        doc.getVersions().remove(version);
        documentVersionRepository.delete(version);

        doc.updateExpirationDateFromVersions();
        if (doc.getVersions().isEmpty()) {
            doc.setReceived(false);
            doc.setFileName(null);
        }
        checklistRepository.save(doc);

        return "redirect:/document/" + doc.getId() + "/history";
    }

    @GetMapping("/provider/{id}/projects")
    public String viewProviderProjects(@PathVariable Long id, Model model) {
        Provider provider = providerRepository.findById(id).orElseThrow();
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByProviderIdOrderByStartDateDesc(id);

        model.addAttribute("provider", provider);
        model.addAttribute("assignments", assignments);
        return "provider-projects";
    }

    @PostMapping("/provider/{id}/projects/new")
    public String assignProviderProject(@PathVariable Long id,
                                        @RequestParam("projectName") String projectName,
                                        @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                        @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Provider provider = providerRepository.findById(id).orElseThrow();

        Project project = projectRepository.findByNameIgnoreCase(projectName).orElseGet(() -> {
            Project p = new Project();
            p.setName(projectName);
            return projectRepository.save(p);
        });

        ProjectAssignment assignment = new ProjectAssignment();
        assignment.setProvider(provider);
        assignment.setProject(project);
        assignment.setStartDate(startDate);
        assignment.setEndDate(endDate);
        projectAssignmentRepository.save(assignment);

        return "redirect:/provider/" + id + "/projects";
    }

    @GetMapping("/provider/{providerId}/projects/{assignmentId}/delete")
    public String deleteProviderProjectAssignment(@PathVariable Long providerId, @PathVariable Long assignmentId) {
        projectAssignmentRepository.deleteById(assignmentId);
        return "redirect:/provider/" + providerId + "/projects";
    }
}