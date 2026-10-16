package com.coffeecreator.app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.coffeecreator.app.core.catalog.BagInventoryItem
import com.coffeecreator.app.core.catalog.BagStatus
import com.coffeecreator.app.core.catalog.CatalogExportPayload
import com.coffeecreator.app.core.catalog.CatalogImportExportEngine
import com.coffeecreator.app.core.catalog.CatalogPhotoTarget
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
import com.coffeecreator.app.core.catalog.OfflineEquipmentOcrParser
import com.coffeecreator.app.core.catalog.RoastBatchRecord
import com.coffeecreator.app.core.catalog.WaterMineralProfile
import com.coffeecreator.app.core.reliability.LogLevel
import java.io.IOException
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CoffeeViewModel.addDetailedBean(bean: DetailedBeanRecord) {
    mutableState.update { it.copy(detailedBeans = it.detailedBeans + bean) }
    diagnosticBuffer.log(LogLevel.INFO, "BEAN", "Added bean '${bean.roaster} - ${bean.name}'")
}

fun CoffeeViewModel.updateDetailedBean(bean: DetailedBeanRecord) {
    mutableState.update {
        it.copy(detailedBeans = it.detailedBeans.map { b -> if (b.id == bean.id) bean else b })
    }
}

fun CoffeeViewModel.deleteDetailedBean(id: String) {
    mutableState.update {
        it.copy(detailedBeans = it.detailedBeans.filterNot { b -> b.id == id })
    }
}

fun CoffeeViewModel.toggleBeanFavorite(id: String) {
    mutableState.update {
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
    mutableState.update {
        it.copy(
            detailedBeans = it.detailedBeans.map { b ->
                if (b.id == beanId) b.copy(bags = b.bags + newBag) else b
            }
        )
    }
}

fun CoffeeViewModel.deductDoseFromBag(bagId: String, doseGrams: BigDecimal): Boolean {
    var success = false
    mutableState.update {
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
    mutableState.update {
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
    mutableState.update { it.copy(grinders = it.grinders + grinder) }
}

fun CoffeeViewModel.toggleGrinderFavorite(id: String) {
    mutableState.update {
        it.copy(grinders = it.grinders.map { g -> if (g.id == id) g.copy(isFavorite = !g.isFavorite) else g })
    }
}

fun CoffeeViewModel.deleteGrinder(id: String) {
    mutableState.update { it.copy(grinders = it.grinders.filterNot { g -> g.id == id }) }
}

fun CoffeeViewModel.addEquipmentItem(item: EquipmentItem) {
    mutableState.update { it.copy(equipmentCatalog = it.equipmentCatalog + item) }
}

fun CoffeeViewModel.toggleEquipmentFavorite(id: String) {
    mutableState.update {
        it.copy(equipmentCatalog = it.equipmentCatalog.map { e -> if (e.id == id) e.copy(isFavorite = !e.isFavorite) else e })
    }
}

fun CoffeeViewModel.deleteEquipmentItem(id: String) {
    mutableState.update { it.copy(equipmentCatalog = it.equipmentCatalog.filterNot { e -> e.id == id }) }
}

fun CoffeeViewModel.addWaterProfile(profile: WaterMineralProfile) {
    mutableState.update { it.copy(waterProfiles = it.waterProfiles + profile) }
}

fun CoffeeViewModel.deleteWaterProfile(id: String) {
    mutableState.update { it.copy(waterProfiles = it.waterProfiles.filterNot { w -> w.id == id }) }
}

fun CoffeeViewModel.addFoodGradeIngredient(item: FoodGradeIngredientItem) {
    mutableState.update { it.copy(foodGradeIngredients = it.foodGradeIngredients + item) }
}

fun CoffeeViewModel.deleteFoodGradeIngredient(id: String) {
    mutableState.update { it.copy(foodGradeIngredients = it.foodGradeIngredients.filterNot { i -> i.id == id }) }
}

fun CoffeeViewModel.addGreenLot(lot: GreenCoffeeLot) {
    mutableState.update { it.copy(greenCoffeeLots = it.greenCoffeeLots + lot) }
}

fun CoffeeViewModel.deleteGreenLot(id: String) {
    mutableState.update { it.copy(greenCoffeeLots = it.greenCoffeeLots.filterNot { l -> l.id == id }) }
}

fun CoffeeViewModel.addRoastBatch(batch: RoastBatchRecord) {
    mutableState.update { it.copy(roastBatches = it.roastBatches + batch) }
}

fun CoffeeViewModel.deleteRoastBatch(id: String) {
    mutableState.update { it.copy(roastBatches = it.roastBatches.filterNot { r -> r.id == id }) }
}

fun CoffeeViewModel.parseBagOcr(photoUri: String?, text: String) {
    val draft = OfflineBagOcrParser.parseBagText(photoUri, text)
    mutableState.update {
        it.copy(
            pendingOcrDraft = draft,
            pendingEquipmentOcrDraft = null,
            photoOcrError = null,
            photoOcrNotice = "Review the manually entered label facts before saving."
        )
    }
}

fun CoffeeViewModel.parseEquipmentOcr(photoUri: String?, text: String) {
    val draft = OfflineEquipmentOcrParser.parseEquipmentText(photoUri, text)
    mutableState.update {
        it.copy(
            pendingEquipmentOcrDraft = draft,
            pendingOcrDraft = null,
            photoOcrError = null,
            photoOcrNotice = "Review the manually entered label facts before saving."
        )
    }
}

fun CoffeeViewModel.scanCatalogPhoto(uri: Uri, target: CatalogPhotoTarget, capturedByCamera: Boolean = false) {
    if (mutableState.value.busy) return
    val job = viewModelScope.launch {
        mutableState.update {
            it.copy(
                busy = true,
                progress = "Recognizing label locally",
                error = null,
                notice = null,
                photoOcrError = null,
                photoOcrNotice = null
            )
        }
        try {
            require(uri.scheme == "content") { "Choose a local image through the photo picker or system camera." }
            val retainedPhotoUri = if (capturedByCamera) {
                uri.toString()
            } else {
                withContext(Dispatchers.IO) { graph.bagPhotoOcr.retainReadGrant(uri) }
                    .takeIf { it }
                    ?.let { uri.toString() }
            }
            val text = graph.bagPhotoOcr.recognize(uri)
            currentCoroutineContext().ensureActive()
            require(text.isNotBlank()) {
                "No readable label text was found. Retake the photo or enter the label text manually."
            }
            val notice = if (retainedPhotoUri == null) {
                "Text recognized offline. This provider did not allow durable photo access, so the image was not attached."
            } else {
                "Label recognized offline. Review every field before saving."
            }
            mutableState.update { state ->
                when (target) {
                    CatalogPhotoTarget.BEAN -> state.copy(
                        pendingOcrDraft = OfflineBagOcrParser.parseBagText(retainedPhotoUri, text),
                        pendingEquipmentOcrDraft = null,
                        photoOcrNotice = notice
                    )
                    CatalogPhotoTarget.EQUIPMENT -> state.copy(
                        pendingOcrDraft = null,
                        pendingEquipmentOcrDraft = OfflineEquipmentOcrParser.parseEquipmentText(retainedPhotoUri, text),
                        photoOcrNotice = notice
                    )
                }
            }
        } catch (cancelled: CancellationException) {
            mutableState.update { it.copy(photoOcrNotice = "Photo scan canceled. No catalog record was saved.") }
            throw cancelled
        } catch (failure: IOException) {
            reportCatalogPhotoError(failure.message ?: PHOTO_OCR_FALLBACK_ERROR)
        } catch (failure: SecurityException) {
            reportCatalogPhotoError(failure.message ?: PHOTO_OCR_FALLBACK_ERROR)
        } catch (failure: IllegalArgumentException) {
            reportCatalogPhotoError(failure.message ?: PHOTO_OCR_FALLBACK_ERROR)
        } catch (failure: IllegalStateException) {
            reportCatalogPhotoError(failure.message ?: PHOTO_OCR_FALLBACK_ERROR)
        } finally {
            mutableState.update { it.copy(busy = false, progress = "") }
            if (photoOcrJob === currentCoroutineContext()[Job]) photoOcrJob = null
        }
    }
    photoOcrJob = job
}

fun CoffeeViewModel.cancelCatalogPhotoOcr() {
    photoOcrJob?.cancel()
}

fun CoffeeViewModel.reportCatalogPhotoError(message: String) {
    mutableState.update { it.copy(photoOcrError = message) }
}

private const val PHOTO_OCR_FALLBACK_ERROR = "Photo could not be read. Select it again or enter text manually."

fun CoffeeViewModel.confirmOcrDraft(confirmedBean: DetailedBeanRecord) = task("Saving reviewed bean label") {
    val photoAvailable = confirmedBean.photoUri?.let { photo ->
        withContext(Dispatchers.IO) { graph.bagPhotoOcr.isReadable(Uri.parse(photo)) }
    } ?: false
    addDetailedBean(confirmedBean.copy(photoUri = confirmedBean.photoUri.takeIf { photoAvailable }))
    mutableState.update {
        it.copy(
            pendingOcrDraft = null,
            photoOcrNotice = if (confirmedBean.photoUri != null && !photoAvailable) {
                "The label image grant expired. Your corrected bean was saved without the attachment; " +
                    "scan or select the image again to attach it."
            } else {
                it.photoOcrNotice
            }
        )
    }
}

fun CoffeeViewModel.confirmEquipmentOcrDraft(confirmedEquipment: EquipmentItem) = task("Saving reviewed equipment label") {
    val photoAvailable = confirmedEquipment.photoUri?.let { photo ->
        withContext(Dispatchers.IO) { graph.bagPhotoOcr.isReadable(Uri.parse(photo)) }
    } ?: false
    addEquipmentItem(confirmedEquipment.copy(photoUri = confirmedEquipment.photoUri.takeIf { photoAvailable }))
    mutableState.update {
        it.copy(
            pendingEquipmentOcrDraft = null,
            photoOcrNotice = if (confirmedEquipment.photoUri != null && !photoAvailable) {
                "The label image grant expired. Your corrected equipment was saved without the attachment; " +
                    "scan or select the image again to attach it."
            } else {
                it.photoOcrNotice
            }
        )
    }
}

fun CoffeeViewModel.dismissOcrDraft() {
    mutableState.update { it.copy(pendingOcrDraft = null) }
}

fun CoffeeViewModel.dismissEquipmentOcrDraft() {
    mutableState.update { it.copy(pendingEquipmentOcrDraft = null) }
}

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
