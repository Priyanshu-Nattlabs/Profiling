package com.profiling.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    private final WebClient webClient;
    private final String apiKey;
    // Note: Using gpt-4o-mini as the actual model name (user requested gpt-4.1-mini which doesn't exist)
    // If you need to use a different model, update this constant
    private static final String MODEL = "gpt-4o-mini";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final int MAX_TOKENS = 1000;
    private static final int MAX_TOKENS_EVALUATION = 4000;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIServiceImpl(@Value("${openai.api.key:}") String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("OpenAI API key must be configured. Set OPENAI_API_KEY environment variable or openai.api.key property.");
        }
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(OPENAI_API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    @Override
    public String enhanceProfile(String profileText) {
        if (profileText == null || profileText.trim().isEmpty()) {
            throw new IllegalArgumentException("Profile text cannot be empty");
        }

        String prompt = buildPrompt(profileText);

        // Build request payload for OpenAI Chat Completions API
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        
        requestBody.put("messages", java.util.List.of(message));
        requestBody.put("max_tokens", MAX_TOKENS);
        requestBody.put("temperature", 0.7);

        try {
            OpenAIResponse response = webClient.post()
                    .uri(OPENAI_API_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("No choices returned from OpenAI API");
            }

            Choice firstChoice = response.getChoices().get(0);
            if (firstChoice.getMessage() == null || firstChoice.getMessage().getContent() == null) {
                throw new RuntimeException("Invalid response structure from OpenAI API");
            }

            String enhancedText = firstChoice.getMessage().getContent().trim();
            return enhancedText;
        } catch (Exception e) {
            throw new RuntimeException("Failed to enhance profile with AI: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String profileText) {
        return String.format(
            "You are an AI specialized in transforming student profiles into polished, professional, and impactful descriptions suitable for resumes, portfolios, and academic applications.Improve the clarity, tone, grammar, and flow of the profile given below. Maintain all factual information—do NOT add any skills, achievements, or experience that the student has not provided.\n\n" +
            "Rewrite the following profile into a more polished, concise, and impactful version.\n\n" +
            "IMPORTANT INSTRUCTIONS:\n" +
            "- If you encounter placeholders in square brackets like [specific field], [institution name], [year of study], etc., " +
            "fill them with realistic and appropriate values that make sense in the context.\n" +
            "- For placeholders, use generic but professional values (e.g., 'Computer Science' for [specific field], " +
            "'XYZ University' for [institution name], 'third year' for [year of study]).\n" +
            "- Remove all placeholder brackets and replace them with actual meaningful content.\n" +
            "- Keep the profile strictly truthful and professional.\n" +
            "- Remove grammar issues and improve clarity, flow, and professionalism.\n" +
            "- Keep it in one cohesive paragraph.\n" +
            "- Maintain all provided real information (like email addresses, names) exactly as given.\n\n" +
            "Profile: %s",
            profileText
        );
    }

    @Override
    public List<String> generateQuestions(Map<String, String> userProfileData) {
        String prompt = buildQuestionGenerationPrompt(userProfileData);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        
        requestBody.put("messages", java.util.List.of(message));
        requestBody.put("max_tokens", 2000);
        requestBody.put("temperature", 0.8);

        try {
            OpenAIResponse response = webClient.post()
                    .uri(OPENAI_API_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("No choices returned from OpenAI API");
            }

            String responseText = response.getChoices().get(0).getMessage().getContent().trim();
            return parseQuestionsFromResponse(responseText);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate questions: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateWhyQuestion(String question, String answer) {
        if (answer == null || answer.trim().length() < 50) {
            return null; // Only ask WHY for substantial answers
        }

        String prompt = String.format(
            "You are Saarthi, an expert AI career counselor. The user answered a question with a substantial response.\n\n" +
            "Original Question: %s\n" +
            "User's Answer: %s\n\n" +
            "Generate a single, thoughtful follow-up 'WHY' question that digs deeper into their reasoning or motivation. " +
            "The question should be concise (one sentence), natural, and help understand their thought process better.\n\n" +
            "If the answer is too brief or doesn't warrant a WHY question, respond with 'SKIP'.\n\n" +
            "Generate only the question, nothing else.",
            question, answer
        );

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        
        requestBody.put("messages", java.util.List.of(message));
        requestBody.put("max_tokens", 150);
        requestBody.put("temperature", 0.7);

        try {
            OpenAIResponse response = webClient.post()
                    .uri(OPENAI_API_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                return null;
            }

            String whyQuestion = response.getChoices().get(0).getMessage().getContent().trim();
            if (whyQuestion.equalsIgnoreCase("SKIP") || whyQuestion.length() < 10) {
                return null;
            }
            return whyQuestion;
        } catch (Exception e) {
            return null; // Fail silently for WHY questions
        }
    }

    @Override
    public String evaluateInterests(Map<String, String> userProfileData, Map<String, String> answers) {
        String prompt = buildEvaluationPrompt(userProfileData, answers);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        
        requestBody.put("messages", java.util.List.of(message));
        requestBody.put("max_tokens", MAX_TOKENS_EVALUATION);
        requestBody.put("temperature", 0.7);
        requestBody.put("response_format", Map.of("type", "json_object"));

        try {
            OpenAIResponse response = webClient.post()
                    .uri(OPENAI_API_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("No choices returned from OpenAI API");
            }

            return response.getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate interests: " + e.getMessage(), e);
        }
    }

    private String buildQuestionGenerationPrompt(Map<String, String> userProfileData) {
        StringBuilder profileContext = new StringBuilder();
        profileContext.append("User Profile Information:\n");
        if (userProfileData.get("name") != null) {
            profileContext.append("- Name: ").append(userProfileData.get("name")).append("\n");
        }
        if (userProfileData.get("institute") != null) {
            profileContext.append("- Institute: ").append(userProfileData.get("institute")).append("\n");
        }
        if (userProfileData.get("branch") != null) {
            profileContext.append("- Branch: ").append(userProfileData.get("branch")).append("\n");
        }
        if (userProfileData.get("yearOfStudy") != null) {
            profileContext.append("- Year of Study: ").append(userProfileData.get("yearOfStudy")).append("\n");
        }
        if (userProfileData.get("technicalSkills") != null) {
            profileContext.append("- Technical Skills: ").append(userProfileData.get("technicalSkills")).append("\n");
        }
        if (userProfileData.get("softSkills") != null) {
            profileContext.append("- Soft Skills: ").append(userProfileData.get("softSkills")).append("\n");
        }
        if (userProfileData.get("certifications") != null) {
            profileContext.append("- Certifications: ").append(userProfileData.get("certifications")).append("\n");
        }
        if (userProfileData.get("achievements") != null) {
            profileContext.append("- Achievements: ").append(userProfileData.get("achievements")).append("\n");
        }
        if (userProfileData.get("hobbies") != null) {
            profileContext.append("- Hobbies: ").append(userProfileData.get("hobbies")).append("\n");
        }
        if (userProfileData.get("goals") != null) {
            profileContext.append("- Goals: ").append(userProfileData.get("goals")).append("\n");
        }

        return String.format(
            "You are Saarthi, an expert AI career counselor. Generate exactly 12 personalized questions to evaluate a student's career interests and personality traits.\n\n" +
            "%s\n\n" +
            "Generate 12 questions divided into 3 stages (4 questions each):\n\n" +
            "Stage 1 (Questions 1-4): Focus on skills, technical interests, and problem-solving approach\n" +
            "Stage 2 (Questions 5-8): Focus on creativity, design thinking, and innovation\n" +
            "Stage 3 (Questions 9-12): Focus on leadership, management, entrepreneurship, and research interests\n\n" +
            "Requirements:\n" +
            "- Questions must be personalized based on the user's profile\n" +
            "- Each question should be thought-provoking and help assess their interests\n" +
            "- Questions should help identify: analytical thinking, creativity, leadership, people orientation, consistency, open-mindedness\n" +
            "- Make questions natural and conversational\n\n" +
            "Output format: Return ONLY a JSON array of exactly 12 question strings, like this:\n" +
            "[\"Question 1 text\", \"Question 2 text\", ..., \"Question 12 text\"]\n\n" +
            "Do not include any other text, explanations, or formatting. Just the JSON array.",
            profileContext.toString()
        );
    }

    private List<String> parseQuestionsFromResponse(String responseText) {
        try {
            // Remove markdown code blocks if present
            String cleaned = responseText.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();

            // Extract JSON array
            int startIdx = cleaned.indexOf('[');
            int endIdx = cleaned.lastIndexOf(']');
            if (startIdx >= 0 && endIdx > startIdx) {
                cleaned = cleaned.substring(startIdx, endIdx + 1);
            }

            @SuppressWarnings("unchecked")
            List<String> questions = objectMapper.readValue(cleaned, List.class);
            
            // Ensure we have exactly 12 questions
            if (questions.size() < 12) {
                // Pad with generic questions if needed
                while (questions.size() < 12) {
                    questions.add("Tell me more about your interests and goals.");
                }
            } else if (questions.size() > 12) {
                questions = questions.subList(0, 12);
            }
            
            return questions;
        } catch (Exception e) {
            // Fallback: return generic questions
            return generateFallbackQuestions();
        }
    }

    private List<String> generateFallbackQuestions() {
        List<String> questions = new ArrayList<>();
        questions.add("What technical skills are you most passionate about?");
        questions.add("How do you approach solving complex problems?");
        questions.add("What projects have you worked on that you're most proud of?");
        questions.add("What technologies or tools do you want to learn next?");
        questions.add("How do you express your creativity in your work or hobbies?");
        questions.add("What role does design thinking play in your projects?");
        questions.add("How do you handle feedback and iterate on your ideas?");
        questions.add("What innovative solutions have you come up with?");
        questions.add("Describe a time when you took on a leadership role.");
        questions.add("How do you manage team conflicts or disagreements?");
        questions.add("What are your thoughts on starting your own venture?");
        questions.add("What research areas interest you the most?");
        return questions;
    }

    private String buildEvaluationPrompt(Map<String, String> userProfileData, Map<String, String> answers) {
        StringBuilder context = new StringBuilder();
        context.append("User Profile:\n");
        userProfileData.forEach((key, value) -> {
            if (value != null && !value.trim().isEmpty()) {
                context.append("- ").append(key).append(": ").append(value).append("\n");
            }
        });
        
        context.append("\nUser Answers:\n");
        answers.forEach((question, answer) -> {
            context.append("Q: ").append(question).append("\n");
            context.append("A: ").append(answer).append("\n\n");
        });

        return String.format(
            "You are Saarthi, an expert AI career counselor. Analyze the user's profile and answers to generate a comprehensive interest evaluation.\n\n" +
            "%s\n\n" +
            "Generate a detailed evaluation in the following JSON format (all fields are required):\n\n" +
            "{\n" +
            "  \"interests\": {\n" +
            "    \"tech\": <number 0-100>,\n" +
            "    \"design\": <number 0-100>,\n" +
            "    \"management\": <number 0-100>,\n" +
            "    \"entrepreneurship\": <number 0-100>,\n" +
            "    \"research\": <number 0-100>\n" +
            "  },\n" +
            "  \"pie_chart_labels\": [\"Tech\", \"Design\", \"Management\", \"Entrepreneurship\", \"Research\"],\n" +
            "  \"pie_chart_values\": [<tech_score>, <design_score>, <management_score>, <entrepreneurship_score>, <research_score>],\n" +
            "  \"interest_persona\": \"<A 2-3 sentence description of their primary interest persona>\",\n" +
            "  \"strengths\": [\"<strength1>\", \"<strength2>\", \"<strength3>\"],\n" +
            "  \"weaknesses\": [\"<weakness1>\", \"<weakness2>\"],\n" +
            "  \"dos\": [\"<do1>\", \"<do2>\", \"<do3>\", \"<do4>\"],\n" +
            "  \"donts\": [\"<dont1>\", \"<dont2>\", \"<dont3>\"],\n" +
            "  \"recommended_roles\": [\"<role1>\", \"<role2>\", \"<role3>\", \"<role4>\"],\n" +
            "  \"roadmap_90_days\": \"<A detailed 90-day roadmap as a single paragraph>\",\n" +
            "  \"suggested_courses\": [\"<course1>\", \"<course2>\", \"<course3>\", \"<course4>\"],\n" +
            "  \"project_ideas\": [\"<idea1>\", \"<idea2>\", \"<idea3>\"],\n" +
            "  \"summary\": \"<A comprehensive 3-4 sentence summary of the evaluation>\"\n" +
            "}\n\n" +
            "IMPORTANT:\n" +
            "- The interest scores should reflect their actual interests based on profile and answers\n" +
            "- All scores should be numbers (not strings)\n" +
            "- pie_chart_values should match the interest scores in the same order\n" +
            "- Be specific and personalized based on their profile\n" +
            "- Return ONLY valid JSON, no markdown, no explanations\n" +
            "- All arrays should have at least 2-4 items\n" +
            "- The summary should be comprehensive and actionable",
            context.toString()
        );
    }

    // Inner classes for JSON deserialization
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class OpenAIResponse {
        @JsonProperty("choices")
        private java.util.List<Choice> choices;

        public java.util.List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(java.util.List<Choice> choices) {
            this.choices = choices;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Choice {
        @JsonProperty("message")
        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Message {
        @JsonProperty("content")
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
