package com.profiling.controller;

import com.profiling.dto.ApiResponse;
import com.profiling.dto.ProfileRequestDTO;
import com.profiling.model.Profile;
import com.profiling.model.ProfileResponse;
import com.profiling.service.PDFService;
import com.profiling.service.ProfileService;
import com.profiling.template.TemplateRenderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;
    private final PDFService pdfService;

    @Autowired
    public ProfileController(ProfileService profileService, PDFService pdfService) {
        this.profileService = profileService;
        this.pdfService = pdfService;
    
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
     * GET endpoint to retrieve all profiles
     * @return List of profiles as JSON
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profile>> getAllProfiles() {
        List<Profile> profiles = profileService.getAllProfiles();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(profiles);
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

    @GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadProfile(@PathVariable String id) {
        Optional<Profile> profileOptional = profileService.getProfileById(id);

        if (profileOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Profile profile = profileOptional.get();
        TemplateRenderResult renderResult = profileService.generateTemplate(profile);
        String templateText = renderResult != null ? renderResult.getRenderedText() : "";
        byte[] pdfBytes = pdfService.generateProfilePDF(profile, templateText);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        "Content-Disposition",
                        ContentDisposition.attachment().filename("profile.pdf").build().toString()
                )
                .body(pdfBytes);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateProfile(@PathVariable String id,
                                                     @RequestBody ProfileRequestDTO requestDTO) {
        Profile updatedProfile = profileService.updateProfile(id, requestDTO);
        TemplateRenderResult renderResult = profileService.generateTemplate(updatedProfile);
        ProfileResponse responseData = new ProfileResponse(updatedProfile, renderResult);

        ApiResponse response = new ApiResponse("Profile updated successfully", responseData);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}

