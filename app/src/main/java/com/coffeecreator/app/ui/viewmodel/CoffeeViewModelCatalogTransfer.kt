package com.coffeecreator.app.ui.viewmodel

import com.coffeecreator.app.core.catalog.CatalogExportPayload
import com.coffeecreator.app.core.catalog.CatalogImportExportEngine
import com.coffeecreator.app.core.catalog.ConflictResolutionStrategy
import com.coffeecreator.app.core.catalog.ImportDryRunSummary
import com.coffeecreator.app.core.catalog.ImportExecutionReceipt
import com.coffeecreator.app.core.reliability.LogLevel
import kotlinx.coroutines.flow.update

fun CoffeeViewModel.previewCatalogImport(jsonContent: String): ImportDryRunSummary {
    val preview = CatalogImportExportEngine.previewImport(
        jsonContent,
        mutableState.value.detailedBeans,
        mutableState.value.equipmentCatalog
    )
    mutableState.update { it.copy(catalogImportPreview = preview) }
    return preview
}

fun CoffeeViewModel.executeCatalogImport(jsonContent: String, strategy: ConflictResolutionStrategy): ImportExecutionReceipt {
    val (receipt, updatedCollections) = CatalogImportExportEngine.executeImport(
        jsonContent,
        strategy,
        mutableState.value.detailedBeans,
        mutableState.value.equipmentCatalog
    )
    mutableState.update {
        it.copy(
            detailedBeans = updatedCollections.first,
            equipmentCatalog = updatedCollections.second,
            catalogImportPreview = null
        )
    }
    diagnosticBuffer.log(
        LogLevel.INFO,
        "IMPORT",
        "Catalog import completed: created=${receipt.createdCount}, updated=${receipt.updatedCount}, " +
            "renamed=${receipt.renamedCount}, skipped=${receipt.skippedCount}"
    )
    return receipt
}

fun CoffeeViewModel.exportCatalogJson(): String {
    val payload = CatalogExportPayload(
        beans = mutableState.value.detailedBeans,
        grinders = mutableState.value.grinders,
        equipment = mutableState.value.equipmentCatalog,
        waters = mutableState.value.waterProfiles,
        ingredients = mutableState.value.foodGradeIngredients
    )
    return CatalogImportExportEngine.exportToJson(payload)
}

fun CoffeeViewModel.exportBeansCsv(): String {
    return CatalogImportExportEngine.exportBeansToCsv(mutableState.value.detailedBeans)
}
