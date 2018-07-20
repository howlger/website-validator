# Website and Eclipse Help Plug-in Validator

Extensible validator based on [jsoup](https://jsoup.org/) to find:

*   **Broken links** (link checker): internal linking only, no validation of external links or _mailto_ links yet
*   **Broken link anchors**: check if the anchor of internal links exists, e. g. for`<a href="foo.html#bar">`, it is checked if the anchor `bar` (e. g. `<a name="bar">`, `<p id="bar">`, etc.) exists in the file `foo.html`
*   **Missing resources**: images, CSS files, JavaScript files, videos, etc.
*   **Dubious files**: unwanted files directly included or contained in ZIP files
    * Windows thumbs cache file (`Thumbs.db`, `ehthumbs.db`, etc.)
    * LibreOffice/OpenOffice lock/temp file (`.~`*`#`)
    * Microsoft Office lock/temp file (`~`*`.tmp`)
    * Microsoft Word auto-recovery file (*`.asd`)
*   **Duplicate ZIP entries**: `doc.zip` or another ZIP file (inside or outside of `doc.zip`) contains multiple entries with the same name


## Usage

	java -jar website-validator-<version>.jar /path/to/website-or-plugin-dir/ [ -htmlreport /path/to/report.html [ -htmlreportbaseurl http://www.example.com/help ] ]

A `.jar` Eclipse help plug-in has to be unzipped first.

HTML report contains [tablesort](https://github.com/tristen/tablesort) ([MIT License](https://github.com/tristen/tablesort/blob/gh-pages/LICENCE)).

Developed by [Advantest Corporation](https://www.advantest.com/) and Holger Voormann