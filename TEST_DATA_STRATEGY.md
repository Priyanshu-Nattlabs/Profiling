# Test Data Strategy for Profiling Service

## Overview
This document outlines the test data strategy for the Profiling Service, including test users, profiles, templates, and other test entities needed for comprehensive testing.

---

## 1. Test Users

### Standard Test Users

#### User A (Primary Test User)
```java
User userA = new User();
userA.setId("user-a-123");
userA.setEmail("usera@test.com");
userA.setName("User A");
userA.setPassword("$2a$10$hashedPassword"); // BCrypt hash
userA.setRole(UserRole.USER);
userA.setProvider("local");
```
**Usage**: Primary user for positive test cases, profile creation, chatbot interactions

#### User B (Secondary Test User)
```java
User userB = new User();
userB.setId("user-b-456");
userB.setEmail("userb@test.com");
userB.setName("User B");
userB.setPassword("$2a$10$hashedPassword");
userB.setRole(UserRole.USER);
userB.setProvider("local");
```
**Usage**: Data isolation tests, cross-user access attempts

#### Admin User
```java
User adminUser = new User();
adminUser.setId("admin-789");
adminUser.setEmail("admin@test.com");
adminUser.setName("Admin User");
adminUser.setPassword("$2a$10$hashedPassword");
adminUser.setRole(UserRole.ADMIN);
adminUser.setProvider("local");
```
**Usage**: Admin-specific functionality tests

#### OAuth User (Google)
```java
User oauthUser = new User();
oauthUser.setId("oauth-321");
oauthUser.setEmail("oauth@test.com");
oauthUser.setName("OAuth User");
oauthUser.setGoogleId("google-123456");
oauthUser.setProvider("google");
oauthUser.setRole(UserRole.USER);
```
**Usage**: OAuth authentication flow tests

### Edge Case Users

#### User with Null Role
```java
User userNullRole = new User();
userNullRole.setId("user-null-role");
userNullRole.setEmail("nullrole@test.com");
userNullRole.setRole(null);
```
**Usage**: Test default role assignment

#### User with Very Long Email
```java
User userLongEmail = new User();
userLongEmail.setEmail("a".repeat(200) + "@test.com");
```
**Usage**: Boundary testing, validation tests

---

## 2. Test Profiles

### Minimal Profile (Required Fields Only)
```java
Profile minimalProfile = new Profile();
minimalProfile.setName("Minimal User");
minimalProfile.setEmail("minimal@test.com");
minimalProfile.setDob("2000-01-01");
minimalProfile.setTemplateType("professional");
```
**Usage**: Test profile creation with minimum data, validation of required fields

### Complete Profile (All Fields)
```java
Profile completeProfile = new Profile();
completeProfile.setName("Complete User");
completeProfile.setEmail("complete@test.com");
completeProfile.setPhone("123-456-7890");
completeProfile.setDob("1995-05-15");
completeProfile.setLinkedin("linkedin.com/in/completeuser");
completeProfile.setInstitute("Test University");
completeProfile.setCurrentDegree("Bachelor of Technology");
completeProfile.setBranch("Computer Science");
completeProfile.setYearOfStudy("Third Year");
completeProfile.setCertifications("AWS Certified, Google Cloud");
completeProfile.setAchievements("Dean's List, Hackathon Winner");
completeProfile.setTechnicalSkills("Java, Python, React, Node.js");
completeProfile.setSoftSkills("Leadership, Communication");
completeProfile.setHobbies("Photography, Reading");
completeProfile.setInterests("Machine Learning, Web Development");
completeProfile.setHasInternship(true);
completeProfile.setInternshipDetails("Software Engineering Intern at Tech Corp");
completeProfile.setHasExperience(false);
completeProfile.setTemplateType("professional");
completeProfile.setProfileImage("data:image/png;base64,iVBORw0KG...");
```
**Usage**: Test template rendering with all fields, comprehensive profile display

### Profile with Special Characters
```java
Profile specialCharProfile = new Profile();
specialCharProfile.setName("User <script>alert('XSS')</script>");
specialCharProfile.setEmail("special@test.com");
specialCharProfile.setTechnicalSkills("Java; DROP TABLE users; --");
specialCharProfile.setAchievements("Award 'Best' & \"Top\" Performer");
```
**Usage**: Security tests, XSS prevention, SQL injection prevention, HTML escaping

### Profile with Very Long Fields
```java
Profile longFieldProfile = new Profile();
longFieldProfile.setName("A".repeat(1000));
longFieldProfile.setAchievements("B".repeat(5000));
longFieldProfile.setTechnicalSkills("C".repeat(3000));
```
**Usage**: Boundary testing, field length validation, truncation tests

### Profile with Image
```java
Profile imageProfile = new Profile();
// Base64 encoded image (small test image)
imageProfile.setProfileImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
```
**Usage**: Photo-required templates, image upload/display tests

### Profile for Cover Letter Template
```java
Profile coverLetterProfile = new Profile();
coverLetterProfile.setName("Cover Letter User");
coverLetterProfile.setEmail("cover@test.com");
coverLetterProfile.setHiringManagerName("John Manager");
coverLetterProfile.setCompanyName("Tech Corp");
coverLetterProfile.setCompanyAddress("123 Tech St, City, State");
coverLetterProfile.setPositionTitle("Software Engineer");
coverLetterProfile.setRelevantExperience("3 years of Java development");
coverLetterProfile.setKeyAchievement("Led team of 5 developers");
coverLetterProfile.setStrengths("Problem-solving, Team leadership");
coverLetterProfile.setClosingNote("Looking forward to contributing");
coverLetterProfile.setTemplateType("cover");
```
**Usage**: Cover letter template tests, template-specific field validation

### Profile with AI Enhancement
```java
Profile aiEnhancedProfile = new Profile();
aiEnhancedProfile.setName("AI Enhanced User");
aiEnhancedProfile.setEmail("ai@test.com");
aiEnhancedProfile.setTemplateType("professional");
aiEnhancedProfile.setAiEnhancedTemplateText("AI-enhanced profile text with improved language and insights");
```
**Usage**: Basic template tests, AI enhancement display, regeneration tests

---

## 3. Test Templates

### Professional Template
```java
TemplateEntity professionalTemplate = new TemplateEntity();
professionalTemplate.setId("professional");
professionalTemplate.setName("Professional");
professionalTemplate.setDescription("A professional template");
professionalTemplate.setContent("I am {{name}}, a {{currentDegree}} student...");
professionalTemplate.setCss("body { font-family: Arial; }");
professionalTemplate.setEnabled(true);
```
**Usage**: Default template tests, template rendering tests

### Cover Letter Template
```java
TemplateEntity coverLetterTemplate = new TemplateEntity();
coverLetterTemplate.setId("cover");
coverLetterTemplate.setName("Cover Letter");
coverLetterTemplate.setContent("Dear {{hiringManagerName}},\n\nI am writing...");
```
**Usage**: Cover letter specific tests, template-specific field tests

### Photo-Required Template
```java
TemplateEntity photoTemplate = new TemplateEntity();
photoTemplate.setId("professional-profile");
photoTemplate.setName("Professional Profile with Photo");
photoTemplate.setContent("{{#profileImage}}<img src='{{profileImage}}'/>{{/profileImage}}...");
```
**Usage**: Photo requirement validation, image display tests

### Invalid Template
```java
// Template that doesn't exist
String invalidTemplateId = "non-existent-template";
```
**Usage**: Template validation tests, error handling

---

## 4. Test Chatbot Data

### Complete Conversation State
```java
ChatState completeState = new ChatState();
List<String> questions = Arrays.asList(
    "What are your career goals?",
    "Describe your ideal work environment.",
    "What motivates you?",
    // ... 15 questions total
);
completeState.setQuestions(questions);
completeState.setCurrentStage(1);
completeState.setCurrentQuestionIndex(0);
completeState.setCurrentQuestion(questions.get(0));
Map<String, String> answers = new HashMap<>();
answers.put("What are your career goals?", "To become a senior developer");
completeState.setAnswers(answers);
```
**Usage**: Chatbot flow tests, conversation completion tests

### Partial Conversation State
```java
ChatState partialState = new ChatState();
partialState.setQuestions(questions);
partialState.setCurrentStage(2);
partialState.setCurrentQuestionIndex(2); // Question 8
partialState.setCurrentQuestion(questions.get(7));
```
**Usage**: Mid-conversation tests, state persistence tests

### Conversation with Empty Answers
```java
ChatState emptyAnswerState = new ChatState();
emptyAnswerState.setAnswers(new HashMap<>());
emptyAnswerState.setCurrentQuestion("Question?");
```
**Usage**: Validation tests, empty answer handling

### Conversation with Very Long Answers
```java
ChatState longAnswerState = new ChatState();
longAnswerState.setAnswers(Map.of(
    "Question?", "A".repeat(10000) // 10,000 character answer
));
```
**Usage**: Boundary testing, answer length validation

### Conversation with Special Characters
```java
ChatState specialCharState = new ChatState();
specialCharState.setAnswers(Map.of(
    "Question?", "<script>alert('XSS')</script>; DROP TABLE--"
));
```
**Usage**: Security tests, answer sanitization

---

## 5. Test Report Data

### Complete Psychometric Report
```java
Map<String, Object> completeReport = new HashMap<>();
completeReport.put("openness", 75);
completeReport.put("conscientiousness", 80);
completeReport.put("extraversion", 70);
completeReport.put("agreeableness", 85);
completeReport.put("neuroticism", 60);
completeReport.put("aptitudeScore", 75.5);
completeReport.put("behavioralScore", 80.0);
completeReport.put("domainScore", 72.5);
completeReport.put("totalQuestions", 50);
completeReport.put("attempted", 48);
completeReport.put("correct", 36);
completeReport.put("wrong", 12);
completeReport.put("strengths", Arrays.asList(
    "Strong analytical skills",
    "Excellent communication",
    "Leadership potential"
));
completeReport.put("fitAnalysis", "The candidate shows strong fit for technical roles...");
completeReport.put("behavioralInsights", "The candidate demonstrates high conscientiousness...");
```
**Usage**: Report generation tests, profile enhancement tests

### Partial Report (Missing Sections)
```java
Map<String, Object> partialReport = new HashMap<>();
partialReport.put("openness", 75);
// Missing other scores
```
**Usage**: Error handling tests, missing data scenarios

### Invalid Report Data
```java
Map<String, Object> invalidReport = new HashMap<>();
invalidReport.put("openness", "not-a-number");
invalidReport.put("strengths", "not-an-array");
```
**Usage**: Data validation tests, error handling

---

## 6. Test OpenAI Responses

### Valid OpenAI Response
```json
{
  "choices": [
    {
      "message": {
        "role": "assistant",
        "content": "Enhanced profile text with AI improvements"
      }
    }
  ]
}
```
**Usage**: Successful AI enhancement tests

### Empty Choices Response
```json
{
  "choices": []
}
```
**Usage**: Error handling tests, invalid response scenarios

### Invalid JSON Response
```json
{
  "invalid": "response",
  "missing": "choices"
}
```
**Usage**: Error handling tests, malformed response scenarios

### Rate Limit Response
```json
{
  "error": {
    "message": "Rate limit exceeded",
    "type": "rate_limit_error"
  }
}
```
**Usage**: Rate limit handling tests

---

## 7. Test Files

### Valid PDF Resume
- **File**: `test-resume.pdf`
- **Size**: ~50KB
- **Content**: Sample resume with name, email, skills, experience
- **Usage**: Resume parsing tests

### Valid DOCX Resume
- **File**: `test-resume.docx`
- **Size**: ~30KB
- **Content**: Sample resume in DOCX format
- **Usage**: DOCX parsing tests

### Invalid File (TXT)
- **File**: `test-resume.txt`
- **Content**: Plain text (not PDF or DOCX)
- **Usage**: File type validation tests

### Corrupted PDF
- **File**: `corrupted-resume.pdf`
- **Content**: Invalid PDF bytes
- **Usage**: Error handling tests

### Empty File
- **File**: `empty.pdf`
- **Size**: 0 bytes
- **Usage**: Empty file validation tests

### Large File
- **File**: `large-resume.pdf`
- **Size**: 10MB+
- **Usage**: File size limit tests

---

## 8. Test Data Factories

### Java Factory Example
```java
public class TestDataFactory {
    public static Profile createMinimalProfile(String userId) {
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setName("Test User");
        profile.setEmail("test@example.com");
        profile.setDob("2000-01-01");
        profile.setTemplateType("professional");
        return profile;
    }

    public static Profile createCompleteProfile(String userId) {
        Profile profile = createMinimalProfile(userId);
        // Add all optional fields
        profile.setPhone("123-456-7890");
        profile.setLinkedin("linkedin.com/in/test");
        // ... set all fields
        return profile;
    }

    public static User createTestUser(String email) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setName("Test User");
        user.setPassword("$2a$10$hashed");
        user.setRole(UserRole.USER);
        user.setProvider("local");
        return user;
    }

    public static ChatState createChatState(int currentQuestion) {
        ChatState state = new ChatState();
        List<String> questions = IntStream.range(1, 16)
            .mapToObj(i -> "Question " + i + "?")
            .collect(Collectors.toList());
        state.setQuestions(questions);
        state.setCurrentStage((currentQuestion - 1) / 5 + 1);
        state.setCurrentQuestionIndex((currentQuestion - 1) % 5);
        state.setCurrentQuestion(questions.get(currentQuestion - 1));
        return state;
    }
}
```

### JavaScript Factory Example
```javascript
export const createTestProfile = (overrides = {}) => ({
  id: 'profile-123',
  userId: 'user-123',
  name: 'Test User',
  email: 'test@example.com',
  dob: '2000-01-01',
  templateType: 'professional',
  ...overrides,
});

export const createTestUser = (overrides = {}) => ({
  id: 'user-123',
  email: 'test@example.com',
  name: 'Test User',
  role: 'USER',
  ...overrides,
});

export const createTestChatState = (currentQuestion = 1) => ({
  questions: Array.from({ length: 15 }, (_, i) => `Question ${i + 1}?`),
  currentStage: Math.floor((currentQuestion - 1) / 5) + 1,
  currentQuestionIndex: (currentQuestion - 1) % 5,
  currentQuestion: `Question ${currentQuestion}?`,
  answers: {},
});
```

---

## 9. Test Data Cleanup Strategy

### Before Each Test
```java
@BeforeEach
void setUp() {
    // Clear test data
    profileRepository.deleteAll();
    userRepository.deleteAll();
    // Reset mocks
    reset(mockService);
}
```

### After Each Test
```java
@AfterEach
void tearDown() {
    // Cleanup test data
    profileRepository.deleteAll();
    userRepository.deleteAll();
}
```

### Test Isolation
- Each test should use unique IDs (UUIDs)
- Tests should not depend on data from other tests
- Use `@DirtiesContext` if needed for integration tests

---

## 10. Test Data Validation Checklist

For each test data set, verify:
- [ ] Required fields are present
- [ ] Data types are correct
- [ ] Field lengths are within limits
- [ ] Special characters are handled
- [ ] Edge cases are covered (null, empty, max length)
- [ ] Security concerns are addressed (XSS, SQL injection)
- [ ] Data is realistic and representative

---

## Summary

This test data strategy ensures:
- ✅ Comprehensive coverage of all scenarios
- ✅ Consistent, reusable test data
- ✅ Edge cases and boundary conditions
- ✅ Security test scenarios
- ✅ Easy maintenance and updates
- ✅ Realistic test data for better test quality


