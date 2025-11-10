package com.profiling.service;

import com.profiling.model.Profile;
import com.profiling.model.ProfileResponse;
import com.profiling.dto.ProfileRequestDTO;
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

    /**
     * Generate the template paragraph for a given profile
     * @param profile The profile to use for template generation
     * @return Generated template text
     */
    String generateTemplate(Profile profile);

    /**
     * Update profile fields for a given profile id
     * @param id Profile identifier
     * @param dto Request payload containing fields to update
     * @return Updated profile entity
     */
    Profile updateProfile(String id, ProfileRequestDTO dto);
}

