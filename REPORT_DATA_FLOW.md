# Psychometric Report Data Flow

## Before Fix (Problem)

```
┌─────────────────────────────────────────────────────────────┐
│                    PSYCHOMETRIC TEST                         │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    RESULT PAGE                               │
│  Frontend calculates:                                        │
│  • Total Questions: 120                                      │
│  • Attempted: 120                                            │
│  • Correct: 39                                               │
│  • Wrong: 81                                                 │
│  • Percentage: 32.50%                                        │
│  • Accuracy: 32.50%                                          │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              SUBMIT TO BACKEND                               │
│  Sends: answers[] + results{}                                │
│  Backend saves: answers only ❌                              │
│  Backend DISCARDS results ❌                                 │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              GENERATE REPORT                                 │
│  Backend RE-CALCULATES:                                      │
│  • Total Questions: ??? (might differ)                       │
│  • Correct: ??? (might differ due to logic differences)      │
│  • Percentage: ??? (INCONSISTENT with result page)           │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    REPORT PAGE                               │
│  Shows DIFFERENT values than Result Page ❌                  │
│  • MCQ Scoring: Different                                    │
│  • Candidate Percentage: Different                           │
│  • Overall Score: Different                                  │
└─────────────────────────────────────────────────────────────┘
```

## After Fix (Solution)

```
┌─────────────────────────────────────────────────────────────┐
│                    PSYCHOMETRIC TEST                         │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    RESULT PAGE                               │
│  Frontend calculates:                                        │
│  • Total Questions: 120                                      │
│  • Attempted: 120                                            │
│  • Correct: 39                                               │
│  • Wrong: 81                                                 │
│  • Percentage: 32.50%                                        │
│  • Accuracy: 32.50%                                          │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              SUBMIT TO BACKEND                               │
│  Sends: answers[] + results{}                                │
│  Backend saves: answers + results ✅                         │
│  Stores in: session.testResults ✅                           │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              GENERATE REPORT                                 │
│  Backend USES SAVED RESULTS:                                 │
│  • report.totalQuestions = session.testResults.totalQuestions│
│  • report.correct = session.testResults.correct              │
│  • report.candidatePercentile = (correct/total) * 100        │
│  • report.overallScore = candidatePercentile ✅              │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    REPORT PAGE                               │
│  Shows SAME values as Result Page ✅                         │
│  • MCQ Scoring: 39/120 (matches result page)                 │
│  • Candidate Percentage: 32.50% (matches result page)        │
│  • Overall Score: 32.50% (matches percentage)                │
└─────────────────────────────────────────────────────────────┘
```

## Key Changes

### 1. PsychometricSession Model
```java
// NEW FIELD ADDED
private TestResults testResults;

// NEW INNER CLASS
public static class TestResults {
    private int totalQuestions;
    private int attempted;
    private int notAttempted;
    private int correct;
    private int wrong;
    private int markedForReview;
    private int answeredAndMarkedForReview;
    private String submittedAt;
    // getters and setters...
}
```

### 2. Submit Test Service
```java
// BEFORE
session.setAnswers(request.getAnswers());
session.setStatus(SessionStatus.COMPLETED);
repository.save(session);

// AFTER
session.setAnswers(request.getAnswers());
session.setStatus(SessionStatus.COMPLETED);

// Save test results from frontend ✅
PsychometricSession.TestResults testResults = new PsychometricSession.TestResults(
    request.getResults().getTotalQuestions(),
    request.getResults().getAttempted(),
    request.getResults().getNotAttempted(),
    request.getResults().getCorrect(),
    request.getResults().getWrong(),
    request.getResults().getMarkedForReview(),
    request.getResults().getAnsweredAndMarkedForReview(),
    request.getResults().getSubmittedAt()
);
session.setTestResults(testResults);

repository.save(session);
```

### 3. Report Generation Service
```java
// BEFORE
// Backend recalculated everything from scratch
int correct = calculateCorrect(session);
int totalQuestions = calculateTotal(session);
double percentage = (correct * 100.0 / totalQuestions);

// AFTER
// Use saved results from frontend
if (session.getTestResults() != null) {
    PsychometricSession.TestResults savedResults = session.getTestResults();
    totalQuestions = savedResults.getTotalQuestions();
    correct = savedResults.getCorrect();
    attempted = savedResults.getAttempted();
    wrong = savedResults.getWrong();
    
    report.setTotalQuestions(totalQuestions);
    report.setCorrect(correct);
    report.setAttempted(attempted);
    report.setWrong(wrong);
    report.setNotAttempted(savedResults.getNotAttempted());
}

// Calculate percentage using saved values
double candidatePercentage = totalQuestions > 0 ? (correct * 100.0 / totalQuestions) : 0.0;
report.setCandidatePercentile(candidatePercentage);
report.setOverallScore(candidatePercentage); // Matches percentage
```

## Result

✅ **Data Consistency**: Result page and report show identical values
✅ **Single Source of Truth**: Frontend calculation is authoritative
✅ **No User Confusion**: Numbers match across all screens
✅ **Backward Compatible**: Old sessions still work with fallback logic







