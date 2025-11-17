package com.profiling.service;

import com.profiling.dto.ChatRequest;
import com.profiling.dto.ChatState;
import com.profiling.dto.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing chatbot conversation flow
 */
@Service
public class ChatbotService {

    private final OpenAIService openAIService;

    @Autowired
    public ChatbotService(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    /**
     * Process chat message and return next question or follow-up
     */
    public ChatResponse processChat(ChatRequest request) {
        ChatState state = request.getConversationState();
        String userMessage = request.getUserMessage();

        if (state == null) {
            throw new IllegalArgumentException("Conversation state is required");
        }

        // If there's a pending WHY question, handle it first
        if (state.getPendingWhyQuestion() != null && !state.getPendingWhyQuestion().isEmpty()) {
            // User answered the WHY question, store it and move on
            String currentQuestion = state.getCurrentQuestion();
            if (currentQuestion != null) {
                // Store the WHY answer with a special key
                state.addAnswer(currentQuestion + " [WHY]", userMessage);
            }
            state.setPendingWhyQuestion(null);
        } else {
            // Store the answer to the current question
            String currentQuestion = state.getCurrentQuestion();
            if (currentQuestion != null) {
                state.addAnswer(currentQuestion, userMessage);

                // Check if we should ask a WHY question
                String whyQuestion = openAIService.generateWhyQuestion(currentQuestion, userMessage);
                if (whyQuestion != null && !whyQuestion.isEmpty()) {
                    state.setPendingWhyQuestion(whyQuestion);
                    return new ChatResponse(whyQuestion, state, false);
                }
            }
        }

        // Move to next question
        state.moveToNextQuestion();

        // Check if conversation is complete
        if (state.isComplete()) {
            return new ChatResponse(null, state, true);
        }

        // Return next question
        String nextQuestion = state.getCurrentQuestion();
        return new ChatResponse(nextQuestion, state, false);
    }

    /**
     * Convert UserProfile to Map for OpenAI service
     */
    public Map<String, String> profileToMap(UserProfile profile) {
        Map<String, String> map = new HashMap<>();
        if (profile != null) {
            if (profile.getName() != null) map.put("name", profile.getName());
            if (profile.getEmail() != null) map.put("email", profile.getEmail());
            if (profile.getInstitute() != null) map.put("institute", profile.getInstitute());
            if (profile.getCurrentDegree() != null) map.put("currentDegree", profile.getCurrentDegree());
            if (profile.getBranch() != null) map.put("branch", profile.getBranch());
            if (profile.getYearOfStudy() != null) map.put("yearOfStudy", profile.getYearOfStudy());
            if (profile.getTechnicalSkills() != null) map.put("technicalSkills", profile.getTechnicalSkills());
            if (profile.getSoftSkills() != null) map.put("softSkills", profile.getSoftSkills());
            if (profile.getCertifications() != null) map.put("certifications", profile.getCertifications());
            if (profile.getAchievements() != null) map.put("achievements", profile.getAchievements());
            if (profile.getHobbies() != null) map.put("hobbies", profile.getHobbies());
            if (profile.getGoals() != null) map.put("goals", profile.getGoals());
        }
        return map;
    }

    /**
     * Response DTO for chat processing
     */
    public static class ChatResponse {
        private String nextQuestion;
        private ChatState updatedState;
        private boolean isComplete;

        public ChatResponse(String nextQuestion, ChatState updatedState, boolean isComplete) {
            this.nextQuestion = nextQuestion;
            this.updatedState = updatedState;
            this.isComplete = isComplete;
        }

        public String getNextQuestion() {
            return nextQuestion;
        }

        public void setNextQuestion(String nextQuestion) {
            this.nextQuestion = nextQuestion;
        }

        public ChatState getUpdatedState() {
            return updatedState;
        }

        public void setUpdatedState(ChatState updatedState) {
            this.updatedState = updatedState;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setComplete(boolean complete) {
            isComplete = complete;
        }
    }
}

