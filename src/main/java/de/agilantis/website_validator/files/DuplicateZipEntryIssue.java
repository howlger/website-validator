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

import java.nio.file.Path;
import java.util.function.Function;

import de.agilantis.website_validator.Issue;

public class DuplicateZipEntryIssue extends Issue {

    private final String zipEntryName;

    public DuplicateZipEntryIssue(Path file, String zipEntryName) {
        super(file);
        this.zipEntryName = zipEntryName;
    }

	@Override
	public String getType() {
		return "Duplicate ZIP entry";
	}

	@Override
	public String getDescription(Function<Path, String> pathToString) {
		return "ZIP file contains contains duplicated entries for '" + zipEntryName + "'";
	}

}
