package com.coffeecreator.app.core.ai

/** Method guidance cannot prove an unrecorded personal measurement, certificate or future tasting. */
object RagEvidencePolicy {
    private val personal = Regex("\\b(my|mine|our|today|today's|currently|latest|last|served)\\b", RegexOption.IGNORE_CASE)
    private val futureSensory = Regex("\\b(will|guarantee)\\b.*\\b(score|taste|flavor|flavour|panel)\\b", RegexOption.IGNORE_CASE)
    private val recordDate = Regex("\\b(?:19|20)\\d{2}[-/]\\d{1,2}[-/]\\d{1,2}\\b")
    private val recordIdentity =
        Regex(
            "\\b(?:lot|batch|experiment|brew|sample|certificate|serial)\\s*(?:id|number|no\\.)?\\s*[:#=]\\s*\\S*\\d\\S*",
            RegexOption.IGNORE_CASE
        )
    private val observation = Regex("\\b(measured|observed|recorded|assessed|certified)\\b", RegexOption.IGNORE_CASE)
    private val example = Regex("\\b(example|hypothetical|fixture|formula|untrusted|instruction|unknown)\\b", RegexOption.IGNORE_CASE)

    fun supportsPersonalFact(query: String, excerpts: List<String>): Boolean {
        if (futureSensory.containsMatchIn(query) && personal.containsMatchIn(query)) return false
        if (!personal.containsMatchIn(query)) return true
        val relevantTerms = RagRanking.tokens(query)
        return excerpts.any { text ->
            val terms = RagRanking.tokens(text)
            val hasIdentity = recordDate.containsMatchIn(text) || recordIdentity.containsMatchIn(text)
            hasIdentity && observation.containsMatchIn(text) && !example.containsMatchIn(text) &&
                relevantTerms.intersect(terms).size >= MIN_PERSONAL_MATCHES
        }
    }
    private const val MIN_PERSONAL_MATCHES = 2
}
