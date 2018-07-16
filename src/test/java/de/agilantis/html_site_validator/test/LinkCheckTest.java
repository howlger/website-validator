/*********************************************************************
* Copyright (c) 2018 Advantest Corporation and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.agilantis.html_site_validator.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.agilantis.website_validator.WebsiteValidator;

class LinkCheckTest {

    WebsiteValidator validator = new WebsiteValidator();

    @Test
    void validFiles() {
        final DummyWebSite files = DummyWebSite.create(1, 3);
        assertTrue(validator.validate(files).getIssues().isEmpty());
        files.put(4, "<a href='2.htm'>link text<a>");
        files.put(5, "<a href=\"2.htm\">link text<a>");
        files.put(6, "<a href='foo/../2.htm'>link text<a>");
        assertTrue(validator.validate(files).getIssues().isEmpty());
    }

    @Test
    void brokenLink() {
        DummyWebSite files = DummyWebSite.create(1, 3);
        files.put(1, "<a href='4.htm'>broken link<a>");
        assertEquals(1, validator.validate(files).getIssues().size());
    }

    @Test
    void anchorLink() {
        DummyWebSite files = DummyWebSite.create(1, 3);
        files.put(1, "<a id='foo'>anchor<a>");
        files.put(2, "<a href='1.htm#foo'>link to anchor<a>");
        assertTrue(validator.validate(files).getIssues().isEmpty());

        // non existing anchor
        files.put(2, "<a href='1.htm#bar'>link to anchor<a>");
        assertEquals(1, validator.validate(files).getIssues().size());
    }

    @Test
    void valid() {
        final DummyWebSite files = DummyWebSite.create(1, 3);
        files.putEmptyFile("foo/image.png");
        files.put(2, "<img src='foo/image.png'>");
        assertTrue(validator.validate(files).getIssues().isEmpty());
    }

    @Test
    void missingImage() {
        final DummyWebSite files = DummyWebSite.create(1, 3);
        files.put(1, "<img src='foo/image1.png'>");
        files.putEmptyFile("foo/image2.png");
        files.put(2, "<img src='foo/../foo/image2.png'>");
        assertEquals(1, validator.validate(files).getIssues().size());
    }

}
