"""Check the source-version label and local README assets without network access."""

import re
import sys
from pathlib import Path
from urllib.parse import unquote, urlsplit


ROOT = Path(__file__).resolve().parents[1]
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"


def check_readme(readme: str, gradle: str, root: Path) -> list[str]:
    issues = []
    source = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
    documented = re.search(r"\*\*Versi sumber:\*\* `([^`]+)`", readme)
    if not source or not documented or source.group(1) != documented.group(1):
        issues.append("README source version must match app/build.gradle.kts versionName")

    targets = re.findall(r"!?\[[^\]]*\]\(([^)\s]+)", readme)
    targets += re.findall(r'<img\s+[^>]*src=["\']([^"\']+)["\']', readme, re.IGNORECASE)
    for target in sorted(set(targets)):
        link = urlsplit(target)
        if link.scheme or link.netloc or not link.path:
            continue
        path = root / unquote(link.path)
        if not path.exists():
            issues.append(f"Missing README target: {target}")
        elif path.is_file() and path.suffix.lower() == ".png":
            with path.open("rb") as image:
                if image.read(len(PNG_SIGNATURE)) != PNG_SIGNATURE:
                    issues.append(f"Invalid PNG signature: {target}")
    return issues


def main() -> int:
    issues = check_readme(
        (ROOT / "README.md").read_text(encoding="utf-8"),
        (ROOT / "app/build.gradle.kts").read_text(encoding="utf-8"),
        ROOT,
    )
    for issue in issues:
        print(issue, file=sys.stderr)
    if not issues:
        print("README source version and local targets: OK")
    return bool(issues)


if __name__ == "__main__":
    sys.exit(main())
