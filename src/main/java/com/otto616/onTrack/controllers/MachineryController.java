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
@RequestMapping("/provider/{providerId}/machinery")
public class MachineryController {

    private final MachineryRepository machineryRepository;
    private final ProviderRepository providerRepository;
    private final ChecklistDocumentRepository checklistRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public MachineryController(MachineryRepository machineryRepository, ProviderRepository providerRepository, ChecklistDocumentRepository checklistRepository, DocumentTypeRepository documentTypeRepository, ProjectRepository projectRepository, ProjectAssignmentRepository projectAssignmentRepository) {
        this.machineryRepository = machineryRepository;
        this.providerRepository = providerRepository;
        this.checklistRepository = checklistRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.projectRepository = projectRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    @GetMapping
    public String listMachinery(@PathVariable Long providerId, Model model) {
        Provider provider = providerRepository.findById(providerId).orElseThrow();
        model.addAttribute("provider", provider);
        model.addAttribute("machineries", machineryRepository.findByProviderId(providerId));
        return "machinery";
    }

    @GetMapping("/new")
    public String newMachineryForm(@PathVariable Long providerId, Model model) {
        model.addAttribute("provider", providerRepository.findById(providerId).orElseThrow());
        model.addAttribute("machinery", new Machinery());
        return "machinery-form";
    }

    @PostMapping("/new")
    public String saveMachinery(@PathVariable Long providerId, @ModelAttribute Machinery machinery) {
        Provider provider = providerRepository.findById(providerId).orElseThrow();
        machinery.setProvider(provider);
        machineryRepository.save(machinery);

        List<DocumentType> requiredDocs = documentTypeRepository.findByProviderType(provider.getProviderType())
                .stream().filter(d -> d.getCategory() == Enums.DocumentCategory.MACHINERY).toList();

        for (DocumentType docType : requiredDocs) {
            ChecklistDocument chk = new ChecklistDocument();
            chk.setProvider(provider);
            chk.setMachinery(machinery);
            chk.setDocumentType(docType);
            chk.setReceived(false);
            checklistRepository.save(chk);
        }

        return "redirect:/provider/" + providerId + "/machinery";
    }

    @GetMapping("/{machineryId}/edit")
    public String editMachineryForm(@PathVariable Long providerId, @PathVariable Long machineryId, Model model) {
        model.addAttribute("provider", providerRepository.findById(providerId).orElseThrow());
        model.addAttribute("machinery", machineryRepository.findById(machineryId).orElseThrow());
        return "machinery-form";
    }

    @PostMapping("/{machineryId}/edit")
    public String updateMachinery(@PathVariable Long providerId, @PathVariable Long machineryId, @ModelAttribute Machinery machinery) {
        Machinery existingMachinery = machineryRepository.findById(machineryId).orElseThrow();
        existingMachinery.setName(machinery.getName());
        existingMachinery.setSerialNumber(machinery.getSerialNumber());
        existingMachinery.setInternalCode(machinery.getInternalCode());
        machineryRepository.save(existingMachinery);
        return "redirect:/provider/" + providerId + "/machinery";
    }

    @GetMapping("/{machineryId}/delete")
    public String deleteMachinery(@PathVariable Long providerId, @PathVariable Long machineryId) {
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByMachineryIdOrderByStartDateDesc(machineryId);
        projectAssignmentRepository.deleteAll(assignments);
        List<ChecklistDocument> docs = checklistRepository.findByMachineryId(machineryId);
        checklistRepository.deleteAll(docs);
        machineryRepository.deleteById(machineryId);
        return "redirect:/provider/" + providerId + "/machinery";
    }

    @GetMapping("/{machineryId}/checklist")
    public String viewMachineryChecklist(@PathVariable Long providerId, @PathVariable Long machineryId, Model model) {
        Provider provider = providerRepository.findById(providerId).orElseThrow();
        Machinery machinery = machineryRepository.findById(machineryId).orElseThrow();
        List<ChecklistDocument> checklist = checklistRepository.findByMachineryId(machineryId);

        List<DocumentType> requiredDocs = documentTypeRepository.findByProviderType(provider.getProviderType())
                .stream().filter(d -> d.getCategory() == Enums.DocumentCategory.MACHINERY).toList();

        for (DocumentType docType : requiredDocs) {
            boolean exists = checklist.stream().anyMatch(c -> c.getDocumentType().getId().equals(docType.getId()));
            if (!exists) {
                ChecklistDocument chk = new ChecklistDocument();
                chk.setProvider(provider);
                chk.setMachinery(machinery);
                chk.setDocumentType(docType);
                chk.setReceived(false);
                checklistRepository.save(chk);
            }
        }

        ChecklistForm form = new ChecklistForm();
        form.setDocuments(checklistRepository.findByMachineryId(machineryId));

        model.addAttribute("provider", provider);
        model.addAttribute("machinery", machinery);
        model.addAttribute("form", form);
        return "machinery-checklist";
    }

    @PostMapping("/{machineryId}/checklist")
    public String saveMachineryChecklist(@PathVariable Long providerId, @PathVariable Long machineryId, @ModelAttribute ChecklistForm form) {
        for (ChecklistDocument doc : form.getDocuments()) {
            ChecklistDocument existingDoc = checklistRepository.findById(doc.getId()).orElseThrow();
            existingDoc.setReceived(doc.isReceived());
            existingDoc.setExpirationDate(doc.getExpirationDate());
            checklistRepository.save(existingDoc);
        }
        return "redirect:/provider/" + providerId + "/machinery/" + machineryId + "/checklist";
    }

    @GetMapping("/{machineryId}/projects")
    public String viewMachineryProjects(@PathVariable Long providerId, @PathVariable Long machineryId, Model model) {
        Provider provider = providerRepository.findById(providerId).orElseThrow();
        Machinery machinery = machineryRepository.findById(machineryId).orElseThrow();
        List<ProjectAssignment> assignments = projectAssignmentRepository.findByMachineryIdOrderByStartDateDesc(machineryId);

        model.addAttribute("provider", provider);
        model.addAttribute("machinery", machinery);
        model.addAttribute("assignments", assignments);
        return "machinery-projects";
    }

    @PostMapping("/{machineryId}/projects/new")
    public String assignProject(@PathVariable Long providerId, @PathVariable Long machineryId,
                                @RequestParam("projectName") String projectName,
                                @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Machinery machinery = machineryRepository.findById(machineryId).orElseThrow();

        Project project = projectRepository.findByNameIgnoreCase(projectName).orElseGet(() -> {
            Project p = new Project();
            p.setName(projectName);
            return projectRepository.save(p);
        });

        ProjectAssignment assignment = new ProjectAssignment();
        assignment.setMachinery(machinery);
        assignment.setProvider(machinery.getProvider());
        assignment.setProject(project);
        assignment.setStartDate(startDate);
        assignment.setEndDate(endDate);
        projectAssignmentRepository.save(assignment);

        return "redirect:/provider/" + providerId + "/machinery/" + machineryId + "/projects";
    }

    @GetMapping("/{machineryId}/projects/{assignmentId}/delete")
    public String deleteProjectAssignment(@PathVariable Long providerId, @PathVariable Long machineryId, @PathVariable Long assignmentId) {
        projectAssignmentRepository.deleteById(assignmentId);
        return "redirect:/provider/" + providerId + "/machinery/" + machineryId + "/projects";
    }
}