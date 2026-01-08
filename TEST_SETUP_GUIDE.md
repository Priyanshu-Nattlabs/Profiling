# Test Setup Guide - Profiling Service

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- MongoDB (or Docker for test containers)
- Gradle 7+
- npm/yarn

### Backend Test Setup

1. **Install Dependencies**
```bash
cd backend
./gradlew build
```

2. **Run Tests**
```bash
# All tests
./gradlew test

# With coverage
./gradlew test jacocoTestReport

# Specific test class
./gradlew test --tests ProfileServiceImplTest
```

3. **View Coverage Report**
```bash
open backend/build/reports/jacoco/test/html/index.html
```

### Frontend Test Setup

1. **Install Dependencies**
```bash
cd frontend
npm install
```

2. **Run Tests**
```bash
# All tests
npm test

# Watch mode
npm test -- --watch

# With coverage
npm test -- --coverage

# Specific test file
npm test ProfileForm.test.jsx
```

3. **View Coverage Report**
```bash
open frontend/coverage/index.html
```

---

## Test Structure

### Backend Test Structure
```
backend/src/test/java/com/profiling/
├── service/
│   ├── AuthServiceImplTest.java
│   ├── ProfileServiceImplTest.java
│   ├── OpenAIServiceImplTest.java
│   └── ChatbotServiceTest.java
├── controller/
│   ├── ProfileControllerTest.java
│   ├── AuthControllerTest.java
│   └── ChatControllerTest.java
└── repository/
    └── ProfileRepositoryTest.java
```

### Frontend Test Structure
```
frontend/src/
├── components/__tests__/
│   ├── ProfileForm.test.jsx
│   ├── TemplateSelection.test.jsx
│   ├── SaarthiChatbot.test.jsx
│   ├── SavedProfiles.test.jsx
│   └── LoginPage.test.jsx
└── api/__tests__/
    └── api.test.js
```

---

## Environment Variables for Testing

### Backend
```properties
# application-test.properties
MONGODB_URI=mongodb://localhost:57017/profiling_test
OPENAI_API_KEY=test-key (mocked in tests)
JWT_SECRET=test-secret-key-for-testing-only
```

### Frontend
```env
# .env.test
VITE_API_BASE_URL=http://localhost:9090
```

---

## Running Specific Test Suites

### Backend
```bash
# Authentication tests only
./gradlew test --tests "*Auth*"

# Service layer tests
./gradlew test --tests "*Service*"

# Controller tests
./gradlew test --tests "*Controller*"
```

### Frontend
```bash
# Component tests only
npm test -- components

# API tests only
npm test -- api
```

---

## Continuous Integration

### GitHub Actions Example
```yaml
name: Tests

on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: cd backend && ./gradlew test jacocoTestReport
      - uses: codecov/codecov-action@v3
        with:
          files: backend/build/reports/jacoco/test/jacocoTestReport.xml

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: cd frontend && npm install && npm test -- --coverage
```

---

## Troubleshooting

### Backend Issues

**Issue**: Tests fail with MongoDB connection error
- **Solution**: Use Testcontainers or ensure MongoDB is running

**Issue**: OpenAI API calls in tests
- **Solution**: Ensure OpenAI service is mocked in all tests

**Issue**: JWT token validation fails
- **Solution**: Mock SecurityUtils in controller tests

### Frontend Issues

**Issue**: Tests fail with "Cannot find module"
- **Solution**: Run `npm install` and check import paths

**Issue**: API calls not mocked
- **Solution**: Ensure MSW handlers are set up or API functions are mocked

**Issue**: Component not rendering
- **Solution**: Check if required props are provided in test

---

## Best Practices

1. **Isolation**: Each test should be independent
2. **Cleanup**: Clean up test data after each test
3. **Mocking**: Mock external dependencies
4. **Naming**: Use descriptive test names
5. **Coverage**: Aim for 85%+ coverage
6. **Documentation**: Document complex test scenarios

---

## Additional Resources

- [TEST_PLAN_COMPLETE.md](./TEST_PLAN_COMPLETE.md) - Complete test plan
- [TEST_MOCKING_STRATEGY.md](./TEST_MOCKING_STRATEGY.md) - Mocking strategy
- [TEST_DATA_STRATEGY.md](./TEST_DATA_STRATEGY.md) - Test data strategy
- [TEST_COVERAGE_CHECKLIST.md](./TEST_COVERAGE_CHECKLIST.md) - Coverage checklist


