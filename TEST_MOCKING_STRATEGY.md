# Mocking Strategy for Profiling Service Tests

## Overview
This document outlines the mocking strategy for testing the Profiling Service, focusing on external dependencies, APIs, and services that should be mocked to ensure reliable, fast, and cost-effective tests.

---

## 1. OpenAI API Mocking

### Why Mock OpenAI API
- **Cost**: Avoid API costs during testing
- **Reliability**: Prevent flaky tests due to network issues
- **Speed**: Faster test execution
- **Control**: Test various response scenarios (success, failure, timeout)

### Mocking Approach

#### Backend (Java/Spring Boot)
```java
@Mock
private OpenAIService openAIService;

// Success response
when(openAIService.enhanceProfile(anyString()))
    .thenReturn("AI-enhanced profile text");

// Failure response
when(openAIService.enhanceProfile(anyString()))
    .thenThrow(new RuntimeException("OpenAI API error"));

// Timeout simulation
when(openAIService.enhanceProfile(anyString()))
    .thenAnswer(invocation -> {
        Thread.sleep(700000); // Simulate timeout
        return "response";
    });

// Invalid response
OpenAIResponse invalidResponse = new OpenAIResponse();
invalidResponse.setChoices(null);
when(webClient.post()...)
    .thenReturn(Mono.just(invalidResponse));
```

#### Frontend (React/JavaScript)
```javascript
// Using MSW (Mock Service Worker) or jest.mock
vi.mock('../api', () => ({
  enhanceProfileWithAI: vi.fn(),
}));

// Success
api.enhanceProfileWithAI.mockResolvedValue({
  success: true,
  data: 'AI-enhanced text',
});

// Failure
api.enhanceProfileWithAI.mockRejectedValue(
  new Error('OpenAI API error')
);
```

### Test Scenarios to Mock
1. ✅ **Valid Response**: Normal OpenAI Chat Completions response
2. ✅ **500 Internal Server Error**: OpenAI service down
3. ✅ **429 Rate Limit**: Too many requests
4. ✅ **Timeout**: Request exceeds 600 seconds
5. ✅ **Invalid JSON**: Malformed response
6. ✅ **Empty Choices**: Response with no choices array
7. ✅ **Missing API Key**: 401 Unauthorized
8. ✅ **Network Error**: Connection failure

---

## 2. Database Mocking

### Why Mock Database
- **Speed**: Avoid database I/O overhead
- **Isolation**: Tests don't affect each other
- **Portability**: Tests run without database setup

### Mocking Approach

#### MongoDB Repository Mocking
```java
@Mock
private ProfileRepository profileRepository;

// Find by ID
when(profileRepository.findByIdAndUserId(profileId, userId))
    .thenReturn(Optional.of(testProfile));

// Find all by user
when(profileRepository.findAllByUserId(userId))
    .thenReturn(Collections.singletonList(testProfile));

// Save
when(profileRepository.save(any(Profile.class)))
    .thenReturn(savedProfile);

// Delete
doNothing().when(profileRepository).deleteById(profileId);

// Exception simulation
when(profileRepository.save(any(Profile.class)))
    .thenThrow(new MongoException("Database error"));
```

#### Integration Tests with Test Containers
For integration tests, use Testcontainers for real MongoDB:
```java
@Container
static MongoDBContainer mongoDBContainer = 
    new MongoDBContainer("mongo:7.0");

@Testcontainers
class ProfileServiceIntegrationTest {
    // Real database for integration tests
}
```

---

## 3. JWT Token Mocking

### Why Mock JWT
- **Unit Tests**: Avoid JWT generation/validation overhead
- **Control**: Test various token scenarios

### Mocking Approach
```java
@Mock
private JwtUtil jwtUtil;

// Valid token
when(jwtUtil.generateToken(userId, role))
    .thenReturn("valid-jwt-token");

// Token extraction
when(jwtUtil.extractUserId("valid-jwt-token"))
    .thenReturn(userId);

// Expired token
when(jwtUtil.extractExpiration("expired-token"))
    .thenReturn(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));

// Mock SecurityUtils for controller tests
try (MockedStatic<SecurityUtils> securityUtils = 
        mockStatic(SecurityUtils.class)) {
    securityUtils.when(SecurityUtils::getCurrentUserId)
        .thenReturn(userId);
    // Test code
}
```

---

## 4. File Storage Mocking

### Why Mock File Storage
- **Speed**: Avoid file I/O
- **Cleanup**: No test files left behind
- **Portability**: Tests run without file system setup

### Mocking Approach
```java
@Mock
private ProfileJsonService profileJsonService;

// Save JSON
when(profileJsonService.saveProfileAsJson(any(Profile.class)))
    .thenReturn("/path/to/profile.json");

// Exception
when(profileJsonService.saveProfileAsJson(any(Profile.class)))
    .thenThrow(new IOException("File system error"));
```

---

## 5. PDF Generation Mocking

### Why Mock PDF Generation
- **Speed**: PDF generation is slow
- **Dependencies**: Avoid PDF library issues in unit tests

### Mocking Approach
```java
@Mock
private PDFService pdfService;

// Generate PDF
byte[] mockPdfBytes = "PDF content".getBytes();
when(pdfService.generateProfilePDF(any(Profile.class), 
        any(TemplateRenderResult.class)))
    .thenReturn(mockPdfBytes);

// Exception
when(pdfService.generateProfilePDF(any(), any()))
    .thenThrow(new RuntimeException("PDF generation failed"));
```

---

## 6. Google OAuth Mocking

### Why Mock OAuth
- **Complexity**: OAuth flow is complex
- **External Dependency**: Avoid Google API calls
- **Control**: Test various OAuth scenarios

### Mocking Approach
```java
@Mock
private OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

// Mock OAuth2 user info
OAuth2User oauth2User = mock(OAuth2User.class);
when(oauth2User.getAttribute("email"))
    .thenReturn("oauth@example.com");
when(oauth2User.getAttribute("name"))
    .thenReturn("OAuth User");

// Test OAuth flow
when(authService.handleGoogleOAuth(any()))
    .thenReturn(new AuthResponse("token", "userId", 
        "oauth@example.com", "OAuth User", "USER"));
```

---

## 7. Frontend API Mocking

### Why Mock Frontend APIs
- **Isolation**: Test components without backend
- **Speed**: Avoid network calls
- **Control**: Test various response scenarios

### Mocking Approach

#### Using MSW (Mock Service Worker)
```javascript
// mocks/handlers.js
import { rest } from 'msw';

export const handlers = [
  rest.post('/api/profiles', (req, res, ctx) => {
    return res(
      ctx.status(201),
      ctx.json({
        message: 'Success',
        data: { profile: { id: 'profile123' } },
      })
    );
  }),

  rest.post('/api/chat', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json({
        message: 'Success',
        data: {
          nextQuestion: 'Question 2?',
          isComplete: false,
        },
      })
    );
  }),

  // Error scenarios
  rest.post('/api/profiles', (req, res, ctx) => {
    return res(
      ctx.status(500),
      ctx.json({ message: 'Internal Server Error' })
    );
  }),
];
```

#### Using Vitest/Jest Mocks
```javascript
// Mock axios
vi.mock('axios', () => ({
  create: vi.fn(() => ({
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  })),
}));

// Mock API functions
vi.mock('../api', () => ({
  submitProfile: vi.fn(),
  fetchTemplates: vi.fn(),
  sendChatMessage: vi.fn(),
}));
```

---

## 8. Template Service Mocking

### Why Mock Template Service
- **Isolation**: Test profile service without template complexity
- **Speed**: Avoid template rendering overhead

### Mocking Approach
```java
@Mock
private TemplateService templateService;

@Mock
private TemplateFactory templateFactory;

// Get template
when(templateService.getTemplateByType("professional", userId))
    .thenReturn(Optional.of(testTemplate));

// Generate template
when(templateFactory.generate("professional", any(Profile.class)))
    .thenReturn(new TemplateRenderResult(testTemplate, 
        "Rendered template"));
```

---

## 9. Resume Parser Mocking

### Why Mock Resume Parser
- **File Handling**: Avoid actual file parsing
- **Speed**: Faster test execution

### Mocking Approach
```java
@Mock
private ResumeParserService resumeParserService;

// Parse resume
ResumeDataDTO mockResumeData = new ResumeDataDTO();
mockResumeData.setName("John Doe");
mockResumeData.setEmail("john@example.com");

when(resumeParserService.parseResume(any(MultipartFile.class)))
    .thenReturn(mockResumeData);

// Exception
when(resumeParserService.parseResume(any()))
    .thenThrow(new IllegalArgumentException("Invalid file type"));
```

---

## 10. WebClient Mocking (Reactive)

### Why Mock WebClient
- **External Calls**: Avoid actual HTTP requests
- **Control**: Test various HTTP scenarios

### Mocking Approach
```java
@Mock
private WebClient.Builder webClientBuilder;

@Mock
private WebClient webClient;

@Mock
private WebClient.RequestBodyUriSpec requestBodyUriSpec;

@Mock
private WebClient.RequestBodySpec requestBodySpec;

@Mock
private WebClient.ResponseSpec responseSpec;

// Setup chain
when(webClientBuilder.build()).thenReturn(webClient);
when(webClient.post()).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
when(requestBodySpec.header(anyString(), anyString()))
    .thenReturn(requestBodySpec);
when(requestBodySpec.bodyValue(any())).thenReturn(requestBodySpec);
when(requestBodySpec.retrieve()).thenReturn(responseSpec);

// Mock response
when(responseSpec.bodyToMono(OpenAIResponse.class))
    .thenReturn(Mono.just(mockResponse));

// Mock error
when(responseSpec.bodyToMono(OpenAIResponse.class))
    .thenReturn(Mono.error(new WebClientResponseException(
        500, "Internal Server Error", null, null, null)));
```

---

## 11. Test Data Factories

### Why Use Test Data Factories
- **Consistency**: Reusable test data
- **Maintainability**: Single source of truth
- **Readability**: Clear test intent

### Example Factory
```java
public class TestDataFactory {
    public static Profile createTestProfile(String userId) {
        Profile profile = new Profile();
        profile.setId("profile-" + UUID.randomUUID());
        profile.setUserId(userId);
        profile.setName("Test User");
        profile.setEmail("test@example.com");
        profile.setTemplateType("professional");
        profile.setCreatedAt(LocalDateTime.now());
        return profile;
    }

    public static User createTestUser() {
        User user = new User();
        user.setId("user-" + UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole(UserRole.USER);
        return user;
    }

    public static OpenAIResponse createMockOpenAIResponse(String content) {
        OpenAIResponse response = new OpenAIResponse();
        Choice choice = new Choice();
        Message message = new Message();
        message.setContent(content);
        choice.setMessage(message);
        response.setChoices(List.of(choice));
        return response;
    }
}
```

---

## 12. Mocking Best Practices

### Do's ✅
1. **Mock External Dependencies**: Always mock external APIs, databases, file systems
2. **Verify Interactions**: Use `verify()` to ensure mocks are called correctly
3. **Reset Mocks**: Clear mocks between tests using `@BeforeEach`
4. **Use Test Containers for Integration**: Use real databases for integration tests
5. **Mock at the Right Level**: Mock at service boundaries, not internal methods

### Don'ts ❌
1. **Don't Mock Everything**: Don't mock the class under test
2. **Don't Mock Value Objects**: Don't mock simple data classes
3. **Don't Over-Mock**: Avoid deep mocking chains
4. **Don't Mock Internal Methods**: Only mock external dependencies
5. **Don't Ignore Integration Tests**: Use real services for integration tests

---

## 13. Mock Verification Checklist

For each test, verify:
- [ ] External API calls are mocked
- [ ] Database operations are mocked (unit tests) or use test containers (integration)
- [ ] File operations are mocked
- [ ] JWT tokens are mocked
- [ ] Error scenarios are tested with mocks
- [ ] Success scenarios are tested with mocks
- [ ] Mocks are reset between tests
- [ ] Mock interactions are verified

---

## 14. Tools and Libraries

### Backend
- **Mockito**: Primary mocking framework
- **JUnit 5**: Test framework
- **Testcontainers**: Real database for integration tests
- **WireMock**: HTTP server mocking (if needed)

### Frontend
- **Vitest/Jest**: Test framework with mocking
- **MSW (Mock Service Worker)**: API mocking
- **React Testing Library**: Component testing
- **@testing-library/user-event**: User interaction simulation

---

## Summary

This mocking strategy ensures:
- ✅ Fast test execution
- ✅ Reliable, non-flaky tests
- ✅ Cost-effective (no API charges)
- ✅ Isolated unit tests
- ✅ Comprehensive error scenario coverage
- ✅ Easy maintenance and updates


