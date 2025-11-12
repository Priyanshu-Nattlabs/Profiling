package com.profiling.service;

import com.profiling.dto.ProfileRequestDTO;
import com.profiling.model.Profile;
import com.profiling.model.ProfileResponse;
import com.profiling.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.profiling.template.TemplateFactory;
import com.profiling.template.TemplateRenderResult;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final TemplateFactory templateFactory;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, TemplateFactory templateFactory) {
        this.profileRepository = profileRepository;
        this.templateFactory = templateFactory;
    }

    @Override
    public ProfileResponse saveProfile(Profile profile) {
        // TODO: Add business logic for profile validation or processing before saving
        // TODO: Add logging for profile creation
        
        // Save profile to MongoDB
        Profile savedProfile = profileRepository.save(profile);

        // Generate template paragraph with metadata
        TemplateRenderResult renderResult = templateFactory.generate(savedProfile.getTemplateType(), savedProfile);

        String resolvedTemplateId = renderResult.getTemplateId();
        if (resolvedTemplateId != null) {
            String currentType = savedProfile.getTemplateType();
            if (currentType == null || !currentType.equals(resolvedTemplateId)) {
                savedProfile.setTemplateType(resolvedTemplateId);
                savedProfile = profileRepository.save(savedProfile);
            }
        }

        // Return response with profile and template
        return new ProfileResponse(savedProfile, renderResult);
    }

    @Override
    public Optional<Profile> getProfileById(String id) {
        // TODO: Add business logic for profile retrieval (e.g., access control, caching)
        // TODO: Add logging for profile retrieval
        return profileRepository.findById(id);
    }

    @Override
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @Override
    public TemplateRenderResult generateTemplate(Profile profile) {
        return templateFactory.generate(profile.getTemplateType(), profile);
    }

    @Override
    public Profile updateProfile(String id, ProfileRequestDTO dto) {
        Profile existingProfile = profileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        applyUpdates(existingProfile, dto);

        return profileRepository.save(existingProfile);
    }

    private void applyUpdates(Profile profile, ProfileRequestDTO dto) {
        if (dto.getName() != null) {
            profile.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            profile.setEmail(dto.getEmail());
        }
        if (dto.getDob() != null) {
            profile.setDob(dto.getDob());
        }
        if (dto.getLinkedin() != null) {
            profile.setLinkedin(dto.getLinkedin());
        }
        if (dto.getInstitute() != null) {
            profile.setInstitute(dto.getInstitute());
        }
        if (dto.getCurrentDegree() != null) {
            profile.setCurrentDegree(dto.getCurrentDegree());
        }
        if (dto.getBranch() != null) {
            profile.setBranch(dto.getBranch());
        }
        if (dto.getYearOfStudy() != null) {
            profile.setYearOfStudy(dto.getYearOfStudy());
        }
        if (dto.getCertifications() != null) {
            profile.setCertifications(dto.getCertifications());
        }
        if (dto.getAchievements() != null) {
            profile.setAchievements(dto.getAchievements());
        }
        if (dto.getTechnicalSkills() != null) {
            profile.setTechnicalSkills(dto.getTechnicalSkills());
        }
        if (dto.getSoftSkills() != null) {
            profile.setSoftSkills(dto.getSoftSkills());
        }
        if (dto.getTemplateType() != null) {
            profile.setTemplateType(dto.getTemplateType());
        }
        if (dto.getHiringManagerName() != null) {
            profile.setHiringManagerName(dto.getHiringManagerName());
        }
        if (dto.getCompanyName() != null) {
            profile.setCompanyName(dto.getCompanyName());
        }
        if (dto.getCompanyAddress() != null) {
            profile.setCompanyAddress(dto.getCompanyAddress());
        }
        if (dto.getPositionTitle() != null) {
            profile.setPositionTitle(dto.getPositionTitle());
        }
        if (dto.getRelevantExperience() != null) {
            profile.setRelevantExperience(dto.getRelevantExperience());
        }
        if (dto.getKeyAchievement() != null) {
            profile.setKeyAchievement(dto.getKeyAchievement());
        }
        if (dto.getStrengths() != null) {
            profile.setStrengths(dto.getStrengths());
        }
        if (dto.getClosingNote() != null) {
            profile.setClosingNote(dto.getClosingNote());
        }
        if (dto.getHasInternship() != null) {
            profile.setHasInternship(dto.getHasInternship());
        }
        if (dto.getInternshipDetails() != null) {
            profile.setInternshipDetails(dto.getInternshipDetails());
        }
        if (dto.getHasExperience() != null) {
            profile.setHasExperience(dto.getHasExperience());
        }
        if (dto.getExperienceDetails() != null) {
            profile.setExperienceDetails(dto.getExperienceDetails());
        }
    }
}

