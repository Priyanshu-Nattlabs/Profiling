# Profiling Service - Complete Test Plan

## Table of Contents
1. [Test Strategy Overview](#test-strategy-overview)
2. [Test Cases by Module](#test-cases-by-module)
3. [Test Coverage Checklist](#test-coverage-checklist)
4. [Test Data Strategy](#test-data-strategy)
5. [Mocking Strategy](#mocking-strategy)

---

## Test Strategy Overview

### Testing Layers
- **Unit Tests**: Service layer, utility classes, business logic
- **Integration Tests**: API endpoints, database interactions, external service calls
- **E2E Tests**: Complete user flows from frontend to backend

### Test Types
- **Positive Tests**: Valid inputs, expected behavior
- **Negative Tests**: Invalid inputs, error handling
- **Edge Cases**: Boundary conditions, null values, empty strings
- **Security Tests**: Authorization, data isolation, injection attacks
- **Performance Tests**: Response times, concurrent requests
- **AI Failure Tests**: OpenAI API failures, timeouts, invalid responses

---

## Test Cases by Module

### MODULE 1: Authentication & Authorization

#### TC-AUTH-001: User Registration - Valid Input
- **Type**: Integration
- **Preconditions**: No existing user with same email
- **Steps**:
  1. POST `/api/auth/register` with valid email, password, name
  2. Verify response contains JWT token
  3. Verify user is saved in database
- **Expected Result**: User registered successfully, token returned, user saved with role=USER
- **Automatable**: ✅

#### TC-AUTH-002: User Registration - Duplicate Email
- **Type**: Integration
- **Preconditions**: User with email already exists
- **Steps**:
  1. POST `/api/auth/register` with existing email
- **Expected Result**: 400 Bad Request, "Email already registered"
- **Automatable**: ✅

#### TC-AUTH-003: User Registration - Invalid Email Format
- **Type**: Integration
- **Preconditions**: None
- **Steps**:
  1. POST `/api/auth/register` with invalid email (e.g., "notanemail")
- **Expected Result**: 400 Bad Request, validation error
- **Automatable**: ✅

#### TC-AUTH-004: User Registration - Missing Required Fields
- **Type**: Integration
- **Preconditions**: None
- **Steps**:
  1. POST `/api/auth/register` with missing email/password/name
- **Expected Result**: 400 Bad Request, validation error
- **Automatable**: ✅

#### TC-AUTH-005: User Login - Valid Credentials
- **Type**: Integration
- **Preconditions**: User exists in database
- **Steps**:
  1. POST `/api/auth/login` with correct email and password
  2. Verify response contains JWT token
- **Expected Result**: 200 OK, token returned, token contains userId and role
- **Automatable**: ✅

#### TC-AUTH-006: User Login - Invalid Email
- **Type**: Integration
- **Preconditions**: User does not exist
- **Steps**:
  1. POST `/api/auth/login` with non-existent email
- **Expected Result**: 401 Unauthorized, "Invalid email or password"
- **Automatable**: ✅

#### TC-AUTH-007: User Login - Invalid Password
- **Type**: Integration
- **Preconditions**: User exists
- **Steps**:
  1. POST `/api/auth/login` with correct email but wrong password
- **Expected Result**: 401 Unauthorized, "Invalid email or password"
- **Automatable**: ✅

#### TC-AUTH-008: JWT Token Validation - Valid Token
- **Type**: Integration
- **Preconditions**: User logged in, valid token obtained
- **Steps**:
  1. GET `/api/auth/me` with valid JWT token in Authorization header
- **Expected Result**: 200 OK, user data returned
- **Automatable**: ✅

#### TC-AUTH-009: JWT Token Validation - Expired Token
- **Type**: Integration
- **Preconditions**: Token expired (wait or manipulate expiration)
- **Steps**:
  1. GET `/api/auth/me` with expired token
- **Expected Result**: 401 Unauthorized
- **Automatable**: ✅

#### TC-AUTH-010: JWT Token Validation - Invalid Token
- **Type**: Integration
- **Preconditions**: None
- **Steps**:
  1. GET `/api/auth/me` with malformed/invalid token
- **Expected Result**: 401 Unauthorized
- **Automatable**: ✅

#### TC-AUTH-011: JWT Token Validation - Missing Token
- **Type**: Integration
- **Preconditions**: None
- **Steps**:
  1. GET `/api/profiles/my-profile` without Authorization header
- **Expected Result**: 401 Unauthorized
- **Automatable**: ✅

#### TC-AUTH-012: Google OAuth - Successful Authentication
- **Type**: Integration
- **Preconditions**: Google OAuth configured
- **Steps**:
  1. Navigate to `/login/oauth2/code/google`
  2. Complete Google OAuth flow
  3. Verify user created/updated in database
  4. Verify JWT token returned
- **Expected Result**: User authenticated, token returned, user saved with provider="google"
- **Automatable**: ⚠️ (Requires OAuth test setup)

#### TC-AUTH-013: Google OAuth - OAuth Failure
- **Type**: Integration
- **Preconditions**: OAuth configured
- **Steps**:
  1. Simulate OAuth failure (deny access, invalid code)
- **Expected Result**: Redirect to frontend with error parameter
- **Automatable**: ⚠️

#### TC-AUTH-014: Data Isolation - User Cannot Access Another User's Profile
- **Type**: Integration 🔴
- **Preconditions**: Two users exist, each with profiles
- **Steps**:
  1. User A logs in, gets token
  2. User A attempts GET `/api/profiles/{userB_profileId}` with User A's token
- **Expected Result**: 404 Not Found or 403 Forbidden (not 200 with User B's data)
- **Automatable**: ✅

#### TC-AUTH-015: Data Isolation - User Cannot Update Another User's Profile
- **Type**: Integration 🔴
- **Preconditions**: Two users exist, User B has a profile
- **Steps**:
  1. User A logs in, gets token
  2. User A attempts PUT `/api/profiles/{userB_profileId}` with User A's token
- **Expected Result**: 404 Not Found or 403 Forbidden
- **Automatable**: ✅

---

### MODULE 2: Profile Form Validation

#### TC-FORM-001: Form Submission - All Required Fields Valid
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Fill all required fields (name, email, dob)
  2. Submit form
- **Expected Result**: Profile created successfully
- **Automatable**: ✅

#### TC-FORM-002: Form Submission - Missing Required Field (Name)
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form without name
- **Expected Result**: 400 Bad Request, validation error
- **Automatable**: ✅

#### TC-FORM-003: Form Submission - Missing Required Field (Email)
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form without email
- **Expected Result**: 400 Bad Request, validation error
- **Automatable**: ✅

#### TC-FORM-004: Form Submission - Missing Required Field (DOB)
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form without date of birth
- **Expected Result**: 400 Bad Request, validation error
- **Automatable**: ✅

#### TC-FORM-005: Form Submission - Invalid Email Format
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with invalid email (e.g., "notanemail")
- **Expected Result**: 400 Bad Request, validation error
- **Automatable**: ✅

#### TC-FORM-006: Form Submission - Invalid Phone Format
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with invalid phone (e.g., "abc123")
- **Expected Result**: 400 Bad Request or phone ignored (if optional)
- **Automatable**: ✅

#### TC-FORM-007: Form Submission - Invalid LinkedIn URL
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with invalid LinkedIn URL (e.g., "notaurl")
- **Expected Result**: 400 Bad Request or URL ignored (if optional)
- **Automatable**: ✅

#### TC-FORM-008: Form Submission - Invalid Date Format
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with invalid date format (e.g., "32/13/2024")
- **Expected Result**: 400 Bad Request, validation error
- **Automatable**: ✅

#### TC-FORM-009: Form Submission - Future Date of Birth
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with DOB in the future
- **Expected Result**: 400 Bad Request or accepted (depending on business rule)
- **Automatable**: ✅

#### TC-FORM-010: Form Submission - Extremely Long Text Fields
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with 10,000+ character strings in text fields
- **Expected Result**: 400 Bad Request or truncated (depending on validation)
- **Automatable**: ✅

#### TC-FORM-011: Form Submission - Special Characters in Fields
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with SQL injection attempts, XSS attempts, special chars
- **Expected Result**: Data sanitized, no security breach
- **Automatable**: ✅

#### TC-FORM-012: Form Submission - Empty Optional Fields
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with only required fields, all optional fields empty
- **Expected Result**: Profile created successfully
- **Automatable**: ✅

#### TC-FORM-013: Resume Parsing - Valid PDF
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/profiles/parse-resume` with valid PDF file
- **Expected Result**: 200 OK, extracted data returned
- **Automatable**: ✅

#### TC-FORM-014: Resume Parsing - Valid DOCX
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/profiles/parse-resume` with valid DOCX file
- **Expected Result**: 200 OK, extracted data returned
- **Automatable**: ✅

#### TC-FORM-015: Resume Parsing - Invalid File Type
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/profiles/parse-resume` with .txt or .jpg file
- **Expected Result**: 400 Bad Request, "Invalid file type"
- **Automatable**: ✅

#### TC-FORM-016: Resume Parsing - Empty File
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/profiles/parse-resume` with empty file
- **Expected Result**: 400 Bad Request, "File is required"
- **Automatable**: ✅

#### TC-FORM-017: Resume Parsing - Corrupted File
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/profiles/parse-resume` with corrupted PDF/DOCX
- **Expected Result**: 400 Bad Request or 500 Internal Server Error with appropriate message
- **Automatable**: ✅

---

### MODULE 3: Template Selection

#### TC-TEMPLATE-001: Fetch All Templates - Authenticated User
- **Type**: Integration
- **Preconditions**: User authenticated, templates exist in database
- **Steps**:
  1. GET `/api/templates/all` with valid JWT token
- **Expected Result**: 200 OK, list of templates returned
- **Automatable**: ✅

#### TC-TEMPLATE-002: Fetch All Templates - Unauthenticated User
- **Type**: Integration
- **Preconditions**: None
- **Steps**:
  1. GET `/api/templates/all` without token
- **Expected Result**: 200 OK (if public) or 401 Unauthorized
- **Automatable**: ✅

#### TC-TEMPLATE-003: Select Valid Template
- **Type**: E2E
- **Preconditions**: User authenticated, form filled
- **Steps**:
  1. Select a valid template (e.g., "professional")
  2. Submit profile with templateType
- **Expected Result**: Profile generated with selected template
- **Automatable**: ✅

#### TC-TEMPLATE-004: Select Invalid Template
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit profile with templateType="invalid-template"
- **Expected Result**: 400 Bad Request, "Template type 'invalid-template' is not available"
- **Automatable**: ✅

#### TC-TEMPLATE-005: Select Template - Null Template Type
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit profile with templateType=null
- **Expected Result**: Default template used ("professional") or 400 Bad Request
- **Automatable**: ✅

#### TC-TEMPLATE-006: Select Template - Empty Template Type
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit profile with templateType=""
- **Expected Result**: Default template used ("professional")
- **Automatable**: ✅

#### TC-TEMPLATE-007: Template Preview - Valid Template
- **Type**: E2E
- **Preconditions**: User authenticated
- **Steps**:
  1. Hover over template in selection page
  2. Verify preview displays
- **Expected Result**: Preview shows sample profile with template styling
- **Automatable**: ⚠️ (UI test)

#### TC-TEMPLATE-008: Template Preview - Missing Preview Image
- **Type**: E2E
- **Preconditions**: User authenticated, template without previewImageUrl
- **Steps**:
  1. Hover over template without preview image
- **Expected Result**: Default preview or no preview shown gracefully
- **Automatable**: ⚠️ (UI test)

---

### MODULE 4: Profile Generation (Free vs Basic)

#### TC-GEN-001: Free Template Generation - Static Template
- **Type**: Integration
- **Preconditions**: User authenticated, form filled, template selected
- **Steps**:
  1. Submit profile with templateType (no AI enhancement)
  2. Verify profile.aiEnhancedTemplateText is null
- **Expected Result**: Profile created, templateText contains static template rendering, no AI call made
- **Automatable**: ✅

#### TC-GEN-002: Basic Template Generation - AI Enhancement Success
- **Type**: Integration
- **Preconditions**: User authenticated, OpenAI API key configured, form filled
- **Steps**:
  1. Submit profile with templateType
  2. Trigger AI enhancement (via regenerate or initial generation with Basic template)
  3. Mock OpenAI to return valid response
- **Expected Result**: Profile created, aiEnhancedTemplateText populated, templateText uses AI-enhanced content
- **Automatable**: ✅

#### TC-GEN-003: Basic Template Generation - OpenAI API Failure
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, OpenAI API configured
- **Steps**:
  1. Submit profile regeneration with AI enhancement
  2. Mock OpenAI to return 500 error
- **Expected Result**: Error handled gracefully, user-friendly message, profile not corrupted
- **Automatable**: ✅

#### TC-GEN-004: Basic Template Generation - OpenAI API Timeout
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, OpenAI API configured
- **Steps**:
  1. Submit profile regeneration with AI enhancement
  2. Mock OpenAI to timeout after 600 seconds
- **Expected Result**: Timeout handled, error message, fallback to static template or clear error
- **Automatable**: ✅

#### TC-GEN-005: Basic Template Generation - OpenAI Invalid Response
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, OpenAI API configured
- **Steps**:
  1. Submit profile regeneration with AI enhancement
  2. Mock OpenAI to return invalid JSON or empty choices
- **Expected Result**: Error handled, user-friendly message, no crash
- **Automatable**: ✅

#### TC-GEN-006: Basic Template Generation - OpenAI Rate Limit
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, OpenAI API configured
- **Steps**:
  1. Submit profile regeneration with AI enhancement
  2. Mock OpenAI to return 429 Too Many Requests
- **Expected Result**: Rate limit error handled, retry logic or user message
- **Automatable**: ✅

#### TC-GEN-007: Basic Template Generation - OpenAI Missing API Key
- **Type**: Integration 🔴
- **Preconditions**: OpenAI API key not configured
- **Steps**:
  1. Submit profile regeneration with AI enhancement
- **Expected Result**: Error handled gracefully, fallback to static template or clear error message
- **Automatable**: ✅

#### TC-GEN-008: Profile Generation - Template Rendering with All Fields
- **Type**: Integration
- **Preconditions**: User authenticated, form filled with all fields
- **Steps**:
  1. Submit profile with all fields populated
  2. Verify template rendering includes all provided data
- **Expected Result**: Template text contains all user-provided information
- **Automatable**: ✅

#### TC-GEN-009: Profile Generation - Template Rendering with Partial Fields
- **Type**: Integration
- **Preconditions**: User authenticated, form filled with only required fields
- **Steps**:
  1. Submit profile with only required fields
  2. Verify template rendering handles missing optional fields gracefully
- **Expected Result**: Template text generated without errors, missing fields handled (not shown or shown as "N/A")
- **Automatable**: ✅

#### TC-GEN-010: Profile Generation - Special Characters in Template
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit profile with special characters (HTML, quotes, newlines)
  2. Verify template rendering escapes/handles special characters
- **Expected Result**: Template renders correctly, no XSS vulnerabilities
- **Automatable**: ✅

#### TC-GEN-011: Profile Generation - Profile Image Handling
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit profile with base64 encoded profile image
  2. Verify image is stored/displayed correctly
- **Expected Result**: Profile image saved and displayed in template
- **Automatable**: ✅

#### TC-GEN-012: Profile Generation - Profile Image Missing
- **Type**: Integration
- **Preconditions**: User authenticated, template requires photo
- **Steps**:
  1. Submit profile without profile image for photo-required template
- **Expected Result**: Template generated with placeholder or error message
- **Automatable**: ✅

---

### MODULE 5: Chatbot Functionality

#### TC-CHAT-001: Chatbot Access - Before Profile Creation
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, no profile created
- **Steps**:
  1. Attempt to access chatbot endpoint
- **Expected Result**: 400 Bad Request or redirect, "Profile must be created first"
- **Automatable**: ✅

#### TC-CHAT-002: Chatbot Access - After Profile Creation
- **Type**: Integration
- **Preconditions**: User authenticated, profile created
- **Steps**:
  1. Access chatbot endpoint after profile creation
- **Expected Result**: Chatbot accessible, questions generated
- **Automatable**: ✅

#### TC-CHAT-003: Generate Questions - Valid Profile
- **Type**: Integration
- **Preconditions**: User authenticated, profile created
- **Steps**:
  1. POST `/api/generate-questions` with user profile
  2. Mock OpenAI to return valid questions
- **Expected Result**: 200 OK, 15 questions returned
- **Automatable**: ✅

#### TC-CHAT-004: Generate Questions - OpenAI Failure
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, profile created
- **Steps**:
  1. POST `/api/generate-questions`
  2. Mock OpenAI to fail
- **Expected Result**: Error handled, user-friendly message
- **Automatable**: ✅

#### TC-CHAT-005: Chat Message - Valid Answer
- **Type**: Integration
- **Preconditions**: User authenticated, conversation state initialized
- **Steps**:
  1. POST `/api/chat` with valid userMessage and conversationState
- **Expected Result**: 200 OK, next question returned or completion status
- **Automatable**: ✅

#### TC-CHAT-006: Chat Message - Missing User Message
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/chat` with empty userMessage
- **Expected Result**: 400 Bad Request, "User message is required"
- **Automatable**: ✅

#### TC-CHAT-007: Chat Message - Missing Conversation State
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/chat` without conversationState
- **Expected Result**: 400 Bad Request, "Conversation state is required"
- **Automatable**: ✅

#### TC-CHAT-008: Chat Flow - Complete 15 Questions
- **Type**: E2E
- **Preconditions**: User authenticated, profile created
- **Steps**:
  1. Start chatbot conversation
  2. Answer all 15 questions sequentially
  3. Verify conversation completes
- **Expected Result**: All 15 questions answered, conversation marked complete, answers stored
- **Automatable**: ✅

#### TC-CHAT-009: Chat Flow - Conversation State Persistence
- **Type**: Integration
- **Preconditions**: User authenticated, conversation started
- **Steps**:
  1. Answer question 5
  2. Verify conversationState tracks current question index
  3. Answer question 6
  4. Verify state updated correctly
- **Expected Result**: Conversation state correctly tracks progress
- **Automatable**: ✅

#### TC-CHAT-010: Chat Flow - Duplicate Question Handling
- **Type**: Integration
- **Preconditions**: User authenticated, conversation started
- **Steps**:
  1. Answer a question
  2. Attempt to answer same question again
- **Expected Result**: System prevents duplicate answers or handles gracefully
- **Automatable**: ✅

#### TC-CHAT-011: Chat Flow - Empty Answer
- **Type**: Integration
- **Preconditions**: User authenticated, conversation started
- **Steps**:
  1. Submit empty string as answer
- **Expected Result**: 400 Bad Request or answer accepted (depending on validation)
- **Automatable**: ✅

#### TC-CHAT-012: Chat Flow - Very Long Answer
- **Type**: Integration
- **Preconditions**: User authenticated, conversation started
- **Steps**:
  1. Submit 10,000+ character answer
- **Expected Result**: Answer accepted or truncated with validation
- **Automatable**: ✅

#### TC-CHAT-013: Chat Flow - Special Characters in Answer
- **Type**: Integration
- **Preconditions**: User authenticated, conversation started
- **Steps**:
  1. Submit answer with HTML, SQL injection attempts, special chars
- **Expected Result**: Answer sanitized, stored safely
- **Automatable**: ✅

---

### MODULE 6: Report Generation

#### TC-REPORT-001: Generate Report - Valid Session
- **Type**: Integration
- **Preconditions**: User authenticated, psychometric session completed
- **Steps**:
  1. POST `/api/psychometric/sessions/{sessionId}/generate-report`
  2. Verify report generated with scores and insights
- **Expected Result**: 200 OK, report with Big Five scores, section scores, insights
- **Automatable**: ✅

#### TC-REPORT-002: Generate Report - Incomplete Session
- **Type**: Integration
- **Preconditions**: User authenticated, session not completed
- **Steps**:
  1. Attempt to generate report for incomplete session
- **Expected Result**: 400 Bad Request, "Session must be completed"
- **Automatable**: ✅

#### TC-REPORT-003: Generate Report - Invalid Session ID
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/psychometric/sessions/invalid-id/generate-report`
- **Expected Result**: 404 Not Found
- **Automatable**: ✅

#### TC-REPORT-004: Generate Report - Another User's Session
- **Type**: Integration 🔴
- **Preconditions**: Two users, User B has completed session
- **Steps**:
  1. User A attempts to generate report for User B's session
- **Expected Result**: 404 Not Found or 403 Forbidden
- **Automatable**: ✅

#### TC-REPORT-005: Report Content - Scores Calculated Correctly
- **Type**: Integration
- **Preconditions**: User authenticated, session completed with known answers
- **Steps**:
  1. Generate report
  2. Verify Big Five scores match expected values
  3. Verify section scores calculated correctly
- **Expected Result**: All scores accurate based on answers
- **Automatable**: ✅

#### TC-REPORT-006: Report Content - Insights Generated
- **Type**: Integration
- **Preconditions**: User authenticated, session completed
- **Steps**:
  1. Generate report
  2. Verify insights section populated
- **Expected Result**: Report contains behavioral insights, fit analysis
- **Automatable**: ✅

#### TC-REPORT-007: Report Generation - OpenAI Failure
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, session completed
- **Steps**:
  1. Generate report
  2. Mock OpenAI to fail during insight generation
- **Expected Result**: Error handled, report generated with scores only or error message
- **Automatable**: ✅

---

### MODULE 7: Profile Enhancement

#### TC-ENHANCE-001: Enhance Profile with Report - Valid Data
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists, report generated
- **Steps**:
  1. POST `/api/profiles/enhance-with-report` with profileId, reportData
  2. Mock OpenAI to return enhanced text
- **Expected Result**: 200 OK, profile enhanced, aiEnhancedTemplateText updated
- **Automatable**: ✅

#### TC-ENHANCE-002: Enhance Profile with Report - Missing Report Data
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. POST `/api/profiles/enhance-with-report` without reportData
- **Expected Result**: 400 Bad Request, "Report data is required"
- **Automatable**: ✅

#### TC-ENHANCE-003: Enhance Profile with Report - OpenAI Failure
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, profile exists, report generated
- **Steps**:
  1. POST `/api/profiles/enhance-with-report`
  2. Mock OpenAI to fail
- **Expected Result**: Error handled, user-friendly message, profile not corrupted
- **Automatable**: ✅

#### TC-ENHANCE-004: Enhance Paragraph with Report - Valid Data
- **Type**: Integration
- **Preconditions**: User authenticated, report generated
- **Steps**:
  1. POST `/api/profiles/enhance-paragraph-with-report` with text and reportData
  2. Mock OpenAI to return enhanced text
- **Expected Result**: 200 OK, enhanced paragraph returned
- **Automatable**: ✅

#### TC-ENHANCE-005: Enhance Paragraph with Report - Empty Text
- **Type**: Integration
- **Preconditions**: User authenticated, report generated
- **Steps**:
  1. POST `/api/profiles/enhance-paragraph-with-report` with empty text
- **Expected Result**: 400 Bad Request, "Uploaded paragraph text is required"
- **Automatable**: ✅

#### TC-ENHANCE-006: Enhance Profile - Word Count Preservation
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. Enhance paragraph with specific word count
  2. Verify enhanced text maintains similar word count
- **Expected Result**: Enhanced text word count within acceptable range
- **Automatable**: ✅

---

### MODULE 8: Save Profile

#### TC-SAVE-001: Save Profile - New Profile
- **Type**: Integration
- **Preconditions**: User authenticated, form filled, template selected
- **Steps**:
  1. POST `/api/profiles` with profile data
  2. Verify profile saved in database
  3. Verify only profile data saved (not chatbot/report data)
- **Expected Result**: Profile saved, ID generated, createdAt set, chatbot/report data NOT in profile document
- **Automatable**: ✅

#### TC-SAVE-002: Save Profile - Update Existing Profile
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. PUT `/api/profiles/{id}` with updated data
  2. Verify profile updated
  3. Verify createdAt preserved
- **Expected Result**: Profile updated, createdAt unchanged, updatedAt set (if tracked)
- **Automatable**: ✅

#### TC-SAVE-003: Save Profile - Verify Chatbot Data Not Saved
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, profile created, chatbot completed
- **Steps**:
  1. Save profile after chatbot interaction
  2. Verify saved profile does NOT contain chatAnswers
- **Expected Result**: Profile saved without chatbot conversation data
- **Automatable**: ✅

#### TC-SAVE-004: Save Profile - Verify Report Data Not Saved
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, profile created, report generated
- **Steps**:
  1. Save profile after report generation
  2. Verify saved profile does NOT contain reportData
- **Expected Result**: Profile saved without report data
- **Automatable**: ✅

#### TC-SAVE-005: Save Profile - Profile Limit (Keep Last 3)
- **Type**: Integration
- **Preconditions**: User authenticated, 4+ profiles exist
- **Steps**:
  1. Create new profile (5th profile)
  2. Verify only last 3 profiles kept
- **Expected Result**: Oldest profiles deleted, only 3 most recent remain
- **Automatable**: ✅

#### TC-SAVE-006: Save Profile - Profile Limit Not Applied on Update
- **Type**: Integration
- **Preconditions**: User authenticated, 3 profiles exist
- **Steps**:
  1. Update existing profile (not create new)
  2. Verify all 3 profiles still exist
- **Expected Result**: No profiles deleted on update
- **Automatable**: ✅

#### TC-SAVE-007: Save Profile as JSON - Valid Profile
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. POST `/api/profiles/{id}/save-json`
  2. Verify JSON file created
- **Expected Result**: 200 OK, JSON file path returned, file contains profile data
- **Automatable**: ✅

#### TC-SAVE-008: Save Profile as JSON - Invalid Profile ID
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/profiles/invalid-id/save-json`
- **Expected Result**: 404 Not Found
- **Automatable**: ✅

#### TC-SAVE-009: Save Profile - Database Failure
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Mock database to throw exception
  2. Attempt to save profile
- **Expected Result**: 500 Internal Server Error or DataSaveException with user-friendly message
- **Automatable**: ✅

---

### MODULE 9: View Profile

#### TC-VIEW-001: Get Current User Profile - Profile Exists
- **Type**: Integration
- **Preconditions**: User authenticated, profile saved
- **Steps**:
  1. GET `/api/profiles/my-profile`
- **Expected Result**: 200 OK, profile with template returned
- **Automatable**: ✅

#### TC-VIEW-002: Get Current User Profile - No Profile Exists
- **Type**: Integration
- **Preconditions**: User authenticated, no profile saved
- **Steps**:
  1. GET `/api/profiles/my-profile`
- **Expected Result**: 404 Not Found, "No saved profile found"
- **Automatable**: ✅

#### TC-VIEW-003: Get All User Profiles - Multiple Profiles
- **Type**: Integration
- **Preconditions**: User authenticated, 3 profiles saved
- **Steps**:
  1. GET `/api/profiles/my-profiles`
- **Expected Result**: 200 OK, list of 3 profiles returned, sorted by createdAt desc
- **Automatable**: ✅

#### TC-VIEW-004: Get All User Profiles - No Profiles
- **Type**: Integration
- **Preconditions**: User authenticated, no profiles saved
- **Steps**:
  1. GET `/api/profiles/my-profiles`
- **Expected Result**: 200 OK, empty array
- **Automatable**: ✅

#### TC-VIEW-005: Get Profile by ID - Valid ID
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. GET `/api/profiles/{id}`
- **Expected Result**: 200 OK, profile returned
- **Automatable**: ✅

#### TC-VIEW-006: Get Profile by ID - Invalid ID
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. GET `/api/profiles/invalid-id`
- **Expected Result**: 404 Not Found
- **Automatable**: ✅

#### TC-VIEW-007: Get Profile by ID - Another User's Profile
- **Type**: Integration 🔴
- **Preconditions**: Two users, User B has profile
- **Steps**:
  1. User A attempts GET `/api/profiles/{userB_profileId}`
- **Expected Result**: 404 Not Found (not 200 with User B's data)
- **Automatable**: ✅

---

### MODULE 10: Download Profile

#### TC-DOWNLOAD-001: Download Profile as PDF - Valid Profile
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. GET `/api/profiles/{id}/download`
  2. Verify PDF generated
  3. Verify PDF content matches profile template text
- **Expected Result**: 200 OK, PDF file downloaded, content matches UI display
- **Automatable**: ✅

#### TC-DOWNLOAD-002: Download Profile as PDF - Invalid Profile ID
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. GET `/api/profiles/invalid-id/download`
- **Expected Result**: 404 Not Found
- **Automatable**: ✅

#### TC-DOWNLOAD-003: Download Profile as PDF - Another User's Profile
- **Type**: Integration 🔴
- **Preconditions**: Two users, User B has profile
- **Steps**:
  1. User A attempts GET `/api/profiles/{userB_profileId}/download`
- **Expected Result**: 404 Not Found
- **Automatable**: ✅

#### TC-DOWNLOAD-004: Download Profile - PDF Content Consistency
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. View profile in UI
  2. Download profile as PDF
  3. Compare PDF content with UI display
- **Expected Result**: PDF content exactly matches UI display (including AI-enhanced text if present)
- **Automatable**: ✅

#### TC-DOWNLOAD-005: Download Profile - PDF Generation Failure
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. Mock PDF generation to fail
  2. Attempt download
- **Expected Result**: 500 Internal Server Error with user-friendly message
- **Automatable**: ✅

---

### MODULE 11: Regenerate Profile

#### TC-REGEN-001: Regenerate Profile - Valid Request
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. POST `/api/profile/regenerate` with formData, templateId
  2. Mock OpenAI to return enhanced text
- **Expected Result**: 200 OK, profile regenerated, aiEnhancedTemplateText updated
- **Automatable**: ✅

#### TC-REGEN-002: Regenerate Profile - Without Re-running Chatbot
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, profile exists, chatbot completed
- **Steps**:
  1. Regenerate profile with existing chatAnswers
  2. Verify chatbot not called again
- **Expected Result**: Profile regenerated using existing chatAnswers, no new chatbot questions
- **Automatable**: ✅

#### TC-REGEN-003: Regenerate Profile - Missing Form Data
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/profile/regenerate` without formData
- **Expected Result**: 400 Bad Request, "Form data is required"
- **Automatable**: ✅

#### TC-REGEN-004: Regenerate Profile - Missing Template ID
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/profile/regenerate` without templateId
- **Expected Result**: 400 Bad Request, "Template type is required"
- **Automatable**: ✅

#### TC-REGEN-005: Regenerate Profile - User ID Mismatch
- **Type**: Integration 🔴
- **Preconditions**: User authenticated
- **Steps**:
  1. POST `/api/profile/regenerate` with userId different from token
- **Expected Result**: 401 Unauthorized, "Token user mismatch"
- **Automatable**: ✅

#### TC-REGEN-006: Regenerate Profile - Profile Image Preservation
- **Type**: Integration
- **Preconditions**: User authenticated, profile with image exists
- **Steps**:
  1. Regenerate profile without providing profileImage in formData
  2. Verify existing profileImage preserved
- **Expected Result**: Profile regenerated, profileImage unchanged
- **Automatable**: ✅

#### TC-REGEN-007: Regenerate Profile - OpenAI Failure
- **Type**: Integration 🔴
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. POST `/api/profile/regenerate`
  2. Mock OpenAI to fail
- **Expected Result**: Error handled, user-friendly message, profile not corrupted
- **Automatable**: ✅

---

### MODULE 12: Delete Profile

#### TC-DELETE-001: Delete Profile - Valid Profile
- **Type**: Integration
- **Preconditions**: User authenticated, profile exists
- **Steps**:
  1. DELETE `/api/profiles/{id}` (if endpoint exists)
  2. Verify profile deleted from database
- **Expected Result**: 200 OK or 204 No Content, profile deleted
- **Automatable**: ✅

#### TC-DELETE-002: Delete Profile - Invalid Profile ID
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. DELETE `/api/profiles/invalid-id`
- **Expected Result**: 404 Not Found
- **Automatable**: ✅

#### TC-DELETE-003: Delete Profile - Another User's Profile
- **Type**: Integration 🔴
- **Preconditions**: Two users, User B has profile
- **Steps**:
  1. User A attempts DELETE `/api/profiles/{userB_profileId}`
- **Expected Result**: 404 Not Found or 403 Forbidden
- **Automatable**: ✅

---

### MODULE 13: Error Handling & Edge Cases

#### TC-ERROR-001: Global Exception Handler - Bad Request
- **Type**: Integration
- **Preconditions**: None
- **Steps**:
  1. Send invalid request (e.g., malformed JSON)
- **Expected Result**: 400 Bad Request with clear error message
- **Automatable**: ✅

#### TC-ERROR-002: Global Exception Handler - Unauthorized
- **Type**: Integration
- **Preconditions**: None
- **Steps**:
  1. Access protected endpoint without token
- **Expected Result**: 401 Unauthorized with clear message
- **Automatable**: ✅

#### TC-ERROR-003: Global Exception Handler - Not Found
- **Type**: Integration
- **Preconditions**: None
- **Steps**:
  1. Access non-existent resource
- **Expected Result**: 404 Not Found with clear message
- **Automatable**: ✅

#### TC-ERROR-004: Global Exception Handler - Internal Server Error
- **Type**: Integration
- **Preconditions**: None
- **Steps**:
  1. Trigger unexpected exception (e.g., null pointer)
- **Expected Result**: 500 Internal Server Error, error logged, user-friendly message
- **Automatable**: ✅

#### TC-ERROR-005: Concurrent Requests - Same User
- **Type**: Performance
- **Preconditions**: User authenticated
- **Steps**:
  1. Send 10 concurrent requests to save profile
- **Expected Result**: All requests handled, no data corruption, appropriate responses
- **Automatable**: ✅

#### TC-ERROR-006: Concurrent Requests - Different Users
- **Type**: Performance
- **Preconditions**: Multiple users authenticated
- **Steps**:
  1. Send concurrent requests from different users
- **Expected Result**: All requests handled, data isolation maintained
- **Automatable**: ✅

#### TC-ERROR-007: Large Payload - Profile Data
- **Type**: Integration
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit profile with extremely large text fields (1MB+)
- **Expected Result**: Request rejected or truncated with validation error
- **Automatable**: ✅

#### TC-ERROR-008: SQL Injection Attempt
- **Type**: Security 🔴
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with SQL injection in text fields (e.g., "'; DROP TABLE--")
- **Expected Result**: Data sanitized, no SQL executed, profile saved safely
- **Automatable**: ✅

#### TC-ERROR-009: XSS Attempt
- **Type**: Security 🔴
- **Preconditions**: User authenticated
- **Steps**:
  1. Submit form with XSS payload (e.g., "<script>alert('XSS')</script>")
- **Expected Result**: Data sanitized, no script executed, safe rendering
- **Automatable**: ✅

---

### MODULE 14: Frontend Component Tests

#### TC-FRONT-001: ProfileForm - Render All Steps
- **Type**: Unit
- **Preconditions**: None
- **Steps**:
  1. Render ProfileForm component
  2. Verify all form steps render correctly
- **Expected Result**: All steps visible, navigation works
- **Automatable**: ✅

#### TC-FRONT-002: ProfileForm - Required Field Validation
- **Type**: Unit
- **Preconditions**: None
- **Steps**:
  1. Attempt to submit form without required fields
  2. Verify validation errors displayed
- **Expected Result**: Validation errors shown, form not submitted
- **Automatable**: ✅

#### TC-FRONT-003: ProfileForm - Resume Upload
- **Type**: Unit
- **Preconditions**: None
- **Steps**:
  1. Upload resume file
  2. Verify file parsed and form auto-filled
- **Expected Result**: Form fields populated from resume
- **Automatable**: ✅

#### TC-FRONT-004: TemplateSelection - Display Templates
- **Type**: Unit
- **Preconditions**: Templates available
- **Steps**:
  1. Render TemplateSelection component
  2. Verify templates displayed
- **Expected Result**: All templates shown with previews
- **Automatable**: ✅

#### TC-FRONT-005: TemplateSelection - Template Selection
- **Type**: Unit
- **Preconditions**: None
- **Steps**:
  1. Click on a template
  2. Verify selection callback triggered
- **Expected Result**: onTemplateSelect called with correct template ID
- **Automatable**: ✅

#### TC-FRONT-006: ProfileDisplay - Display Profile
- **Type**: Unit
- **Preconditions**: Profile data available
- **Steps**:
  1. Render ProfileDisplay with profile data
  2. Verify profile rendered correctly
- **Expected Result**: Profile displayed with all fields
- **Automatable**: ✅

#### TC-FRONT-007: ProfileDisplay - Download PDF
- **Type**: Unit
- **Preconditions**: Profile displayed
- **Steps**:
  1. Click download PDF button
  2. Verify download triggered
- **Expected Result**: PDF download initiated
- **Automatable**: ✅

#### TC-FRONT-008: SaarthiChatbot - Render Chatbot
- **Type**: Unit
- **Preconditions**: Profile created
- **Steps**:
  1. Render SaarthiChatbot component
  2. Verify chatbot UI displayed
- **Expected Result**: Chatbot interface shown
- **Automatable**: ✅

#### TC-FRONT-009: SaarthiChatbot - Send Message
- **Type**: Unit
- **Preconditions**: Chatbot initialized
- **Steps**:
  1. Type message and send
  2. Verify API call made
- **Expected Result**: sendChatMessage API called with correct parameters
- **Automatable**: ✅

#### TC-FRONT-010: SavedProfiles - Display Profiles
- **Type**: Unit
- **Preconditions**: Profiles available
- **Steps**:
  1. Render SavedProfiles with profile list
  2. Verify profiles displayed
- **Expected Result**: All profiles shown in grid
- **Automatable**: ✅

#### TC-FRONT-011: SavedProfiles - Empty State
- **Type**: Unit
- **Preconditions**: No profiles
- **Steps**:
  1. Render SavedProfiles with empty array
  2. Verify empty state shown
- **Expected Result**: "No Saved Profiles" message displayed
- **Automatable**: ✅

#### TC-FRONT-012: LoginPage - Valid Login
- **Type**: Unit
- **Preconditions**: None
- **Steps**:
  1. Enter valid credentials
  2. Submit form
  3. Verify login API called
- **Expected Result**: login API called, token stored
- **Automatable**: ✅

#### TC-FRONT-013: LoginPage - Invalid Login
- **Type**: Unit
- **Preconditions**: None
- **Steps**:
  1. Enter invalid credentials
  2. Submit form
  3. Verify error displayed
- **Expected Result**: Error message shown, token not stored
- **Automatable**: ✅

---

## Test Coverage Checklist

### Backend Coverage Requirements
- [ ] **Authentication Service**: 90%+ coverage
- [ ] **Profile Service**: 90%+ coverage
- [ ] **OpenAI Service**: 85%+ coverage (including failure scenarios)
- [ ] **Chatbot Service**: 90%+ coverage
- [ ] **Report Generation Service**: 85%+ coverage
- [ ] **Template Service**: 90%+ coverage
- [ ] **Controllers**: 80%+ coverage (focus on error handling)
- [ ] **Repositories**: 85%+ coverage
- [ ] **Security Utils**: 95%+ coverage

### Frontend Coverage Requirements
- [ ] **ProfileForm**: 85%+ coverage
- [ ] **TemplateSelection**: 80%+ coverage
- [ ] **ProfileDisplay**: 85%+ coverage
- [ ] **SaarthiChatbot**: 80%+ coverage
- [ ] **SavedProfiles**: 80%+ coverage
- [ ] **LoginPage**: 85%+ coverage
- [ ] **API functions**: 90%+ coverage

### Critical Path Coverage
- [ ] Complete user flow: Register → Login → Fill Form → Select Template → Generate Profile → Chatbot → Report → Enhance → Save → View → Download
- [ ] All error scenarios in critical path
- [ ] All security checks (authorization, data isolation)
- [ ] All OpenAI integration points (success and failure)

---

## Test Data Strategy

### Test Users
- **User A**: Standard user for positive tests
- **User B**: Standard user for data isolation tests
- **Admin User**: For admin-specific tests
- **OAuth User**: For OAuth flow tests

### Test Profiles
- **Minimal Profile**: Only required fields
- **Complete Profile**: All fields populated
- **Profile with Image**: Profile with base64 image
- **Profile with Special Characters**: HTML, SQL, XSS attempts

### Test Templates
- **Professional Template**: Default template
- **Cover Letter Template**: Template with cover letter fields
- **Photo Template**: Template requiring profile image
- **Invalid Template**: Non-existent template ID

### Test Chatbot Data
- **15 Questions**: Complete question set
- **Partial Answers**: Incomplete conversation state
- **Empty Answers**: Empty string answers
- **Long Answers**: 10,000+ character answers

### Test Report Data
- **Complete Report**: All scores and insights
- **Partial Report**: Missing some sections
- **Invalid Report**: Malformed report data

### Test OpenAI Responses
- **Valid Response**: Normal OpenAI API response
- **Error Response**: 500, 429, timeout
- **Invalid Response**: Malformed JSON, empty choices
- **Rate Limit Response**: 429 with retry-after

---

## Mocking Strategy

### OpenAI API Mocking
- **Success Response**: Mock valid OpenAI Chat Completions response
- **Failure Response**: Mock 500, 429, timeout errors
- **Invalid Response**: Mock malformed JSON, empty choices
- **Rate Limit**: Mock 429 with retry-after header

### Database Mocking
- **MongoDB**: Use @DataMongoTest or MockMongoRepository
- **User Repository**: Mock user queries
- **Profile Repository**: Mock profile CRUD operations
- **Template Repository**: Mock template queries

### External Services
- **Google OAuth**: Mock OAuth2 authentication flow
- **File Storage**: Mock file save/read operations
- **PDF Generation**: Mock PDF service (or use real service in integration tests)

### Frontend API Mocking
- **Axios**: Use MSW (Mock Service Worker) or jest.mock
- **API Responses**: Mock success and error responses
- **Network Errors**: Mock network failures, timeouts

---

## Test Execution Strategy

### Unit Tests
- **Frequency**: Run on every commit
- **Execution Time**: < 5 minutes
- **Coverage Goal**: 85%+ overall

### Integration Tests
- **Frequency**: Run on pull requests
- **Execution Time**: < 15 minutes
- **Coverage Goal**: 80%+ of API endpoints

### E2E Tests
- **Frequency**: Run on merge to main
- **Execution Time**: < 30 minutes
- **Coverage Goal**: All critical user flows

### Performance Tests
- **Frequency**: Run weekly or before releases
- **Execution Time**: Variable
- **Coverage Goal**: Response times < 2s for 95th percentile

---

## Notes

- 🔴 indicates critical-risk areas requiring extra attention
- ✅ indicates test is automatable
- ⚠️ indicates test may require special setup or is partially automatable
- All security tests (data isolation, injection attacks) are marked as critical
- All OpenAI integration failure scenarios are marked as critical
- Test data should be cleaned up after each test run
- Use test containers for database integration tests
- Mock external services to avoid API costs and flakiness


