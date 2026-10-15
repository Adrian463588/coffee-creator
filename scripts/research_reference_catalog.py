"""Development-only primary-source research; never shipped as runtime networking.

Usage: python scripts/research_reference_catalog.py [--verify]
Requires requests and beautifulsoup4 in the development environment.
Only short factual name/group/lineage fields are retained, not source prose/images.
"""

from __future__ import annotations

import argparse
import concurrent.futures
from datetime import datetime, timedelta, timezone
import hashlib
import io
import json
from pathlib import Path
import time
import unicodedata
import xml.etree.ElementTree as ET
import zipfile

import requests
from bs4 import BeautifulSoup


ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "app/src/main/assets/reference/varieties-v3.json"
SLUGS = (
    "bourbon typica caturra catuai mundo-novo maragogipe pacamara pacas pache "
    "villa-sarchi geisha-panama java sl14 sl28 sl34 k7 batian ruiru-11 kartika-1 "
    "s795 sln-5b sln-6 costa-rica-95 ihcafe-90 lempira parainema marsellesa "
    "obata-red iapar-59 starmaya centroamericano anacafe-14"
).split()
BASE = "https://varieties.worldcoffeeresearch.org/varieties/"
CACHE = ROOT / "artifacts/reference-sources"
GROUPS = {
    "Bourbon-Typica group (Bourbon related)": "Arabica: Bourbon-related",
    "Bourbon-Typica group (Typica related)": "Arabica: Typica-related",
    "Bourbon-Typica group (Typica and Bourbon related)": "Arabica: Typica/Bourbon-related",
    "Ethiopian landrace": "Arabica: Ethiopian landrace",
    "Introgressed (Catimor related)": "Arabica: introgressed, Catimor-related",
    "Introgressed (Sarchimor related)": "Arabica: introgressed, Sarchimor-related",
    "Introgressed (Other)": "Arabica: other introgressed ancestry",
    "F1 hybrid (introgressed)": "F1 hybrid: introgressed",
    "F1 hybrid (not introgressed)": "F1 hybrid: no reported introgression",
}
LINEAGE = {
    "bourbon": "Bourbon-like ancestry.",
    "typica": "Aliases: Criollo; Indio; Arabigo; Plume Hidalgo; Blue Mountain; Sumatra.",
    "caturra": "Mutation: Bourbon.",
    "maragogipe": "Mutation: Typica.",
    "pacas": "Mutation: Bourbon.",
    "pache": "Mutation: Typica.",
    "villa-sarchi": "Mutation: Bourbon.",
    "sl14": "Typica-like ancestry.",
    "sl28": "Tanganika Drought Resistant selection; Bourbon-like ancestry.",
    "sl34": "Typica-like ancestry.",
    "k7": "French Mission selection; Bourbon-like ancestry.",
    "batian": "Composite ancestry: SL28, SL34, Rume Sudan, N39, K7, SL4, Timor Hybrid.",
    "ruiru-11": "Composite cross: Catimor (maternal) x multicross K7/SL28/N39/Rume Sudan and others (paternal).",
    "sln-6": "S.274 (C. canephora) x Kent (C. arabica).",
}
ACCESSION_TABLE_URL = (
    "https://journals.plos.org/plosone/article/file?"
    "id=10.1371/journal.pone.0299493.s002&type=supplementary"
)


def retrieved_date() -> str:
    return datetime.now(timezone(timedelta(hours=7))).date().isoformat()


def clean(text: str) -> str:
    return " ".join(text.replace("\u00ad", "").replace("\u200b", "").split())


def fetch(slug: str, refresh: bool = False) -> dict:
    url = BASE + slug
    cached = CACHE / f"{slug}.html"
    if cached.exists() and not refresh:
        body = cached.read_bytes()
    else:
        for attempt in range(3):
            try:
                response = requests.get(
                    url,
                    headers={"User-Agent": "CoffeeCreator-ReferenceResearch/1.0 (bounded fact verification)"},
                    timeout=45,
                )
                response.raise_for_status()
                body = response.content
                break
            except requests.RequestException:
                if attempt == 2:
                    raise
                time.sleep(1 + attempt)
        CACHE.mkdir(parents=True, exist_ok=True)
        cached.write_bytes(body)
    if len(body) > 2_000_000:
        raise ValueError(f"Oversized source: {slug}")
    soup = BeautifulSoup(body, "html.parser")

    def field(label: str) -> str:
        node = soup.find(string=lambda value: value and value.strip() == label)
        if node is None:
            raise ValueError(f"Source schema changed, missing {label}: {slug}")
        sibling = node.parent.parent.find_next_sibling()
        return clean(sibling.get_text(" ", strip=True))

    raw = {
        "name": clean(soup.h1.get_text()),
        "geneticGroup": field("Genetic Description"),
        "lineage": field("Lineage"),
    }
    result = {
        "id": "reference-wcr-" + slug,
        "name": raw["name"],
        "geneticGroup": GROUPS[raw["geneticGroup"]],
        "lineage": LINEAGE.get(slug, raw["lineage"]),
        "sourceUrl": url,
        "sourceSha256": hashlib.sha256(body).hexdigest(),
        "retrievedAt": retrieved_date(),
        "sourceFieldsSha256": {key: hashlib.sha256(value.encode("utf-8")).hexdigest() for key, value in raw.items()},
    }
    if not result["name"] or not result["lineage"]:
        raise ValueError(f"Missing primary fact: {slug}")
    return result


def generate_kotlin() -> None:
    """Generate the compile-time view from reviewed JSON; no network required."""
    reviewed = json.loads(OUTPUT.read_text(encoding="utf-8"))
    lines = [
        "package com.coffeecreator.app.core.data",
        "",
        "/** Generated from reference/varieties-v3.json; regenerate with research_reference_catalog.py --generate-kotlin. */",
        "internal object ReferenceCatalogRecords {",
        "    val entries = listOf(",
    ]
    def quoted(value: str) -> str:
        return json.dumps(value, ensure_ascii=False).replace("$", "\\$")
    for entry in reviewed["entries"]:
        lines.append("        VarietyReference(")
        for field in ("id", "name", "geneticGroup", "lineage", "sourceUrl", "sourceSha256", "retrievedAt"):
            lines.append(f"            {field} = {quoted(entry[field])},")
        lines.append("            sourceFieldsSha256 = mapOf(")
        for field, digest in entry["sourceFieldsSha256"].items():
            lines.append(f"                {quoted(field)} to {quoted(digest)},")
        lines[-1] = lines[-1].removesuffix(",")
        lines.extend(["            )", "        ),"])
    lines[-1] = lines[-1].removesuffix(",")
    lines.extend(["    )", "}", ""])
    destination = ROOT / "app/src/main/java/com/coffeecreator/app/core/data/ReferenceCatalogRecords.kt"
    destination.write_text("\n".join(lines), encoding="utf-8")
    print(f"Generated {len(reviewed['entries'])} compile-time reference records.")


def cross_check_accession_names() -> None:
    """CC0 primary accession metadata corroborates names, never commercial lot origin."""
    cache = CACHE / "plos-0299493-s002.docx"
    if cache.exists():
        body = cache.read_bytes()
    else:
        response = requests.get(ACCESSION_TABLE_URL, timeout=60)
        response.raise_for_status()
        body = response.content
        CACHE.mkdir(parents=True, exist_ok=True)
        cache.write_bytes(body)
    with zipfile.ZipFile(io.BytesIO(body)) as document:
        table = ET.fromstring(document.read("word/document.xml"))
    namespace = {"w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main"}
    rows = [
        [clean("".join(cell.itertext())) for cell in row.findall("w:tc", namespace)]
        for row in table.findall(".//w:tr", namespace)
    ]
    def key(value: str) -> str:
        return "".join(character.lower() for character in unicodedata.normalize("NFKD", value) if character.isalnum())
    aliases = {"costarica95": "cr95"}
    reviewed = json.loads(OUTPUT.read_text(encoding="utf-8"))
    matches = []
    for entry in reviewed["entries"]:
        expected = aliases.get(key(entry["name"]), key(entry["name"]))
        related = [row for row in rows[1:] if key(row[1]) == expected]
        if related:
            matches.append({
                "referenceId": entry["id"],
                "accessions": [{"code": row[0], "name": row[1], "holdingInstitution": row[5]} for row in related],
            })
    reviewed["independentAccessionCheck"] = {
        "sourceUrl": ACCESSION_TABLE_URL,
        "articleUrl": "https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0299493",
        "sourceSha256": hashlib.sha256(body).hexdigest(),
        "retrievedAt": retrieved_date(),
        "license": "CC0-1.0",
        "licenseUrl": "https://creativecommons.org/publicdomain/zero/1.0/",
        "attribution": "Millet et al. (2024), PLOS ONE 19(4): e0299493, S2 Table",
        "scope": "Named accession existence only. Not validation of universal variety genetics or commercial lot origin.",
        "matches": matches,
    }
    OUTPUT.write_text(json.dumps(reviewed, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Verified named accessions for {len(matches)} catalog references against primary CC0 S2 Table.")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--verify", action="store_true", help="Verify reviewed facts; do not replace receipts.")
    parser.add_argument("--refresh", action="store_true", help="Fetch primary pages again instead of using research cache.")
    parser.add_argument("--generate-kotlin", action="store_true", help="Regenerate runtime records from reviewed JSON without network.")
    parser.add_argument("--accession-check", action="store_true", help="Record independent CC0 research accession name checks.")
    args = parser.parse_args()
    if args.accession_check:
        cross_check_accession_names()
        return
    if args.generate_kotlin:
        generate_kotlin()
        return
    # Two bounded workers reduce repeated waiting without flooding the source.
    with concurrent.futures.ThreadPoolExecutor(max_workers=2) as executor:
        entries = list(executor.map(lambda slug: fetch(slug, args.refresh), SLUGS))
    if args.verify:
        reviewed = json.loads(OUTPUT.read_text(encoding="utf-8"))
        for expected, actual in zip(reviewed["entries"], entries, strict=True):
            for field in ("id", "name", "geneticGroup", "lineage", "sourceUrl", "sourceFieldsSha256"):
                if expected[field] != actual[field]:
                    raise ValueError(f"Primary fact changed; review required: {expected['id']} {field}")
        print(f"PASS: {len(entries)} primary variety fact records match reviewed values.")
        return
    # Never overwrite a reviewed snapshot without an explicit developer edit/review.
    if OUTPUT.exists():
        raise FileExistsError("Reviewed dataset already exists. Use --verify; review changes manually.")
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    receipt = {
        "version": 3,
        "retrievedAt": retrieved_date(),
        "publisher": "World Coffee Research",
        "reuseBasis": "Short factual metadata only; source catalog is CC BY-NC-ND 4.0, not CC-BY. No copied prose, scores, images or artwork.",
        "sourceFieldMap": {
            "name": "h1 variety name",
            "geneticGroup": "Genetic Description value; normalized group labels, not genotype certification",
            "lineage": "Lineage value; normalized factual parentage reviewed before publication",
        },
        "entries": entries,
    }
    OUTPUT.write_text(json.dumps(receipt, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(entries, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
