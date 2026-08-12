package com.otto616.onTrack.controllers;

import com.otto616.onTrack.models.Provider;
import com.otto616.onTrack.models.Worker;
import com.otto616.onTrack.repositories.ProviderRepository;
import com.otto616.onTrack.repositories.WorkerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/provider/{providerId}/workers")
public class WorkerController {

    private final WorkerRepository workerRepository;
    private final ProviderRepository providerRepository;

    public WorkerController(WorkerRepository workerRepository, ProviderRepository providerRepository) {
        this.workerRepository = workerRepository;
        this.providerRepository = providerRepository;
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
}