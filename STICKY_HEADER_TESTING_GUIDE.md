# Sticky Header Testing Guide

## Quick Start Testing

### 1. Start the Application
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (if not already done)
npm install

# Start the development server
npm run dev
```

### 2. Navigate to Assessment Page
1. Go to the psychometric assessment start page
2. Start a new assessment session
3. Complete the instructions and begin the test

### 3. Test Sticky Header Functionality

#### Desktop Testing (Viewport > 1024px)
✅ **Expected Behavior:**
- Header should be visible at the top when page loads
- Scroll down through questions - header should stick to the top of the viewport
- Header should display:
  - Left: "Assessment in Progress" title and Session ID
  - Center: Webcam preview (200px × 150px)
  - Right: Violations count and Timer display
- Timer should continue counting down smoothly without causing scroll jumps
- Webcam feed should continue streaming without interruption
- Header should have a frosted glass effect (semi-transparent with blur)

#### Tablet Testing (768px - 1024px)
✅ **Expected Behavior:**
- Header layout adjusts to wrap on multiple lines
- Webcam preview shrinks to 160px × 120px
- Sticky behavior is maintained
- All elements remain accessible and readable

#### Mobile Testing (< 720px)
✅ **Expected Behavior:**
- Header switches to vertical layout
- Session info at top
- Stats and timer in the middle
- Webcam preview at bottom (full width, max 240px)
- Sticky behavior is maintained
- No horizontal scrolling

### 4. Functional Testing Checklist

- [ ] Header stays visible when scrolling down
- [ ] Header stays visible when scrolling up
- [ ] Timer updates every second without scroll issues
- [ ] Violations button is clickable and opens modal
- [ ] Webcam preview displays live feed
- [ ] Proctoring warnings appear below the sticky header (not covered)
- [ ] Section tabs appear below the sticky header (not covered)
- [ ] Question navigation works correctly
- [ ] Answer selection doesn't cause header to re-render
- [ ] Marking for review doesn't affect header
- [ ] Moving to next/previous question doesn't break header
- [ ] Switching between sections maintains sticky header
- [ ] Submitting the test works correctly

### 5. Performance Testing

- [ ] Smooth scrolling with no lag
- [ ] Timer updates don't cause visual glitches
- [ ] No flickering or jumping of the header
- [ ] Webcam feed remains stable during scroll
- [ ] Page scrolls smoothly on mobile devices
- [ ] No excessive CPU/GPU usage during scroll

### 6. Cross-Browser Testing

Test in multiple browsers to ensure compatibility:
- [ ] Google Chrome (recommended)
- [ ] Mozilla Firefox
- [ ] Microsoft Edge
- [ ] Safari (macOS/iOS)

### 7. Visual Inspection

**Header Appearance:**
- Background: White with slight transparency (frosted glass effect)
- Border: None on sides (full width)
- Shadow: Prominent shadow below header for depth
- Z-Index: Header appears above all other content
- No overlap with warning banners or section tabs

**Spacing:**
- Adequate spacing between header and content below
- No content hidden behind the header
- Proctoring warnings fully visible below header
- Section tabs fully visible below header

## Common Issues & Solutions

### Issue: Header not sticking
**Solution:** Clear browser cache and hard refresh (Ctrl+F5 or Cmd+Shift+R)

### Issue: Content overlapping header
**Solution:** Verify CSS changes were applied correctly to `psychometric.css`

### Issue: Header jumping during scroll
**Solution:** Ensure no JavaScript is modifying scroll position; check for console errors

### Issue: Timer causing re-renders
**Solution:** Verify `useTimer` and `useProctoring` hooks are using proper React optimization

### Issue: Mobile layout broken
**Solution:** Test in device mode in Chrome DevTools; check responsive CSS breakpoints

## Developer Testing Tools

### Chrome DevTools
```
1. Open DevTools (F12)
2. Go to Elements tab
3. Inspect the header element with class "page__header--sticky"
4. Verify computed styles:
   - position: sticky
   - top: 0px
   - z-index: 1000
5. Use device toolbar to test responsive layouts
```

### Performance Monitoring
```
1. Open DevTools (F12)
2. Go to Performance tab
3. Start recording
4. Scroll the page up and down
5. Stop recording
6. Check for:
   - Smooth 60fps scrolling
   - No long tasks during scroll
   - Minimal repaints/reflows
```

## Expected CSS Properties

When you inspect the `.page__header--sticky` element, you should see:

```css
position: sticky;
top: 0;
z-index: 1000;
background: rgba(255, 255, 255, 0.98);
backdrop-filter: blur(10px);
box-shadow: var(--shadow-lg);
will-change: transform;
transform: translateZ(0);
```

## Success Criteria

The implementation is successful if:
1. ✅ Header remains visible at the top when scrolling
2. ✅ No overlap with content below
3. ✅ Timer and camera feed work without issues
4. ✅ Responsive design works on all screen sizes
5. ✅ Smooth scrolling with no performance issues
6. ✅ All assessment functionality remains intact

## Need Help?

If you encounter any issues:
1. Check browser console for errors
2. Verify all CSS changes were applied
3. Clear cache and hard refresh
4. Test in different browsers
5. Check responsive design at different breakpoints

---

**Last Updated:** December 23, 2025
**Implementation Files:** `frontend/src/styles/psychometric.css`
**Documentation:** `STICKY_HEADER_IMPLEMENTATION.md`






