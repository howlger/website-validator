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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class HtmlReport {

	public static void create(Path reportFile, ValidationResult result, String baseUrl, Website website) {
            final String base =   (baseUrl == null ? "" : baseUrl)
                                + (baseUrl == null || website.getSymbolicName() == null
                                   ? ""
                                   : "/topic/" + website.getSymbolicName() + "/");
            final StringBuilder content = new StringBuilder();
            content.append("<p>").append(result.getStatistics()).append(".</p><p><b>");
            if (result.getIssues().isEmpty()) {
            	content.append("No issues found.</b></p>");
            } else {
            	content.append(result.getIssues().size())
            	       .append(" issues found:</b></p><table");
            	if (result.getIssues().size() < 1000) {
            		content.append(" class=\"sortable filter\"");
            	}
            	content.append("><thead><tr><th class=\"filter-select\">Type</th><th>File</th><th>Description</th></tr>")
            	     .append("</thead><tbody>");
            }
            for (Issue issue : result.getIssues()) {

            	// type
            	content.append("<tr><td>")
            	     .append(asHtml(issue.getType()))
            	     .append("</td>");

            	// location
            	content.append("<td>");
            	final Path location = issue.getLocation();
            	final String file = location == null ? "" : website.pathToString(location).replace('\\', '/');
            	if (baseUrl != null) {
            		content.append("<a href=\"")
            		     .append(file)
            		     .append("\">");
            	}
            	content.append(asHtml(file));
            	if (baseUrl != null) {
            		content.append("</a>");
            	}
            	content.append("</td>");

            	// description
            	content.append("<td>");
            	final String rawDesc =
            			issue.getDescription(path -> "[[[" + website.pathToString(path).replace('\\', '/') + "]]]");
            	content.append(asHtml(rawDesc).replaceAll("\\[\\[\\[(.*?)\\]\\]\\]",
                                                         baseUrl == null
                                                         ? "$1"
                                                         : "<a href=\"" + base + "$1\">$1</a>"));
            	content.append("</td></tr>");
            	content.append(System.getProperty("line.separator"));
            }
            if (!result.getIssues().isEmpty()) {
            	content.append("</tbody></table>");
            }
            final String reportTemplate = asString(WebsiteValidator.class.getResourceAsStream("report.html"));
            try {
				Files.write(reportFile,
				            reportTemplate.replace("<<TITLE>>", website.getTitle())
				                          .replace("<<ISSUES>>", content)
				                          .replace("<<BASE>>", base == null || base.isEmpty()
				                                   ? ""
                                                   : "<base href=\"" + base + "\">")
				                          .getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

    private static String asString(InputStream inputStream) {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private static String asHtml(String string) {
    	return string.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
    }

}
