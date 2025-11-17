package com.profiling.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profiling.dto.EvaluationResult;
import com.profiling.dto.UserProfile;
import com.profiling.util.JsonValidator;
import com.profiling.util.ScoreUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for evaluating user interests and generating results
 */
@Service
public class EvaluationService {

    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;

    @Autowired
    public EvaluationService(OpenAIService openAIService) {
        this.openAIService = openAIService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Evaluate user profile and answers to generate comprehensive evaluation
     */
    public EvaluationResult evaluate(UserProfile userProfile, Map<String, String> answers) {
        // Convert profile to map
        Map<String, String> profileMap = profileToMap(userProfile);

        // Call OpenAI to get evaluation JSON
        String evaluationJson = openAIService.evaluateInterests(profileMap, answers);

        // Extract and clean JSON
        String cleanedJson = JsonValidator.extractJsonFromText(evaluationJson);
        if (!JsonValidator.isValidJson(cleanedJson)) {
            throw new RuntimeException("Invalid JSON response from OpenAI");
        }

        // Parse JSON to EvaluationResult
        EvaluationResult result = parseEvaluationJson(cleanedJson);

        // Normalize scores to sum to 100
        if (result.getInterests() != null) {
            Map<String, Double> normalized = ScoreUtils.normalizeScores(result.getInterests());
            normalized = ScoreUtils.roundScores(normalized, 2);
            result.setInterests(normalized);

            // Update pie chart values to match normalized scores
            if (result.getPieChartLabels() != null && result.getPieChartLabels().size() == 5) {
                List<Double> pieValues = new ArrayList<>();
                pieValues.add(normalized.getOrDefault("tech", 0.0));
                pieValues.add(normalized.getOrDefault("design", 0.0));
                pieValues.add(normalized.getOrDefault("management", 0.0));
                pieValues.add(normalized.getOrDefault("entrepreneurship", 0.0));
                pieValues.add(normalized.getOrDefault("research", 0.0));
                result.setPieChartValues(pieValues);
            }
        }

        return result;
    }

    /**
     * Parse JSON string to EvaluationResult
     */
    private EvaluationResult parseEvaluationJson(String jsonString) {
        try {
            JsonNode root = objectMapper.readTree(jsonString);
            EvaluationResult result = new EvaluationResult();

            // Parse interests
            if (root.has("interests")) {
                JsonNode interestsNode = root.get("interests");
                Map<String, Double> interests = new HashMap<>();
                if (interestsNode.has("tech")) {
                    interests.put("tech", interestsNode.get("tech").asDouble());
                }
                if (interestsNode.has("design")) {
                    interests.put("design", interestsNode.get("design").asDouble());
                }
                if (interestsNode.has("management")) {
                    interests.put("management", interestsNode.get("management").asDouble());
                }
                if (interestsNode.has("entrepreneurship")) {
                    interests.put("entrepreneurship", interestsNode.get("entrepreneurship").asDouble());
                }
                if (interestsNode.has("research")) {
                    interests.put("research", interestsNode.get("research").asDouble());
                }
                result.setInterests(interests);
            }

            // Parse pie chart labels
            if (root.has("pie_chart_labels")) {
                List<String> labels = new ArrayList<>();
                root.get("pie_chart_labels").forEach(node -> labels.add(node.asText()));
                result.setPieChartLabels(labels);
            }

            // Parse pie chart values
            if (root.has("pie_chart_values")) {
                List<Double> values = new ArrayList<>();
                root.get("pie_chart_values").forEach(node -> values.add(node.asDouble()));
                result.setPieChartValues(values);
            }

            // Parse interest persona
            if (root.has("interest_persona")) {
                result.setInterestPersona(root.get("interest_persona").asText());
            }

            // Parse strengths
            if (root.has("strengths")) {
                List<String> strengths = new ArrayList<>();
                root.get("strengths").forEach(node -> strengths.add(node.asText()));
                result.setStrengths(strengths);
            }

            // Parse weaknesses
            if (root.has("weaknesses")) {
                List<String> weaknesses = new ArrayList<>();
                root.get("weaknesses").forEach(node -> weaknesses.add(node.asText()));
                result.setWeaknesses(weaknesses);
            }

            // Parse dos
            if (root.has("dos")) {
                List<String> dos = new ArrayList<>();
                root.get("dos").forEach(node -> dos.add(node.asText()));
                result.setDos(dos);
            }

            // Parse donts
            if (root.has("donts")) {
                List<String> donts = new ArrayList<>();
                root.get("donts").forEach(node -> donts.add(node.asText()));
                result.setDonts(donts);
            }

            // Parse recommended roles
            if (root.has("recommended_roles")) {
                List<String> roles = new ArrayList<>();
                root.get("recommended_roles").forEach(node -> roles.add(node.asText()));
                result.setRecommendedRoles(roles);
            }

            // Parse roadmap
            if (root.has("roadmap_90_days")) {
                result.setRoadmap90Days(root.get("roadmap_90_days").asText());
            }

            // Parse suggested courses
            if (root.has("suggested_courses")) {
                List<String> courses = new ArrayList<>();
                root.get("suggested_courses").forEach(node -> courses.add(node.asText()));
                result.setSuggestedCourses(courses);
            }

            // Parse project ideas
            if (root.has("project_ideas")) {
                List<String> projects = new ArrayList<>();
                root.get("project_ideas").forEach(node -> projects.add(node.asText()));
                result.setProjectIdeas(projects);
            }

            // Parse summary
            if (root.has("summary")) {
                result.setSummary(root.get("summary").asText());
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse evaluation JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Convert UserProfile to Map
     */
    private Map<String, String> profileToMap(UserProfile profile) {
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
}

