# Proctoring System Documentation

## Overview

This document describes the comprehensive, industry-standard proctoring system implemented for the psychometric test platform. The system monitors test-takers in real-time to ensure test integrity and prevent cheating.

---

## Features Implemented

### 1. Camera-Based Monitoring

#### Mandatory Camera Permission
- Camera access is **required** before the test can begin
- Users must grant camera permission on the instructions page
- Clear error messages guide users through permission issues
- Test cannot start without camera access

#### Face Detection
- **No Face Detection**: Alerts when no face is visible in the frame
- **Multiple Face Detection**: Warns if more than one person is detected
- **Face Out of Frame**: Detects when user's face leaves the camera view
- **Excessive Head Movement**: Tracks unusual head movements away from screen
- Detection runs every 2 seconds with intelligent threshold logic

#### Camera Feed Display
- Camera feed is **always visible** in a sticky header
- Cannot be hidden, minimized, or moved
- Real-time status indicators show:
  - ✓ Face detected (green)
  - ⚠ No face detected (yellow, pulsing)
  - ⚠ Multiple faces (red, pulsing)
- Error states clearly displayed when camera fails

### 2. User Behavior Monitoring

#### Tab Switching & Focus Loss
- Detects when user switches to another tab
- Monitors window focus loss (Alt+Tab behavior)
- Detects browser minimize actions

#### Screen Manipulation
- Monitors screen resize events
- Detects fullscreen exit attempts

#### DevTools Detection
- Continuously checks for developer tools being opened
- Monitors window size differentials to detect DevTools
- Checks every 1 second

#### Keyboard Shortcuts Blocking
Restricted shortcuts include:
- **Alt+Tab**: Task switching
- **Ctrl+T**: New tab
- **Ctrl+W**: Close tab
- **PrintScreen**: Screenshot capture
- **F12**: Developer tools
- **Ctrl+Shift+I**: Inspect element
- **Ctrl+Shift+J**: Console
- **Ctrl+Shift+C**: Element inspector
- **Ctrl+U**: View source

#### Right-Click Prevention
- Right-click context menu is disabled
- Logs each attempt as a low-severity violation

### 3. Warning System

#### Warning Counter
- Maximum of **5 warnings** before auto-submission
- Warnings persist across page reloads (localStorage)
- Real-time display shows current warning count
- Critical warning shown when 4 warnings reached

#### Warning Severity Levels
- **Info**: Periodic snapshots (logged only)
- **Low**: Right-click attempts
- **Medium**: Screen resize, excessive head movement
- **High**: Tab switch, focus loss, face not detected
- **Critical**: Multiple faces, DevTools access, stream interruption

#### Auto-Submission
- Test automatically submits when warning limit (5) is reached
- Submission includes warning count and violation details
- User cannot continue after auto-submission
- Clear overlay message informs user of auto-submission

### 4. Snapshot System

#### Periodic Snapshots
- Captured every 60 seconds automatically
- Stored with timestamp and session information
- Sent to backend asynchronously

#### Violation Snapshots
- Captured immediately when violation occurs
- Includes violation type, severity, and timestamp
- Base64 encoded JPEG format (70% quality)
- Sent with violation data to backend

### 5. Backend Integration

#### API Endpoints

**POST /api/test/proctoring/violation**
- Logs proctoring violations with snapshots
- Request body:
  ```json
  {
    "sessionId": "string",
    "userId": "string",
    "type": "string",
    "severity": "string",
    "timestamp": "ISO-8601 timestamp",
    "snapshot": "base64 encoded image"
  }
  ```

**GET /api/test/proctoring/violations/{sessionId}**
- Retrieves all violations for a session
- Returns array of violations with timestamps and metadata

**GET /api/test/proctoring/violations/{sessionId}/stats**
- Returns violation statistics grouped by severity
- Response: `{ "high": 2, "medium": 1, "low": 3 }`

#### Data Storage

**ProctoringViolation Model**
- `type`: Violation type (string)
- `severity`: info/low/medium/high/critical
- `timestamp`: When violation occurred
- `snapshotUrl`: Path to stored snapshot
- `description`: Additional details

**Storage Strategy**
- Snapshots saved to filesystem: `./proctoring-snapshots/{sessionId}/`
- Filename format: `{timestamp}_{uuid}.jpg`
- Violations stored in MongoDB with session
- Configurable via `proctoring.snapshot.storage.path` property

### 6. Network Resilience

#### Graceful Failure Handling
- Violations queued locally if network fails
- Automatic retry with exponential backoff (max 3 attempts)
- Test continues even if backend communication fails
- Local snapshot preservation until successful submission

#### Performance Optimization
- Asynchronous violation submission (non-blocking)
- Snapshot compression (70% JPEG quality)
- Efficient canvas-based face detection
- Minimal performance impact on test experience

### 7. User Experience

#### Clear Communication
- Real-time warning alerts with reason
- Color-coded severity indicators
- Status displays in camera feed
- Helpful error messages for camera issues

#### Non-Intrusive Design
- Proctoring runs in background
- Doesn't interfere with test navigation
- Smooth transitions and animations
- Responsive design for all screen sizes

---

## Technical Implementation

### Frontend Architecture

**useProctoring Hook** (`frontend/src/hooks/psychometric/useProctoring.js`)
- Central proctoring logic
- Face detection engine
- Violation tracking and submission
- Snapshot capture functionality
- Event listeners for browser behavior

**WebcamPreview Component** (`frontend/src/components/psychometric/WebcamPreview.jsx`)
- Displays live camera feed
- Shows detection status indicators
- Handles error states

**ProctoringWarning Component** (`frontend/src/components/psychometric/ProctoringWarning.jsx`)
- Shows warning alerts to user
- Severity-based styling
- Auto-dismissing alerts

**PsychometricInstructions Enhancement**
- Mandatory camera permission flow
- Permission testing before test start
- Clear permission error handling

### Backend Architecture

**ProctoringService** (`backend/src/main/java/com/profiling/service/psychometric/ProctoringService.java`)
- Violation logging business logic
- Snapshot storage management
- Statistics calculation
- Session validation

**ProctoringController** (`backend/src/main/java/com/profiling/controller/ProctoringController.java`)
- REST API endpoints
- Request validation
- Response formatting

**ProctoringViolation Model** (`backend/src/main/java/com/profiling/model/psychometric/ProctoringViolation.java`)
- Violation data structure
- Embedded in PsychometricSession

---

## Configuration

### Backend Configuration

Add to `application.properties`:

```properties
# Proctoring snapshot storage
proctoring.snapshot.storage.path=./proctoring-snapshots
proctoring.snapshot.enabled=true
```

### Frontend Configuration

Environment variables in `.env`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

---

## Security Considerations

1. **Snapshot Security**
   - Snapshots stored outside web-accessible directory
   - Access controlled via backend API only
   - Session-specific directories prevent cross-contamination

2. **Data Privacy**
   - Only face region captured (full screen NOT recorded)
   - Snapshots retained per organizational policy
   - User consent obtained before test starts

3. **Anti-Tampering**
   - Browser events monitored continuously
   - Violation data cannot be modified client-side
   - Backend validation of all submissions

4. **Integrity**
   - Warnings persist across page reloads
   - Auto-submission cannot be bypassed
   - Multiple validation layers

---

## Testing Guidelines

### Manual Testing Checklist

#### Camera Permission Flow
- [ ] Test with camera connected
- [ ] Test with camera disconnected
- [ ] Test with permission denied
- [ ] Test with camera already in use
- [ ] Verify clear error messages

#### Face Detection
- [ ] Test with face visible (should show green indicator)
- [ ] Test with no face (should trigger warning after 10 seconds)
- [ ] Test with multiple people (should trigger immediate warning)
- [ ] Test with face moving out of frame
- [ ] Test with rapid head movements

#### Behavior Monitoring
- [ ] Switch tabs during test (Alt+Tab)
- [ ] Minimize browser window
- [ ] Resize browser window
- [ ] Try to open DevTools (F12)
- [ ] Try restricted keyboard shortcuts
- [ ] Right-click on page

#### Warning System
- [ ] Verify warning counter increments
- [ ] Verify warnings persist after page reload
- [ ] Verify critical warning shown at 4 warnings
- [ ] Verify auto-submission at 5 warnings
- [ ] Check warning data in backend

#### Snapshot System
- [ ] Verify periodic snapshots captured
- [ ] Verify violation snapshots captured
- [ ] Check snapshots stored on backend
- [ ] Verify snapshot quality and size

#### Network Resilience
- [ ] Test with network disconnected
- [ ] Verify violations queued
- [ ] Verify retry on reconnection
- [ ] Check no data loss

### Automated Testing

Consider implementing:
1. Unit tests for face detection logic
2. Integration tests for API endpoints
3. E2E tests for complete proctoring flow
4. Performance tests for camera feed impact

---

## Troubleshooting

### Common Issues

**Camera not working**
- Check browser permissions
- Verify camera is not in use by another app
- Try different browser
- Check camera drivers

**High CPU usage**
- Face detection runs every 2 seconds (configurable)
- Snapshot quality can be reduced
- Consider disabling periodic snapshots

**Violations not saving**
- Check backend connectivity
- Verify API endpoint accessibility
- Check backend logs for errors
- Ensure MongoDB is running

**False positives in face detection**
- Adjust detection thresholds in useProctoring.js
- Consider integrating face-api.js for better accuracy
- Adjust lighting conditions

---

## Future Enhancements

### Potential Improvements

1. **Advanced Face Detection**
   - Integrate face-api.js or MediaPipe
   - More accurate face counting
   - Emotion detection
   - Identity verification

2. **AI-Based Analysis**
   - Suspicious behavior pattern detection
   - Automated violation review
   - Risk scoring

3. **Additional Monitoring**
   - Audio detection (background voices)
   - Eye tracking
   - Screen recording option
   - Mobile device detection

4. **Enhanced Reporting**
   - Admin dashboard for violation review
   - Violation timeline visualization
   - Export capabilities
   - Analytics and trends

5. **Performance**
   - WebWorker for face detection
   - Optimized snapshot compression
   - Batch violation submissions
   - CDN for snapshot storage

---

## Compliance & Privacy

### Data Retention
- Define clear retention policy for snapshots
- Automatic cleanup after review period
- User data deletion on request

### GDPR Compliance
- Obtain explicit consent before proctoring
- Provide clear privacy policy
- Allow data access requests
- Support right to deletion

### Accessibility
- Provide alternative testing for users without cameras
- Support for assistive technologies
- Clear documentation of requirements

---

## Support & Maintenance

### Monitoring
- Log all proctoring errors
- Monitor violation patterns
- Track system performance
- Alert on anomalies

### Updates
- Regular security patches
- Browser compatibility updates
- Performance optimizations
- Feature enhancements based on feedback

---

## Conclusion

This proctoring system provides comprehensive monitoring while maintaining good user experience and performance. It's designed to be production-ready, secure, and maintainable. The modular architecture allows for easy enhancements and customization based on specific requirements.

For questions or issues, please refer to the inline code documentation or contact the development team.









