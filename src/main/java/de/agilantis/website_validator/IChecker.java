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
import java.util.List;

import org.jsoup.nodes.Document;

public interface IChecker {

    void visitFile(Path path, Document doc, Website website, List<Issue> issues);

    List<Issue> getRemainingIssuesAfterVisitingAllFiles(Website website);

}
