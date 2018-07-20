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

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import de.agilantis.website_validator.Issue;

public class BrokenLinkIssue extends Issue {

    private final LinkType linkType;
    private final String attrValue;
    private final Path target;
    private final Optional<String> targetAnchor;
    private final Optional<String> protocol;

    public BrokenLinkIssue(LinkType linkType, Path file, String attrValue, Path target) {
        this(linkType, file, attrValue, target, Optional.empty());
    }

    public BrokenLinkIssue(LinkType linkType, Path file, String attrValue, Path target, Optional<String> targetAnchor) {
    	this(linkType, file, attrValue, target, targetAnchor, Optional.empty());
    }

    public BrokenLinkIssue(LinkType linkType, Path file, String attrValue, String protocol) {
    	this(linkType, file, attrValue, null, Optional.empty(), Optional.of(protocol));
    }

    private BrokenLinkIssue(LinkType linkType,
    		               Path file,
    		               String attrValue,
    		               Path target,
    		               Optional<String> targetAnchor,
    		               Optional<String> protocol) {
        super(file);
        this.linkType = linkType;
        this.attrValue = attrValue;
        this.target = target;
        this.targetAnchor = targetAnchor;
        this.protocol = protocol;
    }

	@Override
	public String getType() {
		return linkType.getIssue() + (targetAnchor.isPresent() ? " anchor" : "");
	}

	@Override
	public String getDescription(Function<Path, String> pathToString) {
        final String common =   "'<"
                              + linkType.getElementName()
                              + " ... "
                              + linkType.getAttributeName()
                              + "=\""
                              + attrValue
                              + "\" ...' ";
        if (protocol.isPresent()) return "In " + common + "the protocol '" + protocol.get() + "' is not allowed or not recommended";
        if (!targetAnchor.isPresent()) return common + "points to the missing file '" + pathToString.apply(target) + "'";
        if (attrValue.startsWith("#")) return common + "points to the missing anchor '" + targetAnchor.get() + "'";
        return   common
        	   + "file '"
        	   + pathToString.apply(target)
        	   + "' which exists but does not contain the anchor '"
        	   + targetAnchor.get()
        	   + "'";
	}

}
