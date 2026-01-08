# Test Execution Guide

## Quick Start

### Prerequisites
- Java 17+ installed
- Node.js 18+ installed
- MongoDB running (or Docker for test containers)
- Gradle 7+ installed

---

## Backend Test Execution

### Windows (PowerShell)
```powershell
cd backend
.\gradlew test
```

### Linux/Mac
```bash
cd backend
./gradlew test
```

### Run Specific Test Class
```powershell
# Windows
.\gradlew test --tests AuthServiceImplTest

# Linux/Mac
./gradlew test --tests AuthServiceImplTest
```

### Run with Coverage Report
```powershell
.\gradlew test jacocoTestReport
```

### View Coverage Report
After running with coverage, open:
```
backend/build/reports/jacoco/test/html/index.html
```

---

## Frontend Test Execution

### Install Dependencies (First Time)
```bash
cd frontend
npm install
```

### Run All Tests
```bash
npm test
```

### Run in Watch Mode
```bash
npm test -- --watch
```

### Run with Coverage
```bash
npm test -- --coverage
```

### View Coverage Report
After running with coverage, open:
```
frontend/coverage/index.html
```

---

## Test Files Location

### Backend Tests
```
backend/src/test/java/com/profiling/
├── service/
│   ├── AuthServiceImplTest.java ✅
│   ├── ProfileServiceImplTest.java ✅
│   ├── OpenAIServiceImplTest.java ✅
│   └── ChatbotServiceTest.java ✅
└── controller/
    └── ProfileControllerTest.java ✅
```

### Frontend Tests
```
frontend/src/
├── components/__tests__/
│   ├── ProfileForm.test.jsx ✅
│   ├── TemplateSelection.test.jsx ✅
│   ├── SaarthiChatbot.test.jsx ✅
│   ├── SavedProfiles.test.jsx ✅
│   └── LoginPage.test.jsx ✅
└── api/__tests__/
    └── api.test.js ✅
```

---

## Troubleshooting

### Backend Issues

**Issue**: Tests fail with "Class not found"
- **Solution**: Run `./gradlew clean build` first

**Issue**: MongoDB connection errors
- **Solution**: Ensure MongoDB is running or use Testcontainers

**Issue**: Mockito errors
- **Solution**: Verify all dependencies are in build.gradle

### Frontend Issues

**Issue**: "Cannot find module" errors
- **Solution**: Run `npm install` to install dependencies

**Issue**: Tests fail with "document is not defined"
- **Solution**: Ensure jsdom is installed and configured in vitest.config.js

**Issue**: Component not rendering
- **Solution**: Check if all required props are provided in test

---

## Expected Test Results

### Backend
- **AuthServiceImplTest**: ~8-10 tests should pass
- **ProfileServiceImplTest**: ~15-20 tests should pass
- **OpenAIServiceImplTest**: ~8-10 tests should pass
- **ChatbotServiceTest**: ~6-8 tests should pass
- **ProfileControllerTest**: ~8-10 tests should pass

### Frontend
- **ProfileForm.test.jsx**: ~4-5 tests should pass
- **TemplateSelection.test.jsx**: ~4-5 tests should pass
- **SaarthiChatbot.test.jsx**: ~3-4 tests should pass
- **SavedProfiles.test.jsx**: ~3-4 tests should pass
- **LoginPage.test.jsx**: ~3-4 tests should pass
- **api.test.js**: ~5-6 tests should pass

---

## Next Steps After Running Tests

1. **Review Failures**: Check which tests fail and why
2. **Fix Issues**: Update tests to match actual implementation
3. **Add Missing Tests**: Implement remaining test cases
4. **Improve Coverage**: Add tests for uncovered code paths
5. **CI/CD Setup**: Configure automated test execution

---

## Test Coverage Goals

- **Backend**: 85%+ overall coverage
- **Frontend**: 80%+ overall coverage
- **Critical Paths**: 100% coverage
- **Security Tests**: 100% coverage

---

**For detailed test case documentation, see**: TEST_PLAN_COMPLETE.md
**For implementation status, see**: TEST_IMPLEMENTATION_STATUS.md


