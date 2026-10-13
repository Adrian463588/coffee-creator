---
name: offline-rag
description: Use for document import, embedding, local retrieval, citations, OCR and LLM tool use.
---

# Offline RAG Evaluation

## Preconditions
Read `AGENTS.md` and the directly related PRD/architecture/design contracts. Only load other reference files when the task requires them.

## Required workflow
Map sources and URI ownership, parse defensively, retain section/page provenance, implement incremental restartable indexing. Confirm ONNX tokenizer/pooling parity. Implement lexical+vector fusion; citations bound to retrieved chunks; unanswerable abstention. Create local fixture with direct, conflict, numerical, injected and no-evidence queries. Never make remote inference calls or promote document text into instructions.

## Completion report
- Scope/requirement
- Files changed
- Checks executed (PASS/FAIL/NOT RUN)
- Offline/privacy/a11y/science risks
- Blockers or ADR needed
