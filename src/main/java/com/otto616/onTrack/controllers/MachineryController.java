package com.otto616.onTrack.controllers;

import com.otto616.onTrack.dto.ChecklistForm;
import com.otto616.onTrack.models.ChecklistDocument;
import com.otto616.onTrack.models.DocumentType;
import com.otto616.onTrack.models.Enums;
import com.otto616.onTrack.models.Machinery;
import com.otto616.onTrack.models.Provider;
import com.otto616.onTrack.repositories.ChecklistDocumentRepository;
import com.otto616.onTrack.repositories.DocumentTypeRepository;
import com.otto616.onTrack.repositories.MachineryRepository;
import com.otto616.onTrack.repositories.ProviderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/provider/{providerId}/machinery")
public class MachineryController {

    private final MachineryRepository machineryRepository;
    private final ProviderRepository providerRepository;
    private final ChecklistDocumentRepository checklistRepository;
    private final DocumentTypeRepository documentTypeRepository;

    public MachineryController(MachineryRepository machineryRepository, ProviderRepository providerRepository, ChecklistDocumentRepository checklistRepository, DocumentTypeRepository documentTypeRepository) {
        this.machineryRepository = machineryRepository;
        this.providerRepository = providerRepository;
        this.checklistRepository = checklistRepository;
        this.documentTypeRepository = documentTypeRepository;
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
}