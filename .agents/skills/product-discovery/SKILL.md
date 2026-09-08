---
name: product-discovery
description: >-
  Clarifies product goals, identifies target users and journeys, extracts functional and non-functional requirements, resolves edge cases, and produces structured product specifications for mobile developer tooling.
---

# Product Discovery Skill

## Purpose
Establishes clear user needs, core developer personas, essential user flows, and acceptance criteria for mobile AI software engineering tooling.

## When to Use
- Starting a new product initiative or major feature.
- User request is high-level, ambiguous, or underspecified (e.g. "I want a coding app for Android").
- Resolving edge cases in user workflows.

## When NOT to Use
- Routine bug fixes or well-defined code refactoring.
- Direct syntax tweaks or minor configuration changes.

## Workflow
1. **Identify Personas & Scenarios**:
   - Mobile developer on-the-go (quick bug fixes, review diffs, test builds).
   - "Vibe coder" building web/mobile apps purely via conversational instructions.
   - Power user requiring real terminal access, custom git workflows, and MCP servers.
2. **Define User Journeys**:
   - Project Onboarding -> Natural Language Prompting -> Live Execution -> Diff Review -> Preview & Iteration.
3. **Map Functional & Quality Constraints**:
   - Touch ergonomics, offline vs online capabilities, battery/thermal constraints.
4. **Output Product Requirements Document (PRD)**:
   - Save to `docs/` or implementation plan with prioritized MoSCoW (Must, Should, Could, Won't) matrix.

## Validation
- Ensure all critical user states (empty, loading, streaming, error, permission request) are mapped.
