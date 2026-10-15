# Design contract

The governing implementation plan is docs/IMPLEMENTATION_PLAN.md. PRD FR-01–FR-10
remain unchanged. Use thin slices, isolated worktrees and one integrator.

Replace prototype simulations with Room records, typed science, SAF operations,
pinned llama.cpp/ONNX/OCR, Hilt, Proto DataStore and ObjectBox/FTS retrieval. UI reads
immutable lifecycle-aware state; optional AI cannot block manual mode. Bound and
validate external inputs. Persist estimates separately from human observations.

Real manual-recipe restore exposed a nullable-bean bug: retain a regression unit
test, complete restore test and corruption-preservation test. Native vocabulary
spellings cannot escape message roles. No-evidence RAG abstains deterministically.
LLM arithmetic requires named-unit input review, not numeric-set matching. Count
tokens with the loaded model before assigning history/source context.

Device/provider/RAM matrices, adjudicated answer faithfulness and barista field UX
are separate acceptance layers. A debug distribution is not production signing.
