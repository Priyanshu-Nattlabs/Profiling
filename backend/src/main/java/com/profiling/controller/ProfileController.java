package com.profiling.controller;

import com.profiling.dto.ApiResponse;
import com.profiling.model.Profile;
import com.profiling.model.ProfileResponse;
import com.profiling.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * POST endpoint to create a new profile
     * @param profile The profile data from request body
     * @return The saved profile and generated template as JSON
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> createProfile(@RequestBody Profile profile) {
        // TODO: Add request validation if needed
        // TODO: Add error handling for duplicate emails or other business rules
        ProfileResponse profileResponse = profileService.saveProfile(profile);
        
        ApiResponse response = new ApiResponse("Profile created successfully", profileResponse);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /**
     * GET endpoint to retrieve a profile by ID
     * @param id The profile ID
     * @return The profile as JSON if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Profile> getProfile(@PathVariable String id) {
        // TODO: Add error handling for invalid ID format
        Optional<Profile> profile = profileService.getProfileById(id);
        
        if (profile.isPresent()) {
            return new ResponseEntity<>(profile.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

