package com.otto616.onTrack.controllers;

import com.otto616.onTrack.models.Project;
import com.otto616.onTrack.models.ProjectAssignment;
import com.otto616.onTrack.repositories.ProjectAssignmentRepository;
import com.otto616.onTrack.repositories.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {

    private final ProjectAssignmentRepository projectAssignmentRepository;
    private final ProjectRepository projectRepository;

    public ProjectController(ProjectAssignmentRepository projectAssignmentRepository, ProjectRepository projectRepository) {
        this.projectAssignmentRepository = projectAssignmentRepository;
        this.projectRepository = projectRepository;
    }

    public static class ProjectSearchResult {
        public String name;
        public String detail;
        public String url;
    }

    @GetMapping("/api/projects/search")
    @ResponseBody
    public List<ProjectSearchResult> asyncProjectSearch(@RequestParam("q") String q) {
        List<ProjectSearchResult> results = new ArrayList<>();
        if (q == null || q.isBlank()) return results;

        String query = q.toLowerCase();
        List<ProjectAssignment> assignments = projectAssignmentRepository.findAllByOrderByStartDateDesc().stream()
                .filter(a -> a.getProject().getName().toLowerCase().contains(query) ||
                        a.getProvider().getName().toLowerCase().contains(query) ||
                        (a.getWorker() != null && a.getWorker().getName().toLowerCase().contains(query)) ||
                        (a.getMachinery() != null && a.getMachinery().getName().toLowerCase().contains(query)))
                .toList();

        for (ProjectAssignment a : assignments) {
            ProjectSearchResult r = new ProjectSearchResult();
            r.name = a.getProject().getName();

            String resource = "🏢 " + a.getProvider().getName();
            if (a.getWorker() != null) resource = "👷 " + a.getWorker().getName();
            else if (a.getMachinery() != null) resource = "🚜 " + a.getMachinery().getName();

            r.detail = resource + " (" + a.getProvider().getName() + ")";

            if (a.getWorker() != null) r.url = "/provider/" + a.getProvider().getId() + "/workers/" + a.getWorker().getId() + "/projects";
            else if (a.getMachinery() != null) r.url = "/provider/" + a.getProvider().getId() + "/machinery/" + a.getMachinery().getId() + "/projects";
            else r.url = "/provider/" + a.getProvider().getId() + "/projects";

            results.add(r);
        }
        return results;
    }

    @GetMapping("/api/projects/autocomplete")
    @ResponseBody
    public List<Map<String, String>> projectAutocomplete(@RequestParam("q") String q) {
        List<Map<String, String>> results = new ArrayList<>();
        if (q == null || q.length() < 2) return results;

        String query = q.toLowerCase();
        List<Project> matchingProjects = projectRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(query))
                .toList();

        for (Project p : matchingProjects) {
            Map<String, String> dto = new HashMap<>();
            dto.put("name", p.getName());

            List<ProjectAssignment> assignments = projectAssignmentRepository.findAll().stream()
                    .filter(a -> a.getProject().getId().equals(p.getId()))
                    .toList();

            if (!assignments.isEmpty()) {
                LocalDate start = assignments.get(0).getStartDate();
                LocalDate end = assignments.get(0).getEndDate();
                dto.put("startDate", start != null ? start.toString() : "");
                dto.put("endDate", end != null ? end.toString() : "");
            } else {
                dto.put("startDate", "");
                dto.put("endDate", "");
            }
            results.add(dto);
        }
        return results;
    }

    @GetMapping("/projects")
    public String listProjects(Model model) {
        model.addAttribute("assignments", projectAssignmentRepository.findAllByOrderByStartDateDesc());
        return "projects";
    }

    @GetMapping("/projects/assignment/{id}/delete")
    public String deleteGlobalAssignment(@PathVariable Long id) {
        projectAssignmentRepository.deleteById(id);
        return "redirect:/projects";
    }

    @GetMapping("/projects/catalog")
    public String listProjectCatalog(Model model) {
        model.addAttribute("projects", projectRepository.findAll());
        return "project-catalog";
    }

    @GetMapping("/projects/catalog/new")
    public String newProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "project-form";
    }

    @PostMapping("/projects/catalog/new")
    public String saveProject(@ModelAttribute Project project) {
        projectRepository.save(project);
        return "redirect:/projects/catalog";
    }

    @GetMapping("/projects/catalog/{id}/edit")
    public String editProjectForm(@PathVariable Long id, Model model) {
        model.addAttribute("project", projectRepository.findById(id).orElseThrow());
        return "project-form";
    }

    @PostMapping("/projects/catalog/{id}/edit")
    public String updateProject(@PathVariable Long id, @ModelAttribute Project projectForm) {
        Project project = projectRepository.findById(id).orElseThrow();
        project.setName(projectForm.getName());
        projectRepository.save(project);
        return "redirect:/projects/catalog";
    }

    @GetMapping("/projects/catalog/{id}/delete")
    public String deleteProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id).orElseThrow();

        List<ProjectAssignment> assignments = projectAssignmentRepository.findAll().stream()
                .filter(a -> a.getProject().getId().equals(id)).toList();
        projectAssignmentRepository.deleteAll(assignments);

        projectRepository.delete(project);
        return "redirect:/projects/catalog";
    }
}