# Performance Budgets & Device Tier Specifications

## 1. Device Tier Classification

Performance cannot be treated as a single universal target. Antigravity establishes 3 hardware tiers for Android devices:

- **Tier A (Flagship / High-End)**: Snapdragon 8 Gen 2/3/4 or Google Tensor G3/G4, 12GB+ RAM, 120Hz display.
- **Tier B (Mid-Range)**: Snapdragon 7 Gen series or MediaTek Dimensity 8000 series, 6GB-8GB RAM, 90Hz/120Hz display.
- **Tier C (Entry-Level / Low-End)**: Snapdragon 6 series or MediaTek Helio/Dimensity 6000, 4GB RAM, 60Hz display.

---

## 2. Measurable Performance Budgets

| Lifecycle / Action | Tier A Budget | Tier B Budget | Tier C Budget | Measurement Method |
| :--- | :--- | :--- | :--- | :--- |
| **Cold App Startup** | $< 600\text{ ms}$ | $< 1000\text{ ms}$ | $< 1800\text{ ms}$ | Time to `MainScreen` first frame drawn (`Macrobenchmark`) |
| **Warm App Startup** | $< 200\text{ ms}$ | $< 350\text{ ms}$ | $< 600\text{ ms}$ | Time to activity resume |
| **Tab / Screen Navigation** | $< 16\text{ ms}$ (60fps) | $< 16\text{ ms}$ (60fps) | $< 33\text{ ms}$ (30fps) | Compose state transition jank frame rate |
| **Editor Key Input Latency** | $< 8\text{ ms}$ | $< 16\text{ ms}$ | $< 25\text{ ms}$ | Time from hardware/touch key event to buffer commit |
| **Editor Scroll Frame Time** | $< 8.3\text{ ms}$ (120fps)| $< 11.1\text{ ms}$ (90fps)| $< 16.6\text{ ms}$ (60fps)| Compose `LazyList` frame rendering budget |
| **AI Time to First Token (TTFT)**| $< 400\text{ ms}$ | $< 600\text{ ms}$ | $< 900\text{ ms}$ | Network dispatch to first SSE stream chunk |
| **AI Streaming Frame Rate** | $120\text{ fps}$ | $90\text{ fps}$ | $60\text{ fps}$ | Markdown token text layout rendering |
| **Terminal ANSI Frame Rate** | $120\text{ fps}$ | $90\text{ fps}$ | $60\text{ fps}$ | ANSI sequence parse and Span update |
| **Dev Server Preview Load** | $< 300\text{ ms}$ | $< 500\text{ ms}$ | $< 800\text{ ms}$ | WebView `onPageFinished` event |
| **Base App Memory (RAM)** | $< 120\text{ MB}$ | $< 100\text{ MB}$ | $< 80\text{ MB}$ | Android Profiler Native + Dalvik Heap |
| **Peak Workspace Memory (50k lines)**| $< 250\text{ MB}$ | $< 200\text{ MB}$ | $< 150\text{ MB}$ | Heap allocation with open project + editor |

---

## 3. Profiling & Optimization Strategy

1. **Zero Allocations in Draw Phase**: All Compose layout calculations, brushes, and text measurements are hoisted out of `@Composable` draw scopes.
2. **Background Tokenization**: Syntax highlighting parsing executes in `Dispatchers.Default` Coroutines, returning pre-calculated `AnnotatedString` spans to the UI thread.
3. **Line Layout Cache**: Rendered editor line heights are cached in an integer array to eliminate layout recalculations during scrolling.
4. **LeakCanary & Android Baseline Profiles**: Integrated into CI to guarantee zero memory leaks and optimized AOT method compilation.
