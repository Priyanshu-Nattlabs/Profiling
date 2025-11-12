package com.profiling.template;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateRepository extends MongoRepository<TemplateEntity, String> {
}


