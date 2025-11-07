package com.profiling.service;

import com.profiling.model.Profile;
import java.util.Optional;

public interface ProfileService {
    
    /**
     * Save a profile to the database
     * @param profile The profile entity to save
     * @return The saved profile with generated ID
     */
    Profile saveProfile(Profile profile);
    
    /**
     * Retrieve a profile by its ID
     * @param id The profile ID
     * @return Optional containing the profile if found
     */
    Optional<Profile> getProfileById(String id);
}

