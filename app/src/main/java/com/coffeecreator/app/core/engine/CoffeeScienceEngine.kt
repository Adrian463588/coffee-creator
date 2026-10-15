package com.coffeecreator.app.core.engine

import com.coffeecreator.app.core.model.BlendComponent
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.CalculationMetadata
import com.coffeecreator.app.core.model.CalculationResult
import com.coffeecreator.app.core.model.InputLimits
import com.coffeecreator.app.core.model.MassFractionPercent
import com.coffeecreator.app.core.model.MassGrams
import com.coffeecreator.app.core.model.RefractometerProfile
import java.math.BigDecimal
import java.math.RoundingMode

/** Validated decimal arithmetic. Output values round once; ppm never derives from a rounded percentage. */
object CoffeeScienceEngine {
    fun calculateExtractionYield(
        doseGrams: Double,
        beverageMassGrams: Double,
        measuredTdsPercent: Double,
        brewMethod: String = "Percolation"
    ): Result<CalculationResult.ExtractionYield> = runCatching {
        calculateExtractionYield(
            MassGrams(DomainValidation.positive(doseGrams, "Dose (g)")),
            MassGrams(DomainValidation.positive(beverageMassGrams, "Beverage mass (g)")),
            MassFractionPercent(DomainValidation.percent(measuredTdsPercent, "Measured TDS (% w/w)")),
            brewMethod
        ).getOrThrow()
    }

    fun calculateExtractionYield(
        dose: MassGrams,
        beverage: MassGrams,
        measuredTds: MassFractionPercent,
        brewMethod: String = "Percolation"
    ): Result<CalculationResult.ExtractionYield> = runCatching {
        require(dose.value.signum() > 0 && beverage.value.signum() > 0) { "Dose and beverage mass must be greater than zero." }
        val solidsTimesHundred = beverage.value.multiply(measuredTds.value)
        require(solidsTimesHundred <= dose.value.multiply(BigDecimal("100"))) {
            "Extraction yield cannot exceed 100% of coffee dose; check measurements and non-coffee solutes."
        }
        val immersion = listOf("immersion", "french press", "aeropress", "cupping", "cold brew")
            .any { brewMethod.contains(it, ignoreCase = true) }
        val assumption = "EY% = (Beverage Mass * Measured TDS%) / Dose. Approximate cup-only mass balance; " +
            "TDS must represent coffee-derived dissolved solids, excluding additives. " +
            if (immersion) {
                "Immersion limitation: retained dissolved solids are omitted; this is not total immersion extraction yield."
            } else {
                "Dissolved solids retained with grounds are omitted; beverage mass is measured, never inferred from brew water."
            }
        CalculationResult.ExtractionYield(
            extractionYieldPercent = finiteOutput(solidsTimesHundred.divide(dose.value, 2, RoundingMode.HALF_UP)),
            beverageMassGrams = beverage.value.toDouble(),
            measuredTdsPercent = measuredTds.value.toDouble(),
            doseGrams = dose.value.toDouble(),
            methodLabel = if (immersion) "Approximate Cup-Only Mass Balance (Immersion)" else "Approximate Percolation Mass Balance",
            assumptions = assumption,
            metadata = metadata(
                mapOf("doseGrams" to dose.value, "beverageMassGrams" to beverage.value, "measuredTdsPercent" to measuredTds.value),
                mapOf("doseGrams" to "g", "beverageMassGrams" to "g", "measuredTdsPercent" to "% w/w"),
                mapOf(
                    "doseGrams" to "Caller-provided dose mass; not independently verified.",
                    "beverageMassGrams" to "Provided as measured by caller; instrument uncertainty not supplied.",
                    "measuredTdsPercent" to "Provided as measured by caller; refractometer method and uncertainty not supplied."
                )
            )
        )
    }

    fun calculateBrewRatio(doseGrams: Double, waterGrams: Double): Result<CalculationResult.BrewRatio> = runCatching {
        val dose = DomainValidation.positive(doseGrams, "Dose (g)")
        val water = DomainValidation.positive(waterGrams, "Water mass (g)")
        val denominator = water.divide(dose, 2, RoundingMode.HALF_UP)
        CalculationResult.BrewRatio(
            ratioDenominator = finiteOutput(denominator),
            ratioString = "1:${denominator.toPlainString()}",
            doseGrams = doseGrams,
            waterGrams = waterGrams,
            metadata = metadata(mapOf("doseGrams" to dose, "waterGrams" to water), mapOf("doseGrams" to "g", "waterGrams" to "g"))
        )
    }

    fun calculateDilution(
        initialMassGrams: Double,
        initialTdsPercent: Double,
        addedWaterGrams: Double
    ): Result<CalculationResult.Dilution> = runCatching {
        val initial = DomainValidation.positive(initialMassGrams, "Initial solution mass (g)")
        val tds = DomainValidation.percent(initialTdsPercent, "Initial TDS (% w/w)")
        val water = DomainValidation.nonNegative(addedWaterGrams, "Added water (g)")
        val finalMass = initial.add(water)
        CalculationResult.Dilution(
            initialMassGrams = initialMassGrams,
            initialTdsPercent = initialTdsPercent,
            addedWaterGrams = addedWaterGrams,
            finalMassGrams = finiteOutput(finalMass),
            finalTdsPercent = finiteOutput(initial.multiply(tds).divide(finalMass, 2, RoundingMode.HALF_UP)),
            metadata = metadata(
                mapOf("initialMassGrams" to initial, "initialTdsPercent" to tds, "addedWaterGrams" to water),
                mapOf("initialMassGrams" to "g", "initialTdsPercent" to "% w/w", "addedWaterGrams" to "g")
            )
        )
    }

    fun calculateConcentration(soluteMassGrams: Double, solutionMassGrams: Double): Result<CalculationResult.Concentration> = runCatching {
        val solute = DomainValidation.nonNegative(soluteMassGrams, "Solute mass (g)")
        val solution = DomainValidation.positive(solutionMassGrams, "Total solution mass (g)")
        require(solute <= solution) { "Solute mass cannot exceed total solution mass." }
        CalculationResult.Concentration(
            soluteMassGrams = soluteMassGrams,
            solutionMassGrams = solutionMassGrams,
            massFractionPercent = finiteOutput(solute.multiply(BigDecimal("100")).divide(solution, 8, RoundingMode.HALF_UP)),
            ppm = finiteOutput(solute.multiply(BigDecimal("1000000")).divide(solution, 6, RoundingMode.HALF_UP)),
            metadata = metadata(
                mapOf("soluteMassGrams" to solute, "solutionMassGrams" to solution),
                mapOf("soluteMassGrams" to "g", "solutionMassGrams" to "g")
            )
        )
    }

    /** Legacy numerical API retained; new tools can expose complete conversion metadata. */
    fun convertVolumeToMass(volumeMl: Double, densityGramsPerMl: Double?): Result<Double> =
        calculateVolumeToMass(volumeMl, densityGramsPerMl).map { it.massGrams }

    fun calculateVolumeToMass(volumeMl: Double, densityGramsPerMl: Double?): Result<CalculationResult.UnitConversion> = runCatching {
        val volume = DomainValidation.nonNegative(volumeMl, "Volume (mL)")
        val density = DomainValidation.positive(
            requireNotNull(densityGramsPerMl) {
                "Volume cannot be converted to mass without a density (g/mL)."
            },
            "Density (g/mL)"
        )
        CalculationResult.UnitConversion(
            volumeMl = volumeMl,
            densityGramsPerMl = densityGramsPerMl,
            massGrams = finiteOutput(volume.multiply(density)),
            metadata = metadata(
                mapOf("volumeMl" to volume, "densityGramsPerMl" to density),
                mapOf("volumeMl" to "mL", "densityGramsPerMl" to "g/mL")
            )
        )
    }

    fun validateBlendRatios(components: List<BlendComponent>): Result<Boolean> = runCatching {
        require(components.isNotEmpty()) { "Blend must contain at least one component." }
        require(components.size <= InputLimits.COLLECTION_ITEMS) { "Blend has too many components." }
        require(components.map { it.beanId }.distinct().size == components.size) { "Blend bean identifiers must be unique." }
        val total = components.fold(BigDecimal.ZERO) { sum, component ->
            require(component.beanId.isNotBlank() && component.beanName.isNotBlank()) {
                "Blend component identifier and name are required."
            }
            val share = DomainValidation.percent(component.percentage, "Blend share (%)")
            require(share.signum() > 0) { "Blend shares must be greater than zero." }
            sum.add(share)
        }
        require(total.compareTo(BigDecimal("100")) == 0) { "Total blend components must sum to 100.00%, got ${total.toPlainString()}%." }
        true
    }

    fun calculateBrixToTds(
        brixPercent: Double,
        profile: RefractometerProfile = RefractometerProfile.STANDARD
    ): Result<CalculationResult.BrixConversion> = runCatching {
        val brix = DomainValidation.nonNegative(brixPercent, "Brix (%)")
        require(brix <= BigDecimal("35.0")) { "Brix reading exceeds supported coffee range (0–35%)." }
        val slope = BigDecimal.valueOf(profile.slope)
        val intercept = BigDecimal.valueOf(profile.intercept)
        val tds = brix.multiply(slope).add(intercept).setScale(4, RoundingMode.HALF_UP)
        CalculationResult.BrixConversion(
            brixPercent = brixPercent,
            tdsPercent = finiteOutput(tds),
            instrumentProfile = profile.name,
            calibrationSlope = profile.slope,
            calibrationIntercept = profile.intercept,
            assumptions = "TDS% = Brix% × ${profile.slope} + ${profile.intercept}. ${profile.citation}.",
            metadata = metadata(
                mapOf("brixPercent" to brix, "calibrationSlope" to slope),
                mapOf("brixPercent" to "% Brix", "calibrationSlope" to "ratio"),
                mapOf(
                    "brixPercent" to "Optical refractometer reading",
                    "calibrationSlope" to profile.citation
                )
            )
        )
    }

    /**
     * Scale recipe steps using the Hamilton-Hare largest remainder apportionment method.
     * Guarantees exact sum matching targetWaterGrams and strictly monotonic cumulative mass.
     */
    fun scaleRecipeSteps(
        steps: List<BrewStep>,
        initialWaterGrams: Double,
        targetWaterGrams: Double,
        precisionQuantum: Double = 0.1
    ): Result<List<BrewStep>> = runCatching {
        require(steps.isNotEmpty()) { "Steps list cannot be empty." }
        val initialWater = DomainValidation.positive(initialWaterGrams, "Initial water mass (g)")
        val targetWater = DomainValidation.positive(targetWaterGrams, "Target water mass (g)")
        val quantum = DomainValidation.positive(precisionQuantum, "Precision quantum (g)")

        val totalQuantums = targetWater.divide(quantum, 0, RoundingMode.HALF_UP).toInt()
        val scaleFactor = targetWater.divide(initialWater, 10, RoundingMode.HALF_UP)

        data class StepScaleItem(
            val index: Int,
            val original: BrewStep,
            val ideal: BigDecimal,
            val baseQuantum: Int,
            val remainder: BigDecimal
        )

        val items = steps.mapIndexed { index, step ->
            val stepWater = DomainValidation.nonNegative(step.targetWaterGrams, "Step water (g)")
            val ideal = stepWater.multiply(scaleFactor)
            val quantumsDec = ideal.divide(quantum, 6, RoundingMode.DOWN)
            val baseQ = quantumsDec.toInt()
            val rem = quantumsDec.subtract(BigDecimal.valueOf(baseQ.toLong()))
            StepScaleItem(index, step, ideal, baseQ, rem)
        }

        val allocated = items.sumOf { it.baseQuantum }
        val deficit = (totalQuantums - allocated).coerceIn(0, items.size)

        val bonusIndices = items.sortedWith(
            compareByDescending<StepScaleItem> { it.remainder }
                .thenBy { it.index }
        ).take(deficit).map { it.index }.toSet()

        var runningCumulative = BigDecimal.ZERO
        items.map { item ->
            val stepQ = item.baseQuantum + if (item.index in bonusIndices) 1 else 0
            val stepGrams = BigDecimal.valueOf(stepQ.toLong()).multiply(quantum).setScale(1, RoundingMode.HALF_UP)
            runningCumulative = runningCumulative.add(stepGrams)
            item.original.copy(
                targetWaterGrams = stepGrams.toDouble(),
                cumulativeMassGrams = runningCumulative.setScale(1, RoundingMode.HALF_UP).toDouble()
            )
        }
    }

    fun calculateConcentrateDosing(
        concentrateTdsPercent: Double,
        targetTdsPercent: Double,
        targetBeverageMassGrams: Double
    ): Result<CalculationResult.ConcentrateDosing> = runCatching {
        val concTds = DomainValidation.positive(concentrateTdsPercent, "Concentrate TDS (%)")
        val targTds = DomainValidation.positive(targetTdsPercent, "Target TDS (%)")
        val targMass = DomainValidation.positive(targetBeverageMassGrams, "Target beverage mass (g)")
        require(targTds <= concTds) { "Target TDS ($targetTdsPercent%) cannot exceed concentrate TDS ($concentrateTdsPercent%)." }

        val concMass = targMass.multiply(targTds).divide(concTds, 2, RoundingMode.HALF_UP)
        val waterMass = targMass.subtract(concMass)

        CalculationResult.ConcentrateDosing(
            concentrateTdsPercent = concentrateTdsPercent,
            targetTdsPercent = targetTdsPercent,
            targetBeverageMassGrams = targetBeverageMassGrams,
            concentrateMassGrams = finiteOutput(concMass),
            dilutionWaterMassGrams = finiteOutput(waterMass),
            metadata = metadata(
                mapOf("concentrateTds" to concTds, "targetTds" to targTds, "targetMass" to targMass),
                mapOf("concentrateTds" to "% w/w", "targetTds" to "% w/w", "targetMass" to "g")
            )
        )
    }

    fun calculateAromaDosage(
        beverageMassGrams: Double,
        stockAdditionGrams: Double,
        activeStockPercent: Double,
        supplierMaxPercent: Double? = null
    ): Result<CalculationResult.AromaDosage> = runCatching {
        val bevMass = DomainValidation.positive(beverageMassGrams, "Beverage mass (g)")
        val stockMass = DomainValidation.nonNegative(stockAdditionGrams, "Stock addition mass (g)")
        val activeFraction = DomainValidation.percent(activeStockPercent, "Active aromatic compound in stock (%)")
        val totalMass = bevMass.add(stockMass)

        val stockDosagePercent = stockMass.multiply(BigDecimal("100")).divide(totalMass, 4, RoundingMode.HALF_UP)
        val activeCompoundPercent = stockMass.multiply(activeFraction).divide(totalMass, 6, RoundingMode.HALF_UP)
        val activePpm = activeCompoundPercent.multiply(BigDecimal("10000")).setScale(2, RoundingMode.HALF_UP)

        val withinLimit = supplierMaxPercent == null || stockDosagePercent <= BigDecimal.valueOf(supplierMaxPercent)

        CalculationResult.AromaDosage(
            beverageMassGrams = beverageMassGrams,
            stockAdditionGrams = stockAdditionGrams,
            stockDosagePercent = finiteOutput(stockDosagePercent.setScale(2, RoundingMode.HALF_UP)),
            activeStockPercent = activeStockPercent,
            activeCompoundPercent = finiteOutput(activeCompoundPercent),
            activeCompoundPpm = finiteOutput(activePpm),
            isWithinSupplierLimit = withinLimit,
            supplierMaxPercent = supplierMaxPercent,
            metadata = metadata(
                mapOf("beverageMass" to bevMass, "stockAddition" to stockMass, "activeStockPercent" to activeFraction),
                mapOf("beverageMass" to "g", "stockAddition" to "g", "activeStockPercent" to "% w/w")
            )
        )
    }

    fun celsiusToFahrenheit(celsius: Double): Double =
        BigDecimal.valueOf(celsius)
            .multiply(BigDecimal("9"))
            .divide(BigDecimal("5"), 4, RoundingMode.HALF_UP)
            .add(BigDecimal("32"))
            .setScale(1, RoundingMode.HALF_UP)
            .toDouble()

    fun fahrenheitToCelsius(fahrenheit: Double): Double =
        BigDecimal.valueOf(fahrenheit)
            .subtract(BigDecimal("32"))
            .multiply(BigDecimal("5"))
            .divide(BigDecimal("9"), 4, RoundingMode.HALF_UP)
            .setScale(1, RoundingMode.HALF_UP)
            .toDouble()

    private fun metadata(
        inputs: Map<String, BigDecimal>,
        units: Map<String, String>,
        provenance: Map<String, String> = inputs.keys.associateWith {
            "Caller-provided input; source and uncertainty not independently verified."
        }
    ): CalculationMetadata = CalculationMetadata(
        exactInputs = inputs.mapValues { it.value.toPlainString() },
        inputUnits = units,
        inputProvenance = provenance
    )

    private fun finiteOutput(value: BigDecimal): Double = value.toDouble().also {
        require(it.isFinite()) { "Result exceeds supported numerical range." }
    }
}
