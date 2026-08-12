package com.otto616.onTrack.controllers;

import com.otto616.onTrack.dto.ChecklistForm;
import com.otto616.onTrack.models.ChecklistDocument;
import com.otto616.onTrack.models.DocumentType;
import com.otto616.onTrack.models.Enums;
import com.otto616.onTrack.models.Provider;
import com.otto616.onTrack.models.Worker;
import com.otto616.onTrack.repositories.ChecklistDocumentRepository;
import com.otto616.onTrack.repositories.DocumentTypeRepository;
import com.otto616.onTrack.repositories.ProviderRepository;
import com.otto616.onTrack.repositories.WorkerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/provider/{providerId}/workers")
public class WorkerController {

    private final WorkerRepository workerRepository;
    private final ProviderRepository providerRepository;
    private final ChecklistDocumentRepository checklistRepository;
    private final DocumentTypeRepository documentTypeRepository;

    public WorkerController(WorkerRepository workerRepository, ProviderRepository providerRepository, ChecklistDocumentRepository checklistRepository, DocumentTypeRepository documentTypeRepository) {
        this.workerRepository = workerRepository;
        this.providerRepository = providerRepository;
        this.checklistRepository = checklistRepository;
        this.documentTypeRepository = documentTypeRepository;
    }

    @GetMapping
    public String listWorkers(@PathVariable Long providerId, Model model) {
        Provider provider = providerRepository.findById(providerId).orElseThrow();
        model.addAttribute("provider", provider);
        model.addAttribute("workers", workerRepository.findByProviderId(providerId));
        return "workers";
    }

    @GetMapping("/new")
    public String newWorkerForm(@PathVariable Long providerId, Model model) {
        model.addAttribute("provider", providerRepository.findById(providerId).orElseThrow());
        model.addAttribute("worker", new Worker());
        return "worker-form";
    }

    @PostMapping("/new")
    public String saveWorker(@PathVariable Long providerId, @ModelAttribute Worker worker) {
        worker.setProvider(providerRepository.findById(providerId).orElseThrow());
        workerRepository.save(worker);
        return "redirect:/provider/" + providerId + "/workers";
    }

    @GetMapping("/{workerId}/edit")
    public String editWorkerForm(@PathVariable Long providerId, @PathVariable Long workerId, Model model) {
        model.addAttribute("provider", providerRepository.findById(providerId).orElseThrow());
        model.addAttribute("worker", workerRepository.findById(workerId).orElseThrow());
        return "worker-form";
    }

    @PostMapping("/{workerId}/edit")
    public String updateWorker(@PathVariable Long providerId, @PathVariable Long workerId, @ModelAttribute Worker worker) {
        Worker existingWorker = workerRepository.findById(workerId).orElseThrow();
        existingWorker.setName(worker.getName());
        existingWorker.setDni(worker.getDni());
        existingWorker.setPhone(worker.getPhone());
        existingWorker.setSocialSecurity(worker.getSocialSecurity());
        workerRepository.save(existingWorker);
        return "redirect:/provider/" + providerId + "/workers";
    }

    @GetMapping("/{workerId}/delete")
    public String deleteWorker(@PathVariable Long providerId, @PathVariable Long workerId) {
        workerRepository.deleteById(workerId);
        return "redirect:/provider/" + providerId + "/workers";
    }

    @GetMapping("/{workerId}/checklist")
    public String viewWorkerChecklist(@PathVariable Long providerId, @PathVariable Long workerId, Model model) {
        Provider provider = providerRepository.findById(providerId).orElseThrow();
        Worker worker = workerRepository.findById(workerId).orElseThrow();
        List<ChecklistDocument> checklist = checklistRepository.findByWorkerId(workerId);

        List<DocumentType> requiredDocs = documentTypeRepository.findByProviderType(provider.getProviderType())
                .stream().filter(d -> d.getCategory() == Enums.DocumentCategory.WORKER).toList();

        for (DocumentType docType : requiredDocs) {
            boolean exists = checklist.stream().anyMatch(c -> c.getDocumentType().getId().equals(docType.getId()));
            if (!exists) {
                ChecklistDocument chk = new ChecklistDocument();
                chk.setProvider(provider);
                chk.setWorker(worker);
                chk.setDocumentType(docType);
                chk.setReceived(false);
                checklistRepository.save(chk);
            }
        }

        ChecklistForm form = new ChecklistForm();
        form.setDocuments(checklistRepository.findByWorkerId(workerId));

        model.addAttribute("provider", provider);
        model.addAttribute("worker", worker);
        model.addAttribute("form", form);
        return "worker-checklist";
    }

    @PostMapping("/{workerId}/checklist")
    public String saveWorkerChecklist(@PathVariable Long providerId, @PathVariable Long workerId, @ModelAttribute ChecklistForm form) {
        for (ChecklistDocument doc : form.getDocuments()) {
            ChecklistDocument existingDoc = checklistRepository.findById(doc.getId()).orElseThrow();
            existingDoc.setReceived(doc.isReceived());
            existingDoc.setExpirationDate(doc.getExpirationDate());
            checklistRepository.save(existingDoc);
        }
        return "redirect:/provider/" + providerId + "/workers/" + workerId + "/checklist";
    }
}