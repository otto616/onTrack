package com.otto616.onTrack.controllers;

import com.otto616.onTrack.dto.ChecklistForm;
import com.otto616.onTrack.models.*;
import com.otto616.onTrack.repositories.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/provider/{providerId}/workers")
public class WorkerController {

    private final WorkerRepository workerRepository;
    private final ProviderRepository providerRepository;
    private final ChecklistDocumentRepository checklistRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public WorkerController(WorkerRepository workerRepository, ProviderRepository providerRepository, ChecklistDocumentRepository checklistRepository, DocumentTypeRepository documentTypeRepository, ProjectRepository projectRepository, ProjectAssignmentRepository projectAssignmentRepository) {
        this.workerRepository = workerRepository;
        this.providerRepository = providerRepository;
        this.checklistRepository = checklistRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.projectRepository = projectRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
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
        Provider provider = providerRepository.findById(providerId).orElseThrow();
        worker.setProvider(provider);
        workerRepository.save(worker);

        List<DocumentType> requiredDocs = documentTypeRepository.findByProviderType(provider.getProviderType())
                .stream().filter(d -> d.getCategory() == Enums.DocumentCategory.WORKER).toList();

        for (DocumentType docType : requiredDocs) {
            ChecklistDocument chk = new ChecklistDocument();
            chk.setProvider(provider);
            chk.setWorker(worker);
            chk.setDocumentType(docType);
            chk.setReceived(false);
            checklistRepository.save(chk);
        }

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
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByWorkerIdOrderByStartDateDesc(workerId);
        projectAssignmentRepository.deleteAll(assignments);
        List<ChecklistDocument> docs = checklistRepository.findByWorkerId(workerId);
        checklistRepository.deleteAll(docs);
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

    @GetMapping("/{workerId}/projects")
    public String viewWorkerProjects(@PathVariable Long providerId, @PathVariable Long workerId, Model model) {
        Provider provider = providerRepository.findById(providerId).orElseThrow();
        Worker worker = workerRepository.findById(workerId).orElseThrow();
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByWorkerIdOrderByStartDateDesc(workerId);

        model.addAttribute("provider", provider);
        model.addAttribute("worker", worker);
        model.addAttribute("assignments", assignments);
        return "worker-projects";
    }

    @PostMapping("/{workerId}/projects/new")
    public String assignProject(@PathVariable Long providerId, @PathVariable Long workerId,
                                @RequestParam("projectName") String projectName,
                                @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Worker worker = workerRepository.findById(workerId).orElseThrow();

        Project project = projectRepository.findByNameIgnoreCase(projectName).orElseGet(() -> {
            Project p = new Project();
            p.setName(projectName);
            return projectRepository.save(p);
        });

        ProjectAssignment assignment = new ProjectAssignment();
        assignment.setWorker(worker);
        assignment.setProvider(worker.getProvider());
        assignment.setProject(project);
        assignment.setStartDate(startDate);
        assignment.setEndDate(endDate);
        projectAssignmentRepository.save(assignment);

        return "redirect:/provider/" + providerId + "/workers/" + workerId + "/projects";
    }

    @GetMapping("/{workerId}/projects/{assignmentId}/delete")
    public String deleteProjectAssignment(@PathVariable Long providerId, @PathVariable Long workerId, @PathVariable Long assignmentId) {
        projectAssignmentRepository.deleteById(assignmentId);
        return "redirect:/provider/" + providerId + "/workers/" + workerId + "/projects";
    }
}