# Jetpack Compose Checklist

- [ ] All composable parameters are stable (`@Stable` or `@Immutable`).
- [ ] No side-effects run directly in composable bodies (use `LaunchedEffect` / `DisposableEffect`).
- [ ] `derivedStateOf` used for fast-changing scroll/offset calculations.
- [ ] `key` supplied to every item in `LazyColumn` and `LazyRow`.
- [ ] Adaptive layouts verified on Compact (phone), Medium (foldable), and Expanded (tablet) widths.
- [ ] Keyboard insets handled cleanly with `WindowInsets.ime` and `imePadding`.
- [ ] Design system tokens used instead of hardcoded colors, padding, and text sizes.
