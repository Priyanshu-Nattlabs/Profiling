package com.profiling.controller;

import com.profiling.dto.ApiResponse;
import com.profiling.dto.GenerateQuestionsRequest;
import com.profiling.dto.UserProfile;
import com.profiling.service.ChatbotService;
import com.profiling.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for question generation endpoint
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QuestionController {

    private final OpenAIService openAIService;
    private final ChatbotService chatbotService;

    @Autowired
    public QuestionController(OpenAIService openAIService, ChatbotService chatbotService) {
        this.openAIService = openAIService;
        this.chatbotService = chatbotService;
    }

    /**
     * POST endpoint to generate personalized questions
     * @param request Contains user profile
     * @return List of 12 personalized questions
     */
    @PostMapping(value = "/generate-questions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> generateQuestions(@RequestBody GenerateQuestionsRequest request) {
        try {
            if (request == null || request.getUserProfile() == null) {
                ApiResponse errorResponse = new ApiResponse("User profile is required", null);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse);
            }

            UserProfile profile = request.getUserProfile();
            Map<String, String> profileMap = chatbotService.profileToMap(profile);

            List<String> questions = openAIService.generateQuestions(profileMap);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("questions", questions);
            responseData.put("totalQuestions", questions.size());

            ApiResponse response = new ApiResponse("Questions generated successfully", responseData);
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
                    "Failed to generate questions: " + e.getMessage(),
                    null
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}

