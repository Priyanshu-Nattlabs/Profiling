package com.profiling.repository;

import com.profiling.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {
    // Additional custom query methods can be added here if needed
    // Example: List<Profile> findByEmail(String email);
}

