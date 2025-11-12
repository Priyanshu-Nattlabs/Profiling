package com.profiling.template;

import java.util.List;
import java.util.Optional;

public interface TemplateService {

    Optional<TemplateEntity> getTemplateByType(String type);

    TemplateEntity saveTemplate(TemplateEntity template);

    TemplateEntity updateTemplate(String type, TemplateEntity updateRequest);

    List<TemplateEntity> listAllTemplates();

    void deleteTemplate(String type);
}


