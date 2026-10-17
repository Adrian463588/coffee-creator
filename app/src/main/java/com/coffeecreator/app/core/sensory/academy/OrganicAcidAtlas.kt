package com.coffeecreator.app.core.sensory.academy

object OrganicAcidAtlas {

    val allAcids: List<OrganicAcidDefinition> = listOf(
        OrganicAcidDefinition(
            id = "acid_citric",
            nameEn = "Citric Acid",
            nameId = "Asam Sitrat",
            chemicalFormula = "C6H8O7",
            molarMassGPerMol = 192.12,
            pKa1 = 3.13,
            typicalSensoryCuesEn = "Bright, tart, mouth-watering lemon/lime sharpness. Fast perception onset, concentrated on the lateral borders of the tongue.",
            typicalSensoryCuesId = "Segar, tajam, memicu air liur seperti jeruk lemon/nipis. Serangan rasa cepat, terasa di tepi lateral lidah.",
            occurrenceInCoffeeEn = "Dominant acid in green and light-roasted Arabica (0.8–1.4% dry mass). Synthesized via plant Krebs cycle. Gradually degrades under prolonged thermal roasting.",
            occurrenceInCoffeeId = "Asam dominan pada biji hijau dan Arabika sangrai terang (0.8–1.4% massa kering). Terdegradasi bertahap pada pemanggangan gelap.",
            safeReferencePreparationNotesEn = "Dissolve 0.50 g of food-grade FCC citric acid monohydrate in 1000 mL demineralized water (<10 ppm TDS). Discard after 24 hours.",
            safeReferencePreparationNotesId = "Larutkan 0.50 g asam sitrat food grade FCC dalam 1000 mL air demineralisasi (<10 ppm TDS). Buang setelah 24 jam.",
            standardTestingConcentrationGramsPerLiter = 0.50,
            maxSafeDailyDosageThresholdGramsPerLiter = 2.00
        ),
        OrganicAcidDefinition(
            id = "acid_malic",
            nameEn = "Malic Acid",
            nameId = "Asam Malat",
            chemicalFormula = "C4H6O5",
            molarMassGPerMol = 134.09,
            pKa1 = 3.40,
            typicalSensoryCuesEn = "Crisp, refreshing green apple and stone fruit tartness. Smooth mid-palate onset with lingering salivary persistence.",
            typicalSensoryCuesId = "Segar renyah khas apel hijau dan buah batu (peach/plum). Terasa di bagian tengah lidah dan bertahan lebih lama dari sitrat.",
            occurrenceInCoffeeEn = "Present in green coffee (0.3–0.6% dry mass). Partially decomposes into fumaric and maleic acids during roasting.",
            occurrenceInCoffeeId = "Terdapat pada biji hijau (0.3–0.6% massa kering). Terurai sebagian menjadi asam fumarat dan maleat selama pemanggangan.",
            safeReferencePreparationNotesEn = "Dissolve 0.40 g of food-grade FCC DL-malic acid in 1000 mL odorless distilled water. Stir until fully transparent.",
            safeReferencePreparationNotesId = "Larutkan 0.40 g asam malat food grade FCC dalam 1000 mL air suling tanpa bau. Aduk hingga larut transparan.",
            standardTestingConcentrationGramsPerLiter = 0.40,
            maxSafeDailyDosageThresholdGramsPerLiter = 1.50
        ),
        OrganicAcidDefinition(
            id = "acid_tartaric",
            nameEn = "Tartaric Acid",
            nameId = "Asam Tartarat",
            chemicalFormula = "C4H6O6",
            molarMassGPerMol = 150.09,
            pKa1 = 2.98,
            typicalSensoryCuesEn = "Hard, drying grape and tamarind sourness. Pronounced astringent tactile sensation along the gums and palate.",
            typicalSensoryCuesId = "Rasa asam tajam khas anggur dan asam jawa dengan sensasi sepat (astringent) mengeringkan gusi dan langit-langit mulut.",
            occurrenceInCoffeeEn = "Minor component in coffee cherries, often concentrated in long natural fermentations and pulped honey lots.",
            occurrenceInCoffeeId = "Komponen minor pada buah kopi, sering terkonsentrasi pada fermentasi natural panjang dan proses honey.",
            safeReferencePreparationNotesEn = "Dissolve 0.40 g of food-grade L-tartaric acid in 1000 mL clean water. Avoid contact with eyes; safe for palate tasting.",
            safeReferencePreparationNotesId = "Larutkan 0.40 g asam L-tartarat food grade dalam 1000 mL air bersih. Hindari kontak mata; aman untuk evaluasi rasa.",
            standardTestingConcentrationGramsPerLiter = 0.40,
            maxSafeDailyDosageThresholdGramsPerLiter = 1.20
        ),
        OrganicAcidDefinition(
            id = "acid_acetic",
            nameEn = "Acetic Acid",
            nameId = "Asam Asetat",
            chemicalFormula = "C2H4O2",
            molarMassGPerMol = 60.05,
            pKa1 = 4.76,
            typicalSensoryCuesEn = "Pungent, volatile vinegar-like aroma and sharp biting sourness. Crosses gustatory and retronasal olfactory pathways.",
            typicalSensoryCuesId = "Aroma menyengat volatil seperti cuka dengan rasa asam tajam menggigit. Menstimulasi reseptor lidah dan penciuman retronasal.",
            occurrenceInCoffeeEn = "Low concentrations (<0.1%) provide pleasant winey complexity. Formed by sucrose pyrolysis during roasting and bacterial fermentation.",
            occurrenceInCoffeeId = "Konsentrasi rendah (<0.1%) memberikan kompleksitas rasa anggur (winey). Terbentuk dari pirolisis sukrosa dan fermentasi bakteri.",
            safeReferencePreparationNotesEn = "Dilute food-grade distilled white vinegar (5% acetic acid): add 1.0 mL vinegar to 500 mL water (~0.1 g/L acetic acid).",
            safeReferencePreparationNotesId = "Encerkan cuka putih food grade (kadar 5%): tambahkan 1.0 mL cuka ke 500 mL air (~0.1 g/L asam asetat).",
            standardTestingConcentrationGramsPerLiter = 0.10,
            maxSafeDailyDosageThresholdGramsPerLiter = 0.50
        ),
        OrganicAcidDefinition(
            id = "acid_lactic",
            nameEn = "Lactic Acid",
            nameId = "Asam Laktat",
            chemicalFormula = "C3H6O3",
            molarMassGPerMol = 90.08,
            pKa1 = 3.86,
            typicalSensoryCuesEn = "Mild, soft, creamy yogurt-like acidity. Imparts a rich, velvety mouthfeel and coating body texture.",
            typicalSensoryCuesId = "Keasaman lembut dan creamy mirip yogurt. Memberikan mouthfeel tebal, bertekstur beludru dan melapisi lidah.",
            occurrenceInCoffeeEn = "Generated during wet fermentation and lactic anaerobic maceration by lactic acid bacteria (LAB) such as Leuconostoc and Lactobacillus.",
            occurrenceInCoffeeId = "Dihasilkan selama fermentasi anaerobik laktat oleh bakteri asam laktat (Leuconostoc dan Lactobacillus).",
            safeReferencePreparationNotesEn = "Dissolve 0.30 g food-grade 88% L-lactic acid in 1000 mL pure water. Store chilled; use within 24 hours.",
            safeReferencePreparationNotesId = "Larutkan 0.30 g asam L-laktat 88% food grade dalam 1000 mL air murni. Simpan dingin; gunakan dalam 24 jam.",
            standardTestingConcentrationGramsPerLiter = 0.30,
            maxSafeDailyDosageThresholdGramsPerLiter = 1.50
        ),
        OrganicAcidDefinition(
            id = "acid_phosphoric",
            nameEn = "Phosphoric Acid (Inorganic)",
            nameId = "Asam Fosfat (Anorganik)",
            chemicalFormula = "H3PO4",
            molarMassGPerMol = 98.00,
            pKa1 = 2.15,
            typicalSensoryCuesEn = "Effervescent, sparkling, cola-like brightness. Extremely fast, clean prickle without lingering sour taste.",
            typicalSensoryCuesId = "Kecerahan effervescent seperti minuman bersoda (cola). Sensasi menggelitik cepat dan bersih tanpa sisa asam yang tertinggal.",
            occurrenceInCoffeeEn = "Derived from soil phosphate uptake in volcanic soils (e.g. Kenya volcanic terroir, SL28/SL34 cultivars). Inorganic mineral acid.",
            occurrenceInCoffeeId = "Berasal dari serapan fosfat tanah vulkanik (seperti di Kenya, kultivar SL28/SL34). Merupakan asam mineral anorganik.",
            safeReferencePreparationNotesEn = "Food-grade 85% phosphoric acid (E338): add 0.05 mL to 1000 mL water (~0.05 g/L). Always dilute into water; never pipette dry.",
            safeReferencePreparationNotesId = "Asam fosfat food grade 85% (E338): tambahkan 0.05 mL ke 1000 mL air (~0.05 g/L). Selalu masukkan asam ke dalam air.",
            standardTestingConcentrationGramsPerLiter = 0.05,
            maxSafeDailyDosageThresholdGramsPerLiter = 0.20
        ),
        OrganicAcidDefinition(
            id = "acid_quinic",
            nameEn = "Quinic Acid",
            nameId = "Asam Kinat",
            chemicalFormula = "C7H12O6",
            molarMassGPerMol = 192.17,
            pKa1 = 3.46,
            typicalSensoryCuesEn = "Astringent, bitter-sour finish typical of dark roasts and prolonged hot-plate holding. Dry puckering mouthfeel.",
            typicalSensoryCuesId = "Finish asam-pahit sepat yang khas pada kopi sangrai gelap atau kopi yang dipanaskan terlalu lama di hot-plate.",
            occurrenceInCoffeeEn = "Formed during roasting via the thermal cleavage of chlorogenic acids (CGA). Increases progressively as roast deepens.",
            occurrenceInCoffeeId = "Terbentuk selama pemanggangan melalui pemecahan termal asam klorogenat (CGA). Meningkat seiring semakin gelapnya sangrai.",
            safeReferencePreparationNotesEn = "Dissolve 0.30 g food-grade D-(-)-quinic acid in 1000 mL water. Mild bitter-sour reference.",
            safeReferencePreparationNotesId = "Larutkan 0.30 g asam D-(-)-kinat food grade dalam 1000 mL air. Referensi rasa asam-pahit yang lembut.",
            standardTestingConcentrationGramsPerLiter = 0.30,
            maxSafeDailyDosageThresholdGramsPerLiter = 1.00
        )
    )

    fun findAcidById(id: String): OrganicAcidDefinition? = allAcids.firstOrNull { it.id.equals(id, ignoreCase = true) }
}
