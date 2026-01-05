# Testing Instructions for Psychometric Report Fix

## Prerequisites
- Backend server running (Spring Boot application on port 8080)
- Frontend server running (React application on port 3000)
- MongoDB running and accessible

## Test Scenario

### Step 1: Start a Psychometric Test
1. Navigate to the psychometric test start page
2. Fill in the user information form
3. Start the test

### Step 2: Complete the Test
1. Answer some questions (doesn't matter if correct or incorrect)
2. You can:
   - Answer all questions
   - Skip some questions
   - Mark some for review
3. Submit the test (either manually or let it auto-submit)

### Step 3: Verify Result Page
1. After submission, you should see the **Result Page**
2. **Record these values** (example from screenshot):
   ```
   Total Questions: 120
   Attempted: 120
   Not Attempted: 0
   Correct Answers: 39
   Wrong Answers: 81
   Accuracy: 32.50%
   ```

3. Take a screenshot or note these numbers

### Step 4: Generate Report
1. Click the **"View Detailed Report"** button on the result page
2. Wait for the report to generate
3. You should be redirected to the report page

### Step 5: Verify Report Values
1. Look at the top section of the report with candidate information
2. **Verify these fields match the result page**:

   **MCQ SCORING:**
   - Should show: `39/120` (same as Correct/Total from result page)
   
   **CANDIDATE PERCENTAGE:**
   - Should show: `32.50%` (same as percentage from result page)
   
   **Progress Bar:**
   - Should be filled to `32.50%`
   - Should show: `32.50/100` below the bar

3. Scroll to the bottom of the report

4. **Verify Overall Score:**
   - Look for the "Overall Score" section
   - Should show: `32.5%` (matches the candidate percentage)

## Expected Results

### ✅ PASS Criteria
- [ ] MCQ Scoring on report = Correct/Total from result page
- [ ] Candidate Percentage on report = Percentage from result page
- [ ] Progress bar shows correct percentage visually
- [ ] Overall Score = Candidate Percentage
- [ ] All numbers are consistent between result page and report

### ❌ FAIL Criteria
- MCQ Scoring is different between result and report pages
- Percentage is different between result and report pages
- Overall score doesn't match candidate percentage

## Example Test Case

### Input (Result Page)
```
Total Questions: 120
Attempted: 120
Correct: 39
Wrong: 81
Percentage: 32.50%
```

### Expected Output (Report Page)
```
MCQ SCORING: 39/120
CANDIDATE PERCENTAGE: 32.50%
Progress Bar: 32.50% filled
Overall Score: 32.5%
```

## Testing Different Scenarios

### Scenario 1: Perfect Score
1. Answer all questions correctly
2. Result page should show: 100% accuracy
3. Report should show: 100% candidate percentage

### Scenario 2: Partial Attempt
1. Answer only 60 out of 120 questions
2. Get 30 correct out of 60 attempted
3. Result page: 60 attempted, 30 correct, 25% overall
4. Report: Should match result page values

### Scenario 3: No Questions Attempted
1. Submit test without answering anything
2. Result page: 0 correct, 0%
3. Report: Should show 0% everywhere

## Debugging

If values don't match, check:

1. **Browser Console**: Look for any JavaScript errors
2. **Network Tab**: Check the response from `/api/psychometric/sessions/{id}/report`
3. **Backend Logs**: Look for any errors during report generation
4. **MongoDB**: Check if `testResults` field is saved in the session document

### Check Session Data in MongoDB
```javascript
db.psychometric_sessions.findOne({_id: "your-session-id"})
```

Should contain:
```json
{
  "_id": "session-id",
  "testResults": {
    "totalQuestions": 120,
    "attempted": 120,
    "correct": 39,
    "wrong": 81,
    "notAttempted": 0,
    "markedForReview": 0,
    "answeredAndMarkedForReview": 0,
    "submittedAt": "2025-12-22T..."
  }
}
```

## Verification Checklist

Before marking the fix as complete:

- [ ] Completed at least one full test
- [ ] Noted values from result page
- [ ] Generated report successfully
- [ ] Verified MCQ Scoring matches
- [ ] Verified Candidate Percentage matches
- [ ] Verified Overall Score = Candidate Percentage
- [ ] Tested with different answer combinations
- [ ] Checked backend logs for errors
- [ ] Verified testResults is saved in MongoDB

## Known Issues to Watch For

1. **Decimal Precision**: Result page shows 32.5%, report might show 32.50% - this is OK (just formatting)
2. **Rounding Differences**: Should be minimal (< 0.01%)
3. **Old Sessions**: Sessions from before the fix won't have testResults, but should still work with fallback calculation

## Rollback Plan

If issues occur:
1. The changes are backward compatible
2. Old sessions will use fallback calculation
3. No database migration needed
4. Can revert code changes if needed

## Success Indicators

✅ **Main Goal Achieved**: Report displays exact same values as result page
✅ **User Experience**: No confusion between result and report pages
✅ **Data Integrity**: Single source of truth from frontend calculation







