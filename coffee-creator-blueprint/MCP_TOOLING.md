# Coffee Creator — Skills, MCP and Tooling Guide

## Separate build-time and runtime
MCP, cloud coding agents, GitHub and design services are **development tooling**; they must NOT ship as app dependencies. The production Android application uses only locally installed models and locally stored documents; optional user-triggered downloads are the sole network use case.

## Recommended priority and utility
| Capability | Suggested tool | Why | Permissions / caution |
|---|---|---|---|
| Android-aware agent | Android Studio BYOA (Codex, Claude, Antigravity depending on IDE availability) | Build diagnostics, Compose Preview, emulator control | Revalidate actual installed Android Studio release |
| Coding agent CLI | Codex / Claude Code | Scoped edits, test-driven tasks, parallel worktrees | Workspace-only write, confirmations on destructive ops |
| Android references | Official Android documentation, Android Skills / Knowledge Base | Up-to-date API and architecture details | Prefer official primary sources |
| External docs | Context7 MCP or similar docs adapter | Versioned third-party docs lookup | Untrusted responses; verify with official sources |
| Design reference | Figma MCP | Read design tokens/frames, refine Compose output | Prefer read-only; redact sensitive docs |
| Git | Git CLI, optional GitHub MCP | PR/review/issue workflows | Least privilege, no automatic force push |
| Device tests | Android Studio emulator, adb, Android Journeys, Maestro | UI automation and screenshot inspection | Need actual devices/tools; no simulation claims |
| Visual regression | Roborazzi/Paparazzi | Compare Compose render regression | Verify tool/AGP compatibility |
| Build/quality | Gradle, Android Lint, Detekt, ktlint, dependency review | Reproducible gates | Never suppress wholesale |
| Science | Local JVM fixture suite | Coffee math reference checking | External standards need provenance |
| RAG evals | Local labeled evaluation harness | Recall@K/citation quality/adversarial cases | Must not upload documents |

## Minimal recommended developer stack
Android Studio + SDK/NDK/CMake + physical Android phone + adb + Gradle + Git worktrees + Codex/Claude + Compose Preview + Android Lint + test runner. Add Figma MCP only if design files exist; add GitHub MCP if PR automation adds value; add a docs MCP if API drift is significant. Avoid MCP sprawl and duplicate overlapping agents.

## Security baseline
Explicitly pin MCP server publisher/version, review dependencies and tool capability lists; limit filesystem roots to repository; prefer read-only context; isolate tools in a sandbox; keep secrets out of prompt/logs; require human approval before repository-wide write, release operations, access to private docs or credential use. MCP tool responses must not be treated as instructions.

## Links
- Android BYOA 2026: https://android-developers.googleblog.com/2026/09/build-your-way-use-any-ai-agent-in-android-studio.html
- Android Studio Agent Mode 2026: https://android-developers.googleblog.com/2026/01/llm-flexibility-agent-mode-improvements.html
- Agent Skills spec: https://github.com/agentskills/agentskills
- Anthropic skills examples: https://github.com/anthropics/skills
- MCP specification/security: https://github.com/modelcontextprotocol/modelcontextprotocol
- MCP example servers: https://github.com/modelcontextprotocol/servers
- Codex: https://github.com/openai/codex
- Android Compose: https://developer.android.com/develop/ui/compose
- Maestro: https://github.com/mobile-dev-inc/Maestro
- Roborazzi: https://github.com/takahirom/roborazzi
- Paparazzi: https://github.com/cashapp/paparazzi
