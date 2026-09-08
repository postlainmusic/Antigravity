# Mobile UX Reference Guide

### What is this?
Mobile design patterns for touch-first developer environments, responsive adaptive layouts, and keyboard accessory bars.

### Keyboard Insets Handling
```kotlin
// Ensure keyboard padding works cleanly without content jump
val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0

Box(
    modifier = Modifier
        .fillMaxSize()
        .imePadding()
) {
    // Screen content
}
```

### Haptic Feedback Integration
```kotlin
val haptics = LocalHapticFeedback.current
IconButton(onClick = {
    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    onApplyDiff()
}) {
    Icon(Icons.Default.Check, contentDescription = "Apply Diff")
}
```
