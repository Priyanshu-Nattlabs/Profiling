package com.profiling.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.profiling.template.TemplateEntity;
import com.profiling.template.TemplateService;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public List<TemplateEntity> listTemplates() {
        return templateService.listAllTemplates();
    }

    @GetMapping("/{type}")
    public TemplateEntity getTemplate(@PathVariable String type) {
        return templateService.getTemplateByType(type)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Template with type '" + type + "' not found."));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TemplateEntity createTemplate(@RequestBody TemplateEntity template) {
        return templateService.saveTemplate(template);
    }

    @PutMapping("/{type}")
    public TemplateEntity updateTemplate(@PathVariable String type, @RequestBody TemplateEntity template) {
        return templateService.updateTemplate(type, template);
    }

    @DeleteMapping("/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(@PathVariable String type) {
        templateService.deleteTemplate(type);
    }
}

