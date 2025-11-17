package com.profiling.controller;

import com.profiling.dto.ApiResponse;
import com.profiling.dto.ChatRequest;
import com.profiling.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for chatbot chat endpoint
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    /**
     * POST endpoint to process chat messages
     * @param request Contains user message and conversation state
     * @return Next question or completion status
     */
    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> chat(@RequestBody ChatRequest request) {
        try {
            if (request == null || request.getUserMessage() == null || request.getUserMessage().trim().isEmpty()) {
                ApiResponse errorResponse = new ApiResponse("User message is required", null);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse);
            }

            if (request.getConversationState() == null) {
                ApiResponse errorResponse = new ApiResponse("Conversation state is required", null);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse);
            }

            ChatbotService.ChatResponse chatResponse = chatbotService.processChat(request);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("nextQuestion", chatResponse.getNextQuestion());
            responseData.put("conversationState", chatResponse.getUpdatedState());
            responseData.put("isComplete", chatResponse.isComplete());
            responseData.put("botName", "Saarthi");

            String message = chatResponse.isComplete() 
                ? "Conversation completed successfully" 
                : "Question processed successfully";

            ApiResponse response = new ApiResponse(message, responseData);
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
                    "Failed to process chat: " + e.getMessage(),
                    null
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}

