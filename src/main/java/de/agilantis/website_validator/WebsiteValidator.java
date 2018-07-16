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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.agilantis.website_validator.files.FileNameChecker;
import de.agilantis.website_validator.links.LinkChecker;

public class WebsiteValidator {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Specify the directory that should be validated.");
            return;
        }
        final Path dir = Paths.get(args[0]);
        final Website webSite = Website.of(dir);
        System.out.println("Validating " + dir);
        ValidationResult result = new WebsiteValidator().validate(webSite);
        List<Issue> issues = result.getIssues();
        System.out.println(  result.getNumberOfHtmlFiles() + " HTML files validated, "
                           + issues.size() + " issues found.");
        for (Issue issue : issues) {
            System.err.println(issue.toString(webSite::pathToString));
        }

        // optional HTML report
        boolean isHtmlReportArg = false;
        boolean isHtmlReportBaseUrlArg = false;
        String htmlReport = null;
        String htmlReportBaseUrl = null;
        for (String argument : args) {
            if (isHtmlReportArg) {
                isHtmlReportArg = false;
                htmlReport = argument;
            } else if (isHtmlReportBaseUrlArg) {
                isHtmlReportBaseUrlArg = false;
                htmlReportBaseUrl = argument;
            }
            isHtmlReportArg = "-htmlreport".equals(argument);
            isHtmlReportBaseUrlArg = "-htmlreportbaseurl".equals(argument);
        }
        if (htmlReport != null) {
            final String base =   htmlReportBaseUrl
                                + (webSite.getSymbolicName() == null
                                   ? ""
                                   : "/topic/" + webSite.getSymbolicName() + "/");
            final StringBuilder rows = new StringBuilder();
            for (Issue issue : issues) {
                final String row = issue.toString(path -> "[[[" + webSite.pathToString(path).replace('\\', '/') + "]]]")
                                        .replace("&", "&amp;")
                                        .replace("<", "&lt;");
                final String td = row.replaceAll("\\[\\[\\[(.*?)\\]\\]\\]",
                                                 htmlReportBaseUrl == null
                                                 ? "$1"
                                                 : "<a href=\"" + base + "$1\">$1</a>");
                rows.append("<tr><td>" + td + "</td></tr>");
            }
            final String reportTemplate = asString(WebsiteValidator.class.getResourceAsStream("report.html"));
            Files.write(Paths.get(htmlReport),
                        reportTemplate.replace("<<ISSUES>>", rows)
                                      .getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String asString(InputStream inputStream) {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public ValidationResult validate(Website webSite) {
        final IChecker[] validators = new IChecker[] {
                new LinkChecker(),
                new FileNameChecker(),
        };
        final List<Issue> issues = new ArrayList<>();
        int numberOfHtmlFiles = 0;
        for (Path file : webSite) {
            try {
                Document doc = isHtml(file)
                               ? Jsoup.parse(webSite.getContent(file),
                                             StandardCharsets.UTF_8.name(),
                                             "")
                               : null;
                if (doc != null) numberOfHtmlFiles++;
                for (IChecker validator : validators) {
                    validator.visitFile(file, doc, webSite, issues);
                }
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
                issues.add(new FileIssue(file) {
                    @Override
                    public String toString(Function<Path, String> pathToString) {
                        return "[Exception] An exception occured while reading file '" + pathToString.apply(file) + "': " + e.getMessage();
                    }
                });
            }
        }
        for (IChecker validator : validators) {
            issues.addAll(validator.getRemainingIssuesAfterVisitingAllFiles(webSite));
        }
        return new ValidationResult(issues, numberOfHtmlFiles);
    }

    private static boolean isHtml(Path path) {
        final String extension = getExtension(path);
        return     "htm".equalsIgnoreCase(extension)
                || "html".equalsIgnoreCase(extension)
                || "xhtml".equalsIgnoreCase(extension);
    }

    private static String getExtension(Path path) {
        final String fileName = path.getFileName().toString();
        final int start = fileName.lastIndexOf('.');
        return start < 0 ? "" : fileName.substring(start + 1);
    }

}
