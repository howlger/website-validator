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
    A("a", "href", "Broken link", true, true, false),
    IMAGE("img", "src", "Missing image", true),
    JS("script", "src", "Missing JavaScript", false),
    CSS("link", "href", "Missing CSS", false),
    VIDEO("video", "src", "Missing video", false),
    VIDEO_POSTER("video", "poster", "Missing video poster/thumbnail", true);

    private final String elementName;
    private final String attributeName;
    private final String issue;
    private boolean anchorSupported;
    private boolean javascriptAllowed;
    private boolean dataSupported;

    private LinkType(String elementName, String attributeName, String issue, boolean dataSupported) {
        this(elementName, attributeName, issue, false, false, dataSupported);
    }

    private LinkType(String elementName,
    		         String attributeName,
    		         String issue,
    		         boolean anchorSupported,
    		         boolean javascriptAllowed,
    		         boolean dataSupported) {
        this.elementName = elementName;
        this.attributeName = attributeName;
        this.issue = issue;
        this.anchorSupported = anchorSupported;
        this.javascriptAllowed = javascriptAllowed;
        this.dataSupported = dataSupported;
    }

    public String getElementName() {
        return elementName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getIssue() {
        return issue;
    }

    public boolean supportsAnchors() {
    	return anchorSupported;
    }

    public boolean allowsJavascript() {
    	return javascriptAllowed;
    }

    public boolean supportsData() {
    	return dataSupported;
    }

}
