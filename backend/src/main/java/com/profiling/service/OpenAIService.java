package com.profiling.service;

import java.util.List;
import java.util.Map;

public interface OpenAIService {
    String enhanceProfile(String profileText);
    
    /**
     * Generate personalized questions based on user profile
     * @param userProfile The user's profile information
     * @return List of 12 questions (4 per stage)
     */
    List<String> generateQuestions(Map<String, String> userProfileData);
    
    /**
     * Generate a follow-up WHY question based on user's answer
     * @param question The original question
     * @param answer The user's answer
     * @return A WHY follow-up question or null if not needed
     */
    String generateWhyQuestion(String question, String answer);
    
    /**
     * Evaluate user profile and answers to generate interest evaluation
     * @param userProfileData User profile information
     * @param answers Map of question to answer
     * @return JSON string containing evaluation result
     */
    String evaluateInterests(Map<String, String> userProfileData, Map<String, String> answers);
}

