package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.model.CoffeeBean
import java.time.LocalDate

/** A variety reference describes genetics, never a purchased lot or an observed cup. */
data class VarietyReference(
    val id: String,
    val name: String,
    val geneticGroup: String,
    val lineage: String,
    val sourceUrl: String,
    val sourceSha256: String,
    val retrievedAt: String,
    val sourceFieldsSha256: Map<String, String>
) {
    fun toBean(): CoffeeBean = CoffeeBean(
        id = id,
        name = "$name — variety reference",
        variety = name,
        isCuratedSeed = true,
        provenance = "Variety reference; not a supplier lot. Genetics: $geneticGroup. Parentage/aliases: $lineage " +
            "Primary fact source: World Coffee Research; $sourceUrl; retrieved $retrievedAt; " +
            "source SHA-256 $sourceSha256. Short factual metadata only; original catalog CC BY-NC-ND 4.0. " +
            "No source descriptions, scores, images or artwork redistributed. Lot origin and measurements remain unknown."
    )
}

/** Receipt and generated runtime records are checked against one another by the JVM suite. */
data class ReferenceCatalogReceipt(val version: Int, val retrievedAt: String, val entries: List<VarietyReference>)

object ReferenceCatalog {
    private const val BASE_URL = "https://varieties.worldcoffeeresearch.org/varieties/"
    private val hashPattern = Regex("[0-9a-f]{64}")
    val entries: List<VarietyReference> = ReferenceCatalogRecords.entries.onEach(::validate)

    fun validate(entry: VarietyReference) {
        require(entry.id.matches(Regex("reference-wcr-[a-z0-9]+(?:-[a-z0-9]+)*"))) { "Invalid reference identifier." }
        require(entry.sourceUrl == BASE_URL + entry.id.removePrefix("reference-wcr-")) { "Primary source URL does not match reference." }
        require(hashPattern.matches(entry.sourceSha256)) { "Missing primary source receipt." }
        require(entry.sourceFieldsSha256.keys == setOf("name", "geneticGroup", "lineage")) { "Missing field provenance." }
        require(entry.sourceFieldsSha256.values.all { hashPattern.matches(it) }) { "Invalid field provenance hash." }
        require(entry.retrievedAt.matches(Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}"))) { "Invalid retrieval date." }
        require(runCatching { LocalDate.parse(entry.retrievedAt) }.isSuccess) { "Invalid retrieval date." }
        require(
            listOf(entry.name, entry.geneticGroup, entry.lineage).all { text ->
                text.isNotBlank() && text.length <= 512 && text.none { it.isISOControl() }
            }
        ) { "Missing or oversized reference fact." }
    }
}
