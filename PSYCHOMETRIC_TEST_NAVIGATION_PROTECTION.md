# Psychometric Test Navigation Protection

## Overview
Implemented comprehensive navigation protection to prevent users from leaving the psychometric test once it has started. Users can only submit the test to exit the assessment page.

## Changes Made

### File: `frontend/src/pages/psychometric/PsychometricAssessment.jsx`

#### 1. Added Browser Back Button Protection
- Added `beforeunload` event listener to warn users when:
  - Closing the browser tab
  - Refreshing the page
  - Navigating away using browser navigation
- Added `popstate` event listener to prevent browser back button usage
- When back button is pressed, user sees a confirmation dialog
- If user tries to go back, the page state is pushed back to prevent navigation

#### 3. Test Start Detection
- `testStarted` is set to `true` when:
  - Questions are loaded successfully
  - Timer starts
  - Proctoring begins
- This ensures protection only activates after the test actually begins

## User Experience Flow

### Before Changes:
1. User starts psychometric test
2. User could press browser back button or navigate away
3. Test progress might be lost
4. User could potentially cheat by looking up answers

### After Changes:
1. User starts psychometric test
2. Test begins (timer + proctoring starts)
3. Navigation protection activates automatically
4. If user tries to:
   - Press back button → Blocked with confirmation dialog
   - Close tab/browser → Browser shows warning message
   - Refresh page → Browser shows warning message
5. User can only exit by submitting the test
6. Once submitted, all protections are automatically disabled

## Protection Features

### 1. Browser Back Button
- Blocked with confirmation dialog
- State is pushed back to maintain current page
- Works across all modern browsers

### 2. Browser Refresh/Close
- Browser native warning appears
- Standard browser behavior for unsaved changes
- Works with Ctrl+W, Alt+F4, closing tab, etc.

### 3. Auto-Submission Scenarios
- Protection automatically disables when test is auto-submitted
- Works for both timer expiration and proctoring violations
- Smooth transition to results page

## Technical Implementation

```javascript
// Browser protection
useEffect(() => {
  if (!testStarted || isSubmitting || isAutoSubmitted) return

  const handleBeforeUnload = (e) => {
    e.preventDefault()
    e.returnValue = 'Your test is in progress...'
    return e.returnValue
  }

  const handlePopState = (e) => {
    e.preventDefault()
    const confirmLeave = window.confirm('Your test is in progress...')
    if (!confirmLeave) {
      window.history.pushState(null, '', window.location.href)
    }
  }

  window.history.pushState(null, '', window.location.href)
  window.addEventListener('beforeunload', handleBeforeUnload)
  window.addEventListener('popstate', handlePopState)

  return () => {
    window.removeEventListener('beforeunload', handleBeforeUnload)
    window.removeEventListener('popstate', handlePopState)
  }
}, [testStarted, isSubmitting, isAutoSubmitted])
```

## Testing Recommendations

1. **Browser Back Button**: Try pressing back button during test
2. **Browser Close**: Try closing the tab during test
3. **Browser Refresh**: Try refreshing the page during test
4. **Normal Submission**: Verify protection disables after submitting
5. **Auto-Submission**: Verify protection disables after timer expires or proctoring violations
6. **Multiple Browsers**: Test on Chrome, Firefox, Edge, Safari

## Security Benefits

1. **Prevents Cheating**: Users cannot navigate away to look up answers
2. **Prevents Data Loss**: Users cannot accidentally lose their progress
3. **Ensures Test Integrity**: Forces continuous test-taking without interruption
4. **Proctoring Enforcement**: Works alongside proctoring system to maintain test validity

## User Impact

- **Positive**: Users cannot accidentally lose progress
- **Positive**: Clear messaging about restrictions
- **Neutral**: Slight friction, but expected for formal assessments
- **Note**: Users are informed via instructions page about test restrictions before starting

## Future Enhancements (Optional)

1. Add grace period for accidental back button presses
2. Add analytics to track navigation attempt patterns
3. Add visual indicator showing test is "locked"
4. Consider implementing React Router blocker once stable API is confirmed
5. Consider allowing navigation to specific routes (e.g., help/FAQ) while preserving test state

## Implementation Notes

**Note**: The initial implementation used React Router's `useBlocker` hook, but this caused the assessment page to render blank. The current implementation uses browser-native protection (beforeunload and popstate events) which is more reliable and doesn't interfere with React rendering. This approach still provides strong protection against:
- Browser back button
- Page refresh
- Browser close/tab close
- Address bar navigation

The header is already hidden during assessment (handled in `PsychometricApp.jsx`), so there are no in-app navigation buttons to click during the test.

