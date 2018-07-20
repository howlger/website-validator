/*********************************************************************
* Copyright (c) 2018 Advantest Corporation and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.agilantis.website_validator.links;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.agilantis.website_validator.Issue;
import de.agilantis.website_validator.CheckerAdapter;
import de.agilantis.website_validator.Website;

public class LinkChecker extends CheckerAdapter {

    private final Map<Path, Set<String>> anchors = new HashMap<>();
    private final List<ExpectedAnchor> expectedAnchors = new ArrayList<>();
    private int checkedLinks = 0;
    private int skippedLinks = 0;

    @Override
    protected void visitHtmlFile(Path file, Document doc, Website webSite, List<Issue> issues) {

        // scan for anchors
        final Set<String> docAnchors = new HashSet<>();
        for (Element anchor : doc.select("[id]")) {
            docAnchors.add(anchor.attr("id"));
        }
        for (Element anchor : doc.select("a[name]")) {
            docAnchors.add(anchor.attr("name"));
        }
        anchors.put(file, docAnchors);

        // check links
        for (LinkType linkType : LinkType.values()) {
            for (Element elem : doc.select(linkType.getElementName() + "[" + linkType.getAttributeName() + "]")) {
                final String attr = elem.attr(linkType.getAttributeName());

                // javascript:...
                if (attr.startsWith("javascript:")) {
                	if (!linkType.allowsJavascript()) {
                		issues.add(new BrokenLinkIssue(linkType, file, attr, "javascript"));
                	}
                	continue;
                }

                // data:...
                if (attr.startsWith("data:")) {
                	if (!linkType.supportsData()) {
                		issues.add(new BrokenLinkIssue(linkType, file, attr, "data"));
                	}
                	continue;
                }

                // other protocols
                if (attr.contains(":")) {
                	skippedLinks++;
                	continue;
                }

                // without anchor support
                if (!linkType.supportsAnchors()) {
                    final Path target = file.resolveSibling(attr).normalize();
                    checkedLinks++;
                    if (!webSite.exists(target)) {
                        issues.add(new BrokenLinkIssue(linkType, file, attr, target));
                    }
                    continue;
                }

                // option anchor
                if ("#".equals(attr)) continue;
                final int anchorStart = attr.indexOf('#'); // fragment identifier
                final int queryStart = attr.indexOf('?');
// TODO ?is-external=true
                final int end = queryStart >= 0 ? queryStart : anchorStart;
                final String fileHref = end < 0 ? attr : attr.substring(0, end);
                final Path target = anchorStart == 0
                                    ? file
                                    : file.resolveSibling(fileHref).normalize();
                checkedLinks++;
                if (!webSite.exists(target)) {
                    issues.add(new BrokenLinkIssue(linkType, file, attr, target));
                    continue;
                }
                if (anchorStart >= 0) {
                    try {
                        final String anchor = URLDecoder.decode(attr.substring(anchorStart + 1), StandardCharsets.UTF_8.name());
                        expectedAnchors.add(new ExpectedAnchor(linkType, target, attr, anchor, file));
                    } catch (UnsupportedEncodingException e) {
                        // ignore
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    @Override
    public List<Issue> getRemainingIssuesAfterVisitingAllFiles(Website webSite) {
        final List<Issue> issues = new ArrayList<>();
        for (ExpectedAnchor expectedAnchor : expectedAnchors) {
            final Set<String> anchorsInTargetFile = anchors.get(expectedAnchor.inFile);
            if (   anchorsInTargetFile == null
                || !anchorsInTargetFile.contains(expectedAnchor.anchor)) {
                issues.add(new BrokenLinkIssue(expectedAnchor.linkType,
                                               expectedAnchor.linkedFrom,
                                               expectedAnchor.href,
                                               expectedAnchor.inFile,
                                               Optional.of(expectedAnchor.anchor)));
            }
        }
        return issues;
    }

    @Override
    public String getStatistics() {
    	// TODO Auto-generated method stub
    	return checkedLinks + " links checked (" + skippedLinks + " links skipped)";
    }

    private static class ExpectedAnchor {
        private final LinkType linkType;
        private final Path inFile;
        private final String href;
        private final String anchor;
        private final Path linkedFrom;
        public ExpectedAnchor(LinkType linkType, Path inFile, String href, String anchor, Path linkedFrom) {
            this.linkType = linkType;
            this.inFile = inFile;
            this.href = href;
            this.anchor = anchor;
            this.linkedFrom = linkedFrom;
        }
    }

}
