/*********************************************************************
* Copyright (c) 2018 Advantest Corporation and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.agilantis.website_validator;

import java.util.List;

public class ValidationResult {

    private final List<Issue> issues;
    private final int numberOfHtmlFiles;

    public ValidationResult(List<Issue> issues, int numberOfHtmlFiles) {
        this.issues = issues;
        this.numberOfHtmlFiles = numberOfHtmlFiles;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public int getNumberOfHtmlFiles() {
        return numberOfHtmlFiles;
    }

}
