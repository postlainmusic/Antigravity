# 12 - Accessibility & Inclusive Design Guidelines

## Accessibility Requirements
1. **Semantics & Screen Readers**:
   - Provide meaningful `contentDescription` for all image and icon buttons.
   - Use Compose `Modifier.semantics { ... }` for custom interactive widgets.
2. **Color Contrast & Dynamic Text**:
   - Maintain minimum WCAG AA contrast ratio (4.5:1 for standard text, 3:1 for large text).
   - Test dynamic type scaling (support 100% to 200% font scaling without UI clipping).
3. **Motion Preferences**:
   - Respect system animation scaling (`Settings.Global.ANIMATOR_DURATION_SCALE`) and reduced motion settings.
