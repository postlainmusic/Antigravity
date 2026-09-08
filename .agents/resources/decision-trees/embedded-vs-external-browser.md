# Decision Tree: Embedded WebView vs External Browser

```mermaid
graph TD
    Start[Preview / Web Target] --> Target{Target Context?}
    Target -- In-App Live Dev / Testing --> Embedded[Embedded Android WebView with JS Console Bridge]
    Target -- External Auth / Deep Web Browsing --> External[External Browser via Custom Tabs / Intent]
```

## Guidelines
- **Embedded WebView**: Fast live coding preview, console log streaming to IDE, multi-device viewport simulation (Mobile/Tablet/Desktop).
- **External Custom Tabs**: OAuth authentication, external documentation links, full browser testing.
