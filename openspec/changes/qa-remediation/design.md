# Design and implementation boundaries

## Change sequence

1. **Truth before polish:** write regression tests for Green QC defaults, sharing consent, false success/hash messages and simulation labels; fix these with smallest shared-path changes.
2. **Persistence:** inspect all Room entities, repository interfaces, serializers and exported schema. Design an additive migration and reversible draft model for S4 and brew timer only after exact field inventory. No raw JSON shortcut unless existing serialization contract proves it safe. Test old-schema fixtures, round trips, process recreation and foreign keys before UI wiring.
3. **Standards/content:** gate `SCA_CVA_CURRENT` pending authorized mapping. Record source, version, rights and review state for lessons. Do not copy protected forms/recipes. Keep unresolved historic catalog/video acceptance blocked.
4. **UI/UX:** preserve navigation and the current theme. Limit changes to selected Brew menu content, selected-chip state, concise loading/success/error feedback, and meaningful expand/collapse transitions. Prefer existing Material 3 components/ripple. Avoid new dependencies, hero gradients, decorative card stacks, continuous animation, or haptics on routine taps.
5. **Delivery records:** reconcile release file metadata against canonical GitHub API. For historical public claim corrections, add a dated, factual erratum to project release documentation and only edit a GitHub release after verifying exact tagged source and the final correction text.

## Motion and accessibility

Motion must show cause, state or hierarchy. Use small state transitions (`AnimatedContent`/`animateContentSize`) only where content changes; retain text labels, status semantics, stable list keys, focus order and 48dp targets. Avoid parallax, auto-playing/infinite animation, spring overshoot in repetitive barista tasks, and motion that delays input. Validate font scaling, TalkBack and reduced-motion mode separately; Compose UI tests do not prove device accessibility.

## External guidance reviewed

- Compose animation APIs and testing: https://developer.android.com/develop/ui/compose/animation/introduction and https://developer.android.com/develop/ui/compose/animation/quick-guide
- Compose touch targets and semantics: https://developer.android.com/develop/ui/compose/accessibility/api-defaults
- Material 3 motion distinguishes functional standard motion from expressive motion: https://m3.material.io/styles/motion/overview/how-it-works
- W3C animation-from-interactions guidance: https://www.w3.org/WAI/WCAG22/Understanding/animation-from-interactions.html
- Interaction feedback should serve a user task and remain restrained: https://www.nngroup.com/articles/animation-purpose-ux/
- OpenSpec change artifacts: https://github.com/Fission-AI/OpenSpec; BMAD build/review workflow: https://github.com/bmad-code-org/BMAD-METHOD

