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
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.agilantis.website_validator.CheckerAdapter;
import de.agilantis.website_validator.FileIssue;
import de.agilantis.website_validator.Issue;
import de.agilantis.website_validator.Website;

public class FileNameChecker extends CheckerAdapter {

    @Override
    protected void visitNonHtmlFile(Path file, Website website, List<Issue> issues) {
        final String lowerCaseFileName = file.getFileName().toString().toLowerCase();
        for (DubiousFileType dubiousFileType : DubiousFileType.values()) {
            if (dubiousFileType.checkFile(lowerCaseFileName)) {
                issues.add(new DubiousFileIssue(dubiousFileType, file));
            }
        }
        if (!lowerCaseFileName.endsWith(".zip")) return;
        try {
            final ZipInputStream zip = new ZipInputStream(website.getContent(file));
            final Set<String> entryNames = new HashSet<>();
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {

                // check file names
                final String entryName = entry.getName();
                final int fileNameStart = entryName.lastIndexOf('/');
                final String lowerCase = (fileNameStart < 0 ? entryName : entryName.substring(fileNameStart + 1)).toLowerCase();
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
            issues.add(new FileIssue(file) {
                @Override
                public String toString(Function<Path, String> pathToString) {
                    return   "[Exception] An exception occured while reading ZIP file '"
                           + pathToString.apply(file)
                           + "':"
                           + e.getMessage();
                }
            });
        }
    }

}
