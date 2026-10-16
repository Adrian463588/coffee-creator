package com.coffeecreator.app.core.data

import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.journal.CafeCupEntry
import com.coffeecreator.app.core.journal.EspressoJournalDetails
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.SensoryObservation

/**
 * Isolated synthetic inputs for deterministic engine tests.
 *
 * Never include these fixtures in app state, seed data, recommendations, backups, or release claims.
 */
object BrewScenarioFixtures {
    const val VERSION = 1

    val recipes: List<Recipe> = listOf(
        // 1. James Hoffmann Ultimate V60
        Recipe(
            id = "recipe-hoffmann-ultimate-v60",
            name = "James Hoffmann Ultimate V60",
            beanId = "bean-ethiopia-yirgacheffe",
            brewMethod = "Pour Over (V60)",
            equipment = "Hario V60 (02), Hario 02 Filter Paper, Gooseneck Kettle, Digital Scale, Cupping Spoon",
            doseGrams = 30.0,
            waterGrams = 500.0,
            beverageMassGrams = 442.0,
            bypassGrams = 0.0,
            agitation = "Synthetic agitation setting for regression tests.",
            measuredTdsPercent = 1.40,
            targetRatio = "1:16.67",
            temperatureCelsius = 100.0,
            pressureBars = null,
            brewTimeSeconds = 210,
            grindSize = "Medium-Fine (~600-700 µm / Comandante 18-20 clicks)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(1, "Pour 60g bloom water in circular motion. Swirl brewer gently until grounds are evenly wet.", 60.0, 45),
                BrewStep(2, "Pour up to 300g total in continuous concentric circles within 30 seconds (60% total water mass).", 300.0, 30),
                BrewStep(
                    3,
                    "Pour remaining water up to 500g total gently to keep slurry level high and thermal mass consistent.",
                    500.0,
                    30
                ),
                BrewStep(
                    4,
                    "Stir once clockwise and once counter-clockwise with spoon. Give one gentle swirl to flatten coffee bed.",
                    500.0,
                    15
                ),
                BrewStep(5, "Allow undisturbed drawdown. Bed should finish flat and uniform.", 500.0, 90)
            )
        ),

        // 2. Tetsu Kasuya 4:6 Method
        Recipe(
            id = "recipe-kasuya-4-6",
            name = "Tetsu Kasuya 4:6 Method",
            beanId = "bean-colombia-huila-pink-bourbon",
            brewMethod = "Pour Over (V60)",
            equipment = "Hario V60-02 (Kasuya Model or Ceramic), Kasuya Filter Paper, Gooseneck Kettle, Digital Scale",
            doseGrams = 20.0,
            waterGrams = 300.0,
            beverageMassGrams = 260.0,
            bypassGrams = 0.0,
            agitation = "None (gravity pulse percolation, allow water to drain completely between pulses)",
            measuredTdsPercent = 1.48,
            targetRatio = "1:15.0",
            temperatureCelsius = 92.0,
            pressureBars = null,
            brewTimeSeconds = 210,
            grindSize = "Coarse (~1000-1100 µm / French press consistency / Comandante 28-30 clicks)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(1, "1st Pour (Acidity): Pour 60g in circular motion. Let water drain through grounds completely.", 60.0, 45),
                BrewStep(2, "2nd Pour (Sweetness): Pour 60g (to 120g total). Let drain completely.", 120.0, 45),
                BrewStep(3, "3rd Pour (Strength 1): Pour 60g (to 180g total). Let drain completely.", 180.0, 45),
                BrewStep(4, "4th Pour (Strength 2): Pour 60g (to 240g total). Let drain completely.", 240.0, 45),
                BrewStep(5, "5th Pour (Strength 3): Pour final 60g (to 300g total). Complete drawdown finishes at 3:30.", 300.0, 30)
            )
        ),

        // 3. Wendelien van Bunnik AeroPress
        Recipe(
            id = "recipe-wendelien-aeropress",
            name = "Wendelien van Bunnik AeroPress Champion Routine",
            beanId = "bean-kenya-nyeri-sl28",
            brewMethod = "AeroPress (Inverted)",
            equipment = "AeroPress, Rinsed Paper Filter, Inverted Setup, Timer, Paddle Stirrer, Decanter Server",
            doseGrams = 30.0,
            waterGrams = 100.0,
            beverageMassGrams = 180.0,
            bypassGrams = 120.0,
            agitation = "20 vigorous back-and-forth paddle stirs in 10 seconds",
            measuredTdsPercent = 1.62,
            targetRatio = "1:7.33",
            temperatureCelsius = 92.0,
            pressureBars = null,
            brewTimeSeconds = 60,
            grindSize = "Coarse (~800-900 µm / table sugar to coarse sea salt / EK43 ~7.5)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(
                    1,
                    "Pour 100g of 92°C water directly onto 30g coarse ground coffee in inverted AeroPress within 10 seconds.",
                    100.0,
                    10
                ),
                BrewStep(2, "Agitate slurry vigorously with paddle stirrer 20 times in 10 seconds (0:10 - 0:20).", 100.0, 10),
                BrewStep(
                    3,
                    "Attach pre-rinsed filter cap, carefully expel residual headspace air until coffee touches filter (0:20 - 0:40).",
                    100.0,
                    20
                ),
                BrewStep(
                    4,
                    "Flip AeroPress onto server and press gently for 20 seconds. Stop immediately upon hearing hiss at 1:00.",
                    100.0,
                    20
                ),
                BrewStep(5, "Add 120g hot water (92°C) bypass directly into server concentrate. Swirl thoroughly and serve.", 220.0, 10)
            )
        ),

        // 4. Scott Rao Chemex Single Pour
        Recipe(
            id = "recipe-scott-rao-chemex",
            name = "Scott Rao Chemex Single Pour",
            beanId = "bean-guatemala-antigua",
            brewMethod = "Chemex",
            equipment = "Chemex 6-Cup, Chemex Bonded Bleached Paper Filter, Gooseneck Kettle, Digital Scale, Cupping Spoon",
            doseGrams = 30.0,
            waterGrams = 510.0,
            beverageMassGrams = 455.0,
            bypassGrams = 0.0,
            agitation = "Spoon excavation during bloom; gentle perimeter stir and Rao swirl at 1:15",
            measuredTdsPercent = 1.38,
            targetRatio = "1:17.0",
            temperatureCelsius = 97.0,
            pressureBars = null,
            brewTimeSeconds = 240,
            grindSize = "Medium-Coarse (~800 µm / Comandante 24-26 clicks)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(1, "Pour 60g bloom water evenly. Immediately dig spoon into bed to wet all grounds thoroughly.", 60.0, 40),
                BrewStep(2, "At 0:40, begin single continuous concentric pour up to 510g total, finishing stream by 1:15.", 510.0, 35),
                BrewStep(
                    3,
                    "Stir once around outer perimeter, then execute gentle rotational Rao spin to wash walls and level bed.",
                    510.0,
                    15
                ),
                BrewStep(4, "Allow uninterrupted gravitational drawdown through bonded Chemex filter. Finishes by 3:45 - 4:00.", 510.0, 150)
            )
        ),

        // 5. James Hoffmann French Press No-Plunge
        Recipe(
            id = "recipe-hoffmann-french-press",
            name = "James Hoffmann French Press No-Plunge",
            beanId = "bean-indonesia-aceh-gayo",
            brewMethod = "French Press",
            equipment = "Standard 8-Cup / 1L Glass French Press, Digital Scale, Two Dessert/Soup Spoons, Timer",
            doseGrams = 30.0,
            waterGrams = 500.0,
            beverageMassGrams = 440.0,
            bypassGrams = 0.0,
            agitation = "Violent initial pour agitation; gentle surface crust break at 4:00 with spoons",
            measuredTdsPercent = 1.36,
            targetRatio = "1:16.67",
            temperatureCelsius = 100.0,
            pressureBars = null,
            brewTimeSeconds = 600,
            grindSize = "Medium (~700-800 µm / standard filter drip grind, NOT coarse)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(1, "Pour 500g rolling boiling water vigorously onto coffee grounds to ensure full saturation.", 500.0, 30),
                BrewStep(2, "Leave immersion steep completely undisturbed for 4 minutes. Do not insert plunger.", 500.0, 210),
                BrewStep(
                    3,
                    "At 4:00, stir the crust until grounds sink; skim foam and floating fines.",
                    500.0,
                    60
                ),
                BrewStep(4, "Wait 5 to 7 additional minutes untouched. Micro-fines settle naturally to the bottom of carafe.", 500.0, 300),
                BrewStep(
                    5,
                    "Place plunger lid gently on top with mesh just below spout (do not plunge!). Decant gently into cup.",
                    500.0,
                    30
                )
            )
        ),

        // 6. Kalita Wave 185 Pulse Brew
        Recipe(
            id = "recipe-kalita-wave-185",
            name = "Kalita Wave 185 Multi-Pulse Brew",
            beanId = "bean-costa-rica-tarrazu",
            brewMethod = "Kalita Wave",
            equipment = "Kalita Wave 185 Stainless/Glass Dripper, Kalita Wave 185 Filters, Gooseneck Kettle, Scale, Server",
            doseGrams = 25.0,
            waterGrams = 390.0,
            beverageMassGrams = 341.0,
            bypassGrams = 0.0,
            agitation = "Gentle swirl during bloom and final pulse to settle flat bed",
            measuredTdsPercent = 1.45,
            targetRatio = "1:15.6",
            temperatureCelsius = 95.0,
            pressureBars = null,
            brewTimeSeconds = 210,
            grindSize = "Medium (~700 µm / Comandante 21-23 clicks)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(1, "Bloom: Pour 50g water evenly over bed. Gentle swirl to saturate grounds.", 50.0, 45),
                BrewStep(2, "Pulse 2: Pour in gentle spirals up to 150g total (+100g).", 150.0, 30),
                BrewStep(3, "Pulse 3: Pour in gentle spirals up to 240g total (+90g).", 240.0, 30),
                BrewStep(4, "Pulse 4: Pour in gentle spirals up to 310g total (+70g).", 310.0, 30),
                BrewStep(5, "Pulse 5: Pour final water up to 390g (+80g). Give one light swirl. Drawdown completes at 3:30.", 390.0, 75)
            )
        ),

        // 7. SCA Standard Modern 9-Bar Double Espresso
        Recipe(
            id = "recipe-sca-double-espresso",
            name = "SCA Standard Modern 9-Bar Double Espresso",
            beanId = "bean-kenya-nyeri-sl28",
            brewMethod = "Espresso",
            equipment = "Espresso machine, 58mm portafilter, precision basket, WDT tool, tamper, scale",
            doseGrams = 18.0,
            waterGrams = 45.0,
            beverageMassGrams = 36.0,
            bypassGrams = 0.0,
            agitation = "WDT mechanical needle distribution before tamping",
            measuredTdsPercent = 10.20,
            targetRatio = "1:2.0",
            temperatureCelsius = 93.0,
            pressureBars = 9.0,
            brewTimeSeconds = 28,
            grindSize = "Fine (~200-250 µm / Espresso calibration)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(1, "Dose 18.0g freshly ground coffee into 18g precision basket. Perform deep WDT distribution.", 0.0, 10),
                BrewStep(2, "Vertical tamp with 15kg force until bed is fully compacted. Flush group head 2 seconds.", 0.0, 5),
                BrewStep(3, "Lock portafilter and engage 9.0 bar pump. First drops appear at 5-7 seconds.", 15.0, 7),
                BrewStep(4, "Extract smooth continuous stream until scale reaches 36.0g liquid mass. Cut pump at 28 seconds.", 45.0, 21)
            )
        ),

        // 8. Cold Brew Kyoto Slow Drip
        Recipe(
            id = "recipe-cold-brew-kyoto",
            name = "Kyoto Style Slow Drip Cold Brew",
            beanId = "bean-rwanda-nyamagabe",
            brewMethod = "Cold Brew",
            equipment = "Kyoto Cold Drip Glass Tower, Paper Filter Top Disc, Ceramic Base Filter, Digital Scale, Ice Water Reservoir",
            doseGrams = 60.0,
            waterGrams = 600.0,
            beverageMassGrams = 525.0,
            bypassGrams = 0.0,
            agitation = "None (gravity droplet percolation)",
            measuredTdsPercent = 2.15,
            targetRatio = "1:10.0",
            temperatureCelsius = 4.0,
            pressureBars = null,
            brewTimeSeconds = 18000,
            grindSize = "Medium-Coarse (~850 µm / Drip coarse)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(1, "Place 60g grounds into column. Pre-wet evenly with 50g cold water. Place paper disc on top.", 50.0, 60),
                BrewStep(2, "Fill upper glass reservoir with 550g mixture of pure water and ice (4°C).", 600.0, 60),
                BrewStep(3, "Adjust precision needle valve to 1 drop every 1.2 seconds (40-45 drops per minute).", 600.0, 180),
                BrewStep(4, "Allow slow dropwise percolation over 5 hours (18,000 seconds) into bottom collection flask.", 600.0, 17700)
            )
        ),

        // 9. Classic Italian Moka Pot (Bialetti technique)
        Recipe(
            id = "recipe-classic-moka-pot",
            name = "Classic Italian Moka Pot (Bialetti Technique)",
            beanId = "bean-indonesia-aceh-gayo",
            brewMethod = "Moka Pot",
            equipment = "Bialetti Moka Express (3-Cup), Stovetop Cooker, Damp Cold Towel / Cold Water Quench Basin",
            doseGrams = 15.0,
            waterGrams = 150.0,
            beverageMassGrams = 112.0,
            bypassGrams = 0.0,
            agitation = "None",
            measuredTdsPercent = 2.55,
            targetRatio = "1:10.0",
            temperatureCelsius = 90.0,
            pressureBars = 1.5,
            brewTimeSeconds = 150,
            grindSize = "Medium-Fine (~400-500 µm / finer than pour-over, coarser than espresso)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(1, "Pour pre-heated water (90°C, 150g) into lower boiler up to bottom of safety pressure valve.", 150.0, 30),
                BrewStep(
                    2,
                    "Fill funnel filter with 15g coffee. Level flat with finger without tamping. Screw upper chamber tightly.",
                    150.0,
                    20
                ),
                BrewStep(3, "Place on low-medium heat with lid open. Coffee stream starts emerging steadily at ~1:30.", 150.0, 70),
                BrewStep(
                    4,
                    "At first sign of bubbling/blonding (~2:30), immediately remove from heat and quench base on cold damp towel.",
                    150.0,
                    30
                )
            )
        ),

        // 10. Lance Hedrick 1-Pour High Extraction V60
        Recipe(
            id = "recipe-lance-hedrick-1-pour",
            name = "Lance Hedrick 1-Pour High Extraction V60",
            beanId = "bean-panama-geisha-esmeralda",
            brewMethod = "Pour Over (V60)",
            equipment = "Hario V60-01/02, Cafec Abaca or Hario Paper Filter, Gooseneck Kettle, Digital Scale",
            doseGrams = 15.0,
            waterGrams = 225.0,
            beverageMassGrams = 195.0,
            bypassGrams = 0.0,
            agitation = "Gentle spiral bloom pours without swirling; energetic center pour provides hydraulic slurry agitation",
            measuredTdsPercent = 1.68,
            targetRatio = "1:15.0",
            temperatureCelsius = 95.0,
            pressureBars = null,
            brewTimeSeconds = 140,
            grindSize = "Medium-Coarse (~800 µm / Comandante 24-26 clicks)",
            notes = "Synthetic scenario note for regression tests.",
            brewSteps = listOf(
                BrewStep(1, "1st Bloom: Pour 45g (3x dose) gently in spiral. Do not swirl or agitate.", 45.0, 30),
                BrewStep(2, "2nd Bloom: Pour 45g (to 90g total) gently in spiral. Allow grounds to degas undisturbed.", 90.0, 30),
                BrewStep(
                    3,
                    "Main pour at 1:00: add 135g to 225g total in the center.",
                    225.0,
                    30
                ),
                BrewStep(
                    4,
                    "Drawdown: Allow uninterrupted gravitational drainage. Total brew time lands between 2:15 and 2:30 (140s).",
                    225.0,
                    50
                )
            )
        )
    )

    val beans: List<CoffeeBean> = listOf(
        CoffeeBean(
            id = "bean-ethiopia-yirgacheffe",
            name = "Ethiopia Yirgacheffe Bedhatu Jibicho Washed",
            country = "Ethiopia",
            region = "Gedeo Zone, Yirgacheffe (Gedeb District)",
            producer = "Bedhatu Jibicho (Robab Bedhadu Farm / Halo Beriti)",
            variety = "Kurume, Dega (Indigenous Heirloom Landraces)",
            process = "Fully Washed (36h underwater fermentation, 14-day parabolic bed drying)",
            altitudeMeters = 2050,
            roastLevel = "Light",
            roastDate = "2026-10-01",
            degasDays = 14,
            densityGramsPerMl = 0.72,
            allergens = "Unknown",
            foodGrade = true,
            isCuratedSeed = false,
            provenance = "Single-producer micro-lot; verified Gedeo origin via Trabocca / Sustainable Harvest direct trade partner."
        ),
        CoffeeBean(
            id = "bean-colombia-huila-pink-bourbon",
            name = "Colombia Huila Monteblanco Pink Bourbon",
            country = "Colombia",
            region = "Acevedo, San Adolfo, Huila",
            producer = "Rodrigo Sanchez Valencia (Finca Monteblanco)",
            variety = "Pink Bourbon (Bourbon Rosado)",
            process = "Washed Cold Aerobic Fermentation (76h controlled thermal fermentation)",
            altitudeMeters = 1750,
            roastLevel = "Light-Medium",
            roastDate = "2026-10-03",
            degasDays = 10,
            densityGramsPerMl = 0.70,
            allergens = "Unknown",
            foodGrade = true,
            isCuratedSeed = false,
            provenance = "Finca Monteblanco direct trade microlot; Rodrigo Sanchez Valencia selective harvesting documentation."
        ),
        CoffeeBean(
            id = "bean-panama-geisha-esmeralda",
            name = "Panama Boquete Hacienda La Esmeralda Geisha",
            country = "Panama",
            region = "Boquete, Chiriqui (Jaramillo Farm)",
            producer = "Peterson Family (Hacienda La Esmeralda)",
            variety = "Geisha (Coffea arabica var. Geisha / Gesha)",
            process = "Fully Washed (Slow dried on raised beds in shaded greenhouse)",
            altitudeMeters = 1750,
            roastLevel = "Light",
            roastDate = "2026-10-05",
            degasDays = 14,
            densityGramsPerMl = 0.74,
            allergens = "Unknown",
            foodGrade = true,
            isCuratedSeed = false,
            provenance = "Hacienda La Esmeralda Private Collection; Jaramillo parcel verified lot certificate."
        ),
        CoffeeBean(
            id = "bean-kenya-nyeri-sl28",
            name = "Kenya Nyeri Gatomboya AA",
            country = "Kenya",
            region = "Nyeri County, Mount Kenya Slopes",
            producer = "Gatomboya Wet Mill (Othaya Farmers Co-operative)",
            variety = "SL28, SL34 (Scott Agricultural Laboratories selections)",
            process = "Fully Washed (Traditional Kenya 72-hour double fermentation with 24-hour clean soak)",
            altitudeMeters = 1800,
            roastLevel = "Light",
            roastDate = "2026-10-02",
            degasDays = 12,
            densityGramsPerMl = 0.73,
            allergens = "Unknown",
            foodGrade = true,
            isCuratedSeed = false,
            provenance = "Othaya Co-operative Society direct auction lot; verified Nyeri AA outturn."
        ),
        CoffeeBean(
            id = "bean-indonesia-aceh-gayo",
            name = "Indonesia Sumatra Aceh Gayo Avatara Anaerobic",
            country = "Indonesia",
            region = "Pegasing, Central Aceh, Sumatra (Lake Laut Tawar)",
            producer = "Hendra Maulizar (Avatara Gayo Mill)",
            variety = "Tim Tim, Ateng (Catimor), Gayo 1",
            process = "Anaerobic Natural (60h sealed cherry fermentation, slow 25-day African bed drying)",
            altitudeMeters = 1500,
            roastLevel = "Medium",
            roastDate = "2026-09-28",
            degasDays = 8,
            densityGramsPerMl = 0.68,
            allergens = "Unknown",
            foodGrade = true,
            isCuratedSeed = false,
            provenance = "Pegasing micro-mill lot; Hendra Maulizar experimental lot documentation."
        ),
        CoffeeBean(
            id = "bean-costa-rica-tarrazu",
            name = "Costa Rica Tarrazu Don Mayo Yellow Honey",
            country = "Costa Rica",
            region = "San Marcos, Tarrazu Valley",
            producer = "Hector Bonilla & Sons (Micro-molino Don Mayo)",
            variety = "Caturra, Red Catuai",
            process = "Yellow Honey (Depulped retaining 50% mucilage, sun-dried on raised African beds)",
            altitudeMeters = 1650,
            roastLevel = "Light-Medium",
            roastDate = "2026-10-04",
            degasDays = 10,
            densityGramsPerMl = 0.71,
            allergens = "Unknown",
            foodGrade = true,
            isCuratedSeed = false,
            provenance = "Don Mayo Micromill direct trade micro-lot; Tarrazu Protected Designation of Origin."
        ),
        CoffeeBean(
            id = "bean-guatemala-antigua",
            name = "Guatemala Antigua Finca Santa Clara",
            country = "Guatemala",
            region = "Panchoy Valley, Antigua",
            producer = "Ricardo Zelaya (Finca Santa Clara)",
            variety = "Bourbon, Caturra",
            process = "Fully Washed (Fermented 30h, washed in spring channels, 100% patio sun-dried)",
            altitudeMeters = 1680,
            roastLevel = "Medium",
            roastDate = "2026-09-29",
            degasDays = 9,
            densityGramsPerMl = 0.69,
            allergens = "Unknown",
            foodGrade = true,
            isCuratedSeed = false,
            provenance = "Ricardo Zelaya Estate; APCA (Antigua Producers Coffee Association) certified genuine Antigua lot."
        ),
        CoffeeBean(
            id = "bean-rwanda-nyamagabe",
            name = "Rwanda Nyamagabe Remera Washed",
            country = "Rwanda",
            region = "Nyamagabe District, Southern Province (Nyungwe Forest Buffer)",
            producer = "Epiphanie Mukashyaka (Buf Coffee / Remera Washing Station)",
            variety = "Red Bourbon (BM 139)",
            process = "Fully Washed (Depulped, 12h dry ferment, 12h soaking, hand-sorted on raised tables)",
            altitudeMeters = 1950,
            roastLevel = "Light-Medium",
            roastDate = "2026-10-02",
            degasDays = 11,
            densityGramsPerMl = 0.72,
            allergens = "Unknown",
            foodGrade = true,
            isCuratedSeed = false,
            provenance = "Buf Coffee / Epiphanie Mukashyaka direct lot; Remera washing station provenance."
        )
    )

    val observations: List<SensoryObservation> = listOf(
        SensoryObservation(
            id = "obs-hoffmann-v60-ethiopia",
            recipeId = "recipe-hoffmann-ultimate-v60",
            aroma = 9,
            acidity = 9,
            sweetness = 8,
            bitterness = 2,
            body = 5,
            clarity = 9,
            finish = 8,
            balance = 9,
            tastingDescriptors = listOf("Jasmine", "Bergamot", "Meyer Lemon", "White Peach", "Earl Grey"),
            descriptorIntensities = mapOf(
                "Jasmine" to 9,
                "Bergamot" to 8,
                "Meyer Lemon" to 8,
                "White Peach" to 7,
                "Earl Grey" to 8
            ),
            notes = "Synthetic scenario note for regression tests.",
            assessor = "Q-Grader Sensory Lab Panel",
            panel = "Calibration Cupping Panel #1",
            timestamp = 1729002000000L
        ),
        SensoryObservation(
            id = "obs-kasuya-pink-bourbon",
            recipeId = "recipe-kasuya-4-6",
            aroma = 8,
            acidity = 8,
            sweetness = 9,
            bitterness = 3,
            body = 6,
            clarity = 8,
            finish = 8,
            balance = 9,
            tastingDescriptors = listOf("Pink Grapefruit", "Papaya", "Raw Honey", "Raspberry", "White Florals"),
            descriptorIntensities = mapOf(
                "Pink Grapefruit" to 8,
                "Papaya" to 9,
                "Raw Honey" to 8,
                "Raspberry" to 7,
                "White Florals" to 7
            ),
            notes = "Synthetic scenario note for regression tests.",
            assessor = "Q-Grader Sensory Lab Panel",
            panel = "Calibration Cupping Panel #1",
            timestamp = 1729005600000L
        ),
        SensoryObservation(
            id = "obs-sca-espresso-kenya",
            recipeId = "recipe-sca-double-espresso",
            aroma = 9,
            acidity = 9,
            sweetness = 8,
            bitterness = 4,
            body = 9,
            clarity = 7,
            finish = 9,
            balance = 8,
            tastingDescriptors = listOf("Blackcurrant", "Grapefruit", "Dark Chocolate", "Cane Sugar", "Red Apple"),
            descriptorIntensities = mapOf(
                "Blackcurrant" to 9,
                "Grapefruit" to 8,
                "Dark Chocolate" to 7,
                "Cane Sugar" to 8,
                "Red Apple" to 7
            ),
            notes = "Synthetic scenario note for regression tests.",
            assessor = "Head Barista & Certified Sensory Judge",
            panel = "Espresso Quality Control",
            timestamp = 1729009200000L
        ),
        SensoryObservation(
            id = "obs-kalita-wave-guatemala",
            recipeId = "recipe-kalita-wave-185",
            aroma = 8,
            acidity = 7,
            sweetness = 9,
            bitterness = 4,
            body = 7,
            clarity = 8,
            finish = 8,
            balance = 9,
            tastingDescriptors = listOf("Dark Cocoa", "Spiced Plum", "Marzipan", "Orange Zest", "Brown Sugar"),
            descriptorIntensities = mapOf(
                "Dark Cocoa" to 8,
                "Spiced Plum" to 8,
                "Marzipan" to 7,
                "Orange Zest" to 6,
                "Brown Sugar" to 8
            ),
            notes = "Synthetic scenario note for regression tests.",
            assessor = "Senior Roaster & Cupper",
            panel = "Calibration Cupping Panel #2",
            timestamp = 1729012800000L
        ),
        SensoryObservation(
            id = "obs-lance-hedrick-panama-geisha",
            recipeId = "recipe-lance-hedrick-1-pour",
            aroma = 10,
            acidity = 9,
            sweetness = 9,
            bitterness = 2,
            body = 5,
            clarity = 10,
            finish = 9,
            balance = 9,
            tastingDescriptors = listOf("Orange Blossom", "Jasmine", "Bergamot", "White Peach", "Lemongrass"),
            descriptorIntensities = mapOf(
                "Orange Blossom" to 10,
                "Jasmine" to 9,
                "Bergamot" to 9,
                "White Peach" to 8,
                "Lemongrass" to 7
            ),
            notes = "Synthetic scenario note for regression tests.",
            assessor = "World Brewers Cup Competitor",
            panel = "Competition Preparation Panel",
            timestamp = 1729016400000L
        )
    )

    val curatedJournalEntries: List<BrewJournalEntry> by lazy {
        recipes.mapIndexed { idx, r ->
            val bean = beans.find { it.id == r.beanId }
            val isEsp = r.brewMethod.contains("Espresso", true)
            val espDetails = if (isEsp) {
                EspressoJournalDetails(
                    targetDoseGrams = r.doseGrams,
                    actualDoseGrams = r.doseGrams + 0.2,
                    targetYieldGrams = r.beverageMassGrams ?: 36.0,
                    actualYieldGrams = (r.beverageMassGrams ?: 36.0) + 1.2,
                    targetDurationSeconds = r.brewTimeSeconds,
                    actualDurationSeconds = r.brewTimeSeconds - 2,
                    puckPrepMethods = listOf("WDT", "Leveler", "Puck Screen"),
                    firstDropSeconds = r.firstDropSeconds ?: 5
                )
            } else {
                null
            }

            BrewJournalEntry(
                id = "journal-entry-${r.id}",
                recipeId = r.id,
                beanId = r.beanId,
                beanName = bean?.name ?: "Curated Specialty Bean",
                brewMethod = r.brewMethod,
                equipment = r.equipment,
                doseGrams = r.doseGrams,
                waterGrams = r.waterGrams,
                beverageMassGrams = r.beverageMassGrams,
                grinderModel = r.grinderModel ?: "Comandante C40 MK4",
                grindSetting = r.grindSize,
                waterTemperatureCelsius = r.temperatureCelsius,
                brewDurationSeconds = r.brewTimeSeconds,
                drawdownSeconds = if (!isEsp) 45 else null,
                measuredTdsPercent = r.measuredTdsPercent,
                rating = 8.5 + (idx % 3) * 0.5,
                tasteNotes = if (isEsp) listOf("Blackcurrant", "Dark Chocolate", "Cane Sugar") else listOf("Floral", "Jasmine", "Citrus"),
                generalNotes = r.notes,
                tags = listOf(r.brewMethod.substringBefore(" "), "Curated", if (isEsp) "DialIn" else "Filter"),
                createdAt = System.currentTimeMillis() - (10 - idx) * 86_400_000L,
                isEspresso = isEsp,
                espressoDetails = espDetails,
                dialInChainId = if (isEsp) "dialin-espresso-kenya" else null
            )
        }
    }

    val curatedCafeCups: List<CafeCupEntry> = listOf(
        CafeCupEntry(
            id = "cafe-tim-wendelboe",
            cafeName = "Tim Wendelboe",
            roaster = "Tim Wendelboe",
            drinkName = "Espresso (Caballero Geisha)",
            originOrBean = "Honduras Geisha",
            pricePaid = "65 NOK",
            rating = 9.8,
            notes = "Jasmine blossoms, sparkling bergamot, crystalline sweetness.",
            timestamp = System.currentTimeMillis() - 86_400_000L * 2
        ),
        CafeCupEntry(
            id = "cafe-fuglen-tokyo",
            cafeName = "Fuglen Tokyo",
            roaster = "Fuglen Coffee Roasters",
            drinkName = "Aeropress Single Origin",
            originOrBean = "Kenya Nyeri Hill",
            pricePaid = "850 JPY",
            rating = 9.2,
            notes = "Bright blackcurrant, juicy red grapefruit, brown sugar finish.",
            timestamp = System.currentTimeMillis() - 86_400_000L * 4
        )
    )
}
