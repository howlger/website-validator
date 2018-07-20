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
import java.util.Comparator;
import java.util.List;

public class ValidationResult {

    private final List<Issue> issues;
    private final String statistics;

    public ValidationResult(List<Issue> issues, String statistics) {
        this.issues = issues;
        this.issues.sort(new ByLocation());
        this.statistics = statistics;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public String getStatistics() {
		return statistics;
	}

    private static class ByLocation implements Comparator<Issue> {

		@Override
		public int compare(Issue issue1, Issue issue2) {
			final Path location1 = issue1.getLocation();
			final Path location2 = issue2.getLocation();
			if (location1 == null) return location2 == null ? 0 : -1;
			if (location2 == null) return 1;
			return location1.toString().compareTo(location2.toString());
		}

    }

}
