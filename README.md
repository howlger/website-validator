# Website and Eclipse Help Plug-in Validator

Extensible validator based on [jsoup](https://jsoup.org/) to find:

*   **Broken links** (link checker): no validation of external links or _mailto_ links yet
*   **Missing resources**: images, CSS files, JavaScript files, videos, etc.
*   **Dubious files**: unwanted files directly included or contained in ZIP files, like Windows thumbs cache file (`Thumbs.db`) , Microsoft Office, LibreOffice and OpenOffice lock/temp files, etc.


## Usage

	java -jar website-validator-<version>.jar /path/to/website-or-plugin-dir/ [ -htmlreport /path/to/report.html [ -htmlreportbaseurl http://www.example.com/help ] ]

HTML report contains [tablesort](https://github.com/tristen/tablesort) ([MIT License](https://github.com/tristen/tablesort/blob/gh-pages/LICENCE)).

Developed by [Advantest Corporation](https://www.advantest.com/) and Holger Voormann