package com.profiling.service;

import com.profiling.model.Profile;
import com.profiling.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile saveProfile(Profile profile) {
        // TODO: Add business logic for profile validation or processing before saving
        // TODO: Add logging for profile creation
        return profileRepository.save(profile);
    }

    @Override
    public Optional<Profile> getProfileById(String id) {
        // TODO: Add business logic for profile retrieval (e.g., access control, caching)
        // TODO: Add logging for profile retrieval
        return profileRepository.findById(id);
    }
}

