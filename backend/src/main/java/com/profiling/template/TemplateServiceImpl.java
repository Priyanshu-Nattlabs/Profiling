package com.profiling.template;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public Optional<TemplateEntity> getTemplateByType(String type) {
        String normalizedType = normalizeType(type);
        return templateRepository.findById(normalizedType);
    }

    @Override
    public TemplateEntity saveTemplate(TemplateEntity template) {
        String normalizedType = normalizeType(template.getId());
        if (templateRepository.existsById(normalizedType)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Template with type '" + normalizedType + "' already exists.");
        }

        Instant now = Instant.now();
        template.setId(normalizedType);
        template.setCreatedAt(now);
        template.setUpdatedAt(now);
        if (template.getCss() == null) {
            template.setCss("");
        }

        return templateRepository.save(template);
    }

    @Override
    public TemplateEntity updateTemplate(String type, TemplateEntity updateRequest) {
        String normalizedType = normalizeType(type);
        TemplateEntity existing = templateRepository.findById(normalizedType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Template with type '" + normalizedType + "' not found."));

        if (updateRequest.getName() != null) {
            existing.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            existing.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getIcon() != null) {
            existing.setIcon(updateRequest.getIcon());
        }
        if (updateRequest.getContent() != null) {
            existing.setContent(updateRequest.getContent());
        }
        if (updateRequest.getCss() != null) {
            existing.setCss(updateRequest.getCss());
        }
        existing.setUpdatedAt(Instant.now());

        return templateRepository.save(existing);
    }

    @Override
    public List<TemplateEntity> listAllTemplates() {
        return templateRepository.findAll();
    }

    @Override
    public void deleteTemplate(String type) {
        String normalizedType = normalizeType(type);
        if (!templateRepository.existsById(normalizedType)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Template with type '" + normalizedType + "' not found.");
        }
        templateRepository.deleteById(normalizedType);
    }

    private String normalizeType(String type) {
        if (type == null || type.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Template type must be provided.");
        }
        return type.trim().toLowerCase();
    }
}


