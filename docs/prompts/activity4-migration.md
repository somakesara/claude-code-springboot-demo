# Activity 4 — Migration Demo Prompt

## The Scenario

Codebase is on Java 11 + Spring Boot 2.7. Migrating to Java 17 + Spring Boot 3.2.
48 classes, 85 unit tests, 12 integration tests.

---

## The Demo Prompt (use as-is)

Give Claude Code this phased prompt:

```
Migrate this Spring Boot project from Java 11 / Spring Boot 2.7 to Java 17 / Spring Boot 3.2.

Work in phases. After each phase, compile and report errors before moving to the next phase.

Phase 1 — pom.xml: Update Java version to 17, Spring Boot parent to 3.2.4.
Update any dependency versions that changed in Spring Boot 3.x.

Phase 2 — javax → jakarta: Replace all javax.* imports with jakarta.*
EXCEPT: do NOT change javax.sql.*, javax.crypto.*, or javax.net.* — those are JDK packages,
not Jakarta EE packages.

Phase 3 — Security config: WebSecurityConfigurerAdapter was removed in Spring Security 6.
Migrate to SecurityFilterChain bean pattern. Count the antMatchers() calls in the old config —
your new requestMatchers() count MUST be equal. If counts differ, stop and report.

Phase 4 — Deprecated APIs: Fix any remaining deprecation warnings.
RestTemplate → RestClient. spring.security.oauth2 property key changes.

Phase 5 — Tests: Run mvn clean test. Fix any test compilation or runtime failures.

Phase 6 — Smoke test: Start the application and confirm /actuator/health returns 200.
```

---

## Critical Rules to Enforce (tell the audience)

### javax → jakarta exclusions
```
// DO change:
import javax.persistence.*      → import jakarta.persistence.*
import javax.validation.*       → import jakarta.validation.*
import javax.transaction.*      → import jakarta.transaction.*
import javax.servlet.*          → import jakarta.servlet.*

// DO NOT change:
import javax.sql.DataSource      ← JDK, not Jakarta EE
import javax.crypto.*            ← JDK
import javax.net.*               ← JDK
```

### Security config count rule
```java
// OLD (count: 3 antMatchers)
.antMatchers("/actuator/health").permitAll()
.antMatchers("/api/v1/public/**").permitAll()
.antMatchers("/api/v1/**").authenticated()

// NEW (must also be 3 requestMatchers)
.requestMatchers("/actuator/health").permitAll()
.requestMatchers("/api/v1/public/**").permitAll()
.requestMatchers("/api/v1/**").authenticated()
```

If Claude Code produces a different count — it merged or split rules incorrectly. This is a **security vulnerability** (open endpoints that should be protected, or locked endpoints that should be public).

---

## What Claude Code Does Well

- Finds all `javax.*` → `jakarta.*` renames mechanically
- Updates `WebSecurityConfigurerAdapter` to `SecurityFilterChain` pattern
- Identifies deprecated `spring.security.oauth2.*` property keys
- Finds `@EnableGlobalMethodSecurity` → `@EnableMethodSecurity` rename
- Iterates with compiler feedback until it compiles

## What Claude Code Struggles With

- Counting antMatchers vs requestMatchers precisely when rules span multiple files
- Understanding the semantic difference between old and new security model
- Ordering of filter chains when there are multiple `@Configuration` security classes

---

## Metrics to Track

| Metric | Target | How to measure |
|---|---|---|
| Migration Accuracy | 100% — no regressions | All tests pass post-migration |
| Time to Compile | < 3 iterations | Count compiler feedback loops |
| Security Rule Preservation | Exact match | antMatcher count = requestMatcher count |
| Manual Fixes Required | Trending down | Lines changed by engineer after Claude |
