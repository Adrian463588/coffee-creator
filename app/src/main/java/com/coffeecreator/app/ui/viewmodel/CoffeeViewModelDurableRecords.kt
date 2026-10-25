package com.coffeecreator.app.ui.viewmodel

import com.coffeecreator.app.core.ai.personalization.PalateProfile
import com.coffeecreator.app.core.ai.personalization.WeatherParameters
import com.coffeecreator.app.core.catalog.BagInventoryItem
import com.coffeecreator.app.core.catalog.CatalogImportExportEngine
import com.coffeecreator.app.core.catalog.DetailedBeanRecord
import com.coffeecreator.app.core.catalog.EquipmentItem
import com.coffeecreator.app.core.catalog.FoodGradeIngredientItem
import com.coffeecreator.app.core.catalog.FreezeEvent
import com.coffeecreator.app.core.catalog.GreenCoffeeLot
import com.coffeecreator.app.core.catalog.GrinderRecord
import com.coffeecreator.app.core.catalog.RoastBatchRecord
import com.coffeecreator.app.core.catalog.RoastDataPoint
import com.coffeecreator.app.core.catalog.WaterMineralProfile
import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.engine.CoffeeScienceEngine
import com.coffeecreator.app.core.engine.JsonInputPolicy
import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.journal.CafeCupEntry
import com.coffeecreator.app.core.reliability.UserUnitPreferences
import com.coffeecreator.app.core.sensory.CuppingSample
import com.coffeecreator.app.core.sensory.CuppingSession
import com.coffeecreator.app.core.sensory.PhysicalQcRecord
import com.coffeecreator.app.core.sensory.ProvenanceBadge
import com.coffeecreator.app.core.sensory.RoastRestObservation
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.Strictness
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.math.abs

private const val EXTRACTION_YIELD_TOLERANCE_PERCENT = 1e-9

internal data class CoffeeDurableSnapshot(
    val journalEntries: List<BrewJournalEntry>,
    val cafeCupEntries: List<CafeCupEntry>,
    val cuppingSessions: List<CuppingSession>,
    val palateProfile: PalateProfile,
    val weatherParameters: WeatherParameters,
    val unitPreferences: UserUnitPreferences,
    val physicalQcRecords: Map<String, PhysicalQcRecord>,
    val roastRestObservations: List<RoastRestObservation>,
    val detailedBeans: List<DetailedBeanRecord>,
    val grinders: List<GrinderRecord>,
    val equipmentCatalog: List<EquipmentItem>,
    val waterProfiles: List<WaterMineralProfile>,
    val foodGradeIngredients: List<FoodGradeIngredientItem>,
    val greenCoffeeLots: List<GreenCoffeeLot>,
    val roastBatches: List<RoastBatchRecord>
) {
    fun changedSettings(previous: CoffeeDurableSnapshot, gson: Gson): Map<String, String> = buildMap {
        if (journalEntries != previous.journalEntries) put("journal_entries", gson.toJson(journalEntries))
        if (cafeCupEntries != previous.cafeCupEntries) put("cafe_cups", gson.toJson(cafeCupEntries))
        if (cuppingSessions != previous.cuppingSessions) put("cuppings", gson.toJson(cuppingSessions))
        if (palateProfile != previous.palateProfile) put("palate_profile", gson.toJson(palateProfile))
        if (weatherParameters != previous.weatherParameters) put("weather_parameters", gson.toJson(weatherParameters))
        if (unitPreferences != previous.unitPreferences) put("user_units", gson.toJson(unitPreferences))
        if (physicalQcRecords != previous.physicalQcRecords) put("physical_qc_records", gson.toJson(physicalQcRecords))
        if (roastRestObservations != previous.roastRestObservations) {
            put("roast_rest_observations", gson.toJson(roastRestObservations))
        }
        if (detailedBeans != previous.detailedBeans) put("detailed_beans", gson.toJson(detailedBeans))
        if (grinders != previous.grinders) put("grinders", gson.toJson(grinders))
        if (equipmentCatalog != previous.equipmentCatalog) put("equipment_catalog", gson.toJson(equipmentCatalog))
        if (waterProfiles != previous.waterProfiles) put("water_profiles", gson.toJson(waterProfiles))
        if (foodGradeIngredients != previous.foodGradeIngredients) put("food_grade_ingredients", gson.toJson(foodGradeIngredients))
        if (greenCoffeeLots != previous.greenCoffeeLots) put("green_coffee_lots", gson.toJson(greenCoffeeLots))
        if (roastBatches != previous.roastBatches) put("roast_batches", gson.toJson(roastBatches))
    }

    fun allSettings(gson: Gson): Map<String, String> = buildMap {
        put("journal_entries", gson.toJson(journalEntries))
        put("cafe_cups", gson.toJson(cafeCupEntries))
        put("cuppings", gson.toJson(cuppingSessions))
        put("palate_profile", gson.toJson(palateProfile))
        put("weather_parameters", gson.toJson(weatherParameters))
        put("user_units", gson.toJson(unitPreferences))
        put("physical_qc_records", gson.toJson(physicalQcRecords))
        put("roast_rest_observations", gson.toJson(roastRestObservations))
        put("detailed_beans", gson.toJson(detailedBeans))
        put("grinders", gson.toJson(grinders))
        put("equipment_catalog", gson.toJson(equipmentCatalog))
        put("water_profiles", gson.toJson(waterProfiles))
        put("food_grade_ingredients", gson.toJson(foodGradeIngredients))
        put("green_coffee_lots", gson.toJson(greenCoffeeLots))
        put("roast_batches", gson.toJson(roastBatches))
    }
}

/** Strict for external backup input; tolerant on startup so a legacy bad setting cannot crash opening. */
internal object CoffeeDurableSnapshotCodec {
    private val strictJson = GsonBuilder().setStrictness(Strictness.STRICT).create()
    internal val serializer: Gson = CatalogImportExportEngine.gson
    private val optionalFields = mapOf(
        BrewJournalEntry::class.java to setOf(
            "recipeId", "beanId", "beverageMassGrams", "drawdownSeconds", "measuredTdsPercent", "extractionYieldPercent",
            "rating", "dialInChainId", "espressoDetails", "recipeSnapshot"
        ),
        com.coffeecreator.app.core.model.Recipe::class.java to setOf(
            "beanId", "beverageMassGrams", "measuredTdsPercent", "pressureBars", "firstDropSeconds",
            "grinderModel", "grinderSetting", "waterHardnessPpm", "waterAlkalinityPpm", "basketPuckPrep"
        ),
        com.coffeecreator.app.core.model.BrewStep::class.java to setOf("temperatureCelsius", "valveStatus"),
        com.coffeecreator.app.core.model.RecipeLine::class.java to setOf("densityGramsPerMl"),
        com.coffeecreator.app.core.journal.EspressoJournalDetails::class.java to setOf(
            "basketSizeGrams", "firstDropSeconds", "peakPressureBars"
        ),
        ProvenanceBadge::class.java to setOf("confidenceLower", "confidenceUpper", "confidenceLevelPercent"),
        CuppingSample::class.java to setOf("actualBeanId", "roaster", "origin", "process", "roastDateEpoch"),
        CuppingSession::class.java to setOf("closedAt"),
        WeatherParameters::class.java to setOf(
            "elevationMeters", "ambientTempCelsius", "relativeHumidityPercent", "calculatedBoilingPointCelsius"
        ),
        FreezeEvent::class.java to setOf("thawedAtEpochMs"),
        BagInventoryItem::class.java to setOf("openedAtEpochMs", "finishedAtEpochMs", "pricePaid"),
        DetailedBeanRecord::class.java to setOf("altitudeMeters", "harvestDate", "roastDate", "photoUri"),
        EquipmentItem::class.java to setOf("photoUri"),
        GrinderRecord::class.java to setOf("clicksPerRotation", "typicalRpm"),
        WaterMineralProfile::class.java to setOf("ph"),
        FoodGradeIngredientItem::class.java to setOf("supplierMaxPercent", "casNumber"),
        RoastDataPoint::class.java to setOf("rateOfRiseCelsiusPerMinute"),
        RoastBatchRecord::class.java to setOf("greenLotId")
    )

    fun read(settings: Map<String, String>, onMalformed: (String, Exception) -> Unit): CoffeeDurableSnapshot =
        snapshot(settings, strict = false, onMalformed)

    fun decode(settings: Map<String, String>): CoffeeDurableSnapshot = snapshot(settings, strict = true) { _, _ -> }

    private fun snapshot(settings: Map<String, String>, strict: Boolean, onMalformed: (String, Exception) -> Unit): CoffeeDurableSnapshot {
        val result = CoffeeDurableSnapshot(
            journalEntries = listSetting(settings, "journal_entries", BrewJournalEntry::class.java, strict, onMalformed),
            cafeCupEntries = listSetting(settings, "cafe_cups", CafeCupEntry::class.java, strict, onMalformed),
            cuppingSessions = listSetting(settings, "cuppings", CuppingSession::class.java, strict, onMalformed),
            palateProfile = objectSetting(settings, "palate_profile", PalateProfile::class.java, strict, onMalformed) ?: PalateProfile(),
            weatherParameters = objectSetting(settings, "weather_parameters", WeatherParameters::class.java, strict, onMalformed)
                ?: WeatherParameters(),
            unitPreferences = objectSetting(settings, "user_units", UserUnitPreferences::class.java, strict, onMalformed)
                ?: UserUnitPreferences(),
            physicalQcRecords = mapSetting(settings, "physical_qc_records", PhysicalQcRecord::class.java, strict, onMalformed),
            roastRestObservations = listSetting(settings, "roast_rest_observations", RoastRestObservation::class.java, strict, onMalformed),
            detailedBeans = listSetting(settings, "detailed_beans", DetailedBeanRecord::class.java, strict, onMalformed),
            grinders = listSetting(settings, "grinders", GrinderRecord::class.java, strict, onMalformed),
            equipmentCatalog = listSetting(settings, "equipment_catalog", EquipmentItem::class.java, strict, onMalformed),
            waterProfiles = listSetting(settings, "water_profiles", WaterMineralProfile::class.java, strict, onMalformed),
            foodGradeIngredients = listSetting(
                settings,
                "food_grade_ingredients",
                FoodGradeIngredientItem::class.java,
                strict,
                onMalformed
            ),
            greenCoffeeLots = listSetting(settings, "green_coffee_lots", GreenCoffeeLot::class.java, strict, onMalformed),
            roastBatches = listSetting(settings, "roast_batches", RoastBatchRecord::class.java, strict, onMalformed)
        )
        if (strict) validateSnapshot(result)
        return result
    }

    private fun <T : Any> listSetting(
        settings: Map<String, String>,
        key: String,
        itemType: Class<T>,
        strict: Boolean,
        onMalformed: (String, Exception) -> Unit
    ): List<T> = decodeSetting(settings, key, strict, onMalformed) { json ->
        val type = TypeToken.getParameterized(List::class.java, itemType).type
        validateShapeIfStrict(json, type, strict)
        val items: List<T> = serializer.fromJson(json, type) ?: emptyList()
        require(items.size <= BackupCodec.MAX_RECORDS) { "Saved collection exceeds the supported record limit." }
        items
    } ?: emptyList()

    private fun <T : Any> objectSetting(
        settings: Map<String, String>,
        key: String,
        valueType: Class<T>,
        strict: Boolean,
        onMalformed: (String, Exception) -> Unit
    ): T? = decodeSetting(settings, key, strict, onMalformed) { json ->
        validateShapeIfStrict(json, valueType, strict)
        serializer.fromJson(json, valueType)
    }

    private fun <T : Any> mapSetting(
        settings: Map<String, String>,
        key: String,
        valueType: Class<T>,
        strict: Boolean,
        onMalformed: (String, Exception) -> Unit
    ): Map<String, T> = decodeSetting(settings, key, strict, onMalformed) { json ->
        val type = TypeToken.getParameterized(Map::class.java, String::class.java, valueType).type
        validateShapeIfStrict(json, type, strict)
        val values: Map<String, T> = serializer.fromJson(json, type) ?: emptyMap()
        require(values.size <= BackupCodec.MAX_RECORDS) { "Saved map exceeds the supported record limit." }
        values
    } ?: emptyMap()

    private inline fun <T> decodeSetting(
        settings: Map<String, String>,
        key: String,
        strict: Boolean,
        noinline onMalformed: (String, Exception) -> Unit,
        decode: (String) -> T
    ): T? {
        val json = settings[key] ?: return null
        return try {
            require(json.length <= BackupCodec.MAX_SETTING_CHARS) { "Saved '$key' exceeds the supported size limit." }
            JsonInputPolicy.validateDepth(json)
            decode(json)
        } catch (failure: JsonParseException) {
            recoverMalformedSetting(key, strict, onMalformed, failure)
        } catch (failure: ClassCastException) {
            recoverMalformedSetting(key, strict, onMalformed, failure)
        } catch (failure: IllegalArgumentException) {
            recoverMalformedSetting(key, strict, onMalformed, failure)
        } catch (failure: IllegalStateException) {
            recoverMalformedSetting(key, strict, onMalformed, failure)
        }
    }

    private fun <T> recoverMalformedSetting(
        key: String,
        strict: Boolean,
        onMalformed: (String, Exception) -> Unit,
        failure: Exception
    ): T? {
        if (strict) throw IllegalArgumentException("Invalid '$key' snapshot; existing records were not changed.", failure)
        onMalformed(key, failure)
        return null
    }

    private fun validateShapeIfStrict(json: String, expectedType: Type, strict: Boolean) {
        if (!strict) return
        val tree = strictJson.fromJson(json, JsonElement::class.java)
            ?: throw IllegalArgumentException("Saved snapshot is empty.")
        validateValue(tree, expectedType, "snapshot")
    }

    private fun validateValue(element: JsonElement, expectedType: Type, path: String) {
        require(!element.isJsonNull) { "$path cannot be null." }
        if (expectedType is ParameterizedType) {
            val rawType = expectedType.rawType as? Class<*> ?: error("Unsupported $path type.")
            when {
                List::class.java.isAssignableFrom(rawType) -> {
                    require(element.isJsonArray) { "$path must be an array." }
                    val values = element.asJsonArray
                    require(values.size() <= BackupCodec.MAX_RECORDS) { "$path exceeds the supported record limit." }
                    values.forEachIndexed { index, value -> validateValue(value, expectedType.actualTypeArguments[0], "$path[$index]") }
                }
                Map::class.java.isAssignableFrom(rawType) -> {
                    require(element.isJsonObject) { "$path must be an object." }
                    val values = element.asJsonObject
                    require(values.size() <= BackupCodec.MAX_RECORDS) { "$path exceeds the supported record limit." }
                    val keyType = expectedType.actualTypeArguments[0]
                    val valueType = expectedType.actualTypeArguments[1]
                    values.entrySet().forEach { (key, value) ->
                        validateMapKey(key, keyType, path)
                        validateValue(value, valueType, "$path.$key")
                    }
                }
                else -> validateRecord(element, rawType, path)
            }
            return
        }
        val type = expectedType as? Class<*> ?: error("Unsupported $path type.")
        when {
            type == String::class.java -> require(element.isJsonPrimitive && element.asJsonPrimitive.isString) { "$path must be text." }
            type == Boolean::class.java || type == Boolean::class.javaPrimitiveType -> {
                require(element.isJsonPrimitive && element.asJsonPrimitive.isBoolean) { "$path must be boolean." }
            }
            type == LocalDate::class.java -> {
                require(element.isJsonPrimitive && element.asJsonPrimitive.isString) { "$path must be an ISO date." }
                LocalDate.parse(element.asString)
            }
            type.isEnum -> {
                require(element.isJsonPrimitive && element.asJsonPrimitive.isString) { "$path must be an enum name." }
                require(type.enumConstants.any { (it as Enum<*>).name == element.asString }) { "$path has an unknown enum value." }
            }
            type.isPrimitive || Number::class.java.isAssignableFrom(type) -> validateNumber(element, type, path)
            type == Any::class.java || type == Object::class.java -> validateAny(element, path)
            else -> validateRecord(element, type, path)
        }
    }

    private fun validateRecord(element: JsonElement, type: Class<*>, path: String) {
        require(element.isJsonObject) { "$path must be an object." }
        val fields = type.declaredFields.filterNot {
            Modifier.isStatic(it.modifiers) || it.isSynthetic || Modifier.isTransient(it.modifiers)
        }
        val optional = optionalFields[type].orEmpty()
        val fieldNames = fields.map { it.name }.toSet()
        val value = element.asJsonObject
        require(value.keySet().all { it in fieldNames }) { "$path contains unknown ${type.simpleName} fields." }
        require((fieldNames - optional).all { it in value.keySet() }) { "$path is missing required ${type.simpleName} fields." }
        fields.forEach { field ->
            val child = value[field.name]
            if (child == null || child.isJsonNull) {
                require(field.name in optional) { "$path.${field.name} cannot be null or missing." }
            } else {
                validateValue(child, field.genericType, "$path.${field.name}")
            }
        }
    }

    private fun validateMapKey(key: String, keyType: Type, path: String) {
        val type = keyType as? Class<*> ?: error("Unsupported map key in $path.")
        when (type) {
            String::class.java -> Unit
            Int::class.javaPrimitiveType, Int::class.javaObjectType ->
                require(key.toIntOrNull() != null) { "$path has a non-integer map key." }
            Long::class.javaPrimitiveType, Long::class.javaObjectType ->
                require(key.toLongOrNull() != null) { "$path has a non-integer map key." }
            else -> error("Unsupported map key in $path.")
        }
    }

    private fun validateNumber(element: JsonElement, type: Class<*>, path: String) {
        if (type == BigDecimal::class.java) {
            require(element.isJsonPrimitive && (element.asJsonPrimitive.isNumber || element.asJsonPrimitive.isString)) {
                "$path must be numeric."
            }
            element.asString.toBigDecimal()
            return
        }
        require(element.isJsonPrimitive && element.asJsonPrimitive.isNumber) { "$path must be numeric." }
        val number = element.asBigDecimal
        when (type) {
            Int::class.java, Int::class.javaPrimitiveType -> number.intValueExact()
            Long::class.java, Long::class.javaPrimitiveType -> number.longValueExact()
            Float::class.java, Float::class.javaPrimitiveType -> require(number.toFloat().isFinite()) { "$path must be finite." }
            Double::class.java, Double::class.javaPrimitiveType -> require(number.toDouble().isFinite()) { "$path must be finite." }
            Short::class.java, Short::class.javaPrimitiveType -> number.shortValueExact()
            Byte::class.java, Byte::class.javaPrimitiveType -> number.byteValueExact()
        }
    }

    private fun validateAny(element: JsonElement, path: String) {
        when {
            element.isJsonArray -> {
                require(element.asJsonArray.size() <= BackupCodec.MAX_RECORDS) { "$path exceeds the supported record limit." }
                element.asJsonArray.forEachIndexed { index, value -> validateAny(value, "$path[$index]") }
            }
            element.isJsonObject -> {
                require(element.asJsonObject.size() <= BackupCodec.MAX_RECORDS) { "$path exceeds the supported record limit." }
                element.asJsonObject.entrySet().forEach { (key, value) -> validateAny(value, "$path.$key") }
            }
            else -> Unit
        }
    }

    private fun validateSnapshot(snapshot: CoffeeDurableSnapshot) {
        val totalRecords = listOf(
            snapshot.journalEntries.size,
            snapshot.cafeCupEntries.size,
            snapshot.cuppingSessions.size,
            snapshot.physicalQcRecords.size,
            snapshot.roastRestObservations.size,
            snapshot.detailedBeans.size,
            snapshot.grinders.size,
            snapshot.equipmentCatalog.size,
            snapshot.waterProfiles.size,
            snapshot.foodGradeIngredients.size,
            snapshot.greenCoffeeLots.size,
            snapshot.roastBatches.size
        ).sum()
        require(totalRecords <= BackupCodec.MAX_RECORDS) { "Personal records exceed the supported backup limit." }
        require(
            snapshot.journalEntries.map { it.id }.toSet().size == snapshot.journalEntries.size
        ) { "Duplicate journal entry identifiers." }
        require(snapshot.cafeCupEntries.map { it.id }.toSet().size == snapshot.cafeCupEntries.size) { "Duplicate cafe cup identifiers." }
        require(snapshot.cuppingSessions.map { it.id }.toSet().size == snapshot.cuppingSessions.size) { "Duplicate cupping identifiers." }
        require(snapshot.detailedBeans.map { it.id }.toSet().size == snapshot.detailedBeans.size) { "Duplicate detailed bean identifiers." }
        require(snapshot.grinders.map { it.id }.toSet().size == snapshot.grinders.size) { "Duplicate grinder identifiers." }
        require(
            snapshot.equipmentCatalog.map { it.id }.toSet().size == snapshot.equipmentCatalog.size
        ) { "Duplicate equipment identifiers." }
        require(snapshot.waterProfiles.map { it.id }.toSet().size == snapshot.waterProfiles.size) { "Duplicate water profile identifiers." }
        require(snapshot.foodGradeIngredients.map { it.id }.toSet().size == snapshot.foodGradeIngredients.size) {
            "Duplicate food-grade ingredient identifiers."
        }
        require(snapshot.greenCoffeeLots.map { it.id }.toSet().size == snapshot.greenCoffeeLots.size) { "Duplicate green-lot identifiers." }
        require(snapshot.roastBatches.map { it.id }.toSet().size == snapshot.roastBatches.size) { "Duplicate roast-batch identifiers." }
        require(snapshot.roastRestObservations.map { it.id }.toSet().size == snapshot.roastRestObservations.size) {
            "Duplicate roast-rest observation identifiers."
        }
        require(snapshot.physicalQcRecords.all { (key, record) -> key == record.sampleId && key.isNotBlank() }) {
            "Physical QC record keys must match their sample identifiers."
        }
        require(snapshot.journalEntries.all { it.id.isNotBlank() && (it.rating == null || it.rating in 1.0..10.0) }) {
            "Invalid journal entry metadata."
        }
        snapshot.journalEntries.forEach(::validateJournalExtractionYield)
        require(snapshot.cafeCupEntries.all { it.id.isNotBlank() && it.rating in 1.0..10.0 }) { "Invalid cafe cup entry." }
        val bagIds = snapshot.detailedBeans.flatMap { bean -> bean.bags.map { bag -> bag.id } }
        require(bagIds.all(String::isNotBlank) && bagIds.toSet().size == bagIds.size) { "Bag identifiers must be unique and non-empty." }
        require(
            snapshot.detailedBeans.all { bean ->
                bean.id.isNotBlank() && bean.bags.all { bag ->
                    bag.beanId == bean.id && bag.packageMassGrams > BigDecimal.ZERO &&
                        bag.remainingMassGrams >= BigDecimal.ZERO && bag.remainingMassGrams <= bag.packageMassGrams
                }
            }
        ) {
            "Bag inventory refers to a different bean."
        }
        require(snapshot.roastRestObservations.all { observation -> bagIds.contains(observation.bagId) }) {
            "Roast-rest observations must refer to a saved bag."
        }
        require(
            snapshot.roastBatches.all { batch ->
                batch.firstCrackTimeSeconds in 1..<batch.totalRoastDurationSeconds &&
                    (batch.greenLotId == null || snapshot.greenCoffeeLots.any { it.id == batch.greenLotId }) &&
                    (
                        batch.blendComponents.isEmpty() || batch.blendComponents
                            .fold(BigDecimal.ZERO) { total, component -> total.add(component.componentPercentage) }
                            .compareTo(BigDecimal("100.00")) == 0
                        )
            }
        ) {
            "Roast batch timing, green-lot links, or blend percentages are invalid."
        }
        require(snapshot.palateProfile.vector.toDoubleArray().all { it.isFinite() && it in 0.0..1.0 }) {
            "Invalid palate profile vector."
        }
        require(
            snapshot.palateProfile.varianceVector.toDoubleArray().all { it.isFinite() && it in 0.0..1.0 } &&
                snapshot.palateProfile.cumulativeEvidenceWeight.isFinite() && snapshot.palateProfile.cumulativeEvidenceWeight >= 0.0 &&
                snapshot.palateProfile.qualifiedSessionCount >= 0
        ) { "Invalid palate profile evidence metadata." }
    }

    private fun validateJournalExtractionYield(entry: BrewJournalEntry) {
        entry.beverageMassGrams?.let { mass ->
            require(mass.isFinite() && mass > 0.0) { "Journal beverage mass must be positive and finite." }
        }
        entry.measuredTdsPercent?.let { tds ->
            require(tds.isFinite() && tds in 0.0..100.0) { "Journal measured TDS must be finite and in [0, 100]." }
        }
        val storedYield = entry.extractionYieldPercent ?: return
        require(entry.beverageMassGrams != null && entry.measuredTdsPercent != null) {
            "Stored extraction yield requires measured beverage mass and measured TDS."
        }
        val calculated = CoffeeScienceEngine.calculateExtractionYield(
            entry.doseGrams,
            entry.beverageMassGrams,
            entry.measuredTdsPercent,
            entry.brewMethod
        ).getOrElse { failure ->
            throw IllegalArgumentException("Stored extraction-yield measurement inputs are invalid.", failure)
        }.extractionYieldPercent
        require(storedYield.isFinite() && abs(storedYield - calculated) <= EXTRACTION_YIELD_TOLERANCE_PERCENT) {
            "Stored extraction yield does not match its measured mass-balance inputs."
        }
    }
}
