package com.coffeecreator.app.ui.viewmodel

import com.coffeecreator.app.core.catalog.BagInventoryItem
import com.coffeecreator.app.core.catalog.BagStatus
import com.coffeecreator.app.core.catalog.CatalogExportPayload
import com.coffeecreator.app.core.catalog.CatalogImportExportEngine
import com.coffeecreator.app.core.catalog.ConflictResolutionStrategy
import com.coffeecreator.app.core.catalog.DetailedBeanRecord
import com.coffeecreator.app.core.catalog.EquipmentItem
import com.coffeecreator.app.core.catalog.FoodGradeIngredientItem
import com.coffeecreator.app.core.catalog.GreenCoffeeLot
import com.coffeecreator.app.core.catalog.GrinderRecord
import com.coffeecreator.app.core.catalog.ImportDryRunSummary
import com.coffeecreator.app.core.catalog.ImportExecutionReceipt
import com.coffeecreator.app.core.catalog.InventoryDeductionEngine
import com.coffeecreator.app.core.catalog.InventoryDeductionResult
import com.coffeecreator.app.core.catalog.OfflineBagOcrParser
import com.coffeecreator.app.core.catalog.RoastBatchRecord
import com.coffeecreator.app.core.catalog.WaterMineralProfile
import com.coffeecreator.app.core.reliability.LogLevel
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update


fun CoffeeViewModel.addDetailedBean(bean: DetailedBeanRecord) {
    _state.update { it.copy(detailedBeans = it.detailedBeans + bean) }
    diagnosticBuffer.log(LogLevel.INFO, "BEAN", "Added bean '${bean.roaster} - ${bean.name}'")
}

fun CoffeeViewModel.updateDetailedBean(bean: DetailedBeanRecord) {
    _state.update {
        it.copy(detailedBeans = it.detailedBeans.map { b -> if (b.id == bean.id) bean else b })
    }
}

fun CoffeeViewModel.deleteDetailedBean(id: String) {
    _state.update {
        it.copy(detailedBeans = it.detailedBeans.filterNot { b -> b.id == id })
    }
}

fun CoffeeViewModel.toggleBeanFavorite(id: String) {
    _state.update {
        it.copy(detailedBeans = it.detailedBeans.map { b -> if (b.id == id) b.copy(isFavorite = !b.isFavorite) else b })
    }
}

fun CoffeeViewModel.addBagToBean(beanId: String, packageMassGrams: BigDecimal, remainingMassGrams: BigDecimal, price: BigDecimal? = null) {
    require(packageMassGrams > BigDecimal.ZERO) { "Package mass must be positive." }
    require(remainingMassGrams >= BigDecimal.ZERO && remainingMassGrams <= packageMassGrams) {
        "Remaining mass must be between zero and package mass."
    }
    val newBag = BagInventoryItem(
        id = UUID.randomUUID().toString(),
        beanId = beanId,
        packageMassGrams = packageMassGrams,
        remainingMassGrams = remainingMassGrams,
        status = when {
            remainingMassGrams == BigDecimal.ZERO -> BagStatus.FINISHED
            remainingMassGrams < packageMassGrams -> BagStatus.ACTIVE
            else -> BagStatus.PURCHASED
        },
        pricePaid = price
    )
    _state.update {
        it.copy(
            detailedBeans = it.detailedBeans.map { b ->
                if (b.id == beanId) b.copy(bags = b.bags + newBag) else b
            }
        )
    }
}

fun CoffeeViewModel.deductDoseFromBag(bagId: String, doseGrams: BigDecimal): Boolean {
    var success = false
    _state.update {
        it.copy(
            detailedBeans = it.detailedBeans.map { b ->
                val bag = b.bags.find { bag -> bag.id == bagId }
                if (bag != null) {
                    val result = InventoryDeductionEngine.deductDose(bag, doseGrams)
                    if (result is InventoryDeductionResult.Success) {
                        success = true
                        diagnosticBuffer.log(LogLevel.INFO, "INVENTORY", "Deducted ${doseGrams}g from bag $bagId on bean '${b.name}'")
                        b.copy(bags = b.bags.map { bg -> if (bg.id == bagId) result.updatedBag else bg })
                    } else {
                        b
                    }
                } else {
                    b
                }
            }
        )
    }
    return success
}

fun CoffeeViewModel.toggleFreezeBag(bagId: String) {
    _state.update {
        it.copy(
            detailedBeans = it.detailedBeans.map { b ->
                val bag = b.bags.find { bag -> bag.id == bagId }
                if (bag != null) {
                    val updated = if (bag.status == BagStatus.FROZEN) {
                        InventoryDeductionEngine.thawBag(bag)
                    } else {
                        InventoryDeductionEngine.freezeBag(bag)
                    }
                    b.copy(bags = b.bags.map { bg -> if (bg.id == bagId) updated else bg })
                } else {
                    b
                }
            }
        )
    }
}

fun CoffeeViewModel.addGrinder(grinder: GrinderRecord) {
    _state.update { it.copy(grinders = it.grinders + grinder) }
}

fun CoffeeViewModel.toggleGrinderFavorite(id: String) {
    _state.update {
        it.copy(grinders = it.grinders.map { g -> if (g.id == id) g.copy(isFavorite = !g.isFavorite) else g })
    }
}

fun CoffeeViewModel.deleteGrinder(id: String) {
    _state.update { it.copy(grinders = it.grinders.filterNot { g -> g.id == id }) }
}

fun CoffeeViewModel.addEquipmentItem(item: EquipmentItem) {
    _state.update { it.copy(equipmentCatalog = it.equipmentCatalog + item) }
}

fun CoffeeViewModel.toggleEquipmentFavorite(id: String) {
    _state.update {
        it.copy(equipmentCatalog = it.equipmentCatalog.map { e -> if (e.id == id) e.copy(isFavorite = !e.isFavorite) else e })
    }
}

fun CoffeeViewModel.deleteEquipmentItem(id: String) {
    _state.update { it.copy(equipmentCatalog = it.equipmentCatalog.filterNot { e -> e.id == id }) }
}

fun CoffeeViewModel.addWaterProfile(profile: WaterMineralProfile) {
    _state.update { it.copy(waterProfiles = it.waterProfiles + profile) }
}

fun CoffeeViewModel.deleteWaterProfile(id: String) {
    _state.update { it.copy(waterProfiles = it.waterProfiles.filterNot { w -> w.id == id }) }
}

fun CoffeeViewModel.addFoodGradeIngredient(item: FoodGradeIngredientItem) {
    _state.update { it.copy(foodGradeIngredients = it.foodGradeIngredients + item) }
}

fun CoffeeViewModel.deleteFoodGradeIngredient(id: String) {
    _state.update { it.copy(foodGradeIngredients = it.foodGradeIngredients.filterNot { i -> i.id == id }) }
}

fun CoffeeViewModel.addGreenLot(lot: GreenCoffeeLot) {
    _state.update { it.copy(greenCoffeeLots = it.greenCoffeeLots + lot) }
}

fun CoffeeViewModel.deleteGreenLot(id: String) {
    _state.update { it.copy(greenCoffeeLots = it.greenCoffeeLots.filterNot { l -> l.id == id }) }
}

fun CoffeeViewModel.addRoastBatch(batch: RoastBatchRecord) {
    _state.update { it.copy(roastBatches = it.roastBatches + batch) }
}

fun CoffeeViewModel.deleteRoastBatch(id: String) {
    _state.update { it.copy(roastBatches = it.roastBatches.filterNot { r -> r.id == id }) }
}

fun CoffeeViewModel.parseBagOcr(photoUri: String?, text: String) {
    val draft = OfflineBagOcrParser.parseBagText(photoUri, text)
    _state.update { it.copy(pendingOcrDraft = draft) }
}

fun CoffeeViewModel.confirmOcrDraft(confirmedBean: DetailedBeanRecord) {
    addDetailedBean(confirmedBean)
    _state.update { it.copy(pendingOcrDraft = null) }
}

fun CoffeeViewModel.dismissOcrDraft() {
    _state.update { it.copy(pendingOcrDraft = null) }
}

fun CoffeeViewModel.previewCatalogImport(jsonContent: String): ImportDryRunSummary {
    val preview = CatalogImportExportEngine.previewImport(
        jsonContent,
        _state.value.detailedBeans,
        _state.value.equipmentCatalog
    )
    _state.update { it.copy(catalogImportPreview = preview) }
    return preview
}

fun CoffeeViewModel.executeCatalogImport(jsonContent: String, strategy: ConflictResolutionStrategy): ImportExecutionReceipt {
    val (receipt, updatedCollections) = CatalogImportExportEngine.executeImport(
        jsonContent,
        strategy,
        _state.value.detailedBeans,
        _state.value.equipmentCatalog
    )
    _state.update {
        it.copy(
            detailedBeans = updatedCollections.first,
            equipmentCatalog = updatedCollections.second,
            catalogImportPreview = null
        )
    }
    diagnosticBuffer.log(
        LogLevel.INFO,
        "IMPORT",
        "Catalog import completed: created=${receipt.createdCount}, updated=${receipt.updatedCount}, renamed=${receipt.renamedCount}, skipped=${receipt.skippedCount}"
    )
    return receipt
}

fun CoffeeViewModel.exportCatalogJson(): String {
    val payload = CatalogExportPayload(
        beans = _state.value.detailedBeans,
        grinders = _state.value.grinders,
        equipment = _state.value.equipmentCatalog,
        waters = _state.value.waterProfiles,
        ingredients = _state.value.foodGradeIngredients
    )
    return CatalogImportExportEngine.exportToJson(payload)
}

fun CoffeeViewModel.exportBeansCsv(): String {
    return CatalogImportExportEngine.exportBeansToCsv(_state.value.detailedBeans)
}
