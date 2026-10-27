import tempfile
import unittest
from pathlib import Path

from check_readme_consistency import check_readme


class ReadmeConsistencyTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        (self.root / "guide.md").write_text("guide", encoding="utf-8")
        self.gradle = 'versionName = "1.14.7"'
        self.readme = '**Versi sumber:** `1.14.7`\n[Guide](guide.md)\n'

    def test_current_version_and_local_link_pass(self):
        self.assertEqual([], check_readme(self.readme, self.gradle, self.root))

    def test_source_version_drift_fails(self):
        issues = check_readme(self.readme, 'versionName = "1.14.8"', self.root)
        self.assertTrue(any("version" in issue for issue in issues))

    def test_missing_relative_link_fails_but_external_link_is_not_fetched(self):
        readme = self.readme + '[Missing](missing.md) [External](https://example.com)\n'
        issues = check_readme(readme, self.gradle, self.root)
        self.assertTrue(any("missing.md" in issue for issue in issues))
        self.assertEqual(1, len(issues))

    def test_invalid_png_signature_fails(self):
        (self.root / "broken.png").write_bytes(b"not a PNG")
        issues = check_readme(self.readme + '![Brew](broken.png)\n', self.gradle, self.root)
        self.assertTrue(any("broken.png" in issue for issue in issues))

    def test_html_image_reference_is_checked(self):
        issues = check_readme(self.readme + '<img src="missing.png" alt="Brew">', self.gradle, self.root)
        self.assertTrue(any("missing.png" in issue for issue in issues))

    def test_missing_source_version_label_fails(self):
        issues = check_readme("# Coffee Creator", self.gradle, self.root)
        self.assertTrue(any("version" in issue for issue in issues))


if __name__ == "__main__":
    unittest.main()
