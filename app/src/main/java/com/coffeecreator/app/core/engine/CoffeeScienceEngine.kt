package com.coffeecreator.app.core.engine

import com.coffeecreator.app.core.model.CalculationResult
import java.math.BigDecimal
import java.math.RoundingMode

object CoffeeScienceEngine {

    /**
     * FR-03 Golden Extraction Yield Calculation.
     * Extraction Yield (%) = (Beverage Mass (g) * Measured TDS (%)) / Dose (g)
     * Golden Test: Dose = 18.0g, Beverage = 270.0g, TDS = 1.30% => EY = 19.50%
     */
    fun calculateExtractionYield(
        doseGrams: Double,
        beverageMassGrams: Double,
        measuredTdsPercent: Double
    ): Result<CalculationResult.ExtractionYield> {
        if (doseGrams <= 0.0) {
            return Result.failure(IllegalArgumentException("Dose must be greater than zero."))
        }
        if (beverageMassGrams < 0.0) {
            return Result.failure(IllegalArgumentException("Beverage mass cannot be negative."))
        }
        if (measuredTdsPercent < 0.0) {
            return Result.failure(IllegalArgumentException("Measured TDS cannot be negative."))
        }

        // BigDecimal arithmetic for deterministic financial/scientific precision
        val dose = BigDecimal.valueOf(doseGrams)
        val beverage = BigDecimal.valueOf(beverageMassGrams)
        val tds = BigDecimal.valueOf(measuredTdsPercent)

        // Extraction Yield = (beverage * tds) / dose
        val ey = beverage.multiply(tds)
            .divide(dose, 4, RoundingMode.HALF_UP)
            .setScale(2, RoundingMode.HALF_UP)

        return Result.success(
            CalculationResult.ExtractionYield(
                extractionYieldPercent = ey.toDouble(),
                beverageMassGrams = beverageMassGrams,
                measuredTdsPercent = measuredTdsPercent,
                doseGrams = doseGrams,
                methodLabel = "Percolation Mass Balance",
                assumptions = "Assumes dissolved solids retention in grounds is excluded from cup mass. EY = (Beverage Mass * TDS%) / Dose.",
                provenance = "Deterministic Kotlin BigDecimal calculation"
            )
        )
    }

    /**
     * Water-to-coffee brew ratio calculation.
     * Ratio = 1 : (Water / Dose)
     */
    fun calculateBrewRatio(
        doseGrams: Double,
        waterGrams: Double
    ): Result<CalculationResult.BrewRatio> {
        if (doseGrams <= 0.0) {
            return Result.failure(IllegalArgumentException("Dose must be greater than zero."))
        }
        if (waterGrams <= 0.0) {
            return Result.failure(IllegalArgumentException("Water mass must be greater than zero."))
        }

        val dose = BigDecimal.valueOf(doseGrams)
        val water = BigDecimal.valueOf(waterGrams)
        val denominator = water.divide(dose, 2, RoundingMode.HALF_UP).toDouble()

        return Result.success(
            CalculationResult.BrewRatio(
                ratioNumerator = 1.0,
                ratioDenominator = denominator,
                ratioString = "1:$denominator",
                doseGrams = doseGrams,
                waterGrams = waterGrams
            )
        )
    }

    /**
     * Dilution calculation (Bypass water addition or concentrate dilution).
     * M_final = M_initial + M_water
     * TDS_final = (M_initial * TDS_initial) / M_final
     */
    fun calculateDilution(
        initialMassGrams: Double,
        initialTdsPercent: Double,
        addedWaterGrams: Double
    ): Result<CalculationResult.Dilution> {
        if (initialMassGrams <= 0.0 || addedWaterGrams < 0.0 || initialTdsPercent < 0.0) {
            return Result.failure(IllegalArgumentException("Invalid dilution inputs."))
        }

        val finalMass = initialMassGrams + addedWaterGrams
        val dissolvedSolids = BigDecimal.valueOf(initialMassGrams)
            .multiply(BigDecimal.valueOf(initialTdsPercent))
        val finalTds = dissolvedSolids
            .divide(BigDecimal.valueOf(finalMass), 4, RoundingMode.HALF_UP)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

        return Result.success(
            CalculationResult.Dilution(
                initialMassGrams = initialMassGrams,
                initialTdsPercent = initialTdsPercent,
                addedWaterGrams = addedWaterGrams,
                finalMassGrams = finalMass,
                finalTdsPercent = finalTds
            )
        )
    }

    /**
     * Solute concentration calculation (% w/w and ppm).
     * ppm = (% w/w) * 10,000
     */
    fun calculateConcentration(
        soluteMassGrams: Double,
        solutionMassGrams: Double
    ): Result<CalculationResult.Concentration> {
        if (solutionMassGrams <= 0.0 || soluteMassGrams < 0.0) {
            return Result.failure(IllegalArgumentException("Solution mass must be positive, solute cannot be negative."))
        }

        val solute = BigDecimal.valueOf(soluteMassGrams)
        val solution = BigDecimal.valueOf(solutionMassGrams)
        val massFraction = solute.divide(solution, 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(4, RoundingMode.HALF_UP)
            .toDouble()

        val ppm = BigDecimal.valueOf(massFraction)
            .multiply(BigDecimal.valueOf(10000))
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

        return Result.success(
            CalculationResult.Concentration(
                soluteMassGrams = soluteMassGrams,
                solutionMassGrams = solutionMassGrams,
                massFractionPercent = massFraction,
                ppm = ppm
            )
        )
    }

    /**
     * Strict Volume to Mass conversion requiring density.
     */
    fun convertVolumeToMass(
        volumeMl: Double,
        densityGramsPerMl: Double?
    ): Result<Double> {
        if (densityGramsPerMl == null || densityGramsPerMl <= 0.0) {
            return Result.failure(IllegalArgumentException("Volume cannot be converted to mass without a valid positive density (g/mL)."))
        }
        if (volumeMl < 0.0) {
            return Result.failure(IllegalArgumentException("Volume cannot be negative."))
        }
        val mass = BigDecimal.valueOf(volumeMl)
            .multiply(BigDecimal.valueOf(densityGramsPerMl))
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
        return Result.success(mass)
    }

    /**
     * FR-01: Validates that blend components sum to exactly 100.0% (within 0.01% precision).
     */
    fun validateBlendRatios(components: List<com.coffeecreator.app.core.model.BlendComponent>): Result<Boolean> {
        if (components.isEmpty()) {
            return Result.failure(IllegalArgumentException("Blend must contain at least one component."))
        }
        var total = BigDecimal.ZERO
        for (comp in components) {
            if (comp.percentage <= 0.0 || comp.percentage > 100.0) {
                return Result.failure(IllegalArgumentException("Component '${comp.beanName}' percentage must be between 0% and 100%."))
            }
            total = total.add(BigDecimal.valueOf(comp.percentage))
        }
        val roundedTotal = total.setScale(2, RoundingMode.HALF_UP)
        if (roundedTotal.compareTo(BigDecimal("100.00")) != 0) {
            return Result.failure(IllegalArgumentException("Total blend components must sum to 100.00%, got $roundedTotal%."))
        }
        return Result.success(true)
    }
}
