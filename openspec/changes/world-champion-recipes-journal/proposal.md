# Proposal: 76 World Champion, Signature & Traditional Recipes Catalog in Journal

## Problem Statement
Specialty coffee practitioners and baristas need authoritative, source-attributed references for world-class brewing routines. While Coffee Creator supports custom recipe formulation and manual journal logging, users lack direct access to verified championship routines (World Brewers Cup, World AeroPress Championship), signature methods from world-renowned baristas (James Hoffmann, Tetsu Kasuya, Scott Rao, Lance Hedrick), and heritage traditional recipes (Kopi Tubruk, Kopi Joss, Cà Phê Sữa Đá, Greek/Turkish Cezve) directly within their brew journal.

## Objectives
1. **Curated World Champion Catalog (76 Recipes)**:
   - Provide an offline, pure Kotlin catalog of 76 recipes across 3 distinct categories:
     - **Competition** (WBrC & WAC champions & podium finishers: Tetsu Kasuya 4:6, George Peng, Martin Wölfl, George Stanica, Némo Pop, Tay Wipvasutt, Jibbi Little, Wendelien van Bunnik, Bayu Prawiro, Carlos Medina, Chad Wang, Emi Fukahori, Sherry Hsu, Matt Perger, Ryan Wibawa, Sophan Nugraha, Jan Ahrend, Dharun Vyas, Du Jianing, Carolina Garay).
     - **Signature** (Hoffmann Ultimate V60, 1-Cup V60, AeroPress, French Press, Lance Hedrick 1-2-1, Scott Rao, Simon Gautherin, Square Mile 5-Pour, Tetsu Kasuya 10-Pour, Ultimate Switch God/Devil, New Ultimate Switch 2025, Kurasu Kyoto Iced Switch, Sightglass, Bayu Prawiro Beans Lokal).
     - **Traditional / Standard** (AeroPress Inverted/Upright, Affogato, Bandrek, Bee House, Cà Phê Sữa Đá, Café de Olla, Latte, Cappuccino, Cascara, Chemex, Clever, Cold Brew, Cold Drip, Cortado, Es Kopi Pandan, Es Kopi Susu Gula Aren, Espresso Ristretto/Normale/Lungo, Batch Brew, Flat White, French Press, Greek Coffee, Japanese Iced, Jebena, Kalita Wave, Kopi Joss, Kopi Kothok, Kopi Rarobang, Kopi Sanger, Kopi Talua, Kopi Tubruk, Melitta, Moka Pot, Napoletana, Nitro, Origami, Percolator, Red Eye, Siphon, Turkish Coffee, V60 Pemula, Vietnamese Phin).
2. **Epistemological Provenance & Verification Badges**:
   - Explicitly categorize verification statuses:
     - `OFFICIAL`: Verified from championship organizers or authors.
     - `DOCUMENTED_RECREATION`: Documented recreation by specialists/manufacturers.
     - `SECONDARY_ADAPTATION`: Standardized adaptation; not authenticated original.
     - `NEEDS_VERIFICATION`: Traditional / heritage recipes needing individual roaster calibration.
3. **Seamless 1-Tap Journal & Timer Integration**:
   - `Log to Journal`: Instantly creates an immutable `BrewJournalEntry` with exact extraction parameters, recipe tags, and provenance notes.
   - `Brew with Timer`: Arms the multi-stage pour steps into the active chronometer for guided brewing.
   - `Save as Template`: Saves the recipe directly into the user's Recipe Composer library.
4. **Tactile Industrial & Anti-AI Slop UI**:
   - Dedicated `JournalTab.CHAMPION_RECIPES` ("World Recipes") with single-line horizontal filter rows, search bar, tabular figures (`tnum`), and 48dp tactile buttons.
