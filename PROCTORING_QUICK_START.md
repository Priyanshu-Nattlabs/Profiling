# Proctoring System - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### Step 1: Backend Configuration

Add to `backend/src/main/resources/application.properties`:

```properties
# Proctoring Configuration
proctoring.snapshot.storage.path=./proctoring-snapshots
proctoring.snapshot.enabled=true
```

### Step 2: Create Snapshot Directory

```bash
mkdir -p proctoring-snapshots
```

### Step 3: Restart Backend

```bash
cd backend
./gradlew bootRun
```

Or if already built:
```bash
java -jar build/libs/profiling-service-*.jar
```

### Step 4: Start Frontend

```bash
cd frontend
npm run dev
```

### Step 5: Test the System

1. Navigate to the psychometric test
2. You'll be prompted for camera permission (**new requirement**)
3. Grant camera access
4. Complete the instruction cards
5. Start the test
6. Camera feed will be visible in the header
7. Try triggering warnings (switch tabs, resize window, etc.)

---

## ✅ What to Verify

### Camera Permission Flow
1. Open test instructions page
2. See camera permission section
3. Click "Grant Access" button
4. Browser prompts for camera permission
5. After granting, see green checkmark
6. "Begin Test" button becomes enabled

### During Test
1. Camera feed visible in header (top center)
2. Green "✓ Face" indicator when face detected
3. Try switching tabs → Warning appears
4. Try pressing F12 → Blocked and warning
5. Try right-clicking → Prevented and warning
6. Warning counter increments with each violation
7. At 5 warnings → Test auto-submits

### Backend Verification
Check snapshots are being saved:
```bash
ls -la proctoring-snapshots/
```

You should see session directories with JPEG files.

---

## 🔧 Troubleshooting

### Camera Not Working

**Issue**: Camera permission denied
**Solution**: 
1. Check browser settings
2. Ensure camera not used by other app
3. Try different browser
4. Check camera device is connected

**Issue**: "Camera already in use"
**Solution**: Close other apps using camera (Zoom, Teams, etc.)

### No Violations Being Saved

**Issue**: Violations not appearing in MongoDB
**Solution**:
1. Check backend logs for errors
2. Verify MongoDB is running
3. Check API endpoint: `http://localhost:8080/api/test/proctoring/violation`
4. Test with curl:
   ```bash
   curl -X POST http://localhost:8080/api/test/proctoring/violation \
     -H "Content-Type: application/json" \
     -d '{
       "sessionId": "test123",
       "userId": "user@example.com",
       "type": "test_violation",
       "severity": "medium",
       "timestamp": "2024-01-01T00:00:00Z",
       "snapshot": ""
     }'
   ```

### High CPU Usage

**Issue**: Browser consuming too much CPU
**Solution**:
1. Reduce face detection frequency in `useProctoring.js`:
   ```javascript
   const FACE_DETECTION_INTERVAL = 3000 // Change from 2000 to 3000
   ```
2. Reduce snapshot quality:
   ```javascript
   const SNAPSHOT_QUALITY = 0.5 // Change from 0.7 to 0.5
   ```
3. Disable periodic snapshots (keep only violation snapshots):
   ```javascript
   const PERIODIC_SNAPSHOT_INTERVAL = 0 // Disables periodic snapshots
   ```

---

## 🎯 Key Endpoints

### Test Proctoring Endpoints

**Log Violation**
```
POST http://localhost:8080/api/test/proctoring/violation
Content-Type: application/json

{
  "sessionId": "session-id",
  "userId": "user-email",
  "type": "Tab switched or window minimized",
  "severity": "high",
  "timestamp": "2024-01-01T00:00:00Z",
  "snapshot": "data:image/jpeg;base64,/9j/4AAQ..."
}
```

**Get Session Violations**
```
GET http://localhost:8080/api/test/proctoring/violations/{sessionId}
```

**Get Violation Statistics**
```
GET http://localhost:8080/api/test/proctoring/violations/{sessionId}/stats
```

---

## 📊 Understanding Violations

### Violation Types

| Type | Severity | Description |
|------|----------|-------------|
| Tab switched or window minimized | High | User left the test window |
| Window lost focus | High | Test window no longer active |
| Face not detected or out of frame | High | No face visible for 10+ seconds |
| Multiple faces detected | Critical | More than one person detected |
| Video stream interrupted | Critical | Camera stopped working |
| Developer tools detected | Critical | DevTools opened |
| Screen resized during test | Medium | Browser window resized |
| Excessive head movement detected | Medium | Unusual head movement |
| Right-click attempted | Low | Context menu attempted |
| Attempted restricted action | High | Blocked keyboard shortcut |
| periodic_snapshot | Info | Regular monitoring snapshot |

### Warning Limits

- **Default Max**: 5 warnings
- **Critical Alert**: At 4 warnings
- **Auto-Submit**: At 5 warnings
- **Configurable**: Change `MAX_WARNINGS` in `useProctoring.js`

---

## 🎨 Customization

### Adjust Detection Thresholds

**Face Detection Interval**
```javascript
// In useProctoring.js
const FACE_DETECTION_INTERVAL = 2000 // milliseconds (default: 2s)
```

**Head Movement Sensitivity**
```javascript
// In useProctoring.js
const HEAD_MOVEMENT_THRESHOLD = 0.15 // 0.0 to 1.0 (default: 15%)
```

**No Face Timeout**
```javascript
// In useProctoring.js
consecutiveNoFaceCount >= 5 // Change 5 to adjust (default: 10 seconds)
```

### Modify Snapshot Settings

**Periodic Snapshot Interval**
```javascript
// In useProctoring.js
const PERIODIC_SNAPSHOT_INTERVAL = 60000 // milliseconds (default: 60s)
```

**Snapshot Quality**
```javascript
// In useProctoring.js
const SNAPSHOT_QUALITY = 0.7 // 0.0 to 1.0 (default: 70%)
```

### Change Warning Limit

```javascript
// In useProctoring.js
const MAX_WARNINGS = 5 // Change to your desired limit
```

---

## 📱 Browser Support

### Tested Browsers
✅ Chrome 90+
✅ Edge 90+
✅ Firefox 88+
✅ Safari 14+ (macOS)
✅ Opera 76+

### Browser Permissions Required
- **Camera**: Required for proctoring
- **JavaScript**: Required for test functionality

### Not Supported
❌ Internet Explorer
❌ Browsers without camera API support

---

## 🔐 Security Checklist

Before deploying to production:

- [ ] Set strong MongoDB credentials
- [ ] Restrict snapshot directory permissions
- [ ] Use HTTPS for all communications
- [ ] Configure CORS appropriately
- [ ] Set up firewall rules
- [ ] Enable rate limiting on API
- [ ] Implement user authentication
- [ ] Add audit logging
- [ ] Set up monitoring alerts
- [ ] Configure backup for violations
- [ ] Define data retention policy
- [ ] Get user consent for proctoring

---

## 📈 Monitoring in Production

### Key Metrics to Track

1. **Violation Rate**
   - Average violations per session
   - Most common violation types
   - Sessions with auto-submission

2. **Technical Issues**
   - Camera permission denial rate
   - Face detection false positive rate
   - Network failure frequency
   - API response times

3. **Storage**
   - Snapshot directory size
   - Average snapshots per session
   - Storage growth rate

### Monitoring Queries

**Total violations by type:**
```javascript
db.psychometric_sessions.aggregate([
  { $unwind: "$proctoringViolations" },
  { $group: { 
      _id: "$proctoringViolations.type", 
      count: { $sum: 1 } 
    }
  },
  { $sort: { count: -1 } }
])
```

**Sessions with most violations:**
```javascript
db.psychometric_sessions.aggregate([
  { $project: { 
      sessionId: "$_id", 
      violationCount: { $size: "$proctoringViolations" } 
    }
  },
  { $sort: { violationCount: -1 } },
  { $limit: 10 }
])
```

---

## 🆘 Common Issues & Solutions

### Issue: "Camera Access Required" button doesn't work
**Solution**: User may have permanently denied camera. Guide them to:
1. Click the lock icon in address bar
2. Allow camera permission
3. Refresh the page

### Issue: Face detection too sensitive
**Solution**: Adjust thresholds:
```javascript
// Increase no-face threshold
consecutiveNoFaceCount >= 8 // Instead of 5

// Increase movement threshold
HEAD_MOVEMENT_THRESHOLD = 0.25 // Instead of 0.15
```

### Issue: Warnings not resetting between sessions
**Solution**: Clear localStorage or use unique session IDs

### Issue: Snapshots taking too much space
**Solution**: 
1. Reduce quality: `SNAPSHOT_QUALITY = 0.5`
2. Increase interval: `PERIODIC_SNAPSHOT_INTERVAL = 120000`
3. Implement automatic cleanup job
4. Compress old snapshots

---

## 📚 Additional Resources

- **Full Documentation**: `PROCTORING_SYSTEM_DOCUMENTATION.md`
- **Implementation Details**: `PROCTORING_IMPLEMENTATION_SUMMARY.md`
- **Code Comments**: Check inline documentation in source files
- **API Docs**: Check controller classes for endpoint details

---

## 🎉 Success!

If you can:
1. ✅ Start the test with camera permission
2. ✅ See camera feed in header
3. ✅ Trigger warnings by switching tabs
4. ✅ See violations saved in MongoDB
5. ✅ See snapshots in filesystem

Then your proctoring system is **fully operational**! 🎊

---

## 💡 Pro Tips

1. **Test thoroughly** in a staging environment before production
2. **Monitor violation rates** to tune false positive thresholds
3. **Communicate clearly** with test-takers about proctoring
4. **Have a backup plan** for users with camera issues
5. **Review violations** periodically to ensure system accuracy
6. **Keep documentation** updated with any customizations
7. **Set up alerts** for unusual violation patterns
8. **Regularly clean** old snapshots to manage storage

---

## 🤝 Need Help?

1. Check the troubleshooting section above
2. Review the full documentation
3. Check browser console for errors
4. Review backend logs
5. Verify MongoDB connection
6. Test API endpoints with curl
7. Check camera device status

---

**Happy Testing! 🚀**









