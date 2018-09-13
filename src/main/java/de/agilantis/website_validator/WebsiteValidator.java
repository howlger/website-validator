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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.agilantis.website_validator.files.FileNameChecker;
import de.agilantis.website_validator.links.LinkChecker;

public class WebsiteValidator {

	public static final List<Class<? extends IChecker>> DEFAULT_CHECKERS =
			Arrays.asList(LinkChecker.class, FileNameChecker.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Specify the directory that should be validated.");
            return;
        }

        // reading arguments: <input> [-<arg_name> <arg_value>]*
        final Map<String, String> arguments = new HashMap<>();
        String input = null;
        String argName = null;
        for (String argument : args) {
        	if (argument.startsWith("-")) {
        		argName = argument.substring(1).toLowerCase();
        		continue;
        	}
        	if (argName != null) {
        		arguments.put(argName, argument);
        		argName = null;
        		continue;
        	}
        	input = argument;
        }

        // validate
        final Path dir = Paths.get(input);
        System.out.println("Validating " + dir);
        final Website website = Website.of(dir);
        ValidationResult result = validate(website, arguments);
        List<Issue> issues = result.getIssues();
        System.out.println(  (issues.isEmpty() ? "no" : "" + issues.size())
        		           + " issues found"
        		           + (result.getStatistics().isEmpty() ? "" : " (" + result.getStatistics() + ")")
        		           + (issues.isEmpty() ? "." : ":"));
        for (Issue issue : issues) {
            System.err.println(issue.toString(website::pathToString));
        }

        // optional HTML report
        if (arguments.containsKey("htmlreport")) {
        	HtmlReport.create(Paths.get(arguments.get("htmlreport")),
        			          result,
        			          arguments.get("htmlreportbaseurl"),
        			          website);
        }
    }

    public static ValidationResult validate(Website website,
    		                                Map<String, String> arguments) {
    	return validate(website,
    			        checkersFromClasses(DEFAULT_CHECKERS, arguments));
    }

    public static ValidationResult validate(Website website,
    		                                List<IChecker> checkers) {
        final List<Issue> issues = new ArrayList<>(website.getIssues());
        int numberOfHtmlFiles = 0;
        int numberOfOtherFiles = 0;
        for (Path file : website) {
            try {
                Document doc = isHtml(file)
                               ? Jsoup.parse(website.getContent(file),
                                             StandardCharsets.UTF_8.name(),
                                             "")
                               : null;
                if (doc != null) {
                	numberOfHtmlFiles++;
                } else {
                	numberOfOtherFiles++;
                }
                for (IChecker checker : checkers) {
                    checker.visitFile(file, doc, website, issues);
                }
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
                issues.add(new ExceptionIssue(file, e));
            }
        }
        for (IChecker checker : checkers) {
            issues.addAll(checker.getRemainingIssuesAfterVisitingAllFiles(website));
        }
        String statistics =   numberOfHtmlFiles
        		            + " HTML and "
        		            + numberOfOtherFiles
        		            + " other files validated";
        for (IChecker checker : checkers) {
        	if (checker.getStatistics().isEmpty()) continue;
        	statistics += ", " + checker.getStatistics();
        }
        return new ValidationResult(issues, statistics);
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

    private static List<IChecker> checkersFromClasses(List<Class<? extends IChecker>> checkerClasses,
    		                                          Map<String, String> arguments) {
    	final List<IChecker> checkers = new ArrayList<>();
    	for (Class<? extends IChecker> checkerClass : checkerClasses) {
    		try {
				final IChecker newChecker = checkerClass.newInstance();
				newChecker.configure(arguments);
				checkers.add(newChecker);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
    	return checkers;
    }

}
