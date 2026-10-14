package com.coffeecreator.app.core.engine

import com.coffeecreator.app.core.model.BlendComponent
import com.coffeecreator.app.core.model.CalculationMetadata
import com.coffeecreator.app.core.model.CalculationResult
import com.coffeecreator.app.core.model.MassFractionPercent
import com.coffeecreator.app.core.model.MassGrams
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
        require(solidsTimesHundred <= dose.value.multiply(BigDecimal("100"))) { "Extraction yield cannot exceed 100% of coffee dose; check measurements and non-coffee solutes." }
        val immersion = listOf("immersion", "french press", "aeropress", "cupping", "cold brew")
            .any { brewMethod.contains(it, ignoreCase = true) }
        val assumption = "EY% = (Beverage Mass * Measured TDS%) / Dose. Approximate cup-only mass balance; " +
            "TDS must represent coffee-derived dissolved solids, excluding additives. " +
            if (immersion) "Immersion limitation: dissolved solids retained with grounds are omitted; this is not total immersion extraction yield." else "Dissolved solids retained with grounds are omitted; beverage mass is measured, never inferred from brew water."
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
                mapOf("doseGrams" to "Caller-provided dose mass; not independently verified.",
                    "beverageMassGrams" to "Provided as measured by caller; instrument uncertainty not supplied.",
                    "measuredTdsPercent" to "Provided as measured by caller; refractometer method and uncertainty not supplied.")
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
            metadata = metadata(mapOf("initialMassGrams" to initial, "initialTdsPercent" to tds, "addedWaterGrams" to water),
                mapOf("initialMassGrams" to "g", "initialTdsPercent" to "% w/w", "addedWaterGrams" to "g"))
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
            metadata = metadata(mapOf("soluteMassGrams" to solute, "solutionMassGrams" to solution),
                mapOf("soluteMassGrams" to "g", "solutionMassGrams" to "g"))
        )
    }

    /** Legacy numerical API retained; new tools can expose complete conversion metadata. */
    fun convertVolumeToMass(volumeMl: Double, densityGramsPerMl: Double?): Result<Double> =
        calculateVolumeToMass(volumeMl, densityGramsPerMl).map { it.massGrams }

    fun calculateVolumeToMass(volumeMl: Double, densityGramsPerMl: Double?): Result<CalculationResult.UnitConversion> = runCatching {
        val volume = DomainValidation.nonNegative(volumeMl, "Volume (mL)")
        val density = DomainValidation.positive(requireNotNull(densityGramsPerMl) {
            "Volume cannot be converted to mass without a density (g/mL)."
        }, "Density (g/mL)")
        CalculationResult.UnitConversion(
            volumeMl = volumeMl,
            densityGramsPerMl = densityGramsPerMl,
            massGrams = finiteOutput(volume.multiply(density)),
            metadata = metadata(mapOf("volumeMl" to volume, "densityGramsPerMl" to density),
                mapOf("volumeMl" to "mL", "densityGramsPerMl" to "g/mL"))
        )
    }

    fun validateBlendRatios(components: List<BlendComponent>): Result<Boolean> = runCatching {
        require(components.isNotEmpty()) { "Blend must contain at least one component." }
        require(components.size <= 1000) { "Blend has too many components." }
        require(components.map { it.beanId }.distinct().size == components.size) { "Blend bean identifiers must be unique." }
        val total = components.fold(BigDecimal.ZERO) { sum, component ->
            require(component.beanId.isNotBlank() && component.beanName.isNotBlank()) { "Blend component identifier and name are required." }
            val share = DomainValidation.percent(component.percentage, "Blend share (%)")
            require(share.signum() > 0) { "Blend shares must be greater than zero." }
            sum.add(share)
        }
        require(total.compareTo(BigDecimal("100")) == 0) { "Total blend components must sum to 100.00%, got ${total.toPlainString()}%." }
        true
    }

    private fun metadata(
        inputs: Map<String, BigDecimal>,
        units: Map<String, String>,
        provenance: Map<String, String> = inputs.keys.associateWith { "Caller-provided input; source and uncertainty not independently verified." }
    ): CalculationMetadata = CalculationMetadata(
        exactInputs = inputs.mapValues { it.value.toPlainString() },
        inputUnits = units,
        inputProvenance = provenance
    )

    private fun finiteOutput(value: BigDecimal): Double = value.toDouble().also {
        require(it.isFinite()) { "Result exceeds supported numerical range." }
    }
}
