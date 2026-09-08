# Code Editor Reference Guide

### What is this?
Architecture and best practices for implementing mobile code editors with syntax highlighting, search/replace, line numbers, and touch handles.

### Syntax Highlighting Pipeline
```
Raw Source Code 
      │
      ▼ (Background Dispatchers.Default)
Lexer / Tokenizer (Kotlin, JS, HTML, CSS, JSON)
      │
      ▼
List<SyntaxToken> (Keywords, Types, Strings, Numbers, Comments)
      │
      ▼
AnnotatedString with SpanStyles
      │
      ▼ (Compose LazyColumn Rendering)
Visible Line Items on UI Thread
```
