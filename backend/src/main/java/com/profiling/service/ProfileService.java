package com.profiling.service;

import com.profiling.model.Profile;
import com.profiling.model.ProfileResponse;
import java.util.Optional;

public interface ProfileService {
    
    /**
     * Save a profile to the database and generate template
     * @param profile The profile entity to save
     * @return ProfileResponse containing the saved profile and generated template
     */
    ProfileResponse saveProfile(Profile profile);
    
    /**
     * Retrieve a profile by its ID
     * @param id The profile ID
     * @return Optional containing the profile if found
     */
    Optional<Profile> getProfileById(String id);
}

