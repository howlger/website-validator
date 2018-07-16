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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.agilantis.website_validator.Website;

public class DummyWebSite extends Website {

    private static final String HTML_SKELETON =
            "<html><head><title>${title}</title></head>\r\n"
          + "<body><p>${p}</p></body>\r\n</html>\r\n";

    private final Map<Path, String> files = new HashMap<>();

    private DummyWebSite() {
        super(null, null, null, null);
    }

    public static DummyWebSite create(int startNr, int endNr) {
        return create(startNr, endNr, 1);
    }

    public static DummyWebSite create(int startNr, int endNr, int nrIncrement) {
        DummyWebSite dummy = new DummyWebSite();
        for (int i = startNr; i < endNr; i+=nrIncrement) {
            dummy.put(i, "Sample " + i);
        }
        for (int i = startNr; i < endNr; i+=nrIncrement) {
            dummy.putEmptyFile("image_" + i + ".png");
        }
        return dummy;
    }

    @Override
    public Iterator<Path> iterator() {
        return files.keySet().iterator();
    }

    @Override
    public InputStream getContent(Path path) throws IOException {
        final String content = files.get(path);
        if (content == null) throw new FileNotFoundException("File '" + path + "' not found.");
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean exists(Path path) {
        return files.containsKey(path);
    }

    public void put(int nr, String paragraph) {
        final String html = HTML_SKELETON.replace("${title}", "Sample " + 1)
                                         .replace("${p}", paragraph);
        files.put(Paths.get(nr + ".htm"), html);
    }

    public void putEmptyFile(String path) {
        files.put(Paths.get(path), "");
    }

}
