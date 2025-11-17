package com.profiling.controller;

import com.profiling.dto.ApiResponse;
import com.profiling.dto.EvaluateRequest;
import com.profiling.dto.EvaluationResult;
import com.profiling.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for evaluation endpoint
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Autowired
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /**
     * POST endpoint to evaluate user interests
     * @param request Contains user profile and all answers
     * @return Comprehensive evaluation result
     */
    @PostMapping(value = "/evaluate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> evaluate(@RequestBody EvaluateRequest request) {
        try {
            if (request == null || request.getUserProfile() == null) {
                ApiResponse errorResponse = new ApiResponse("User profile is required", null);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse);
            }

            if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
                ApiResponse errorResponse = new ApiResponse("Answers are required", null);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse);
            }

            EvaluationResult result = evaluationService.evaluate(
                    request.getUserProfile(),
                    request.getAnswers()
            );

            ApiResponse response = new ApiResponse("Evaluation completed successfully", result);
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
                    "Failed to evaluate interests: " + e.getMessage(),
                    null
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}

