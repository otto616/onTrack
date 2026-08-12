package com.otto616.onTrack.controllers;

import com.otto616.onTrack.models.Machinery;
import com.otto616.onTrack.models.Provider;
import com.otto616.onTrack.repositories.MachineryRepository;
import com.otto616.onTrack.repositories.ProviderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/provider/{providerId}/machinery")
public class MachineryController {

    private final MachineryRepository machineryRepository;
    private final ProviderRepository providerRepository;

    public MachineryController(MachineryRepository machineryRepository, ProviderRepository providerRepository) {
        this.machineryRepository = machineryRepository;
        this.providerRepository = providerRepository;
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
        machinery.setProvider(providerRepository.findById(providerId).orElseThrow());
        machineryRepository.save(machinery);
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
}