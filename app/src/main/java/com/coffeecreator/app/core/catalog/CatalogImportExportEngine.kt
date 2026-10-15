package com.coffeecreator.app.core.catalog

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID

object CatalogImportExportEngine {

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
            JsonPrimitive(src.toString())
        })
        .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
            LocalDate.parse(json.asString)
        })
        .registerTypeAdapter(BigDecimal::class.java, JsonSerializer<BigDecimal> { src, _, _ ->
            JsonPrimitive(src.toPlainString())
        })
        .registerTypeAdapter(BigDecimal::class.java, JsonDeserializer { json, _, _ ->
            BigDecimal(json.asString)
        })
        .setPrettyPrinting()
        .create()

    fun exportToJson(payload: CatalogExportPayload): String {
        return gson.toJson(payload)
    }

    fun exportBeansToCsv(beans: List<DetailedBeanRecord>): String {
        val sb = StringBuilder()
        sb.append("id,roaster,name,originCountry,region,varietal,process,altitudeMeters,roastDate,roastLevel,isFavorite\n")
        for (b in beans) {
            val row = listOf(
                b.id,
                escapeCsv(b.roaster),
                escapeCsv(b.name),
                escapeCsv(b.originCountry),
                escapeCsv(b.region),
                escapeCsv(b.varietal),
                b.process.name,
                b.altitudeMeters?.toString() ?: "",
                b.roastDate.toString(),
                b.roastLevel.name,
                b.isFavorite.toString()
            )
            sb.append(row.joinToString(",")).append("\n")
        }
        return sb.toString()
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    fun previewImport(
        jsonContent: String,
        existingBeans: List<DetailedBeanRecord>,
        existingEquipment: List<EquipmentItem>
    ): ImportDryRunSummary {
        val payload = runCatching { gson.fromJson(jsonContent, CatalogExportPayload::class.java) }.getOrNull()
            ?: return ImportDryRunSummary(0, 0, 0, listOf("Invalid JSON payload."))

        val existingBeanIds = existingBeans.map { it.id }.toSet()
        val existingBeanNames = existingBeans.map { "${it.roaster.trim().lowercase()}:${it.name.trim().lowercase()}" }.toSet()
        val existingEquipIds = existingEquipment.map { it.id }.toSet()

        var newCount = 0
        var conflictCount = 0
        val conflicts = mutableListOf<String>()

        for (bean in payload.beans) {
            val nameKey = "${bean.roaster.trim().lowercase()}:${bean.name.trim().lowercase()}"
            if (existingBeanIds.contains(bean.id) || existingBeanNames.contains(nameKey)) {
                conflictCount++
                conflicts.add("Bean Conflict: '${bean.roaster} - ${bean.name}' matches existing record.")
            } else {
                newCount++
            }
        }

        for (equip in payload.equipment) {
            if (existingEquipIds.contains(equip.id)) {
                conflictCount++
                conflicts.add("Equipment Conflict: '${equip.brand} ${equip.model}' ID exists.")
            } else {
                newCount++
            }
        }

        val total = payload.beans.size + payload.equipment.size + payload.grinders.size + payload.waters.size
        return ImportDryRunSummary(
            totalRecords = total,
            newRecordsCount = newCount,
            conflictingRecordsCount = conflictCount,
            conflicts = conflicts
        )
    }

    fun executeImport(
        jsonContent: String,
        strategy: ConflictResolutionStrategy,
        currentBeans: List<DetailedBeanRecord>,
        currentEquipment: List<EquipmentItem>
    ): Pair<ImportExecutionReceipt, Pair<List<DetailedBeanRecord>, List<EquipmentItem>>> {
        val payload = runCatching { gson.fromJson(jsonContent, CatalogExportPayload::class.java) }.getOrNull()
            ?: return Pair(
                ImportExecutionReceipt(0, 0, 0, 0, listOf("Invalid JSON format.")),
                Pair(currentBeans, currentEquipment)
            )

        val beanMap = currentBeans.associateBy { it.id }.toMutableMap()
        val equipMap = currentEquipment.associateBy { it.id }.toMutableMap()

        var created = 0
        var updated = 0
        var renamed = 0
        var skipped = 0

        // Process Beans
        for (incoming in payload.beans) {
            val existing = beanMap[incoming.id]
                ?: beanMap.values.find {
                    it.roaster.equals(incoming.roaster, ignoreCase = true) &&
                        it.name.equals(incoming.name, ignoreCase = true)
                }

            if (existing == null) {
                beanMap[incoming.id] = incoming
                created++
            } else {
                when (strategy) {
                    ConflictResolutionStrategy.OVERWRITE -> {
                        beanMap[existing.id] = incoming.copy(id = existing.id)
                        updated++
                    }
                    ConflictResolutionStrategy.KEEP_BOTH_RENAME -> {
                        val newId = UUID.randomUUID().toString()
                        val renamedBean = incoming.copy(
                            id = newId,
                            name = "${incoming.name} (Imported)"
                        )
                        beanMap[newId] = renamedBean
                        renamed++
                    }
                    ConflictResolutionStrategy.SKIP_DUPLICATE -> {
                        skipped++
                    }
                }
            }
        }

        // Process Equipment
        for (incoming in payload.equipment) {
            val existing = equipMap[incoming.id]
                ?: equipMap.values.find {
                    it.brand.equals(incoming.brand, ignoreCase = true) &&
                        it.model.equals(incoming.model, ignoreCase = true) &&
                        it.category == incoming.category
                }

            if (existing == null) {
                equipMap[incoming.id] = incoming
                created++
            } else {
                when (strategy) {
                    ConflictResolutionStrategy.OVERWRITE -> {
                        equipMap[existing.id] = incoming.copy(id = existing.id)
                        updated++
                    }
                    ConflictResolutionStrategy.KEEP_BOTH_RENAME -> {
                        val newId = UUID.randomUUID().toString()
                        val renamedEquip = incoming.copy(
                            id = newId,
                            model = "${incoming.model} (Imported)"
                        )
                        equipMap[newId] = renamedEquip
                        renamed++
                    }
                    ConflictResolutionStrategy.SKIP_DUPLICATE -> {
                        skipped++
                    }
                }
            }
        }

        val receipt = ImportExecutionReceipt(
            createdCount = created,
            updatedCount = updated,
            renamedCount = renamed,
            skippedCount = skipped
        )

        return Pair(receipt, Pair(beanMap.values.toList(), equipMap.values.toList()))
    }
}
