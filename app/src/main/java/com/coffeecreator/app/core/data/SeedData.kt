package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.DocumentChunk
import com.coffeecreator.app.core.model.KnowledgeDocument
import com.coffeecreator.app.core.model.ModelAsset
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.core.model.Recipe

object SeedData {

    val curatedBeans = listOf(
        CoffeeBean(
            id = "bean-eth-01",
            name = "Ethiopia Yirgacheffe Chelchele",
            country = "Ethiopia",
            region = "Gedeo Zone, Yirgacheffe",
            producer = "Smallholders / METAD",
            variety = "Kurume & Dega (Heirloom)",
            process = "Washed",
            altitudeMeters = 2050,
            roastLevel = "Light",
            densityGramsPerMl = 0.72,
            allergens = "None",
            foodGrade = true,
            supplierLimitsNotes = "Curated single origin. Organic certified.",
            isCuratedSeed = true
        ),
        CoffeeBean(
            id = "bean-col-02",
            name = "Colombia Huila Pitalito",
            country = "Colombia",
            region = "Huila",
            producer = "Finca El Paraiso",
            variety = "Castillo, Caturra",
            process = "Washed",
            altitudeMeters = 1750,
            roastLevel = "Medium-Light",
            densityGramsPerMl = 0.70,
            allergens = "None",
            foodGrade = true,
            supplierLimitsNotes = "Standard specialty lot. Fair trade verified.",
            isCuratedSeed = true
        ),
        CoffeeBean(
            id = "bean-ken-03",
            name = "Kenya Nyeri Hill SL28",
            country = "Kenya",
            region = "Nyeri Central Highlands",
            producer = "Nyeri Farmers Cooperative",
            variety = "SL-28 & SL-34",
            process = "Washed",
            altitudeMeters = 1900,
            roastLevel = "Light",
            densityGramsPerMl = 0.74,
            allergens = "None",
            foodGrade = true,
            supplierLimitsNotes = "High phosphoric acidity profile.",
            isCuratedSeed = true
        ),
        CoffeeBean(
            id = "bean-cr-04",
            name = "Costa Rica Tarrazú La Pastora",
            country = "Costa Rica",
            region = "Tarrazú",
            producer = "CoopeTarrazú RL",
            variety = "Catuai",
            process = "Honey",
            altitudeMeters = 1600,
            roastLevel = "Medium",
            densityGramsPerMl = 0.69,
            allergens = "None",
            foodGrade = true,
            supplierLimitsNotes = "High sweetness, yellow honey processing.",
            isCuratedSeed = true
        ),
        CoffeeBean(
            id = "bean-idn-05",
            name = "Indonesia Sumatra Gayo Mountain",
            country = "Indonesia",
            region = "Aceh Gayo",
            producer = "Ketiara Cooperative",
            variety = "Ateng, Tim Tim",
            process = "Wet Hulled",
            altitudeMeters = 1450,
            roastLevel = "Medium-Dark",
            roastDate = "2026-10-01",
            degasDays = 12,
            densityGramsPerMl = 0.67,
            allergens = "None",
            foodGrade = true,
            supplierLimitsNotes = "Heavy body, low acidity, earthy herbal notes.",
            isCuratedSeed = true
        ),
        CoffeeBean(
            id = "bean-blend-06",
            name = "Specialty Espresso House Blend",
            country = "Colombia / Ethiopia",
            region = "Huila & Yirgacheffe",
            producer = "Blend Formulation",
            variety = "Castillo (60%) & Heirloom (40%)",
            process = "Washed Blend",
            altitudeMeters = 1850,
            roastLevel = "Medium",
            roastDate = "2026-10-05",
            degasDays = 8,
            blendComponents = listOf(
                com.coffeecreator.app.core.model.BlendComponent("bean-col-02", "Colombia Huila", 60.0),
                com.coffeecreator.app.core.model.BlendComponent("bean-eth-01", "Ethiopia Chelchele", 40.0)
            ),
            densityGramsPerMl = 0.71,
            allergens = "None",
            foodGrade = true,
            supplierLimitsNotes = "60/40 balanced espresso blend summing to 100%.",
            isCuratedSeed = true
        )
    )

    val sampleRecipes = listOf(
        Recipe(
            id = "rec-01",
            name = "Competition V60 Pour Over",
            beanId = "bean-eth-01",
            brewMethod = "Pour Over (V60)",
            equipment = "Hario V60 02 Plastic, Cafec Abaca Filter",
            doseGrams = 18.0,
            waterGrams = 300.0,
            beverageMassGrams = 270.0,
            bypassGrams = 0.0,
            agitation = "Gentle spiral pour with bloom swirl",
            measuredTdsPercent = 1.30,
            targetRatio = "1:16.67",
            temperatureCelsius = 93.0,
            pressureBars = null,
            brewTimeSeconds = 180,
            grindSize = "Medium-Fine (Comandante 22 clicks)",
            brewSteps = listOf(
                com.coffeecreator.app.core.model.BrewStep(1, "Bloom with 50g water + gentle swirl", 50.0, 45),
                com.coffeecreator.app.core.model.BrewStep(2, "Second pour to 180g (spiral pattern)", 180.0, 45),
                com.coffeecreator.app.core.model.BrewStep(3, "Final pour to 300g (center draw-down)", 300.0, 90)
            ),
            notes = "Bloom 50g 45s, second pour to 180g, third pour to 300g.",
            version = 1
        ),
        Recipe(
            id = "rec-02",
            name = "Sweet & Balanced Aeropress",
            beanId = "bean-col-02",
            brewMethod = "Aeropress (Inverted)",
            equipment = "AeroPress Original, Aesir paper filter",
            doseGrams = 15.0,
            waterGrams = 225.0,
            beverageMassGrams = 195.0,
            bypassGrams = 20.0,
            agitation = "Paddle stir 5 rotations",
            measuredTdsPercent = 1.45,
            targetRatio = "1:15.00",
            temperatureCelsius = 90.0,
            pressureBars = null,
            brewTimeSeconds = 120,
            grindSize = "Medium (Comandante 18 clicks)",
            brewSteps = listOf(
                com.coffeecreator.app.core.model.BrewStep(1, "Add 15g coffee, pour 225g water at 90C", 225.0, 30),
                com.coffeecreator.app.core.model.BrewStep(2, "Stir 5 times with paddle and steep", 225.0, 60),
                com.coffeecreator.app.core.model.BrewStep(3, "Attach cap, invert, and press gently", 225.0, 30)
            ),
            notes = "Inverted method. Stir 5 times, steep 60s, press gently for 30s.",
            version = 1
        )
    )

    val personas = listOf(
        AssistantPersona(
            id = "persona-barista",
            title = "Head Barista",
            description = "Specializes in extraction efficiency, daily brew dials, TDS targeting, and tasting calibration.",
            systemPrompt = "You are an expert Head Barista. You ground your answers in scientific extraction principles. You strictly use calculation tools for math and cite local reference documents. Never fabricate extraction yields or TDS without measurements."
        ),
        AssistantPersona(
            id = "persona-rd",
            title = "Coffee R&D Specialist",
            description = "Formulates custom concentrates, bypass dilutions, water chemistry, and ingredient safety limits.",
            systemPrompt = "You are a Coffee R&D Chemist. You enforce strict food-grade constraints, allergen safety, exact mass fractions, and dilution physics."
        ),
        AssistantPersona(
            id = "persona-sensory",
            title = "Sensory Evaluator",
            description = "Trained in the SCA Coffee Taster's Flavor Wheel, cupping protocols, and structured descriptors.",
            systemPrompt = "You are a licensed Q-Grader and sensory evaluator. You treat flavor wheel descriptors as descriptive classifications, not deterministic physical equations. Always communicate uncertainty."
        ),
        AssistantPersona(
            id = "persona-trainer",
            title = "Coffee Trainer",
            description = "Answers brewing questions citing official manuals, curriculum notes, and SCA brewing standards.",
            systemPrompt = "You are an authoritative Coffee Trainer. You cite local documentation chunks with page numbers and abstain when no evidence is found in imported documents."
        )
    )

    val curatedDocuments = listOf(
        KnowledgeDocument(
            id = "doc-sca-01",
            title = "SCA Coffee Brewing Handbook & Standards",
            sourceUri = "asset://manuals/sca_standards.txt",
            fileType = "TXT",
            pageCount = 3,
            chunkCount = 4,
            contentHash = "hash_sca_brew_2026",
            importedAt = System.currentTimeMillis()
        ),
        KnowledgeDocument(
            id = "doc-water-02",
            title = "Specialty Coffee Water Quality Guidelines",
            sourceUri = "asset://manuals/water_guidelines.txt",
            fileType = "TXT",
            pageCount = 2,
            chunkCount = 3,
            contentHash = "hash_water_chem_2026",
            importedAt = System.currentTimeMillis()
        )
    )

    val curatedChunks = listOf(
        DocumentChunk(
            id = "chunk-sca-01",
            documentId = "doc-sca-01",
            documentTitle = "SCA Coffee Brewing Handbook & Standards",
            pageNumber = 1,
            chunkIndex = 1,
            content = "The Specialty Coffee Brewing Control Chart defines the ideal target extraction yield range as 18.0% to 22.0% with a target TDS concentration of 1.15% to 1.35% for drip brewing."
        ),
        DocumentChunk(
            id = "chunk-sca-02",
            documentId = "doc-sca-01",
            documentTitle = "SCA Coffee Brewing Handbook & Standards",
            pageNumber = 1,
            chunkIndex = 2,
            content = "Water temperature for brewing must be maintained at 90°C to 96°C (195°F to 205°F) at the point of contact with the coffee grounds. Lower temperatures cause under-extraction (sourness), while higher temperatures risk extracting bitter polyphenols."
        ),
        DocumentChunk(
            id = "chunk-sca-03",
            documentId = "doc-sca-01",
            documentTitle = "SCA Coffee Brewing Handbook & Standards",
            pageNumber = 2,
            chunkIndex = 3,
            content = "Brew water-to-coffee ratio is classically defined between 55g/L and 65g/L of water, which corresponds to approximately 1:15 to 1:18 by mass. The golden cup standard benchmark is 60g of coffee per 1000g of water (1:16.67)."
        ),
        DocumentChunk(
            id = "chunk-sca-04",
            documentId = "doc-sca-01",
            documentTitle = "SCA Coffee Brewing Handbook & Standards",
            pageNumber = 3,
            chunkIndex = 4,
            content = "Extraction Yield formula: EY% = (Beverage Mass in grams * Measured TDS%) / Coffee Dose in grams. Grounds retain approximately 1.5 to 2.1 times their weight in water (absorbed water retention)."
        ),
        DocumentChunk(
            id = "chunk-water-01",
            documentId = "doc-water-02",
            pageNumber = 1,
            chunkIndex = 1,
            documentTitle = "Specialty Coffee Water Quality Guidelines",
            content = "Total Dissolved Solids (TDS) of brew water should ideally be between 75 ppm and 150 ppm (target 150 ppm). Calcium hardness target is 50-68 ppm CaCO3 equivalent, and total alkalinity target is 40 ppm CaCO3 equivalent."
        ),
        DocumentChunk(
            id = "chunk-water-02",
            documentId = "doc-water-02",
            pageNumber = 2,
            chunkIndex = 2,
            documentTitle = "Specialty Coffee Water Quality Guidelines",
            content = "Alkalinity acts as a buffer against fruit acids. Too much alkalinity (>80 ppm) flattens pleasant acidity and produces chalky, dull brews. Too little alkalinity (<20 ppm) causes harsh, sour, unbuffered acidity."
        )
    )

    val curatedModels = listOf(
        ModelAsset(
            id = "model-qwen-1.5b",
            name = "Qwen2.5-1.5B-Instruct-GGUF (Mobile Barista)",
            revision = "qwen2.5-1.5b-q4_k_m",
            format = "GGUF Q4_K_M",
            expectedSizeBytes = 986000000L, // ~986 MB
            sha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf",
            license = "Apache-2.0",
            status = ModelStatus.CATALOGUED
        ),
        ModelAsset(
            id = "model-llama3.2-1b",
            name = "Llama-3.2-1B-Instruct-GGUF (Ultra-Light)",
            revision = "llama-3.2-1b-q4_k_m",
            format = "GGUF Q4_K_M",
            expectedSizeBytes = 724000000L, // ~724 MB
            sha256 = "c249a0ec494a7e937d54238e8331165d4b5336e890c9103e6396f432729a4a75",
            downloadUrl = "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf",
            license = "Llama 3.2 Community License",
            status = ModelStatus.CATALOGUED
        )
    )
}
