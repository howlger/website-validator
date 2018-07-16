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

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Document;

public abstract class CheckerAdapter implements IChecker {

    public void visitFile(Path file, Document doc, Website website, List<Issue> issues) {
        if (doc == null) {
            visitNonHtmlFile(file, website, issues);
        } else {
            visitHtmlFile(file, doc, website, issues);
        }
    }

    protected void visitHtmlFile(Path file, Document doc, Website website, List<Issue> issues) {
    }

    protected void visitNonHtmlFile(Path file, Website website, List<Issue> issues) {
    }

    @Override
    public List<Issue> getRemainingIssuesAfterVisitingAllFiles(Website wesSite) {
        return Collections.emptyList();
    }

}
