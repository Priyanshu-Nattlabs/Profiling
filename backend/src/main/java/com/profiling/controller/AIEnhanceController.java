package com.profiling.controller;

import com.profiling.dto.EnhanceRequest;
import com.profiling.dto.EnhanceResponse;
import com.profiling.dto.ApiResponse;
import com.profiling.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AIEnhanceController {

    private final OpenAIService openAIService;

    @Autowired
    public AIEnhanceController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    /**
     * POST endpoint to enhance profile using AI
     * @param request The profile text to enhance
     * @return The enhanced profile text
     */
    @PostMapping(value = "/ai-enhance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> enhanceProfile(@RequestBody EnhanceRequest request) {
        try {
            if (request.getProfile() == null || request.getProfile().trim().isEmpty()) {
                ApiResponse errorResponse = new ApiResponse("Profile text is required", null);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse);
            }

            String enhancedProfile = openAIService.enhanceProfile(request.getProfile());
            EnhanceResponse responseDTO = new EnhanceResponse(enhancedProfile);
            
            ApiResponse response = new ApiResponse("Profile enhanced successfully", responseDTO);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse errorResponse = new ApiResponse(e.getMessage(), null);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(
                    "Failed to enhance profile: " + e.getMessage(), 
                    null
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}

