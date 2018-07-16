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

public enum LinkType {

    // see https://stackoverflow.com/a/2725168/6505250
    A("a", "href", true, "Broken link"),
    IMAGE("img", "src", "Missing image"),
    JS("script", "src", "Missing JavaScript"),
    CSS("link", "href", "Missing CSS"),
    VIDEO("video", "src", "Missing video"),
    VIDEO_POSTER("video", "poster", "Missing video poster/thumbnail");

    private final String elementName;
    private final String attributeName;
    private boolean anchorSupport;
    private final String issue;

    private LinkType(String elementName, String attributeName, String issue) {
        this(elementName, attributeName, false, issue);
    }

    private LinkType(String elementName, String attributeName, boolean anchorSupport, String issue) {
        this.elementName = elementName;
        this.attributeName = attributeName;
        this.anchorSupport = anchorSupport;
        this.issue = issue;
    }

    public String getElementName() {
        return elementName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean supportAnchor() {
        return anchorSupport;
    }

    public String getIssue() {
        return issue;
    }

}
