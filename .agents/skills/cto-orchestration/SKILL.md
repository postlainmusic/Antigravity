---
name: cto-orchestration
description: >-
  Executive engineering orchestrator: triages incoming requests, determines required skills, specialist agents, context budgets, security reviews, visual QA gates, and coordinates end-to-end delivery.
---

# AI CTO Orchestration Skill

## Purpose
Acts as the central engineering brain and executive dispatcher across all specialized agents and skills, ensuring that every task is properly scoped, staffed, executed, tested, and validated.

## When to Use
- User presents a complex, multi-faceted engineering request.
- Dispatching subtasks across specialized roles (System Architect, Android Principal, UI Director, Security Engineer, QA Director, Red Team).
- Deciding whether a task requires architectural planning vs immediate execution.

## Executive Triage Matrix

```mermaid
graph TD
    UserReq[Incoming User Request] --> ScopeCheck{Task Complexity?}
    
    ScopeCheck -- Trivial Tweak / Single Line --> DirectExec[Direct Execution by Implementer Agent]
    
    ScopeCheck -- Medium Feature / Bug Fix --> Triage[CTO Triage & Task Allocation]
    
    ScopeCheck -- Major Architectural Change --> ArchReview[System Architect + PRD Review]
    
    Triage --> ContextEng[Context Engineer: Assemble Token-Bounded Context]
    ContextEng --> Implement[Staff Implementer: Write Code & Tools]
    Implement --> Review[Code Reviewer + Visual QA]
    Review --> RedTeam[Red Team Adversarial Attack Check]
    RedTeam --> Test[QA Director: Golden Tests & Regressions]
    Test --> Ship[Sign-off & Update Project Memory]
```

## CTO Decision Checklist
For every major task, verify:
1. **Specialist Assigned**: Correct role owner assigned from the Capability Matrix.
2. **Context Budget**: Context bounded to < 25% model window; no unbounded file dumps.
3. **Security Screening**: Prompt injection defense active; paths canonicalized.
4. **Visual QA**: UI components verified against Obsidian design system tokens.
5. **Adversarial Red Team**: Tested for edge cases, nullability, memory leaks, and permission bypass.
6. **Regression Guard**: New regression test added to golden dataset if fixing a bug.
