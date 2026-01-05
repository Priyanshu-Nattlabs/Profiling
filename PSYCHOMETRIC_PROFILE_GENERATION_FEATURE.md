# Psychometric Profile Generation Feature

## Overview
This feature adds a "Create Profile from Psychometric Report" button to the psychometric report page. When clicked, it generates a concise profile (10-15 lines) from the psychometric report data, primarily using content from the Bio, SWOT analysis, test summary, and fit analysis sections.

## Implementation Details

### Backend Changes

#### 1. New Service: `ProfileFromReportService.java`
**Location:** `backend/src/main/java/com/profiling/service/psychometric/ProfileFromReportService.java`

**Purpose:** Generates a concise profile from psychometric report data.

**Key Method:**
```java
public String generateProfileFromReport(PsychometricReport report)
```

**Profile Content Structure (10-15 lines):**
1. **Basic Info (1-2 lines):** Name, degree, specialization, career interest
2. **Bio Summary (2-3 lines):** Technical and soft skills from bio
3. **Key Strengths (2-3 lines):** Top 3 strengths from SWOT analysis
4. **Development Areas (1-2 lines):** One weakness and one opportunity from SWOT
5. **Performance Insight (1-2 lines):** Overall score and performance bucket
6. **Fit Analysis Snippet (1-2 lines):** First sentence from fit analysis
7. **Personality Snippet (1-2 lines):** Top personality traits (Big Five)

#### 2. Controller Endpoint
**Location:** `backend/src/main/java/com/profiling/controller/PsychometricSessionController.java`

**New Endpoint:**
```
GET /api/psychometric/sessions/{sessionId}/generate-profile
```

**Response:** Plain text profile content

**Features:**
- Automatically generates report if not already created
- Returns concise profile text
- Error handling for missing sessions

### Frontend Changes

#### 1. API Function
**Location:** `frontend/src/api/psychometric.js`

**New Function:**
```javascript
export async function generateProfileFromReport(sessionId)
```

**Purpose:** Calls backend endpoint to generate profile from report

#### 2. UI Components
**Location:** `frontend/src/pages/psychometric/PsychometricReport.jsx`

**New Button:**
- Added "CREATE PROFILE FROM REPORT" button in the action buttons section
- Positioned between "DOWNLOAD FULL REPORT" and "ENHANCE PROFILE" buttons
- Shows "GENERATING..." state while processing

**New Modal:**
- Displays generated profile content in a clean, readable format
- Shows profile paragraphs with proper spacing
- Includes "Download Profile" button
- Includes "Close" button

**New State Variables:**
```javascript
const [showProfileModal, setShowProfileModal] = useState(false)
const [generatedProfile, setGeneratedProfile] = useState(null)
const [isGeneratingProfile, setIsGeneratingProfile] = useState(false)
```

**New Handler Functions:**
- `handleCreateProfile()` - Generates profile from report
- `closeProfileModal()` - Closes the profile modal
- `handleDownloadProfile()` - Downloads profile as text file

#### 3. Styling
**Location:** `frontend/src/pages/psychometric/PsychometricReport.css`

**New Styles:**
- `.btn-create-profile` - Orange button styling with hover effects
- `.profile-modal` - Modal specific styling (max-width: 800px)
- `.generated-profile-content` - Content area with scrollbar
- Responsive design for mobile devices

**Button Color Scheme:**
- Background: `#FF9800` (Orange)
- Hover: `#F57C00` (Darker Orange)
- Disabled state with opacity

## User Flow

1. User completes psychometric test and views report
2. User clicks "CREATE PROFILE FROM REPORT" button
3. Button shows "GENERATING..." state
4. Backend generates concise profile from report data
5. Modal opens displaying the generated profile
6. User can read the profile in the modal
7. User can click "Download Profile" to save as `.txt` file
8. User can click "Close" to dismiss the modal

## Download Functionality

**File Format:** Plain text (.txt)
**File Name Pattern:** `profile-{candidateName}-{sessionId}.txt`
**Content:** Multi-paragraph profile with double-line spacing

## Key Features

✅ **Concise Profile:** 10-15 lines focusing on key insights
✅ **Multiple Data Sources:** Bio, SWOT, performance, fit analysis, personality
✅ **Clean UI:** Modal with scrollable content area
✅ **Download Option:** Save profile as text file
✅ **Loading States:** Visual feedback during generation
✅ **Error Handling:** Graceful error messages
✅ **Responsive Design:** Works on mobile and desktop
✅ **No Authentication Required:** Works for all users viewing reports

## Technical Notes

- Profile generation is server-side for consistency
- Uses existing report data (no additional AI calls)
- Text extraction uses sentence parsing for clean snippets
- Personality traits filtered by threshold (≥70 for positive traits, ≤30 for neuroticism)
- SWOT analysis limited to top 3 items for brevity
- Modal uses same styling system as enhance profile modal

## Testing Recommendations

1. Test with reports containing all sections (bio, SWOT, fit analysis)
2. Test with reports missing some sections (graceful degradation)
3. Test download functionality with different candidate names
4. Test on mobile devices for responsive layout
5. Test with very long profile content (scrolling)
6. Test button states (loading, disabled)

## Future Enhancements (Optional)

- Add option to customize profile length
- Add option to select which sections to include
- Add PDF download option for profile
- Add option to email profile directly
- Add profile templates (formal, casual, etc.)
- Add ability to edit profile before downloading









