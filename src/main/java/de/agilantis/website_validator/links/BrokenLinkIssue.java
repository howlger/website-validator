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

import de.agilantis.website_validator.FileIssue;

public class BrokenLinkIssue extends FileIssue {

    private final LinkType linkType;
    private final String attrValue;
    private final Path target;
    private final Optional<String> targetAnchor;

    public BrokenLinkIssue(LinkType linkType, Path file, String attrValue, Path target) {
        this(linkType, file, attrValue, target, Optional.empty());
    }

    public BrokenLinkIssue(LinkType linkType, Path file, String attrValue, Path target, Optional<String> targetAnchor) {
        super(file);
        this.linkType = linkType;
        this.attrValue = attrValue;
        this.target = target;
        this.targetAnchor = targetAnchor;
    }

    public String toString(Function<Path, String> pathToString) {
        final String common =   "["
                              + linkType.getIssue()
                              + (targetAnchor.isPresent() ? " anchor" : "")
                              + "] In file '"
                              + pathToString.apply(getFile())
                              + "' <"
                              + linkType.getElementName()
                              + " ... "
                              + linkType.getAttributeName()
                              + "=\""
                              + attrValue
                              + "\" points to the ";
        if (!targetAnchor.isPresent()) return common + "missing file '" + pathToString.apply(target) + "'.";
        if (attrValue.startsWith("#")) return common + "missing anchor '" + targetAnchor.get() + "'.";
        return   common
               + "file '"
               + pathToString.apply(target)
               + "' which exists but does not contain the anchor '"
               + targetAnchor.get()
               + "'.";
    }

}
