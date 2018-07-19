/*********************************************************************
* Copyright (c) 2018 Advantest Corporation and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.agilantis.website_validator.files;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.agilantis.website_validator.CheckerAdapter;
import de.agilantis.website_validator.ExceptionIssue;
import de.agilantis.website_validator.Issue;
import de.agilantis.website_validator.Website;

public class FileNameChecker extends CheckerAdapter {

	private int checkedFileNames = 0;
	private int readZipFiles = 0;

    @Override
    protected void visitNonHtmlFile(Path file, Website website, List<Issue> issues) {
    	checkedFileNames++;
        final String lowerCaseFileName = file.getFileName().toString().toLowerCase();
        for (DubiousFileType dubiousFileType : DubiousFileType.values()) {
            if (dubiousFileType.checkFile(lowerCaseFileName)) {
                issues.add(new DubiousFileIssue(dubiousFileType, file));
            }
        }
        if (!lowerCaseFileName.endsWith(".zip")) return;
        try {
            final ZipInputStream zip = new ZipInputStream(website.getContent(file));
            checkedFileNames--;
            readZipFiles++;
            final Set<String> entryNames = new HashSet<>();
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {

                // check file names
                final String entryName = entry.getName();
                final int fileNameStart = entryName.lastIndexOf('/');
                final String lowerCase = (fileNameStart < 0 ? entryName : entryName.substring(fileNameStart + 1)).toLowerCase();
                checkedFileNames++;
                for (DubiousFileType dubiousFileType : DubiousFileType.values()) {
                    if (dubiousFileType.checkFile(lowerCase)) {
                        issues.add(new DubiousFileIssue(dubiousFileType, file, entryName));
                    }
                }

                // duplicate entries
                if (entryNames.add(entry.getName())) continue;
                issues.add(new DuplicateZipEntryIssue(file, entryName));

            }
        } catch (IOException e) {
            e.printStackTrace();
            issues.add(new ExceptionIssue(file, e));
        }
    }

    @Override
    public String getStatistics() {
    	return checkedFileNames + " file names checked (including files in " + readZipFiles + " ZIP files)";
    }

}
