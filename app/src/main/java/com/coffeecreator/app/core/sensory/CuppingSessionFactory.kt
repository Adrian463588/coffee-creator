package com.coffeecreator.app.core.sensory

import java.util.UUID

object CuppingSessionFactory {
    fun create(
        title: String,
        location: String,
        templateType: CuppingTemplateType,
        maskingMode: MaskingMode,
        evaluatorNames: List<String>,
        sampleBeanNames: List<String>,
        createdAtEpochMs: Long
    ): CuppingSession {
        val cleanTitle = title.trim()
        val cleanEvaluators = evaluatorNames.map(String::trim).filter(String::isNotEmpty)
        val cleanSamples = sampleBeanNames.map(String::trim).filter(String::isNotEmpty)
        require(cleanTitle.isNotEmpty()) { "Session title is required." }
        require(cleanEvaluators.isNotEmpty()) { "Add at least one evaluator." }
        require(cleanSamples.isNotEmpty()) { "Add at least one bean sample." }

        val evaluators = cleanEvaluators.mapIndexed { index, name ->
            Evaluator(
                id = "eval_${index + 1}_${UUID.randomUUID().toString().take(4)}",
                name = name,
                role = if (index == 0) EvaluatorRole.LEAD_CUPPER else EvaluatorRole.Q_GRADER
            )
        }
        val samples = cleanSamples.mapIndexed { index, beanName ->
            val sampleNumber = index + 1
            CuppingSample(
                sampleId = "sample_${sampleNumber}_${UUID.randomUUID().toString().take(4)}",
                blindCode = "Sample #${'A' + index}$sampleNumber",
                actualBeanId = null,
                beanName = beanName,
                roaster = null,
                origin = null,
                process = null,
                roastDateEpoch = null,
                tablePosition = sampleNumber,
                isRevealed = maskingMode == MaskingMode.OPEN_LABEL
            )
        }
        val sessionId = UUID.randomUUID().toString()
        val leadEvaluator = evaluators.first()
        return CuppingSession(
            id = sessionId,
            title = cleanTitle,
            location = location.trim(),
            template = StandardCuppingTemplates.getTemplate(templateType),
            maskingMode = maskingMode,
            status = SessionStatus.OPEN,
            evaluators = evaluators,
            samples = samples,
            sheets = emptyList(),
            auditTrail = listOf(
                AuditEntry(
                    timestamp = createdAtEpochMs,
                    editorId = leadEvaluator.id,
                    action = "CREATE_SESSION",
                    previousStateSummary = "NONE",
                    newStateSummary = "OPEN",
                    reason = "Session initialized by ${leadEvaluator.name}"
                )
            ),
            createdAt = createdAtEpochMs
        )
    }
}
