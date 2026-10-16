package com.coffeecreator.app.core.catalog

import java.math.BigDecimal
import java.math.RoundingMode

sealed class InventoryDeductionResult {
    data class Success(
        val updatedBag: BagInventoryItem,
        val consumedGrams: BigDecimal,
        val wasDepleted: Boolean
    ) : InventoryDeductionResult()

    data class Error(val reason: String) : InventoryDeductionResult()
}

object InventoryDeductionEngine {

    fun deductDose(
        bag: BagInventoryItem,
        doseGrams: BigDecimal,
        timestampEpochMs: Long = System.currentTimeMillis()
    ): InventoryDeductionResult {
        if (doseGrams <= BigDecimal.ZERO) {
            return InventoryDeductionResult.Error("Dose must be greater than zero.")
        }
        if (bag.status == BagStatus.FROZEN) {
            return InventoryDeductionResult.Error("FROZEN_BAG_CANNOT_BREW: Thaw bag before brewing.")
        }
        if (bag.status == BagStatus.FINISHED || bag.remainingMassGrams <= BigDecimal.ZERO) {
            return InventoryDeductionResult.Error("Bag is already finished.")
        }

        val actualDose = doseGrams.setScale(2, RoundingMode.HALF_UP)
        val remaining = bag.remainingMassGrams.setScale(2, RoundingMode.HALF_UP)

        val newRemaining = maxOf(BigDecimal.ZERO, remaining.subtract(actualDose)).setScale(2, RoundingMode.HALF_UP)
        val wasDepleted = newRemaining.compareTo(BigDecimal.ZERO) == 0

        val newStatus = when {
            wasDepleted -> BagStatus.FINISHED
            bag.status == BagStatus.PURCHASED -> BagStatus.ACTIVE
            else -> bag.status
        }

        val openedAt = if (bag.status == BagStatus.PURCHASED && newStatus == BagStatus.ACTIVE) {
            timestampEpochMs
        } else {
            bag.openedAtEpochMs
        }

        val finishedAt = if (wasDepleted) timestampEpochMs else bag.finishedAtEpochMs

        val updated = bag.copy(
            remainingMassGrams = newRemaining,
            status = newStatus,
            openedAtEpochMs = openedAt,
            finishedAtEpochMs = finishedAt
        )

        return InventoryDeductionResult.Success(
            updatedBag = updated,
            consumedGrams = minOf(actualDose, remaining),
            wasDepleted = wasDepleted
        )
    }

    fun freezeBag(bag: BagInventoryItem, freezeTimestampEpochMs: Long = System.currentTimeMillis()): BagInventoryItem {
        require(bag.status != BagStatus.FINISHED) { "Finished bags cannot be frozen." }
        if (bag.status == BagStatus.FROZEN) return bag

        val newEvent = FreezeEvent(frozenAtEpochMs = freezeTimestampEpochMs)
        return bag.copy(
            status = BagStatus.FROZEN,
            freezeHistory = bag.freezeHistory + newEvent
        )
    }

    fun thawBag(bag: BagInventoryItem, thawTimestampEpochMs: Long = System.currentTimeMillis()): BagInventoryItem {
        if (bag.status != BagStatus.FROZEN) return bag

        val updatedHistory = bag.freezeHistory.mapIndexed { index, event ->
            if (index == bag.freezeHistory.lastIndex && event.isOngoing) {
                event.copy(thawedAtEpochMs = thawTimestampEpochMs)
            } else {
                event
            }
        }

        return bag.copy(
            status = BagStatus.ACTIVE,
            freezeHistory = updatedHistory
        )
    }
}
