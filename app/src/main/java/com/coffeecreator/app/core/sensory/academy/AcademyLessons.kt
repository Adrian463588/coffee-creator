package com.coffeecreator.app.core.sensory.academy

object AcademyLessonRegistry {

    val allLessons: List<AcademyLesson> = listOf(
        // ====================================================================
        // EDU-01: Coffee Botany & Provenance
        // ====================================================================
        AcademyLesson(
            id = "EDU-01",
            moduleNumber = 1,
            category = AcademyCurriculumCategory.BOTANY_AND_AGRONOMY,
            titleEn = "Coffee Botany: Taxonomy, Species & Terroir",
            titleId = "Botani Kopi: Taksonomi, Spesies & Terroir",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Distinguish genetic differences between Coffea arabica and Coffea canephora.",
                "Understand the impact of altitude, latitude, and soil composition on seed density.",
                "Avoid sensory stereotyping based solely on variety names."
            ),
            objectivesId = listOf(
                "Membedakan perbedaan genetik antara Coffea arabica dan Coffea canephora.",
                "Memahami pengaruh ketinggian, garis lintang, dan tanah terhadap densitas biji.",
                "Menghindari stereotip rasa yang hanya didasarkan pada nama varietas."
            ),
            coreConceptsEn = listOf(
                "Arabica is allotetraploid (44 chromosomes, self-pollinating); Canephora is diploid (22 chromosomes, outcrossing).",
                "Higher altitude delays cherry maturation, promoting organic acid synthesis and higher sucrose density.",
                "Terroir represents environmental interaction, not a deterministic guarantee of cup profile."
            ),
            coreConceptsId = listOf(
                "Arabika bersifat alotetraploid (44 kromosom, menyerbuk sendiri); Canephora bersifat diploid (22 kromosom," +
                    " penyerbukan silang).",
                "Ketinggian memperlambat pematangan ceri, memicu sintesis asam organik dan akumulasi sukrosa.",
                "Terroir adalah interaksi lingkungan, bukan jaminan deterministik profil cangkir."
            ),
            contentMarkdownEn = ("""
# Coffee Botany and Genetics

Coffee belongs to the genus *Coffea* in the family Rubiaceae. While over 120 species exist, commercial""" +
                """ production centers on:
1. **Coffea arabica** (~60% global volume): Indigenous to Ethiopia and South Sudan. Allotetraploid""" +
                """ ($2n=4x=44$), predominantly self-pollinating. Characterized by higher sucrose (6–9%), lower caffeine""" +
                """ (0.9–1.5%), and diverse organic acid profiles.
2. **Coffea canephora** (Robusta, ~40% global volume): Indigenous to Western/Central sub-Saharan Africa.""" +
                """ Diploid ($2n=2x=22$), outcrossing. Higher caffeine (1.8–2.7%), higher chlorogenic acids (CGA), and robust""" +
                """ disease resistance.

```
+-------------------------------------------------------------+
|              GENUS COFFEA (Rubiaceae)                       |
|          +------------------+------------------+            |
|          |                                     |            |
|   Coffea arabica                        Coffea canephora    |
|   (2n = 44, Allotetraploid)             (2n = 22, Diploid)  |
|   - High Sucrose (6-9%)                 - High Caffeine     |
|   - Delicate Acidity                    - Heavy Body        |
|   - Complex Volatiles                   - High Chlorogenic  |
+-------------------------------------------------------------+
```

Altitude and latitude dictate temperature fluctuations. Diurnal temperature swings at high elevations (>1,500""" +
                """ MASL) decelerate bean respiration, accumulating dense structural carbohydrates and enzymatic precursors that""" +
                """ translate to vibrant malic and citric acidity.
            """).trimIndent(),
            contentMarkdownId = ("""
# Botani dan Genetika Kopi

Kopi termasuk dalam genus *Coffea* famili Rubiaceae. Spesies komersial utama meliputi:
1. **Coffea arabica** (~60% produksi dunia): Asli dari Etiopia dan Sudan Selatan. Alotetraploid ($2n=44$),""" +
                """ menyerbuk sendiri. Sukrosa tinggi (6–9%), kafein rendah (0.9–1.5%), dan profil asam organik kaya.
2. **Coffea canephora** (Robusta, ~40%): Diploid ($2n=22$), penyerbukan silang. Kafein tinggi (1.8–2.7%), asam""" +
                """ klorogenat tinggi, dan tahan penyakit.

Ketinggian dan garis lintang mengatur fluktuasi suhu harian. Suhu dingin malam hari memperlambat respirasi""" +
                """ buah, mengonsentrasikan sukrosa dan prekursor asam organik yang menghasilkan rasa manis dan asam buah segar.
            """).trimIndent(),
            asciiDiagram = """
    [High Elevation >1500m] ---> Slow Maturation ---> Dense Seed + High Sucrose + Complex Acids
    [Low Elevation  <800m]  ---> Fast Maturation ---> Lower Density + Faster Yield
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Allotetraploid",
                    "Alotetraploid",
                    "Organism with four sets of chromosomes derived from hybridization.",
                    "Organisme dengan 4 set kromosom dari hibridisasi."
                ),
                GlossaryEntry(
                    "Cultivar",
                    "Kultivar",
                    "Plant variety produced by selective horticultural breeding.",
                    "Varietas tanaman hasil seleksi budidaya manusia."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU01-1",
                    promptEn = "What is the primary genetic chromosomal distinction of Coffea arabica?",
                    promptId = "Apa perbedaan genetik kromosom utama dari Coffea arabica?",
                    options = listOf(
                        QuizOption("A", "Diploid with 22 chromosomes", "Diploid dengan 22 kromosom", false),
                        QuizOption("B", "Allotetraploid with 44 chromosomes", "Alotetraploid dengan 44 kromosom", true),
                        QuizOption("C", "Triploid sterile hybrid", "Hibrida triploid steril", false),
                        QuizOption("D", "Hexaploid with 66 chromosomes", "Heksaploid dengan 66 kromosom", false)
                    ),
                    explanationEn = "Arabica is allotetraploid (2n=4x=44), resulting from hybridization between C. canephora and C." +
                        " eugenioides.",
                    explanationId = "Arabika adalah alotetraploid (2n=4x=44) hasil persilangan alami antara C. canephora dan C." +
                        " eugenioides."
                )
            ),
            accessibilityNotesEn = "Diagram contrasts genetic traits of Arabica and Canephora in a clear vertical box.",
            accessibilityNotesId = "Diagram membandingkan karakteristik genetik Arabika dan Canephora dalam kotak vertikal.",
            citations = listOf(
                AcademyCitation(
                    "Coffee: Botany, Biochemistry and Production",
                    "Clifford, M. N. & Willson, K. C.",
                    1985,
                    "https://doi.org/10.1007/978-1-4615-6657-1"
                )
            )
        ),

        // ====================================================================
        // EDU-02: From Cherry to Green
        // ====================================================================
        AcademyLesson(
            id = "EDU-02",
            moduleNumber = 2,
            category = AcademyCurriculumCategory.PROCESSING_AND_FERMENTATION,
            titleEn = "From Cherry to Green: Processing & Fermentation Dynamics",
            titleId = "Dari Ceri ke Kopi Beras: Dinamika Pengolahan & Fermentasi",
            estimatedDurationMinutes = 25,
            objectivesEn = listOf(
                "Compare Washed, Natural, and Honey post-harvest processing steps.",
                "Understand microbial fermentation dynamics and organic acid production.",
                "Identify risks of over-fermentation leading to phenolic or acetic taints."
            ),
            objectivesId = listOf(
                "Membandingkan tahapan proses Basah (Washed), Alami (Natural), dan Honey.",
                "Memahami dinamika fermentasi mikrob dan produksi asam organik.",
                "Mengidentifikasi risiko over-fermentasi yang memicu cacat fenolik atau asetat."
            ),
            coreConceptsEn = listOf(
                "Washed removes mucilage before drying; emphasizes bean genetics and terroir acidity.",
                "Natural dries with intact fruit; microbial mass transfer produces esters and heavy body.",
                "Over-fermentation generates excessive acetic acid, ethyl acetate, and butyric faults."
            ),
            coreConceptsId = listOf(
                "Proses Basah membuang lendir sebelum penjemuran; menonjolkan keasaman terroir.",
                "Proses Alami menjemur buah utuh; transfer massa mikrob menghasilkan ester dan body tebal.",
                "Over-fermentasi menghasilkan asam asetat berlebih, etil asetat, dan cacat butirat."
            ),
            contentMarkdownEn = ("""
# Post-Harvest Processing Workflows

```
1. WASHED (Wet Process):
   [Harvest Cherry] -> [Depulping] -> [Mucilage Fermentation (12-36h)] -> [Washing] -> [Drying 10-12%]

2. NATURAL (Dry Process):
   [Harvest Cherry] -> [Flotation / Cleaning] -> [Direct Sun/Bed Drying (15-30 days)] -> [Dry Hulling]

3. HONEY / PULPED NATURAL:
   [Harvest Cherry] -> [Depulping (Mucilage Retained)] -> [Controlled Bed Drying] -> [Hulling]
```

Microorganisms (yeasts such as *Saccharomyces cerevisiae*, lactic acid bacteria like *Leuconostoc*, and acetic""" +
                """ bacteria) ferment the pectin-rich mesocarp. Controlled fermentation enriches cup complexity; uncontrolled""" +
                """ fermentation leads to over-ripe, oniony, or nail-polish-remover (ethyl acetate) taints.
            """).trimIndent(),
            contentMarkdownId = ("""
# Alur Pengolahan Pascapanen

Pemisahan biji kopi dari kulit buah dan lendir (mucilage) sebelum dikeringkan hingga kadar air 10–12%.
1. **Washed (Basah)**: Lendir didegradasi melalui fermentasi air (12–36 jam) lalu dicuci bersih. Menghasilkan""" +
                """ rasa bersih (clean cup) dan keasaman sitrat/malat cerah.
2. **Natural (Kering)**: Buah dijemur utuh di atas para-para (15–30 hari). Mikroba berinteraksi dengan pulp""" +
                """ buah, menghasilkan aroma buah matang, ester, dan body tebal.
3. **Honey**: Kulit buah dikupas, lendir dibiarkan menempel sebagian/seluruhnya selama penjemuran.
            """).trimIndent(),
            asciiDiagram = """
    Washed  : Cherry -> Pulp Removed -> Wash Mucilage -> Clean Bean Drying
    Natural : Cherry -> Intact Fruit Drying -> Delayed Dry Hulling
    Honey   : Cherry -> Pulp Removed -> Sticky Mucilage Drying
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Mucilage",
                    "Musilase",
                    "Sugary, pectin-rich hydrogel layer between coffee skin and parchment.",
                    "Lapisan gel berlendir kaya pektin dan gula antara kulit buah dan kulit tanduk."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU02-1",
                    promptEn = "Which volatile compound is primarily responsible for solvent/vinegar defects in over-fermented coffees?",
                    promptId = "Senyawa volatil apa yang memicu cacat bau pelarut cuka pada kopi over-fermentasi?",
                    options = listOf(
                        QuizOption("A", "Ethyl acetate and excess acetic acid", "Etil asetat dan asam asetat berlebih", true),
                        QuizOption("B", "Linalool and citric acid", "Linalool dan asam sitrat", false),
                        QuizOption("C", "Pyrazine and chlorogenic acid", "Pirazin dan asam klorogenat", false),
                        QuizOption("D", "Furfuryl mercaptan", "Furfuril merkaptan", false)
                    ),
                    explanationEn = "Uncontrolled wild anaerobic fermentation generates high levels of ethyl acetate and acetic acid," +
                        " smelling like vinegar or solvent.",
                    explanationId = "Fermentasi anaerob liar yang tak terkontrol memproduksi etil asetat dan asam asetat berlebih," +
                        " menyerupai bau cuka atau pembersih cat kuku."
                )
            ),
            accessibilityNotesEn = "Diagram details the 3 distinct pathways from fresh cherry to dry parchment.",
            accessibilityNotesId = "Diagram merinci 3 jalur pemrosesan dari ceri segar hingga biji kering.",
            citations = listOf(
                AcademyCitation(
                    "Fermentation and Processing of Coffee",
                    "Schwan, R. F. & Fleet, G. H.",
                    2014,
                    "https://doi.org/10.1201/b17536"
                )
            )
        ),

        // ====================================================================
        // EDU-03: Sensory Science & Psychophysics
        // ====================================================================
        AcademyLesson(
            id = "EDU-03",
            moduleNumber = 3,
            category = AcademyCurriculumCategory.SENSORY_PSYCHOPHYSICS,
            titleEn = "Sensory Science: Stimulus, Thresholds & Cognitive Bias",
            titleId = "Sains Sensori: Stimulus, Ambang Batas & Bias Kognitif",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Distinguish between detection threshold and recognition threshold.",
                "Recognize sensory adaptation, palate fatigue, and halo effect.",
                "Structure blinded environments to minimize psychological bias."
            ),
            objectivesId = listOf(
                "Membedakan ambang deteksi (detection threshold) dan ambang rekognisi (recognition threshold).",
                "Mengenali adaptasi sensori, kelelahan lidah, dan efek halo.",
                "Menyusun lingkungan blind cupping untuk meminimalkan bias psikologis."
            ),
            coreConceptsEn = listOf(
                "Detection threshold: minimum physical stimulus to perceive 'something is present'.",
                "Recognition threshold: higher concentration where quality is correctly identified.",
                "Expectation bias: knowing farm or price alters neural sensory processing."
            ),
            coreConceptsId = listOf(
                "Ambang deteksi: konsentrasi terendah untuk menyadari adanya rangsangan rasa.",
                "Ambang rekognisi: konsentrasi lebih tinggi saat modalitas rasa teridentifikasi spesifik.",
                "Bias ekspektasi: mengetahui harga atau reputasi produsen mengubah persepsi rasa di otak."
            ),
            contentMarkdownEn = ("""
# Sensory Psychophysics

Sensory evaluation measures human biological responses to physical stimuli. Psychophysics defines two""" +
                """ fundamental thresholds:
1. **Absolute Detection Threshold**: The lowest concentration of a tastant or odorant that produces a sensory""" +
                """ response significantly different from pure solvent (water), even if the observer cannot name the taste.
2. **Recognition Threshold**: The higher concentration at which the specific sensory modality (e.g., "sour",""" +
                """ "sweet", "jasmine") is accurately named.

```
+-------------------------------------------------------------+
| STIMULUS CONCENTRATION SPECTRUM                             |
| 0 ppm ------------> Detection Threshold ----> Recognition  |
| (Pure Water)       (Perceive 'something')    (Named taste)  |
+-------------------------------------------------------------+
```

### Cognitive Biases in Cupping
- **Halo Effect**: A dazzling aroma leads cuppers to overestimate body, sweetness, and aftertaste.
- **Position Bias**: Samples tasted first or last on a table receive statistically distorted scores.
- **Expectation Bias**: Knowing an Ethiopian lot is an auction lot elevates subjective affective pleasure.""" +
                """ Blinding is mandatory.
            """).trimIndent(),
            contentMarkdownId = ("""
# Psikofisika Sensori

Evaluasi sensori mengukur respons biologis manusia terhadap rangsangan fisik.
1. **Ambang Deteksi (Detection Threshold)**: Konsentrasi paling rendah di mana indra mendeteksi ada rasa,""" +
                """ meski belum bisa menyebut namanya.
2. **Ambang Rekognisi (Recognition Threshold)**: Konsentrasi lebih tinggi saat rasa dapat dinamai secara pasti""" +
                """ (misalnya: "asam sitrat", "manis").

### Bias Kognitif Saat Cupping
- **Efek Halo**: Aroma kering yang sangat harum membuat evaluator bias melebih-lebihkan skor rasa, kebersihan,""" +
                """ dan body.
- **Bias Posisi**: Cangkir pertama dan terakhir pada meja sering mendapat perhatian berbeda.
- **Bias Ekspektasi**: Mengetahui nama varietas bergengsi atau harga lelang mempengaruhi otak. Itulah sebabnya""" +
                """ blind cupping mutlak diperlukan.
            """).trimIndent(),
            asciiDiagram = """
    Physical Stimulus -> Olfactory/Gustatory Receptors -> Neural Transduction -> Cognitive Interpretation
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Psychophysics",
                    "Psikofisika",
                    "Scientific study of relationships between physical stimuli and sensory sensations.",
                    "Kajian ilmiah hubungan antara stimulus fisik dan sensasi sensori."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU03-1",
                    promptEn = "What is the difference between detection threshold and recognition threshold?",
                    promptId = "Apa perbedaan antara ambang deteksi dan ambang rekognisi?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "Detection perceives a stimulus is present; recognition identifies its specific quality",
                            "Deteksi menyadari adanya rangsangan; rekognisi mengidentifikasi jenis rasanya",
                            true
                        ),
                        QuizOption(
                            "B",
                            "Recognition occurs at lower concentration than detection",
                            "Rekognisi terjadi pada konsentrasi lebih rendah dari deteksi",
                            false
                        ),
                        QuizOption("C", "Detection threshold is always zero", "Ambang deteksi selalu bernilai nol", false),
                        QuizOption("D", "They are identical biological terms", "Keduanya adalah istilah biologis yang sama", false)
                    ),
                    explanationEn = "Detection requires less stimulus to notice a change from baseline water; recognition requires more" +
                        " concentration to label the specific taste.",
                    explanationId = "Deteksi membutuhkan konsentrasi lebih rendah untuk merasakan perbedaan dari air murni; rekognisi" +
                        " butuh konsentrasi lebih tinggi untuk mengenali jenisnya."
                )
            ),
            accessibilityNotesEn = "Diagram demonstrates linear progression from physical stimulus to cognitive interpretation.",
            accessibilityNotesId = "Diagram menunjukkan alur linear dari stimulus fisik ke persepsi kognitif.",
            citations = listOf(
                AcademyCitation(
                    "Sensory Evaluation of Food: Principles and Practices",
                    "Lawless, H. T. & Heymann, H.",
                    2010,
                    "https://doi.org/10.1007/978-1-4419-6488-5"
                )
            )
        ),

        // ====================================================================
        // EDU-04: Olfactory Anatomy
        // ====================================================================
        AcademyLesson(
            id = "EDU-04",
            moduleNumber = 4,
            category = AcademyCurriculumCategory.PHYSIOLOGY_AND_ANATOMY,
            titleEn = "Olfactory Anatomy: Orthonasal vs Retronasal Pathways",
            titleId = "Anatomi Penciuman: Jalur Ortonasal vs Retronasal",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Differentiate orthonasal smelling from retronasal aroma perception.",
                "Understand olfactory receptor neurons and combinatorial odor coding.",
                "Master the cupping slurp technique to maximize retronasal aerosolization."
            ),
            objectivesId = listOf(
                "Membedakan penciuman ortonasal dan persepsi aroma retronasal.",
                "Memahami neuron reseptor olfaktori dan pengkodean kombinatorial bau.",
                "Menguasai teknik slurp cupping untuk memaksimalkan aerasi retronasal."
            ),
            coreConceptsEn = listOf(
                "Orthonasal: breathing in through nostrils (dry fragrance, wet crust aroma).",
                "Retronasal: volatile compounds travel from oral cavity up pharynx during swallowing/slurping.",
                "Over 80% of perceived 'flavor' is actually retronasal olfaction, not tongue taste."
            ),
            coreConceptsId = listOf(
                "Ortonasal: menghirup aroma lewat lubang hidung (fragrance bubuk kering, aroma crust).",
                "Retronasal: uap volatil naik dari rongga mulut melalui faring saat menelan/menyeruput.",
                "Lebih dari 80% yang kita sebut 'rasa' (flavor) sebenarnya adalah penciuman retronasal, bukan reseptor lidah."
            ),
            contentMarkdownEn = ("""
# Orthonasal vs Retronasal Olfaction

Human olfactory epithelium contains approximately 6 million olfactory receptor neurons expressing ~400""" +
                """ functional receptor types.

```
       [Orthonasal Path] (Inhalation through nose)
            |
            v
   [Olfactory Bulb & Epithelium] <--- [Retronasal Path] (Aerosol from oral cavity)
                                            ^
                                            |
                                  [Tongue & Pharynx] (Slurp / Swallow)
```

1. **Orthonasal Olfaction**: Occurs when sniffing the dry grounds (Fragrance) or the crust of brewing coffee""" +
                """ (Aroma). Volatiles enter directly through the external nares.
2. **Retronasal Olfaction**: Occurs during ingestion. Vigorous slurping atomizes the coffee liquid into""" +
                """ micro-droplets inside the mouth, warming volatiles and expelling them up through the nasopharynx to the""" +
                """ olfactory cleft during expiration.
            """).trimIndent(),
            contentMarkdownId = ("""
# Penciuman Ortonasal vs Retronasal

Mukosa penciuman manusia memiliki sekitar 6 juta neuron reseptor olfaktori.
1. **Penciuman Ortonasal**: Menghirup aroma langsung melalui hidung. Ini digunakan saat mencium bubuk kopi""" +
                """ kering (Fragrance) dan lapisan atas cangkir basah (Aroma).
2. **Penciuman Retronasal**: Terjadi di dalam rongga mulut. Seruputan (slurp) yang kuat memecah cairan kopi""" +
                """ menjadi butiran aerosol halus, menghangatkan senyawa volatil, dan mendorongnya naik melewati faring menuju""" +
                """ rongga hidung belakang saat mengembuskan napas.
            """).trimIndent(),
            asciiDiagram = """
    Nose -> Nasal Cavity -> Olfactory Bulb (Orthonasal)
    Mouth -> Pharynx -> Nasopharynx -> Olfactory Bulb (Retronasal)
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Retronasal",
                    "Retronasal",
                    "Perception of odors originating from oral cavity during eating/drinking.",
                    "Persepsi aroma yang berasal dari rongga mulut saat makan/minum."
                ),
                GlossaryEntry(
                    "Orthonasal",
                    "Ortonasal",
                    "Perception of odors inhaled through the external nostrils.",
                    "Persepsi bau yang dihirup melalui lubang hidung luar."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU04-1",
                    promptEn = "Why do professional coffee tasters slurp coffee vigorously from the cupping spoon?",
                    promptId = "Mengapa pencicip kopi profesional menyeruput kopi dengan kuat dari sendok cupping?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "To aerate the liquid and drive volatile compounds into the retronasal pathway",
                            "Untuk mengaerasikan cairan dan mendorong senyawa volatil ke jalur retronasal",
                            true
                        ),
                        QuizOption(
                            "B",
                            "To cool the coffee instantly to room temperature",
                            "Untuk mendinginkan kopi seketika ke suhu ruang",
                            false
                        ),
                        QuizOption("C", "To dilute coffee with mouth saliva", "Untuk mengencerkan kopi dengan air liur", false),
                        QuizOption("D", "To prevent burning the tongue tip", "Agar ujung lidah tidak tersengat panas", false)
                    ),
                    explanationEn = "Slurping atomizes the coffee liquid across the palate and propels aromatic volatiles up the pharynx" +
                        " into the nasal cavity for retronasal perception.",
                    explanationId = "Seruputan kuat menyemprotkan aerosol kopi ke seluruh lidah dan mendorong uap aromatik melewati" +
                        " faring ke rongga hidung belakang."
                )
            ),
            accessibilityNotesEn = "Diagram visualizes both airflow routes converging at the olfactory epithelium.",
            accessibilityNotesId = "Diagram menggambarkan kedua jalur aliran udara bertemu di epitelium olfaktori.",
            citations = listOf(
                AcademyCitation("Neurobiology of Sensation and Reward", "Shepherd, G. M.", 2011, "https://doi.org/10.1201/b10886")
            )
        ),

        // ====================================================================
        // EDU-05: Gustatory Anatomy & The Five Basic Tastes (SCIENTIFIC CORRECTION)
        // ====================================================================
        AcademyLesson(
            id = "EDU-05",
            moduleNumber = 5,
            category = AcademyCurriculumCategory.PHYSIOLOGY_AND_ANATOMY,
            titleEn = "Gustatory Anatomy: The Five Basic Tastes & Acidity Demystified",
            titleId = "Anatomi Pengecapan: Lima Rasa Dasar & Meluruskan Makna Asam",
            estimatedDurationMinutes = 25,
            objectivesEn = listOf(
                "Identify the five universally recognized basic tastes: sweet, sour, salty, bitter, and umami.",
                "Understand why 'Acid' is NOT a separate sixth basic taste.",
                "Distinguish physiological sourness (proton detection) from perceived complex acidity.",
                "Debunk the outdated tongue-map myth."
            ),
            objectivesId = listOf(
                "Mengidentifikasi lima rasa dasar yang diakui secara ilmiah: manis, asam, asin, pahit, dan umami.",
                "Memahami mengapa 'Asam' BUKAN rasa dasar keenam yang berdiri sendiri.",
                "Membedakan rasa asam fisiologis (deteksi proton H+) dari persepsi keasaman kompleks.",
                "Membantah mitos peta lidah kuno (tongue map myth)."
            ),
            coreConceptsEn = listOf(
                "There are FIVE basic tastes: Sweet (T1R2+T1R3), Umami (T1R1+T1R3), Bitter (T2Rs), Salty (ENaC), Sour (OTOP1).",
                "Sourness is the taste modality detected via OTOP1 ion channels responding to free hydrogen ions (H+).",
                "Acidity in coffee is a holistic sensory experience combining gustatory sourness, organic acid character," +
                    " saliva release, and retronasal fruit aromatics.",
                "All areas of the tongue with taste buds can perceive all five tastes."
            ),
            coreConceptsId = listOf(
                "Terdapat LIMA rasa dasar: Manis (T1R2+T1R3), Umami (T1R1+T1R3), Pahit (T2Rs), Asin (ENaC), Asam (OTOP1).",
                "Rasa asam adalah modalitas pengecapan yang dideteksi kanal ion OTOP1 saat merespons ion hidrogen bebas (H+).",
                "Keasaman (acidity) pada kopi adalah pengalaman sensori menyeluruh: rasa asam lidah + karakter asam organik +" +
                    " air liur + aroma buah retronasal.",
                "Semua bagian lidah yang memiliki kuncup pengecap mampu merasakan kelima rasa dasar."
            ),
            contentMarkdownEn = ("""
# The Five Basic Tastes & The Nature of Acidity

Scientific consensus recognizes **FIVE basic tastes**:
1. **Sweetness**: Triggered by carbohydrates and sugars (detected by T1R2 + T1R3 G-protein-coupled receptors).
2. **Sourness**: Triggered by free hydrogen ions (H+) through proton-selective ion channels (**OTOP1**).
3. **Saltiness**: Triggered by sodium ions (Na+) through epithelial sodium channels (ENaC).
4. **Bitterness**: Triggered by alkaloids and toxic compounds (detected by ~25 distinct T2R receptors).
5. **Umami**: Triggered by L-glutamate and 5'-ribonucleotides (detected by T1R1 + T1R3 receptors).

```
+-------------------------------------------------------------+
| THE FIVE BASIC GUSTATORY TASTES                             |
| 1. SWEET   (Sucrose / Sugars)                               |
| 2. SOUR    (H+ Protons / Organic Acids)                     |
| 3. SALTY   (Sodium Ions Na+)                                |
| 4. BITTER  (Caffeine / Quinine / Chlorogenic lactones)      |
| 5. UMAMI   (Monosodium Glutamate / Amino Acids)             |
|                                                             |
| * "ACID" IS NOT A 6TH TASTE!                                |
|   Sourness is the basic taste; Acidity is a multi-modal     |
|   flavor sensation encompassing taste, saliva, and aroma.   |
+-------------------------------------------------------------+
```

### Dispelling the "Tongue Map" Myth
The 1901 D.P. Hänig diagram misinterpreted relative sensitivities. Modern physiology proves that taste buds""" +
                """ containing all receptor types are distributed across fungiform, foliate, and circumvallate papillae""" +
                """ throughout the tongue, soft palate, and epiglottis.
            """).trimIndent(),
            contentMarkdownId = ("""
# Lima Rasa Dasar & Hakikat Keasaman Kopi

Konsensus ilmiah fisiologi mengakui **LIMA rasa dasar**:
1. **Manis**: Dipicu sukrosa dan karbohidrat (reseptor T1R2 + T1R3).
2. **Asam (Sour)**: Dipicu ion hidrogen bebas (H+) melalui kanal ion proton **OTOP1**.
3. **Asin**: Dipicu ion natrium (Na+) melalui kanal natrium epitel.
4. **Pahit**: Dipicu alkaloid, kafein, dan lakton asam klorogenat (reseptor T2Rs).
5. **Umami**: Dipicu asam amino L-glutamat (reseptor T1R1 + T1R3).

### Koreksi Ilmiah: Mengapa "Acid" Bukan Rasa Keenam?
Kadang orang menyebut "asam" dan "acid" seolah dua hal berbeda. Sebenarnya rasa dasarnya adalah **Sour (rasa""" +
                """ asam)**. Sedangkan istilah **Acidity** dalam kopi adalah persepsi multimodal yang menggabungkan rasa asam di""" +
                """ lidah, pelepasan air liur segar, dan aroma buah-buahan retronasal.

### Runtuhnya Mitos Peta Lidah
Mitos bahwa manis hanya di ujung lidah dan pahit di pangkal telah dibantah secara medis. Semua kuncup pengecap""" +
                """ di seluruh lidah mampu merasakan kelima rasa dasar.
            """).trimIndent(),
            asciiDiagram = """
    Lidah Manusia:
    Semua papila (fungiform, foliate, circumvallate) merasakan:
    [MANIS + ASAM + ASIN + PAHIT + UMAMI] secara merata.
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "OTOP1",
                    "OTOP1",
                    "Otopetrin-1: proton-selective ion channel mediating cellular sour taste perception.",
                    "Kanal ion selektif proton penerima rasa asam pada sel pengecap."
                ),
                GlossaryEntry(
                    "Basic Taste",
                    "Rasa Dasar",
                    "Primary gustatory modality recognized by dedicated cellular receptors.",
                    "Modalitas pengecapan utama yang didukung oleh reseptor seluler spesifik."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU05-1",
                    promptEn = "Is 'Acid' a distinct sixth basic taste alongside sweet, sour, salty, bitter, and umami?",
                    promptId = "Apakah 'Acid/Keasaman' adalah rasa dasar keenam yang terpisah dari manis, asam, asin, pahit, dan umami?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "No, sourness is the basic taste; acidity is a culinary/sensory perception of sourness and aroma",
                            "Tidak, rasa dasarnya adalah rasa asam; acidity adalah persepsi sensori asam dan aroma",
                            true
                        ),
                        QuizOption(
                            "B",
                            "Yes, coffee contains unique acid receptors separate from sour receptors",
                            "Ya, kopi memiliki reseptor asam terpisah dari reseptor masam",
                            false
                        ),
                        QuizOption(
                            "C",
                            "Yes, umami replaced salt, making acid the fifth taste",
                            "Ya, umami menggantikan garam sehingga acid jadi rasa kelima",
                            false
                        ),
                        QuizOption(
                            "D",
                            "No, there are only four basic tastes in science",
                            "Tidak, hanya ada empat rasa dasar dalam sains",
                            false
                        )
                    ),
                    explanationEn = "There are exactly five basic tastes: sweet, sour, salty, bitter, and umami. 'Acid' is the English" +
                        " chemical term for proton donors perceived as the sour taste.",
                    explanationId = "Terdapat lima rasa dasar yang terbukti secara ilmiah: manis, asam, asin, pahit, dan umami. 'Acid'" +
                        " merujuk pada senyawa asam yang direspons oleh reseptor rasa asam."
                )
            ),
            accessibilityNotesEn = "Diagram emphasizes that all 5 tastes are distributed across the entire tongue without localized zones.",
            accessibilityNotesId = "Diagram menegaskan kelima rasa dasar tersebar merata di seluruh permukaan lidah.",
            citations = listOf(
                AcademyCitation(
                    "The Cells and Logic for Mammalian Sour Taste Detection",
                    "Tuo, J., et al.",
                    2019,
                    "https://doi.org/10.1016/j.cell.2019.08.020"
                )
            )
        ),

        // ====================================================================
        // EDU-06: Trigeminal & Mouthfeel
        // ====================================================================
        AcademyLesson(
            id = "EDU-06",
            moduleNumber = 6,
            category = AcademyCurriculumCategory.PHYSIOLOGY_AND_ANATOMY,
            titleEn = "Trigeminal Somatosensation & Coffee Mouthfeel Texture",
            titleId = "Somatosensasi Trigeminal & Tekstur Mouthfeel Kopi",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Understand the role of the trigeminal nerve (Cranial Nerve V) in mouthfeel.",
                "Differentiate astringency, viscosity, thermal perception, and body texture.",
                "Identify tactile defects: chalky, drying, and harsh finishes."
            ),
            objectivesId = listOf(
                "Memahami peran saraf trigeminal (Saraf Kranial V) dalam persepsi mouthfeel.",
                "Membedakan astringensi (sepat), viskositas, persepsi suhu, dan tekstur body.",
                "Mengidentifikasi cacat sensasi taktil: berkapur, mengeringkan, dan kasar."
            ),
            coreConceptsEn = listOf(
                "Mouthfeel is tactile somatosensation mediated by mechanoreceptors and the trigeminal nerve.",
                "Viscosity correlates with extracted soluble solids (TDS) and suspended insoluble micro-colloids.",
                "Astringency is the precipitation of salivary proline-rich proteins by polyphenols."
            ),
            coreConceptsId = listOf(
                "Mouthfeel adalah sensasi rabaan fisik yang dimediasi oleh saraf trigeminal.",
                "Viskositas berkorelasi dengan padatan terlarut (TDS) dan koloid minyak tersuspensi.",
                "Astringensi (sepat) terjadi saat polifenol mengendapkan protein pelumas air liur, menimbulkan rasa kering."
            ),
            contentMarkdownEn = ("""
# Mouthfeel and Trigeminal Chemesthesis

Mouthfeel is not perceived by taste buds, but by mechanoreceptors, thermoreceptors, and nociceptors innervated""" +
                """ by the **trigeminal nerve (CN V)**:
- **Viscosity**: Perceived fluid resistance between tongue and hard palate, driven by extracted carbohydrates""" +
                """ and suspended micro-colloids.
- **Body Texture Spectrum**: Watery -> Tea-like -> Silky -> Velvety -> Creamy -> Syrupy -> Oily.
- **Astringency**: A puckering, drying tactile sensation caused by polyphenolic tannins binding to and""" +
                """ precipitating lubricating salivary proteins.
            """).trimIndent(),
            contentMarkdownId = ("""
# Mouthfeel dan Saraf Trigeminal

Mouthfeel bukanlah rasa di lidah, melainkan sensasi fisik rabaan yang dirasakan oleh saraf trigeminal:
- **Viskositas**: Kekentalan cairan antara lidah dan langit-langit mulut.
- **Spektrum Tekstur**: Encer -> Seperti Teh -> Sutra -> Beludru -> Berisi (Creamy) -> Kental Sirup ->""" +
                """ Berminyak.
- **Astringensi (Sepat)**: Sensasi kering kesat akibat polifenol mengendapkan protein pelumas air liur.
            """).trimIndent(),
            asciiDiagram = ("""
    Soluble Solids (TDS) + Suspended Lipids ---> Tactile Mechanoreceptors (CN V) ---> Perceived Body /""" +
                """ Viscosity
            """).trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Astringency",
                    "Astringensi",
                    "Tactile drying and puckering sensation caused by loss of oral lubrication.",
                    "Sensasi kesat dan kering di mulut akibat hilangnya pelumas air liur."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU06-1",
                    promptEn = "What causes the drying, puckering sensation known as astringency in coffee?",
                    promptId = "Apa yang menyebabkan sensasi kesat mengeringkan (astringensi) pada kopi?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "Precipitation of lubricating salivary proteins by polyphenols",
                            "Pengendapan protein pelumas air liur oleh senyawa polifenol",
                            true
                        ),
                        QuizOption(
                            "B",
                            "Excessive sugar concentration coating the tongue",
                            "Konsentrasi gula berlebih yang melapisi lidah",
                            false
                        ),
                        QuizOption("C", "Extreme cold temperature stimulating cold receptors", "Suhu yang terlalu dingin", false),
                        QuizOption("D", "Activation of umami receptors", "Aktivasi reseptor umami", false)
                    ),
                    explanationEn = "Polyphenols bind to salivary proline-rich proteins, causing them to aggregate and precipitate," +
                        " stripping away the lubricating film inside the oral cavity.",
                    explanationId = "Senyawa polifenol mengikat protein air liur dan mengendapkannya, menghilangkan lapisan pelicin di" +
                        " dalam mulut."
                )
            ),
            accessibilityNotesEn = "Diagram tracks mechanical force to trigeminal tactile feedback.",
            accessibilityNotesId = "Diagram melacak gaya mekanik cairan ke saraf peraba lidah.",
            citations = listOf(
                AcademyCitation("Mechanisms of Astringency", "Bate-Smith, E. C.", 1973, "https://doi.org/10.1021/jf60189a022")
            )
        ),

        // ====================================================================
        // EDU-07: Organic Acid Chemistry
        // ====================================================================
        AcademyLesson(
            id = "EDU-07",
            moduleNumber = 7,
            category = AcademyCurriculumCategory.ORGANIC_ACIDS_CHEMISTRY,
            titleEn = "Organic Acid Chemistry: Molecules, Perception & References",
            titleId = "Kimia Asam Organik: Molekul, Karakter & Standar Kalibrasi",
            estimatedDurationMinutes = 25,
            objectivesEn = listOf(
                "Identify the major organic acids in coffee: Citric, Malic, Tartaric, Acetic, Lactic, Phosphoric, Quinic.",
                "Understand why perceived acidity does not directly identify a single chemical compound.",
                "Review safe food-grade calibration reference preparation protocols."
            ),
            objectivesId = listOf(
                "Mengenali asam organik utama pada kopi: Sitrat, Malat, Tartarat, Asetat, Laktat, Fosfat, Kinat.",
                "Memahami mengapa sensasi asam tidak serta merta membuktikan keberadaan satu molekul murni tunggal.",
                "Mempelajari protokol penyiapan larutan kalibrasi food-grade yang aman."
            ),
            coreConceptsEn = listOf(
                "Acids differ in molecular structure, number of carboxyl groups, and dissociation constants (pKa).",
                "Citric: bright lemon; Malic: crisp green apple; Tartaric: drying grape; Acetic: pungent vinegar.",
                "Never ingest industrial laboratory reagents; use strictly food-grade (FCC/USP) certified standards."
            ),
            coreConceptsId = listOf(
                "Asam berbeda dalam struktur molekul, jumlah gugus karboksil, dan konstanta disosiasi (pKa).",
                "Sitrat: lemon cerah; Malat: apel hijau segar; Tartarat: anggur kesat; Asetat: cuka tajam.",
                "Dilarang mencicipi bahan kimia reagen laboratorium; hanya gunakan bahan bersertifikat food grade (FCC/USP)."
            ),
            contentMarkdownEn = ("""
# Organic Acids in Brewed Coffee

Organic and inorganic acids determine the vibrant brightness of specialty coffee:
1. **Citric Acid** (C6H8O7): Dominant in light roasts, sharp lemon salivary stimulus.
2. **Malic Acid** (C4H6O5): Stone fruit and crisp green apple character.
3. **Phosphoric Acid** (H3PO4): Inorganic volcanic acid creating effervescent brightness.
4. **Acetic Acid** (C2H4O2): Volatile vinegar aroma formed by fermentation and sugar pyrolysis.
5. **Lactic Acid** (C3H6O3): Soft, buttery, creamy acidity.
6. **Quinic Acid** (C7H12O6): Bitter-sour astringency derived from thermal breakdown of chlorogenic acids""" +
                """ during roasting.

> [!WARNING]
> Sensory observations reflect human perception, NOT laboratory mass spectrometry. Never claim that a""" +
                """ lemon-like sourness guarantees high citric acid without laboratory chromatography.
            """).trimIndent(),
            contentMarkdownId = ("""
# Asam Organik dalam Seduhan Kopi

Asam menentukan kecerahan rasa kopi specialty:
1. **Asam Sitrat**: Dominan pada sangrai terang, memberikan kesegaran lemon yang merangsang air liur.
2. **Asam Malat**: Memberikan karakter buah apel hijau dan pir yang renyah.
3. **Asam Fosfat**: Asam anorganik dari tanah vulkanik yang memicu sensasi sparkling (effervescent).
4. **Asam Asetat**: Asam volatil khas cuka dari fermentasi dan pirolisis gula.
5. **Asam Laktat**: Asam lembut dan creamy khas yogurt.
6. **Asam Kinat**: Asam-pahit yang terbentuk dari peruraian asam klorogenat saat sangrai gelap.

> [!WARNING]
> Pengecapan lidah adalah persepsi manusia, bukan alat kromatografi laboratorium. Jangan pernah mengklaim rasa""" +
                """ lemon pasti berarti kandungan sitrat tinggi tanpa uji lab kimia.
            """).trimIndent(),
            asciiDiagram = """
    Green Bean (Citric + Malic + Chlorogenic) ---> Roasting Heat ---> Quinic + Acetic Formed; Citric Degraded
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "pKa",
                    "pKa",
                    "Logarithmic acid dissociation constant measuring acid strength.",
                    "Ukuran kuantitatif kekuatan asam dalam mendonasikan proton."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU07-1",
                    promptEn = "Which acid increases during roasting as chlorogenic acid breaks down?",
                    promptId = "Asam apa yang kadarnya justru meningkat saat sangrai akibat terurainya asam klorogenat?",
                    options = listOf(
                        QuizOption("A", "Quinic acid", "Asam kinat", true),
                        QuizOption("B", "Citric acid", "Asam sitrat", false),
                        QuizOption("C", "Malic acid", "Asam malat", false),
                        QuizOption("D", "Phosphoric acid", "Asam fosfat", false)
                    ),
                    explanationEn = "During roasting, chlorogenic acids (CGA) thermally cleave into quinic acid and caffeic acid," +
                        " increasing perceived bitterness and astringency.",
                    explanationId = "Selama penyangraian, asam klorogenat terurai menjadi asam kinat dan asam kafeat, meningkatkan rasa" +
                        " pahit dan kesat."
                )
            ),
            accessibilityNotesEn = "Diagram contrasts acid transformation from green seed to roasted bean.",
            accessibilityNotesId = "Diagram membandingkan transformasi asam dari biji hijau ke biji sangrai.",
            citations = listOf(
                AcademyCitation(
                    "Acids in Coffee: A Review of Sensory and Chemical Profiles",
                    "Balzer, H. H.",
                    2001,
                    "https://doi.org/10.1002/9780470690499"
                )
            )
        ),

        // ====================================================================
        // EDU-08: Coffee Aromatic Chemistry
        // ====================================================================
        AcademyLesson(
            id = "EDU-08",
            moduleNumber = 8,
            category = AcademyCurriculumCategory.AROMA_AND_VOLATILES,
            titleEn = "Coffee Aromatic Chemistry: Volatiles & Roasting Reactions",
            titleId = "Kimia Aromatik Kopi: Senyawa Volatil & Reaksi Sangrai",
            estimatedDurationMinutes = 25,
            objectivesEn = listOf(
                "Categorize coffee volatiles into Enzymatic, Sugar Browning, and Dry Distillation families.",
                "Understand the Maillard reaction, Strecker degradation, and lipid oxidation.",
                "Identify key aroma compounds: furfuryl mercaptan, pyrazines, and aldehydes."
            ),
            objectivesId = listOf(
                "Mengelompokkan volatil kopi ke dalam keluarga Enzimatik, Sugar Browning, dan Distilasi Kering.",
                "Memahami reaksi Maillard, degradasi Strecker, dan oksidasi lipid.",
                "Mengenali senyawa aroma kunci: furfuril merkaptan, pirazin, dan aldehida."
            ),
            coreConceptsEn = listOf(
                "Enzymatic (biological origin): fruity, floral, grassy compounds (aldehydes, terpene esters).",
                "Sugar Browning (Maillard & caramelization): nutty, caramelly, chocolatey (pyrazines, furans).",
                "Dry Distillation (thermal pyrolysis): spicy, smoky, resinous, ashy (phenols, pyridines)."
            ),
            coreConceptsId = listOf(
                "Enzimatik: buah, bunga, herbal segar (aldehida, ester terpena dari genetik & pemrosesan).",
                "Sugar Browning: kacang, karamel, cokelat (pirazin, furan hasil reaksi Maillard).",
                "Distilasi Kering: rempah, asap, tembakau (fenol, piridina hasil pirolisis sangrai gelap)."
            ),
            contentMarkdownEn = ("""
# Coffee Volatile Chemical Families

Roasted coffee contains over 800 volatile compounds. These are grouped into three primary thermal classes:
1. **Enzymatic (Biological Precursors)**: Flowery, fruity, herbal notes synthesized during cherry growth and""" +
                """ post-harvest fermentation. Volatiles: *linalool* (floral), *hexanal* (grassy), *beta-damascenone* (fruity).
2. **Sugar Browning (Maillard Reactions)**: Formed when amino acids react with reducing sugars at 130–180°C.""" +
                """ Volatiles: *2-ethyl-3,5-dimethylpyrazine* (roasted hazelnut), *furaneol* (caramel), *furfuryl mercaptan*""" +
                """ (roasty coffee aroma).
3. **Dry Distillation (Destructive Pyrolysis)**: Formed above 200°C by thermal decomposition of wood fibers""" +
                """ and trigonelline. Volatiles: *guaiacol* (smoky/spicy), *pyridine* (burnt).
            """).trimIndent(),
            contentMarkdownId = ("""
# Keluarga Senyawa Volatil Kopi

Kopi sangrai memiliki lebih dari 800 senyawa volatil yang dikelompokkan ke dalam tiga fase termal:
1. **Enzimatik**: Aroma bunga dan buah dari biokimia buah dan fermentasi. Contoh: *linalool* (bunga),""" +
                """ *beta-damascenon* (buah berry).
2. **Sugar Browning (Karamelisasi & Reaksi Maillard)**: Asam amino bereaksi dengan gula pada 130–180°C.""" +
                """ Contoh: *pirazin* (kacang panggang), *furfuril merkaptan* (aroma kopi sangrai).
3. **Distilasi Kering**: Pirolisis serat selulosa di atas 200°C. Menghasilkan aroma rempah, tembakau, dan""" +
                """ asap.
            """).trimIndent(),
            asciiDiagram = """
    Green Precursors (Sugars + Amino Acids)
        |---> 130-180°C: Maillard & Strecker ---> Nutty, Caramel, Cocoa Pyrazines
        |---> >200°C: Fiber Pyrolysis        ---> Spicy, Clove, Smoky Guaiacols
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Maillard Reaction",
                    "Reaksi Maillard",
                    "Chemical reaction between amino acids and reducing sugars under heat.",
                    "Reaksi kimia antara asam amino dan gula pereduksi saat dipanaskan."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU08-1",
                    promptEn = "Which volatile compound is widely recognized as providing the quintessential 'fresh roasted coffee' aroma?",
                    promptId = "Senyawa volatil apa yang paling dikenal menghasilkan aroma khas 'kopi baru disangrai'?",
                    options = listOf(
                        QuizOption("A", "2-Furfurylthiol (Furfuryl mercaptan)", "2-Furfuriltiol (Furfuril merkaptan)", true),
                        QuizOption("B", "Diacetyl", "Diasetil", false),
                        QuizOption("C", "Geosmin", "Geosmin", false),
                        QuizOption("D", "Acetic acid", "Asam asetat", false)
                    ),
                    explanationEn = "2-Furfurylthiol has an extremely low sensory threshold and provides the characteristic roasted" +
                        " coffee bean aroma.",
                    explanationId = "2-Furfuriltiol memiliki ambang deteksi sangat rendah dan menghasilkan aroma khas biji kopi sangrai" +
                        " segar."
                )
            ),
            accessibilityNotesEn = "Diagram categorizes aroma development across roasting temperature milestones.",
            accessibilityNotesId = "Diagram mengkategorikan pembentukan aroma berdasarkan tonggak suhu sangrai.",
            citations = listOf(
                AcademyCitation("Volatile Compounds in Coffee", "Flament, I.", 2001, "https://doi.org/10.1002/9780470774588")
            )
        ),

        // ====================================================================
        // EDU-09: Aroma Training & Physical Kit Companion
        // ====================================================================
        AcademyLesson(
            id = "EDU-09",
            moduleNumber = 9,
            category = AcademyCurriculumCategory.AROMA_AND_VOLATILES,
            titleEn = "Aroma Training: Olfactory Memory & Physical Kit Practice",
            titleId = "Pelatihan Aroma: Memori Olfaktori & Praktik Kit Fisik",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Build disciplined olfactory memory using physical aromatic references.",
                "Utilize the Le Nez du Café 60 companion with user-owned physical vials.",
                "Respect intellectual property boundaries by using independent taxonomies."
            ),
            objectivesId = listOf(
                "Membangun memori olfaktori disiplin menggunakan referensi aroma fisik.",
                "Menggunakan pendamping kit Le Nez du Café 60 untuk botol fisik milik pengguna.",
                "Menghormati hak kekayaan intelektual dengan menggunakan taksonomi independen."
            ),
            coreConceptsEn = listOf(
                "Olfactory memory requires active linguistic labeling and spaced repetition.",
                "A mobile phone cannot emit smells; hands-on physical vials or real food references are mandatory.",
                "Coffee Creator provides generic descriptors and custom user vial mapping without reproducing copyrighted kits."
            ),
            coreConceptsId = listOf(
                "Memori penciuman membutuhkan pelabelan linguistik aktif dan pengulangan berkala.",
                "Ponsel tidak bisa mengeluarkan bau; latihan fisik dengan botol aroma atau bahan makanan asli adalah mutlak.",
                "Coffee Creator menyediakan deskriptor generik dan penomoran botol kustom tanpa menyalin materi berhak cipta."
            ),
            contentMarkdownEn = ("""
# Olfactory Training Methodology

Olfactory sensory neurons regenerate every 30–60 days, and cognitive scent memory responds remarkably to""" +
                """ structured practice:
1. **Blind Sniffing**: Take 3 short, gentle bunny sniffs from a physical vial or food sample without viewing""" +
                """ the label.
2. **Verbal Encoding**: Mentally anchor the aroma with personal memories, category classification, and formal""" +
                """ descriptor names.
3. **Physical Kit Companion**: Users who own commercial 60-aroma kits (such as Le Nez du Café) can assign""" +
                """ their physical vial numbers to our generic taxonomy for randomized drills.

> [!NOTE]
> Coffee Creator contains no unlicensed proprietary booklet content, photographs, or proprietary exercises""" +
                """ from commercial kit publishers.
            """).trimIndent(),
            contentMarkdownId = ("""
# Metodologi Pelatihan Penciuman

Memori bau dapat dilatih secara konsisten melalui tiga langkah:
1. **Mencium Buta (Blind Sniffing)**: Hirup 3 kali secara lembut tanpa melihat label botol atau sampel""" +
                """ makanan.
2. **Pengkodean Verbal**: Hubungkan bau dengan memori pengalaman, kelompok aroma, dan nama deskriptor resmi.
3. **Pendamping Kit Fisik**: Pengguna yang memiliki kit aroma fisik (seperti Le Nez du Café 60) dapat""" +
                """ memetakan nomor botol fisik mereka ke dalam taksonomi aplikasi untuk latihan acak.
            """).trimIndent(),
            asciiDiagram = """
    Physical Vial -> 3 Gentle Sniffs -> Category Guess -> Check Answer -> Spaced Repetition (Day 1, 3, 7)
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Spaced Repetition",
                    "Pengulangan Berkala",
                    "Learning technique incorporating increasing intervals of time between reviews.",
                    "Teknik belajar dengan jeda waktu yang meningkat untuk menguatkan memori jangka panjang."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU09-1",
                    promptEn = "Can virtual app reading replace smelling physical aromatic references?",
                    promptId = "Bisakah membaca aplikasi menggantikan latihan mencium referensi aroma fisik secara langsung?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "No, olfactory memory requires direct biological stimulation of receptor neurons",
                            "Tidak, memori penciuman mutlak membutuhkan stimulasi fisik neuron reseptor",
                            true
                        ),
                        QuizOption("B", "Yes, visual reading builds identical olfactory memory", "Ya, membaca saja sudah cukup", false),
                        QuizOption("C", "Yes, digital sounds stimulate olfactory bulbs", "Ya, suara digital merangsang penciuman", false),
                        QuizOption("D", "No, but only synthetic vials work", "Tidak, tetapi hanya vial sintetis yang berguna", false)
                    ),
                    explanationEn = "Sensory learning demands physical biological activation of olfactory receptor neurons; apps provide" +
                        " structure and tracking, not scent molecules.",
                    explanationId = "Pelatihan sensori menuntut aktivasi biologis langsung pada reseptor hidung; aplikasi berfungsi" +
                        " sebagai pencatat dan penguji logika."
                )
            ),
            accessibilityNotesEn = "Diagram illustrates physical smelling and repetition loop.",
            accessibilityNotesId = "Diagram menggambarkan siklus latihan mencium dan pengulangan berkala.",
            citations = listOf(
                AcademyCitation("Le Nez du Café — The Aroma of Coffee", "Lenoir, J.", 1997, "https://www.editions-jeanlenoir.com/")
            )
        ),

        // ====================================================================
        // EDU-10: WCR Lexicon & Flavor Wheel
        // ====================================================================
        AcademyLesson(
            id = "EDU-10",
            moduleNumber = 10,
            category = AcademyCurriculumCategory.STANDARDS_AND_PROTOCOLS,
            titleEn = "Sensory Lexicons & The Coffee Taster's Flavor Wheel",
            titleId = "Leksikon Sensori & Roda Rasa Kopi (Flavor Wheel)",
            estimatedDurationMinutes = 25,
            objectivesEn = listOf(
                "Navigate hierarchical sensory taxonomies from broad families to specific descriptors.",
                "Understand World Coffee Research (WCR) sensory lexicon reference standards.",
                "Appreciate Indonesian localized descriptors (pandan, gula aren, salak, manggis)."
            ),
            objectivesId = listOf(
                "Menavigasi taksonomi sensori hierarkis dari keluarga besar ke deskriptor spesifik.",
                "Memahami standar referensi leksikon World Coffee Research (WCR).",
                "Memahami deskriptor lokal Indonesia (pandan, gula aren, salak, manggis)."
            ),
            coreConceptsEn = listOf(
                "SCA Flavor Wheel organizes sensory space into 9 primary categories branching outward.",
                "Descriptors are anchored to calibrated food/chemical references in sensory science.",
                "Indonesian Roda Rasa Kopi extends the vocabulary for cultural tropical relevance."
            ),
            coreConceptsId = listOf(
                "Roda Rasa SCA mengorganisasi ruang rasa ke dalam 9 kategori utama yang bercabang ke luar.",
                "Deskriptor terikat pada referensi kalibrasi makanan/kimiawi standar.",
                "Roda Rasa Kopi Indonesia memperluas kosakata dengan cita rasa tropis nusantara."
            ),
            contentMarkdownEn = ("""
# The Architecture of Flavor Taxonomies

In 2016, the Specialty Coffee Association (SCA) and World Coffee Research (WCR) published the redesigned""" +
                """ Coffee Taster's Flavor Wheel, underpinned by the WCR Sensory Lexicon.

```
+-------------------------------------------------------------+
| SCA / WCR FLAVOR WHEEL HIERARCHY                            |
| Center (Category)  --> Middle (Subcategory) --> Outer Ring  |
| [Fruity]           --> [Citrus Fruit]       --> Grapefruit  |
| [Floral]           --> [Black Tea]          --> Darjeeling  |
| [Sweet]            --> [Brown Sugar]        --> Molasses    |
+-------------------------------------------------------------+
```

### Local Cultural Extensions
While the SCA wheel establishes a global lingua franca, local markets require culturally authentic anchors.""" +
                """ Coffee Creator includes the **Indonesian 221-Descriptor Extension**, introducing tropical references like""" +
                """ *Pandan, Gula Aren, Salak, Manggis, Rambutan,* and *Temulawak* alongside official SCA references.
            """).trimIndent(),
            contentMarkdownId = ("""
# Arsitektur Taksonomi Rasa

Roda Rasa Kopi membagi cita rasa ke dalam 9 kategori utama:
1. Floral (Bunga)
2. Fruity (Buah-buahan)
3. Sour / Fermented (Asam / Fermentasi)
4. Green / Vegetative (Herbal / Daun)
5. Roasted (Sangrai)
6. Spices (Rempah)
7. Nutty / Cocoa (Kacang / Cokelat)
8. Sweet (Manis)
9. Other / Defects (Cacat Rasa / Lainnya)

### Ekstensi Rasa Nusantara (221 Deskriptor)
Coffee Creator menggabungkan 84 deskriptor referensi global dengan 137 deskriptor lokal Indonesia (Pandan,""" +
                """ Gula Aren, Salak, Manggis, Cengkih, Kayu Manis), memungkinkan penilaian yang relevan bagi barista dan petani""" +
                """ Indonesia.
            """).trimIndent(),
            asciiDiagram = """
    Category (Fruity) -> Subcategory (Berry) -> Specific Descriptor (Blackberry)
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Sensory Lexicon",
                    "Leksikon Sensori",
                    "Standardized dictionary of sensory descriptors defined by empirical references.",
                    "Kamus standar deskriptor rasa yang didefinisikan dengan referensi empiris."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU10-1",
                    promptEn = "How is the SCA Flavor Wheel designed to be read?",
                    promptId = "Bagaimana cara membaca Roda Rasa Kopi SCA yang benar?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "From the center outward: start broad, then refine to specific descriptors",
                            "Dari tengah ke luar: mulai dari kategori umum, lalu ke deskriptor spesifik",
                            true
                        ),
                        QuizOption("B", "From outer ring to center", "Dari luar ke dalam", false),
                        QuizOption("C", "Clockwise starting from 12 o'clock only", "Searah jarum jam dari arah jam 12 saja", false),
                        QuizOption("D", "Randomly based on color preference", "Secara acak berdasarkan warna", false)
                    ),
                    explanationEn = "Tasters start at the center to identify broad families (e.g., Fruity), then move outward to" +
                        " subcategories (Citrus), and finally pinpoint specific outer descriptors (Grapefruit).",
                    explanationId = "Pencicip mulai dari lingkaran tengah (kategori umum seperti Fruity), lalu bergerak ke luar ke" +
                        " subkategori (Citrus), hingga ke lingkaran terluar (Grapefruit)."
                )
            ),
            accessibilityNotesEn = "Diagram shows concentric hierarchy moving from broad category to specific flavor descriptor.",
            accessibilityNotesId = "Diagram memperlihatkan hierarki konsentris dari kategori umum ke deskriptor spesifik.",
            citations = listOf(
                AcademyCitation(
                    "World Coffee Research Sensory Lexicon 2.0",
                    "World Coffee Research",
                    2017,
                    "https://worldcoffeeresearch.org/resources/sensory-lexicon"
                )
            )
        ),

        // ====================================================================
        // EDU-11: Sample Prep & Quality Control
        // ====================================================================
        AcademyLesson(
            id = "EDU-11",
            moduleNumber = 11,
            category = AcademyCurriculumCategory.STANDARDS_AND_PROTOCOLS,
            titleEn = "Sample Preparation & Cupping Room Compliance",
            titleId = "Preparasi Sampel & Standar Kepatuhan Ruang Cupping",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Apply SCA cupping sample preparation ratios and tolerances.",
                "Control water temperature, TDS, grind size, and cup cleanliness.",
                "Prevent cross-contamination between bowls and assessors."
            ),
            objectivesId = listOf(
                "Menerapkan rasio preparasi dan toleransi sampel cupping SCA.",
                "Mengontrol suhu air, TDS, ukuran gilingan, dan kebersihan cangkir.",
                "Mencegah kontaminasi silang antar cangkir dan antar evaluator."
            ),
            coreConceptsEn = listOf(
                "Standard ratio: 8.25g coffee per 150 mL water (or 55g/L +/- 2g/L).",
                "Roast level: Agtron 63 on whole bean, 58 on ground (+/- 1 unit), roasted within 24h before cupping.",
                "Water specification: Clean, odorless, 75–175 ppm TDS, neutral pH 7.0, 93°C at pour."
            ),
            coreConceptsId = listOf(
                "Rasio standar: 8.25g kopi per 150 mL air (atau 55g/L +/- 2g/L).",
                "Tingkat sangrai: Agtron 63 biji utuh, 58 biji giling (+/- 1 unit), disangrai 8–24 jam sebelumnya.",
                "Standar air: Bersih, tidak berbau, 75–175 ppm TDS, pH netral 7.0, suhu 93°C saat dituang."
            ),
            contentMarkdownEn = ("""
# Standardized Sample Preparation (SCA-102)

To ensure repeatability, every parameter is tightly controlled:
- **Coffee-to-Water Ratio**: $55\,\text{g/L} \pm 2\,\text{g/L}$. For a 200 mL vessel, use exactly""" +
                """ $11.0\,\text{g} \pm 0.1\,\text{g}$ of coffee.
- **Grind Particle Size**: 70–75% passes through a US Standard Size 20 sieve (~850 microns). Slightly coarser""" +
                """ than drip filter.
- **Vessel Specification**: Tempered glass or ceramic, 200–260 mL volume, identical dimensions across all""" +
                """ samples.
- **Pour Technique**: Water at $93.0^\circ\text{C}$ poured directly and vigorously over ground coffee to""" +
                """ ensure complete wetting.

```
+-------------------------------------------------------------+
| SCA CUPPING PREPARATION BENCHMARK                           |
| Ratio       : 55 g/L (e.g. 11.0g per 200 mL)                |
| Water Temp  : 93°C (200°F)                                  |
| Grind Size  : 70-75% pass US #20 sieve (~850 µm)            |
| Cups        : 5 identical bowls per sample lot              |
| Rest Period : 8 to 24 hours post-roasting                   |
+-------------------------------------------------------------+
```
            """).trimIndent(),
            contentMarkdownId = ("""
# Preparasi Sampel Terstandar (SCA-102)

Untuk memastikan pengujian dapat diulang dengan akurat:
- **Rasio Kopi-Air**: 55 g/L (+/- 2 g/L). Untuk mangkuk 200 mL, gunakan tepat 11.0 gram kopi.
- **Ukuran Gilingan**: 70–75% lolos saringan US #20 (~850 mikron), sedikit lebih kasar dari seduh saring""" +
                """ filter.
- **Wadah**: Keramik atau kaca tahan panas, volume 200–260 mL dengan bentuk identik.
- **Air**: Suhu 93°C, TDS 75–175 ppm, dituang langsung membasahi seluruh bubuk secara merata.
            """).trimIndent(),
            asciiDiagram = """
    Weigh 11.0g -> Grind ~850µm -> Smell Fragrance -> Pour 200mL 93°C -> 4:00 Min Break Crust
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Agtron",
                    "Agtron",
                    "Commercial spectrophotometer scale measuring roasted coffee color reflectance.",
                    "Skala spektrofotometri untuk mengukur pantulan warna sangrai kopi."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU11-1",
                    promptEn = "What is the official SCA standard brewing ratio for cupping?",
                    promptId = "Berapakah rasio seduhan standar resmi SCA untuk cupping?",
                    options = listOf(
                        QuizOption("A", "55 g/L (+/- 2 g/L) or ~8.25g per 150 mL", "55 g/L (+/- 2 g/L) atau ~8.25g per 150 mL", true),
                        QuizOption("B", "75 g/L", "75 g/L", false),
                        QuizOption("C", "35 g/L", "35 g/L", false),
                        QuizOption("D", "100 g/L espresso ratio", "100 g/L rasio espresso", false)
                    ),
                    explanationEn = "SCA standards prescribe 55 grams of coffee per liter of water (+/- 2 g/L), translating to 8.25g for" +
                        " 150 mL or 11.0g for 200 mL.",
                    explanationId = "Standar SCA menetapkan 55 gram kopi per liter air (+/- 2 g/L), setara dengan 8.25g per 150 mL atau" +
                        " 11.0g per 200 mL air."
                )
            ),
            accessibilityNotesEn = "Diagram provides step-by-step visual sequence of sample prep and 4-minute break.",
            accessibilityNotesId = "Diagram memberikan urutan langkah penyiapan sampel dan break 4 menit.",
            citations = listOf(
                AcademyCitation(
                    "SCA-102: Cupping Sample Preparation Standard",
                    "Specialty Coffee Association",
                    2024,
                    "https://sca.coffee/research/coffee-standards"
                )
            )
        ),

        // ====================================================================
        // EDU-12: SCA CVA Philosophy
        // ====================================================================
        AcademyLesson(
            id = "EDU-12",
            moduleNumber = 12,
            category = AcademyCurriculumCategory.STANDARDS_AND_PROTOCOLS,
            titleEn = "SCA Coffee Value Assessment (CVA) Philosophy & Architecture",
            titleId = "Filosofi & Arsitektur SCA Coffee Value Assessment (CVA)",
            estimatedDurationMinutes = 25,
            objectivesEn = listOf(
                "Understand why the SCA transitioned from the 2004 form to the modern CVA.",
                "Differentiate the four perspectives: Physical, Descriptive, Affective, Extrinsic.",
                "Prevent conflating descriptive sensory intensity with quality or price value."
            ),
            objectivesId = listOf(
                "Memahami mengapa SCA beralih dari form cupping 2004 ke CVA modern.",
                "Membedakan empat perspektif: Fisik, Deskriptif, Afektif, Ekstrinsik.",
                "Mencegah pencampuradukan antara intensitas rasa deskriptif dan kualitas atau harga."
            ),
            coreConceptsEn = listOf(
                "The 2004 form mixed descriptive words and quality scores on a single 100-point scale.",
                "CVA separates 'What does it taste like?' (Descriptive) from 'How much do you like it?' (Affective).",
                "Extrinsic value (fair trade, organic, story) is evaluated independently to avoid unconscious bias."
            ),
            coreConceptsId = listOf(
                "Form 2004 mencampur kata deskriptif dan nilai kualitas pada satu lembar penilaian 100 poin.",
                "CVA memisahkan 'Seperti apa rasanya?' (Deskriptif) dari 'Seberapa Anda menyukainya?' (Afektif).",
                "Nilai ekstrinsik (sertifikasi, kisah petani) dinilai terpisah untuk mencegah bias bawah sadar."
            ),
            contentMarkdownEn = ("""
# The Coffee Value Assessment (CVA) Revolution

Adopted globally by the SCA in 2024, the Coffee Value Assessment (CVA) fundamentally modernizes coffee""" +
                """ evaluation:

```
+-------------------------------------------------------------+
| SCA COFFEE VALUE ASSESSMENT (4 INDEPENDENT PERSPECTIVES)    |
|                                                             |
| 1. PHYSICAL ASSESSMENT    : Moisture, defects, bean color   |
| 2. DESCRIPTIVE ASSESSMENT : Intensities (0-5) & descriptors |
| 3. AFFECTIVE ASSESSMENT   : Subjective hedonic preference   |
| 4. EXTRINSIC ASSESSMENT   : Traceability, ethics, origins   |
+-------------------------------------------------------------+
```

### Why the Change?
In the 2004 system, a cupper who disliked floral notes might score an exceptional Geisha poorly, confusing""" +
                """ personal preference with objective quality. CVA ensures that intense floral character is recorded accurately""" +
                """ on the Descriptive sheet, while subjective scoring occurs independently on the Affective sheet.
            """).trimIndent(),
            contentMarkdownId = ("""
# Revolusi Coffee Value Assessment (CVA)

Pada tahun 2024, SCA memperbarui protokol penilaian kopi melalui CVA:
1. **Penilaian Fisik**: Kadar air, cacat biji hijau, warna sangrai.
2. **Penilaian Deskriptif**: Intensitas rasa (skala 0–5) dan deskriptor tanpa prasangka suka/tidak suka.
3. **Penilaian Afektif**: Evaluasi hedonik preferensi cupper (seberapa suka kualitasnya).
4. **Penilaian Ekstrinsik**: Keterlacakan asal-usul, sertifikasi, nilai etis perdagangan.

### Mengapa Berubah?
Pada form 2004, evaluator yang tidak suka rasa bunga bisa memberi skor rendah pada kopi Geisha juara. CVA""" +
                """ memisahkan fakta rasa deskriptif dari preferensi subjektif.
            """).trimIndent(),
            asciiDiagram = """
    Coffee Sample -> [Physical] -> [Descriptive (Objective)] -> [Affective (Subjective)] -> [Extrinsic]
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "CVA",
                    "CVA",
                    "Coffee Value Assessment: modern four-perspective SCA sensory framework.",
                    "Kerangka penilaian sensori modern SCA berbasis empat perspektif."
                ),
                GlossaryEntry(
                    "Affective",
                    "Afektif",
                    "Evaluation based on personal feelings, preferences, and hedonic liking.",
                    "Penilaian berdasarkan perasaan pribadi, preferensi, dan tingkat kesukaan."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU12-1",
                    promptEn = "What is the key scientific reason CVA separates Descriptive from Affective assessment?",
                    promptId = "Apa alasan ilmiah utama CVA memisahkan penilaian Deskriptif dan Afektif?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "To prevent personal taste preference from distorting objective sensory descriptors",
                            "Untuk mencegah preferensi pribadi mengaburkan deskripsi sensori objektif",
                            true
                        ),
                        QuizOption("B", "To make cupping sessions twice as long", "Agar sesi cupping lebih lama", false),
                        QuizOption("C", "To eliminate the need for trained cuppers", "Agar tidak butuh evaluator terlatih", false),
                        QuizOption("D", "Because descriptive words have no meaning", "Karena kata deskriptif tidak memiliki makna", false)
                    ),
                    explanationEn = "Separating descriptive intensity from hedonic preference allows panels to agree objectively on what" +
                        " the coffee tastes like even if individual consumer preferences vary.",
                    explanationId = "Memisahkan intensitas deskriptif dari preferensi hedonik memungkinkan panel menyepakati rasa kopi" +
                        " secara objektif meski selera pribadi berbeda."
                )
            ),
            accessibilityNotesEn = "Diagram contrasts the 4 CVA perspective pillars cleanly.",
            accessibilityNotesId = "Diagram menampilkan 4 pilar perspektif CVA secara terpisah.",
            citations = listOf(
                AcademyCitation(
                    "The SCA Coffee Value Assessment: An Overview",
                    "Specialty Coffee Association",
                    2023,
                    "https://sca.coffee/value-assessment/"
                )
            )
        ),

        // ====================================================================
        // EDU-13: CVA Descriptive Form
        // ====================================================================
        AcademyLesson(
            id = "EDU-13",
            moduleNumber = 13,
            category = AcademyCurriculumCategory.STANDARDS_AND_PROTOCOLS,
            titleEn = "CVA Descriptive: Measuring Intensity & Character",
            titleId = "CVA Deskriptif: Mengukur Intensitas & Karakter Rasa",
            estimatedDurationMinutes = 25,
            objectivesEn = listOf(
                "Master the 0 to 5 continuous intensity scale for sensory attributes.",
                "Record fragrance, aroma, flavor, aftertaste, acidity, sweetness, and mouthfeel.",
                "Understand CVA check-all-that-apply (CATA) descriptor tagging."
            ),
            objectivesId = listOf(
                "Menguasai skala intensitas kontinu 0 sampai 5 untuk atribut sensori.",
                "Mencatat fragrance, aroma, flavor, aftertaste, keasaman, rasa manis, dan mouthfeel.",
                "Memahami penandaan deskriptor check-all-that-apply (CATA) pada CVA."
            ),
            coreConceptsEn = listOf(
                "Intensity (0=none, 5=extremely intense) measures sensory strength, NOT quality.",
                "A coffee with Acidity Intensity 5.0 is simply very sour; it may be delicious or unpalatable.",
                "CATA descriptors capture aromatic notes without assigning subjective point weights."
            ),
            coreConceptsId = listOf(
                "Intensitas (0=tidak ada, 5=sangat intens) mengukur kekuatan rangsangan, BUKAN kualitas.",
                "Kopi dengan intensitas asam 5.0 berarti sangat asam; bisa sangat enak atau sebaliknya terlalu menusuk.",
                "Deskriptor CATA mendokumentasikan aroma tanpa memberi bobot angka subjektif."
            ),
            contentMarkdownEn = """
# CVA Descriptive Assessment Protocol (SCA-103)

The descriptive form measures **sensory intensity** independently of hedonic appreciation:
- **Scale**: 0.0 (None / Undetectable) to 5.0 (Extremely Intense).
- **Evaluated Attributes**:
  1. *Fragrance* (Dry Grounds intensity)
  2. *Aroma* (Crust Break intensity)
  3. *Flavor* (Retronasal + Taste intensity)
  4. *Aftertaste* (Length and lingering intensity)
  5. *Acidity* (Strength of sour sensation + quality tags: citric, malic, etc.)
  6. *Sweetness* (Strength of sweet sensation)
  7. *Mouthfeel* (Weight/thickness + texture: silky, creamy, astringent)

```
0.0 --------- 1.0 --------- 2.0 --------- 3.0 --------- 4.0 --------- 5.0
None         Low          Mild          Moderate       High       Extreme
```
            """.trimIndent(),
            contentMarkdownId = ("""
# Protokol CVA Deskriptif (SCA-103)

Formulir deskriptif mengukur **kekuatan rangsangan rasa**:
- **Skala**: 0.0 (Tidak ada) hingga 5.0 (Sangat kuat).
- Atribut yang dinilai: Fragrance bubuk kering, Aroma crust basah, Flavor seruputan, Aftertaste, Intensitas""" +
                """ Asam, Intensitas Manis, dan Mouthfeel.
            """).trimIndent(),
            asciiDiagram = """
    Intensity 0.0 [None] ------------------------> 5.0 [Extreme]
    * High intensity is an objective measurement, not necessarily a badge of high quality.
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "CATA",
                    "CATA",
                    "Check-All-That-Apply: sensory profiling method where assessors select all applicable descriptors.",
                    "Metode profil sensori di mana evaluator menandai semua deskriptor yang terdeteksi."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU13-1",
                    promptEn = "In CVA Descriptive assessment, does an Acidity score of 4.5 mean the acidity is high quality?",
                    promptId = "Pada CVA Deskriptif, apakah nilai keasaman 4.5 berarti keasamannya berkualitas tinggi?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "No, it indicates the acidity intensity is high; quality is rated on the Affective sheet",
                            "Tidak, itu hanya menunjukkan keasaman sangat intens; kualitas dinilai pada lembar Afektif",
                            true
                        ),
                        QuizOption(
                            "B",
                            "Yes, higher scores always mean better coffee",
                            "Ya, nilai tinggi selalu berarti kopi lebih enak",
                            false
                        ),
                        QuizOption("C", "Yes, 4.5 qualifies the coffee as Specialty Grade 1", "Ya, 4.5 otomatis specialty grade 1", false),
                        QuizOption("D", "No, 4.5 indicates a defect", "Tidak, 4.5 berarti ada cacat", false)
                    ),
                    explanationEn = "Descriptive scales measure intensity only (how strong the sensation is). Whether that intense" +
                        " acidity is pleasant or harsh is evaluated separately on the Affective form.",
                    explanationId = "Skala deskriptif hanya mengukur intensitas (seberapa kuat rangsangannya). Apakah keasaman itu nikmat" +
                        " atau terlalu tajam dinilai terpisah di lembar Afektif."
                )
            ),
            accessibilityNotesEn = "Diagram displays the 0 to 5 continuous slider scale.",
            accessibilityNotesId = "Diagram menampilkan skala geser kontinu 0 hingga 5.",
            citations = listOf(
                AcademyCitation(
                    "SCA-103: Descriptive Assessment Standard",
                    "Specialty Coffee Association",
                    2024,
                    "https://sca.coffee/value-assessment/"
                )
            )
        ),

        // ====================================================================
        // EDU-14: CVA Affective Form
        // ====================================================================
        AcademyLesson(
            id = "EDU-14",
            moduleNumber = 14,
            category = AcademyCurriculumCategory.STANDARDS_AND_PROTOCOLS,
            titleEn = "CVA Affective: Hedonic Evaluation & Quality Impressions",
            titleId = "CVA Afektif: Evaluasi Hedonik & Penilaian Kualitas",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Understand the 6.00 to 10.00 hedonic quality impression scale.",
                "Separate affective evaluation from physical and descriptive measurements.",
                "Calculate total scores with defect penalty deductions."
            ),
            objectivesId = listOf(
                "Memahami skala kesan kualitas hedonik 6.00 hingga 10.00.",
                "Memisahkan evaluasi afektif dari pengukuran fisik dan deskriptif.",
                "Menghitung skor total dengan pengurangan penalti cacat rasa."
            ),
            coreConceptsEn = listOf(
                "Affective scales capture how much a qualified assessor or consumer values the sensory experience.",
                "Scale spans from 6.00 (Good) to 10.00 (Outstanding) in 0.25 point intervals.",
                "Defect deductions subtract points for non-uniform bowls or commercial taints."
            ),
            coreConceptsId = listOf(
                "Skala afektif mencatat seberapa tinggi apresiasi evaluator terhadap kualitas rasa cangkir.",
                "Rentang skala dari 6.00 (Baik) hingga 10.00 (Luar Biasa) dengan kelipatan 0.25 poin.",
                "Pengurangan cacat memotong poin jika ditemukan cangkir tidak seragam atau cacat taints/faults."
            ),
            contentMarkdownEn = """
# CVA Affective Assessment Protocol (SCA-104)

The Affective form records the **hedonic quality impression**:
- **Scale**: 6.00 (Good) to 10.00 (Outstanding) in 0.25 increments.
- Attributes evaluated: Fragrance, Flavor, Aftertaste, Acidity, Sweetness, Mouthfeel, and Overall Impression.
- **Defects Penalty**:
  - *Taint* (-2.0 points per defective cup): Noticeable off-flavor that detracts but does not overwhelm.
  - *Fault* (-4.0 points per defective cup): Overwhelming, repulsive defect (mold, chemical, phenol).
            """.trimIndent(),
            contentMarkdownId = """
# Protokol CVA Afektif (SCA-104)

Formulir Afektif mencatat **apresiasi kualitas rasa**:
- **Skala**: 6.00 (Good) sampai 10.00 (Outstanding) dengan interval 0.25 poin.
- Atribut yang dinilai: Fragrance, Flavor, Aftertaste, Acidity, Sweetness, Mouthfeel, dan Overall.
- **Pengurangan Cacat**:
  - *Taint* (-2 poin per cangkir cacat): Bau menyimpang ringan yang mengurangi kenikmatan.
  - *Fault* (-4 poin per cangkir cacat): Cacat parah menusuk (jamur, fenolik, busuk).
            """.trimIndent(),
            asciiDiagram = """
    6.00 [Good] ---> 7.00 [Very Good] ---> 8.00 [Excellent] ---> 9.00-10.00 [Outstanding]
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Hedonic Scale",
                    "Skala Hedonik",
                    "Psychological scale measuring degrees of liking or pleasure.",
                    "Skala psikologis pengukur tingkat kesukaan atau kenikmatan."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU14-1",
                    promptEn = "What is the penalty deduction for a severe 'Fault' defect in traditional specialty cupping scoring?",
                    promptId = "Berapakah pengurangan penalti untuk cacat parah (Fault) pada sistem penilaian cupping tradisional?",
                    options = listOf(
                        QuizOption("A", "4.0 points per defective cup", "4.0 poin per cangkir cacat", true),
                        QuizOption("B", "2.0 points per defective cup", "2.0 poin per cangkir cacat", false),
                        QuizOption("C", "0.5 points per defective cup", "0.5 poin per cangkir cacat", false),
                        QuizOption("D", "Automatic zero total score", "Otomatis nilai nol", false)
                    ),
                    explanationEn = "A minor taint deducts 2 points per cup; a severe overwhelming fault deducts 4 points per cup.",
                    explanationId = "Cacat ringan (taint) memotong 2 poin per cangkir; cacat parah (fault) memotong 4 poin per cangkir."
                )
            ),
            accessibilityNotesEn = "Diagram visualizes 6.00 to 10.00 rating thresholds.",
            accessibilityNotesId = "Diagram menampilkan ambang batas penilaian dari 6.00 hingga 10.00.",
            citations = listOf(
                AcademyCitation(
                    "SCA-104: Affective Assessment Standard",
                    "Specialty Coffee Association",
                    2024,
                    "https://sca.coffee/value-assessment/"
                )
            )
        ),

        // ====================================================================
        // EDU-15: CVA Extrinsic Assessment
        // ====================================================================
        AcademyLesson(
            id = "EDU-15",
            moduleNumber = 15,
            category = AcademyCurriculumCategory.STANDARDS_AND_PROTOCOLS,
            titleEn = "CVA Extrinsic: Traceability, Ethics & Controlled Reveal",
            titleId = "CVA Ekstrinsik: Keterlacakan, Etika & Pembukaan Terkontrol",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Understand extrinsic attributes: origin, producer, elevation, certifications, and price.",
                "Enforce blind evaluation gates to protect sensory objectivity.",
                "Audit intentional unblinding events in professional sessions."
            ),
            objectivesId = listOf(
                "Memahami atribut ekstrinsik: asal-usul, nama petani, ketinggian, sertifikasi, dan harga.",
                "Menerapkan gerbang blind evaluation untuk melindungi objektivitas sensori.",
                "Mencatat audit pembukaan identitas sampel pada sesi profesional."
            ),
            coreConceptsEn = listOf(
                "Extrinsic value reflects information not directly detectable in the liquid.",
                "Consumers value sustainability, ethical sourcing, organic practices, and farmer welfare.",
                "Extrinsic data must remain concealed until sensory scoring is locked."
            ),
            coreConceptsId = listOf(
                "Nilai ekstrinsik mencerminkan informasi yang tidak bisa dirasakan langsung di dalam cangkir.",
                "Konsumen menghargai keberlanjutan lingkungan, perdagangan adil, organik, dan kesejahteraan petani.",
                "Data ekstrinsik wajib disembunyikan sampai penilaian sensori terkunci."
            ),
            contentMarkdownEn = """
# CVA Extrinsic Assessment (SCA-105)

Extrinsic attributes represent the informational and contextual value of coffee:
- **Geographical Traceability**: Country, region, micro-region, farm, washing station.
- **Agronomic Metadata**: Variety (Geisha, Bourbon, Typica), process, elevation.
- **Certifications & Sustainability**: Fair Trade, Organic, Rainforest Alliance, Bird Friendly.
- **Commercial Transparency**: Farmgate price, export FOB price, carbon footprint.

```
[Blind Tasting Phase]  --->  [Lock Sensory Scores]  --->  [Reveal Extrinsic Info]
(Origin Concealed)           (Scores Immutable)          (Holistic Value Assessment)
```
            """.trimIndent(),
            contentMarkdownId = """
# Penilaian CVA Ekstrinsik (SCA-105)

Atribut ekstrinsik mewakili nilai informasi dan kontekstual:
- **Keterlacakan Geografis**: Negara, wilayah, nama kebun/koperasi.
- **Metadata Agronomi**: Varietas, metode proses, ketinggian MASL.
- **Sertifikasi**: Fair Trade, Organik, Rainforest Alliance.
- **Transparansi Perdagangan**: Harga beli di tingkat petani (farmgate price).
            """.trimIndent(),
            asciiDiagram = """
    Concealed Origin -> Sensory Scores Committed -> Extrinsic Audit Reveal
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Extrinsic Attribute",
                    "Atribut Ekstrinsik",
                    "Product characteristics that are not part of the physical product itself (e.g. price, brand, origin).",
                    "Karakteristik produk yang bukan bagian fisik dari zat produk itu sendiri (harga, asal, sertifikasi)."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU15-1",
                    promptEn = "When should extrinsic information be revealed to cuppers in a standardized CVA session?",
                    promptId = "Kapan informasi ekstrinsik boleh diungkap kepada evaluator dalam sesi CVA terstandar?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "Only after blind sensory scores have been submitted and locked",
                            "Hanya setelah penilaian sensori buta selesai diisi dan dikunci",
                            true
                        ),
                        QuizOption("B", "Before smelling the dry fragrance", "Sebelum mencium fragrance bubuk kering", false),
                        QuizOption("C", "While waiting for the 4-minute crust break", "Saat menunggu 4 menit break crust", false),
                        QuizOption("D", "Whenever the cupper requests it", "Kapan pun evaluator memintanya", false)
                    ),
                    explanationEn = "To prevent expectation bias from contaminating objective taste scoring, extrinsic data is only" +
                        " revealed after sensory evaluations are locked.",
                    explanationId = "Agar bias ekspektasi tidak merusak objektivitas penilaian rasa, data ekstrinsik baru boleh dibuka" +
                        " setelah skor sensori dikunci."
                )
            ),
            accessibilityNotesEn = "Diagram shows mandatory gate sequence between blind scoring and extrinsic reveal.",
            accessibilityNotesId = "Diagram menunjukkan urutan wajib antara penilaian buta dan pembukaan data ekstrinsik.",
            citations = listOf(
                AcademyCitation(
                    "SCA-105: Extrinsic Assessment Standard",
                    "Specialty Coffee Association",
                    2024,
                    "https://sca.coffee/value-assessment/"
                )
            )
        ),

        // ====================================================================
        // EDU-16: Green Coffee Classification
        // ====================================================================
        AcademyLesson(
            id = "EDU-16",
            moduleNumber = 16,
            category = AcademyCurriculumCategory.PHYSICAL_GREEN_GRADING,
            titleEn = "Green Coffee Physical Grading: Defects, Sieve & Moisture",
            titleId = "Grading Fisik Kopi Beras: Cacat Biji, Ukuran Ayakan & Kadar Air",
            estimatedDurationMinutes = 25,
            objectivesEn = listOf(
                "Classify Category 1 (Primary) vs Category 2 (Secondary) green defects per 350g sample.",
                "Calculate full defect equivalents using official ratios.",
                "Conduct sieve size analysis and verify moisture within 9.0% to 13.0% standards."
            ),
            objectivesId = listOf(
                "Mengklasifikasikan cacat biji Kategori 1 (Utama) vs Kategori 2 (Sekunder) per sampel 350g.",
                "Menghitung ekuivalen cacat penuh menggunakan rasio resmi.",
                "Melakukan analisis ukuran ayakan dan verifikasi kadar air standar 9.0% hingga 13.0%."
            ),
            coreConceptsEn = listOf(
                "Category 1 defects (Full Black, Full Sour, Pod, Fungus, Foreign Matter, Severe Insect) disqualify lot from" +
                    " Specialty Grade 1.",
                "Category 2 defects (Partial Black/Sour, Floater, Immature, Broken) require multiple beans to equal 1 full defect.",
                "Sample size for green grading is exactly 350 grams."
            ),
            coreConceptsId = listOf(
                "Cacat Kategori 1 (Hitam Penuh, Asam Penuh, Gelondong, Berjamur, Benda Asing) otomatis menggagalkan status" +
                    " Specialty Grade 1.",
                "Cacat Kategori 2 (Hitam Sebagian, Melayang, Muda, Pecah) butuh beberapa biji untuk setara dengan 1 cacat penuh.",
                "Berat sampel standar untuk uji fisik biji hijau adalah tepat 350 gram."
            ),
            contentMarkdownEn = """
# Green Arabica Defect Grading (350g Standard)

Under the SCA 2004 washed Arabica grading standard:
- **Sample Mass**: Exactly $350\,\text{g}$.
- **Category 1 (Primary Full Defects)**:
  - 1 Full Black = 1 Full Defect
  - 1 Full Sour = 1 Full Defect
  - 1 Dried Cherry / Pod = 1 Full Defect
  - 1 Fungus Damaged = 1 Full Defect
  - 1 Foreign Matter piece = 1 Full Defect
  - 5 Severe Insect Damaged beans = 1 Full Defect
- **Category 2 (Secondary Defects)**:
  - 3 Partial Black or 3 Partial Sour = 1 Full Defect
  - 5 Parchment, Floater, Immature, Withered, Shell, Broken, or Hull = 1 Full Defect
  - 10 Slight Insect Damaged beans = 1 Full Defect

### Specialty Grade 1 Thresholds
- Zero (0) Category 1 defects.
- Maximum five (5) Category 2 full defect equivalents.
- Moisture content strictly between 9.0% and 13.0% (ideally 10.0–12.0%).
            """.trimIndent(),
            contentMarkdownId = """
# Standar Grading Cacat Biji Kopi Arabika (Sampel 350g)

Berdasarkan standar fisik SCA:
- **Berat Sampel**: Tepat 350 gram.
- **Cacat Kategori 1 (Utama)**:
  - 1 Biji Hitam Penuh = 1 Cacat Penuh
  - 1 Biji Asam Penuh = 1 Cacat Penuh
  - 1 Buah Kering (Gelondong) = 1 Cacat Penuh
  - 1 Biji Berjamur = 1 Cacat Penuh
  - 1 Potong Benda Asing = 1 Cacat Penuh
  - 5 Biji Rusak Serangga Parah = 1 Cacat Penuh
- **Cacat Kategori 2 (Sekunder)**:
  - 3 Hitam Sebagian / Asam Sebagian = 1 Cacat Penuh
  - 5 Biji Kulit Tanduk, Melayang, Biji Muda, Keriput, Pecah = 1 Cacat Penuh
  - 10 Biji Serangga Ringan = 1 Cacat Penuh

### Syarat Specialty Grade 1
- Nol (0) cacat Kategori 1.
- Maksimal lima (5) ekuivalen cacat penuh Kategori 2.
- Kadar air harus di antara 9.0% hingga 13.0%.
            """.trimIndent(),
            asciiDiagram = ("""
    350g Sample -> Inspect Category 1 (Must be 0) -> Inspect Category 2 (<=5) -> Check Moisture (9-13%) ->""" +
                """ Specialty Grade 1
            """).trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Full Black",
                    "Hitam Penuh",
                    "Bean turned completely black from microflora fermentation and enzymatic breakdown.",
                    "Biji yang seluruhnya menghitam akibat fermentasi berlebih dan jamur."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU16-1",
                    promptEn = "How many Category 1 defects are permitted in a 350g sample for Specialty Grade 1?",
                    promptId = "Berapa banyak cacat Kategori 1 yang diizinkan dalam sampel 350g untuk klasifikasi Specialty Grade 1?",
                    options = listOf(
                        QuizOption("A", "Zero (0)", "Nol (0)", true),
                        QuizOption("B", "Up to 3", "Maksimal 3", false),
                        QuizOption("C", "Up to 5", "Maksimal 5", false),
                        QuizOption("D", "Unlimited if taste is good", "Tak terbatas jika rasanya enak", false)
                    ),
                    explanationEn = "Specialty Grade 1 washed Arabica strictly allows zero Category 1 primary defects in a 350g sample.",
                    explanationId = "Standar Specialty Grade 1 Arabika basah secara mutlak tidak mentoleransi adanya cacat Kategori 1" +
                        " (harus 0) dalam 350g sampel."
                )
            ),
            accessibilityNotesEn = "Diagram illustrates grading decision tree from 350g sample inspection.",
            accessibilityNotesId = "Diagram menampilkan pohon keputusan penilaian dari sampel 350g.",
            citations = listOf(
                AcademyCitation(
                    "SCA Green Arabica Coffee Defect Guide",
                    "Specialty Coffee Association of America",
                    2004,
                    "https://sca.coffee/education/how-to-prepare"
                )
            )
        ),

        // ====================================================================
        // EDU-17: Triangulation & Discrimination Testing
        // ====================================================================
        AcademyLesson(
            id = "EDU-17",
            moduleNumber = 17,
            category = AcademyCurriculumCategory.CALIBRATION_AND_PANEL_OPS,
            titleEn = "Triangulation: Discrimination Testing & Binomial Mathematics",
            titleId = "Uji Triangulasi: Pengujian Diskriminasi & Matematika Binomial",
            estimatedDurationMinutes = 25,
            objectivesEn = listOf(
                "Master the forced-choice Triangle Test methodology (two alike, one different).",
                "Understand the 6 balanced presentation permutations (AAB, ABA, BAA, BBA, BAB, ABB).",
                "Calculate exact cumulative binomial probabilities against the 1/3 chance baseline."
            ),
            objectivesId = listOf(
                "Menguasai metodologi Triangle Test (dua cangkir sama, satu berbeda).",
                "Memahami 6 permutasi penyajian seimbang (AAB, ABA, BAA, BBA, BAB, ABB).",
                "Menghitung probabilitas kumulatif binomial eksak terhadap peluang tebakan murni 1/3."
            ),
            coreConceptsEn = listOf(
                "Participant must identify the single odd cup among three coded bowls.",
                "Chance baseline probability under null hypothesis is exactly p = 1/3 (~33.3%).",
                "Binomial formula: P(X >= k) = sum_{i=k}^n C(n,i) * (1/3)^i * (2/3)^(n-i)."
            ),
            coreConceptsId = listOf(
                "Peserta wajib menebak satu cangkir ganjil di antara tiga cangkir berkode rahasia.",
                "Peluang tebakan murni secara acak (hipotesis nol) adalah tepat 1/3 (~33.3%).",
                "Rumus Binomial: P(X >= k) = sum_{i=k}^n C(n,i) * (1/3)^i * (2/3)^(n-i)."
            ),
            contentMarkdownEn = ("""
# Triangle Discrimination Testing

The Triangle Test is the international gold standard for sensory discrimination (ISO 4120 / ASTM E1885):
- **Setup**: Three coded samples are presented simultaneously. Two are identical (Reference A), one is""" +
                """ different (Contrast B).
- **The 6 Permutations**:
  1. `A - A - B` (Odd cup: 3)
  2. `A - B - A` (Odd cup: 2)
  3. `B - A - A` (Odd cup: 1)
  4. `B - B - A` (Odd cup: 3)
  5. `B - A - B` (Odd cup: 2)
  6. `A - B - B` (Odd cup: 1)

```
[Bowl #1: 729]    [Bowl #2: 418]    [Bowl #3: 903]
     (A)               (A)               (B)  <--- Odd Cup!
```

### Statistical Significance (Binomial Test)
Because guessing yields a 1/3 success rate, statistical significance requires demonstrating performance well""" +
                """ above chance:
- For n=6 trials: 5 correct required (p = 0.0178) for alpha = 0.05.
- For n=18 trials: 10 correct required (p = 0.0377) for alpha = 0.05.
            """).trimIndent(),
            contentMarkdownId = ("""
# Pengujian Diskriminasi Triangulasi

Uji Triangulasi adalah standar internasional (ISO 4120) untuk menguji kepekaan membedakan dua produk:
- Tiga cangkir berkode acak disajikan bersamaan: 2 cangkir sama, 1 cangkir berbeda.
- Terdapat 6 kombinasi seimbang: AAB, ABA, BAA, BBA, BAB, ABB.
- Peluang menebak benar secara kebetulan adalah 1 banding 3 (33.3%).
- Untuk membuktikan kemampuan diskriminasi pada 6 kali uji coba, peserta wajib menebak benar minimal 5 dari 6""" +
                """ kali (p < 0.05).
            """).trimIndent(),
            asciiDiagram = """
    Cup 1 [294]    Cup 2 [815]    Cup 3 [602]
         A              B              A       ---> Odd Cup is #2
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Null Hypothesis",
                    "Hipotesis Nol",
                    "Statistical assumption that any observed difference is due to chance alone.",
                    "Asumsi statistik bahwa hasil yang diperoleh hanya karena faktor kebetulan tebakan murni."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU17-1",
                    promptEn = "What is the baseline probability of correctly identifying the odd cup in a triangle test by pure random" +
                        " guessing?",
                    promptId = "Berapakah probabilitas dasar menebak cangkir ganjil secara acak murni pada uji triangulasi?",
                    options = listOf(
                        QuizOption("A", "1/3 (~33.3%)", "1/3 (~33.3%)", true),
                        QuizOption("B", "1/2 (50.0%)", "1/2 (50.0%)", false),
                        QuizOption("C", "1/4 (25.0%)", "1/4 (25.0%)", false),
                        QuizOption("D", "1/6 (~16.7%)", "1/6 (~16.7%)", false)
                    ),
                    explanationEn = "With 3 cups and exactly 1 odd cup, a blind guess has a 1 in 3 chance of success.",
                    explanationId = "Dengan 3 cangkir di mana hanya 1 yang berbeda, peluang tebakan murni adalah 1 dari 3 pilihan (33.3%)."
                )
            ),
            accessibilityNotesEn = "Diagram displays 3 cups with opaque 3-digit randomized identification numbers.",
            accessibilityNotesId = "Diagram menampilkan 3 cangkir dengan kode acak 3 digit.",
            citations = listOf(
                AcademyCitation(
                    "ISO 4120: Sensory Analysis — Methodology — Triangle Test",
                    "International Organization for Standardization",
                    2021,
                    "https://www.iso.org/standard/76256.html"
                )
            )
        ),

        // ====================================================================
        // EDU-18: Calibration & Panel Management
        // ====================================================================
        AcademyLesson(
            id = "EDU-18",
            moduleNumber = 18,
            category = AcademyCurriculumCategory.CALIBRATION_AND_PANEL_OPS,
            titleEn = "Panel Calibration: Repeatability, Agreement & Fatigue",
            titleId = "Kalibrasi Panel: Keterulangan, Kesepakatan & Kelelahan",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Measure assessor repeatability (intra-rater consistency).",
                "Calculate inter-rater consensus agreement and standard deviation across panels.",
                "Manage sensory fatigue with palate cleansers and proper session spacing."
            ),
            objectivesId = listOf(
                "Mengukur keterulangan evaluator (konsistensi intra-evaluator).",
                "Menghitung kesepakatan konsensus inter-evaluator dan standar deviasi panel.",
                "Mengelola kelelahan sensori dengan pembersih lidah dan jeda waktu yang tepat."
            ),
            coreConceptsEn = listOf(
                "A calibrated panel produces tight score standard deviations (sigma < 1.0) on identical lots.",
                "Repeatability checks present blind duplicate samples to the same evaluator.",
                "Palate cleansers: room-temperature pure water, unsalted crackers, pectin-free water."
            ),
            coreConceptsId = listOf(
                "Panel yang terkalibrasi menghasilkan standar deviasi nilai yang rapat (sigma < 1.0) pada lot yang sama.",
                "Uji keterulangan menyajikan sampel duplikat buta kepada evaluator yang sama untuk menguji konsistensi nilainya.",
                "Pembersih lidah terbaik: air murni suhu ruang dan biskuit tawar tanpa garam."
            ),
            contentMarkdownEn = ("""
# Sensory Panel Management

Professional cupping panels calibrate regularly to align descriptive vocabulary and scoring scales:
- **Intra-Rater Repeatability**: Assessor evaluates blind duplicate cups of the same coffee. Scores should""" +
                """ fall within $\pm 0.5$ points.
- **Inter-Rater Consensus**: Difference between panel members. High agreement exhibits low standard deviation""" +
                """ across all attributes ($\sigma < 0.75$).
- **Mitigating Palate Fatigue**:
  - Limit sessions to a maximum of 3 tables (18–24 samples) per day.
  - Rinse with lukewarm filtered water between bowls; avoid carbonated beverages or spicy foods before""" +
                """ cupping.
            """).trimIndent(),
            contentMarkdownId = ("""
# Manajemen Panel Sensori

Panel cupping profesional rutin melakukan kalibrasi:
- **Konsistensi Personal (Intra-Rater)**: Evaluator menilai sampel duplikat yang disamarkan tanpa tahu itu""" +
                """ kopi yang sama. Nilainya harus konsisten dalam rentang $\pm 0.5$ poin.
- **Kesepakatan Antar-Penilai (Inter-Rater)**: Standar deviasi antar anggota panel harus rapat ($\sigma <""" +
                """ 0.75$).
- **Mengatasi Kelelahan Lidah**: Batasi maksimal 18–24 sampel per hari. Selalu kumur dengan air murni suhu""" +
                """ ruang di antara cangkir.
            """).trimIndent(),
            asciiDiagram = """
    Individual Cuppers -> Independent Blind Scoring -> Panel Aggregation -> Consensus Discussion
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Inter-Rater Reliability",
                    "Reliabilitas Antar-Penilai",
                    "Degree of agreement among independent observers.",
                    "Tingkat kesepakatan nilai di antara pengamat yang independen."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU18-1",
                    promptEn = "What is the recommended maximum number of cupping samples per session to prevent sensory fatigue?",
                    promptId = "Berapa batas sampel cupping yang disarankan per sesi untuk mencegah kelelahan lidah?",
                    options = listOf(
                        QuizOption("A", "18 to 24 samples (3 to 4 tables)", "18 hingga 24 sampel (3 hingga 4 meja)", true),
                        QuizOption("B", "100 samples", "100 sampel", false),
                        QuizOption("C", "Unlimited as long as coffee is hot", "Tak terbatas selama kopi masih panas", false),
                        QuizOption("D", "Only 1 sample per day", "Hanya 1 sampel per hari", false)
                    ),
                    explanationEn = "Sensory adaptation and receptor fatigue degrade discrimination accuracy after tasting approximately" +
                        " 20 to 25 samples.",
                    explanationId = "Adaptasi sensori dan kejenuhan reseptor menurunkan ketajaman penilaian setelah mencicipi sekitar 20" +
                        " hingga 25 cangkir."
                )
            ),
            accessibilityNotesEn = "Diagram shows progression from individual blind score to panel consensus.",
            accessibilityNotesId = "Diagram menunjukkan alur dari skor individu ke konsensus panel.",
            citations = listOf(
                AcademyCitation("Sensory Panel Management", "Kemp, S. E. et al.", 2018, "https://doi.org/10.1002/9781119994336")
            )
        ),

        // ====================================================================
        // EDU-19: Roast Defects & Confounding
        // ====================================================================
        AcademyLesson(
            id = "EDU-19",
            moduleNumber = 19,
            category = AcademyCurriculumCategory.STANDARDS_AND_PROTOCOLS,
            titleEn = "Roasting Defects: Baked, Scorched, Underdeveloped & Quakers",
            titleId = "Cacat Penyangraian: Baked, Scorched, Underdeveloped & Quaker",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Identify physical and sensory profiles of roasting defects.",
                "Differentiate bean agricultural faults from roasting operational errors.",
                "Recognize underdeveloped (grassy/peanut) vs baked (flat/bread) cups."
            ),
            objectivesId = listOf(
                "Mengidentifikasi profil fisik dan rasa dari cacat sangrai.",
                "Membedakan cacat pertanian biji dari kesalahan operasional sangrai.",
                "Mengenali rasa underdeveloped (rumput/kacang mentah) vs baked (roti hambar/flat)."
            ),
            coreConceptsEn = listOf(
                "Underdevelopment: heat did not penetrate core; high chlorogenic acid, grassy, vegetal, raw cereal taste.",
                "Baked: stalled rate of rise (RoR); destruction of sugars and acidity, flat, dull, bread-like.",
                "Scorched/Facing: drum heat too high; charred bean surfaces, smoky, ashy, bitter.",
                "Quakers: immature beans lacking sugar that fail to brown; peanut shell, straw, paper taste."
            ),
            coreConceptsId = listOf(
                "Underdeveloped: panas gagal menembus inti biji; rasa rumput, kacang tanah mentah, dan sepat.",
                "Baked: laju kenaikan suhu (RoR) terhenti (stall); gula hancur tanpa karamelisasi, rasa roti tawar hambar dan datar.",
                "Scorched: drum terlalu panas; permukaan biji gosong bertotol, rasa abu dan asap arang.",
                "Quakers: biji muda kurang gula yang tidak bisa cokelat saat disangrai; rasa kulit kacang dan jerami."
            ),
            contentMarkdownEn = ("""
# Roasting Defects in Sensory Evaluation

Roasting errors must not be conflated with the intrinsic green coffee quality:
1. **Underdeveloped**: Internal core fails to reach complete pyrolysis. Leaves unreacted chlorogenic acids.""" +
                """ Sensory: *raw green peas, grassy, drying peanut skin, puckering sourness*.
2. **Baked**: Heat transfer stalls during the roast profile, stretching out time without temperature""" +
                """ advancement. Destroys acidity and aromatics. Sensory: *flat, cardboard, dry toast, lack of sweetness*.
3. **Scorched & Faced**: Charging into an overly hot drum or excessive contact heat burns the bean exterior""" +
                """ while the interior lags. Sensory: *smoky, bitter charcoal, acrid pungency*.
4. **Quakers**: Caused by immature cherries with insufficient carbohydrates to undergo the Maillard reaction.""" +
                """ Remain pale light tan post-roast. Sensory: *peanut shell, paper, dry straw*.
            """).trimIndent(),
            contentMarkdownId = ("""
# Cacat Penyangraian dalam Evaluasi Sensori

Kesalahan sangrai tidak boleh disalahartikan sebagai kualitas bawaan biji hijau:
1. **Underdeveloped**: Panas gagal mematangkan bagian tengah biji. Rasa: *rumput segar, kacang tanah mentah,""" +
                """ keasaman menusuk tak sedap*.
2. **Baked**: Profil sangrai terhenti (stalled) terlalu lama. Gula dan aroma rusak. Rasa: *roti tawar hambar,""" +
                """ kardus, hampa tanpa rasa manis*.
3. **Scorched (Gosong Permukaan)**: Drum pemanggang terlalu panas. Rasa: *arang pahit, asap menusuk*.
4. **Quaker**: Berasal dari biji mentah (immature) yang kekurangan gula sehingga warnanya tetap pucat kuning""" +
                """ saat disangrai. Rasa: *kulit kacang tanah, kertas, jerami kering*.
            """).trimIndent(),
            asciiDiagram = """
    Normal Roast        : Even golden-brown color + sweet caramelization
    Underdeveloped Roast: Light core + grassy astringent taste
    Baked Roast         : Stalled RoR curve + flat lifeless cup
    Scorched Roast      : Burn spots on bean surface + acrid smoke taste
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Quaker",
                    "Quaker",
                    "Immature coffee bean that fails to roast to normal dark color due to lack of reducing sugars.",
                    "Biji kopi muda yang warnanya tetap pucat saat disangrai karena kekurangan gula pereduksi."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU19-1",
                    promptEn = "What sensory character is typical of a 'baked' roast defect?",
                    promptId = "Karakter sensori apa yang khas pada cacat sangrai 'baked'?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "Dull, flat, bread-like taste with loss of sweetness and acidity",
                            "Rasa datar hambar mirip roti tawar dengan hilangnya rasa manis dan asam",
                            true
                        ),
                        QuizOption("B", "Vibrant tropical fruit acidity", "Keasaman buah tropis cerah", false),
                        QuizOption("C", "Intense floral aroma", "Aroma bunga yang tajam", false),
                        QuizOption("D", "Sweet creamy vanilla", "Manis krim vanila", false)
                    ),
                    explanationEn = "A baked roast occurs when heat transfer stalls, dissipating sweet aromatics and leaving a dull," +
                        " flat, bread/toast character.",
                    explanationId = "Cacat baked terjadi saat laju panas terhenti, mengikis keasaman dan kemanisan, menyisakan rasa datar" +
                        " mirip roti panggang hambar."
                )
            ),
            accessibilityNotesEn = "Diagram contrasts visual bean cross-sections across roasting defect types.",
            accessibilityNotesId = "Diagram membandingkan penampang fisik biji pada berbagai cacat sangrai.",
            citations = listOf(
                AcademyCitation("The Coffee Roaster's Companion", "Rao, S.", 2014, "https://www.scottrao.com/")
            )
        ),

        // ====================================================================
        // EDU-20: Professional Reporting & Ethics
        // ====================================================================
        AcademyLesson(
            id = "EDU-20",
            moduleNumber = 20,
            category = AcademyCurriculumCategory.ETHICS_GOVERNANCE_ACCESSIBILITY,
            titleEn = "Professional Ethics, Governance & Cupping Data Integrity",
            titleId = "Etika Profesional, Tata Kelola & Integritas Data Cupping",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Maintain data integrity: immutable audit logs and versioned amendments.",
                "Adhere to ethical commercial standards: protect grower identity during blind panels.",
                "Ensure inclusive accessibility for assessors with color vision or dietary limits."
            ),
            objectivesId = listOf(
                "Menjaga integritas data: log audit yang tidak dapat diubah (immutable) dan revisi bernomor.",
                "Mematuhi standar etika perdagangan: melindungi identitas produsen saat uji buta.",
                "Memastikan aksesibilitas inklusif bagi penilai dengan keterbatasan visual atau pantangan diet."
            ),
            coreConceptsEn = listOf(
                "Once a cupping session is locked, original scores cannot be silently deleted.",
                "Amendments must record operator identity, timestamp, and explicit justification.",
                "Cupping data influences livelihoods; scores must be scientifically defensible."
            ),
            coreConceptsId = listOf(
                "Setelah sesi cupping dikunci, nilai awal tidak boleh dihapus diam-diam.",
                "Revisi wajib mencatat identitas pelaku, stempel waktu, dan alasan yang jelas.",
                "Nilai cupping mempengaruhi harga jual dan nafkah petani; penilaian harus objektif dan dapat dipertanggungjawabkan."
            ),
            contentMarkdownEn = ("""
# Data Integrity & Professional Ethics

In the global specialty coffee trade, cupping scores dictate contract pricing and producer livelihoods:
1. **Audit Trails**: In Coffee Creator, locking a session produces a SHA-256 cryptographic digest. Any change""" +
                """ creates an explicit `SessionAmendment` record documenting who made the change, when, and why.
2. **Commercial Neutrality**: Blind codes prevent bias towards prestigious estates or against emerging""" +
                """ smallholders.
3. **Inclusive Assessment**:
   - Provide high-contrast text and screen reader support for visually impaired cuppers.
   - Do not require color discrimination as the sole feedback channel.
            """).trimIndent(),
            contentMarkdownId = ("""
# Integritas Data & Etika Profesional

Nilai cupping menentukan harga kontrak dan kelangsungan hidup petani kopi:
1. **Jejak Audit**: Mengunci sesi menghasilkan tanda tangan digital SHA-256. Setiap koreksi lanjutan dicatat""" +
                """ dalam riwayat revisi (amendment) yang transparan.
2. **Netralitas Perdagangan**: Penomoran buta mencegah perlakuan istimewa pada perkebunan besar.
3. **Aksesibilitas Inklusif**: Menyediakan pembaca layar (TalkBack), kontras tinggi, dan tidak bergantung pada""" +
                """ warna saja.
            """).trimIndent(),
            asciiDiagram = """
    Session Draft -> Complete -> Lock Session (SHA-256 Hash) -> Amendments require Audited Justification
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Audit Trail",
                    "Jejak Audit",
                    "Chronological record providing documentary evidence of sequence of activities.",
                    "Catatan kronologis yang memberikan bukti dokumenter dari setiap perubahan data."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU20-1",
                    promptEn = "Why must cupping score sheets have an immutable audit trail upon locking?",
                    promptId = "Mengapa lembar nilai cupping harus memiliki jejak audit yang tidak bisa diubah setelah dikunci?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "To prevent retroactive manipulation of commercial grading data that affects contracts",
                            "Untuk mencegah manipulasi nilai mutu komersial yang mempengaruhi kontrak dagang",
                            true
                        ),
                        QuizOption("B", "To make it impossible to correct obvious typos", "Agar salah ketik tidak bisa diperbaiki", false),
                        QuizOption("C", "Because paper forms cannot be erased", "Karena kertas tidak bisa dihapus", false),
                        QuizOption("D", "To hide errors from the client", "Untuk menyembunyikan kesalahan", false)
                    ),
                    explanationEn = "Cupping scores drive financial transactions; an immutable audit trail ensures transparency and" +
                        " protects against fraudulent score tampering.",
                    explanationId = "Nilai cupping mendasari transaksi keuangan jual-beli kopi; jejak audit menjamin transparansi dan" +
                        " mencegah manipulasi skor secara ilegal."
                )
            ),
            accessibilityNotesEn = "Diagram emphasizes cryptographic lock state and revision tracking.",
            accessibilityNotesId = "Diagram menegaskan status penguncian digital dan pelacakan revisi.",
            citations = listOf(
                AcademyCitation(
                    "Ethical Cupping Practices and Supply Chain Transparency",
                    "Specialty Coffee Association",
                    2022,
                    "https://sca.coffee/research"
                )
            )
        ),

        // ====================================================================
        // EDU-21: Legacy 2004 vs Modern CVA
        // ====================================================================
        AcademyLesson(
            id = "EDU-21",
            moduleNumber = 21,
            category = AcademyCurriculumCategory.STANDARDS_AND_PROTOCOLS,
            titleEn = "Legacy 2004 SCA Form vs Modern CVA: Why Standards Evolved",
            titleId = "Form SCA 2004 Warisan vs CVA Modern: Mengapa Standar Berevolusi",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Understand the design and limitations of the historic 2004 SCA Cupping Form.",
                "Know why legacy scores cannot be mathematically converted to CVA scores.",
                "Correctly label historical data as LEGACY in the application."
            ),
            objectivesId = listOf(
                "Memahami struktur dan keterbatasan formulir cupping warisan SCA 2004.",
                "Mengetahui mengapa nilai lama 2004 tidak dapat dikonversi secara matematis ke CVA.",
                "Memberi label LEGACY secara tepat pada data historis di dalam aplikasi."
            ),
            coreConceptsEn = listOf(
                "The 2004 form combined 10 categories into a single 100-point total score.",
                "CVA separates descriptive facts from affective liking; there is no 1-to-1 conversion formula.",
                "Coffee Creator preserves the 2004 form as an archived LEGACY protocol."
            ),
            coreConceptsId = listOf(
                "Formulir 2004 menggabungkan 10 kategori ke dalam skor total tunggal 100 poin.",
                "CVA memisahkan fakta deskriptif dari apresiasi afektif; tidak ada rumus konversi langsung 1 banding 1.",
                "Coffee Creator mempertahankan form 2004 sebagai protokol arsip berlabel LEGACY."
            ),
            contentMarkdownEn = ("""
# Legacy 2004 Cupping vs Modern CVA

The historic 2004 SCA Cupping Form served the specialty coffee industry for two decades. However, sensory""" +
                """ science revealed core structural flaws:
- **Conflation of Scales**: Descriptors (e.g. "citrus") were directly tied to numerical point scores.
- **The "80-Point Specialty Cliff"**: Coffees below 80 points were labeled non-specialty, creating artificial""" +
                """ economic penalties.
- **Inability to Separate Preference**: Cuppers unconsciously penalized unfamiliar or experimental processing""" +
                """ profiles.

### No Cross-Protocol Conversion Rule
There is **NO valid mathematical equation** to convert an 85.5 score from the 2004 form into CVA. They measure""" +
                """ different sensory constructs.
            """).trimIndent(),
            contentMarkdownId = ("""
# Form Cupping Warisan 2004 vs CVA Modern

Formulir SCA 2004 telah berjasa besar selama 20 tahun. Namun sains sensori modern menemukan beberapa kelemahan""" +
                """ mendasar:
- **Pencampuran Skala**: Kata deskriptif langsung terikat pada angka nilai.
- **Jurang Skor 80 Poin**: Kopi di bawah 80 langsung dianggap non-specialty.
- **Ketidakmampuan Memisahkan Selera**: Evaluator sering memotong nilai kopi yang karakternya tidak ia sukai""" +
                """ secara pribadi.

### Tidak Ada Konversi Skor Otomatis
SCA secara tegas menyatakan **TIDAK ADA rumus matematika** untuk mengubah nilai 86 pada form 2004 menjadi skor""" +
                """ CVA. Keduanya mengukur konsep yang berbeda.
            """).trimIndent(),
            asciiDiagram = """
    2004 Legacy Form (100-pt Single Sheet) =/= CVA (4 Independent Perspectives)
    [Never attempt automated numeric conversion between protocols]
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "Legacy Protocol",
                    "Protokol Warisan",
                    "Historical standard that has been officially superseded by updated methodologies.",
                    "Standar historis yang secara resmi telah digantikan oleh metodologi terbaru."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU21-1",
                    promptEn = "Can an 86-point score from the legacy 2004 form be automatically converted to a CVA score?",
                    promptId = "Bisakah nilai 86 poin dari formulir lama 2004 dikonversi secara otomatis ke skor CVA?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "No, because the protocols measure fundamentally different sensory constructs",
                            "Tidak, karena kedua protokol mengukur konsep sensori yang berbeda secara fundamental",
                            true
                        ),
                        QuizOption("B", "Yes, by adding exactly 5 points", "Ya, dengan menambah 5 poin", false),
                        QuizOption("C", "Yes, using a linear percentage multiplier", "Ya, menggunakan perkalian persentase", false),
                        QuizOption("D", "Yes, CVA is identical to the 2004 form", "Ya, CVA identik dengan form 2004", false)
                    ),
                    explanationEn = "SCA guidelines state explicitly that there is no score equivalence or conversion between the legacy" +
                        " 2004 cupping form and CVA assessment.",
                    explanationId = "Panduan resmi SCA menegaskan tidak ada kesetaraan skor atau rumus konversi antara form lama 2004 dan" +
                        " sistem CVA modern."
                )
            ),
            accessibilityNotesEn = "Diagram emphasizes non-equivalence between legacy and modern protocols.",
            accessibilityNotesId = "Diagram menegaskan bahwa kedua protokol tidak dapat dikonversi satu sama lain.",
            citations = listOf(
                AcademyCitation("The Evolution of Cupping Standards", "Specialty Coffee Association", 2024, "https://sca.coffee/sca-news")
            )
        ),

        // ====================================================================
        // EDU-22: Q-Grader Pathways & Professional Literacy
        // ====================================================================
        AcademyLesson(
            id = "EDU-22",
            moduleNumber = 22,
            category = AcademyCurriculumCategory.ETHICS_GOVERNANCE_ACCESSIBILITY,
            titleEn = "Q Grader Pathways: Certification Literacy & Practice Limits",
            titleId = "Jalur Q Grader: Literasi Sertifikasi & Batasan Aplikasi",
            estimatedDurationMinutes = 20,
            objectivesEn = listOf(
                "Understand the official Q Grader certification process governed by the Coffee Quality Institute (CQI).",
                "Know the exam modules: Sensory Skills, Triangulation, Olfactory, Cupping, Green/Roast Grading.",
                "Recognize that mobile app drills provide practice only and cannot confer official credentials."
            ),
            objectivesId = listOf(
                "Memahami proses sertifikasi resmi Q Grader yang diselenggarakan oleh Coffee Quality Institute (CQI).",
                "Mengenal materi ujian: Keterampilan Sensori, Triangulasi, Olfaktori, Cupping, Grading Biji Hijau/Sangrai.",
                "Menyadari bahwa latihan aplikasi ponsel hanya sarana belajar mandiri dan tidak memberikan gelar resmi."
            ),
            coreConceptsEn = listOf(
                "Official Q Grader certification requires an intensive in-person examination under a licensed CQI instructor.",
                "Exam topics: Olfactory test, sensory taste thresholds, triangulation, cupping calibration, green grading.",
                "Coffee Creator is an independent practice tool; it does not award official certificates or licenses."
            ),
            coreConceptsId = listOf(
                "Sertifikasi resmi Q Grader menuntut ujian tatap muka intensif di lab terakreditasi bersama instruktur berlisensi CQI.",
                "Materi ujian: Uji olfaktori, ambang rasa sensori, triangulasi, kalibrasi cupping, grading biji hijau.",
                "Coffee Creator adalah sarana latihan mandiri; aplikasi tidak memberikan sertifikat atau gelar profesional resmi."
            ),
            contentMarkdownEn = ("""
# Understanding the Q Grader Program

The Q Coffee System, administered by the **Coffee Quality Institute (CQI)**, is the premier international""" +
                """ professional credential for sensory evaluation:
- **Course & Exam Components**:
  1. *Sensory Skills*: Identifying basic taste modalities and complex mixtures of sweet, sour, and salty.
  2. *Cupping Calibration*: Scoring flights across multiple tables within panel consensus tolerances.
  3. *Triangulation*: Identifying the odd cup across multiple flights under red light.
  4. *Olfactory Skills*: Blind identification of aroma families.
  5. *Green & Roasted Coffee Grading*: Identifying physical defects and quakers.

> [!IMPORTANT]
> **Regulatory Notice**: Practicing on Coffee Creator builds sensory skill and familiarity, but does **NOT**""" +
                """ grant CQI Q Grader or R Grader credentials. Official certification requires passing sanctioned exams at an""" +
                """ accredited lab.
            """).trimIndent(),
            contentMarkdownId = ("""
# Mengenal Program Q Grader

Program Q Grader yang dikelola oleh **Coffee Quality Institute (CQI)** adalah standar sertifikasi penilai mutu""" +
                """ sensori kopi internasional:
- **Materi Ujian**:
  1. *Keterampilan Sensori*: Mengidentifikasi larutan rasa manis, asam, asin pada berbagai konsentrasi.
  2. *Kalibrasi Cupping*: Konsistensi nilai cupping bersama instruktur dan panel.
  3. *Uji Triangulasi*: Menemukan 1 cangkir berbeda di bawah penerangan lampu merah.
  4. *Keterampilan Olfaktori*: Menebak aroma botol fisik secara buta.
  5. *Grading Fisik*: Menemukan cacat pada biji hijau dan sangrai.

> [!IMPORTANT]
> **Pemberitahuan Resmi**: Latihan pada aplikasi Coffee Creator melatih kepekaan dan pemahaman Anda, namun""" +
                """ **BUKAN** pengganti sertifikasi resmi CQI Q Grader. Sertifikat resmi hanya bisa didapatkan melalui ujian""" +
                """ langsung di lab terakreditasi.
            """).trimIndent(),
            asciiDiagram = """
    Mobile Practice (Coffee Creator) ---> Build Acuity ---> Official In-Person Exam (CQI Accredited Lab)
            """.trimIndent(),
            glossary = listOf(
                GlossaryEntry(
                    "CQI",
                    "CQI",
                    "Coffee Quality Institute: non-profit organization managing the Q Coffee certification system.",
                    "Organisasi nirlaba pengelola sistem sertifikasi kopi Q Grader dunia."
                )
            ),
            practiceQuestions = listOf(
                AcademyQuizQuestion(
                    id = "Q-EDU22-1",
                    promptEn = "Does completing all exercises in this app make you a certified Q Grader?",
                    promptId = "Apakah menyelesaikan semua latihan di aplikasi ini otomatis membuat Anda menjadi Q Grader bersertifikat?",
                    options = listOf(
                        QuizOption(
                            "A",
                            "No, official certification requires in-person examination at a CQI-accredited laboratory",
                            "Tidak, sertifikasi resmi mewajibkan ujian tatap muka di lab terakreditasi CQI",
                            true
                        ),
                        QuizOption(
                            "B",
                            "Yes, this app issues valid CQI digital certificates",
                            "Ya, aplikasi ini mengeluarkan sertifikat CQI resmi",
                            false
                        ),
                        QuizOption("C", "Yes, upon scoring 100% on the quizzes", "Ya, jika nilai kuis 100%", false),
                        QuizOption("D", "Yes, recognized by international export agencies", "Ya, diakui oleh dinas ekspor", false)
                    ),
                    explanationEn = "Coffee Creator provides educational practice only. Official Q Grader credentials require rigorous" +
                        " in-person laboratory examinations overseen by licensed CQI instructors.",
                    explanationId = "Aplikasi Coffee Creator hanya sarana latihan belajar mandiri. Gelar resmi Q Grader secara hukum" +
                        " menuntut ujian tatap muka langsung di lab resmi berlisensi CQI."
                )
            ),
            accessibilityNotesEn = "Diagram emphasizes that mobile practice leads to, but does not replace, in-person exam.",
            accessibilityNotesId = "Diagram menegaskan latihan mandiri melatih kemampuan menuju ujian tatap muka resmi.",
            citations = listOf(
                AcademyCitation("Q Grader Program Overview", "Coffee Quality Institute", 2024, "https://www.coffeeinstitute.org/")
            )
        )
    )

    fun findLessonById(id: String): AcademyLesson? = allLessons.firstOrNull { it.id.equals(id, ignoreCase = true) }
}
