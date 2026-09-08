# Android & Jetpack Checklist

- [ ] ViewModel survives configuration changes without state corruption.
- [ ] UI states collected using `collectAsStateWithLifecycle()`.
- [ ] Safe arguments passed between navigation destinations.
- [ ] Process death and state restoration handled via `SavedStateHandle`.
- [ ] Permissions requested gracefully with clear rationale dialogs.
- [ ] App lifecycle listeners release memory and pause background rendering on trim/pause.
