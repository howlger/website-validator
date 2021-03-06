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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Website implements Iterable<Path> {

    private final Map<Path, ZipEntry> docZipFiles = new HashMap<>();
    private final Set<Path> otherFiles = new HashSet<>();
    private final ZipFile docZip;
    private final Path dir;
    private final String title;
    private final String symbolicName;
    private final Path base; // "/web_site_to_validate" or "/<Bundle-SymbolicName>"
    private final List<Issue> issues = new ArrayList<>();

    /** Only for testing */
    protected Website(ZipFile docZip, Path dir, String title, String symbolicName, Path base) {
        this.docZip = docZip;
        this.dir = dir;
        this.title = title;
        this.symbolicName = symbolicName;
        this.base = base;
    }

    /** Directory of HTML and other files or Eclipse help plug-in directory */
    public static Website of(Path dir) throws IOException {
        final String symbolicName = computeSymbolicName(dir);
        final boolean isEclipseHelpPlugin = symbolicName != null;
        final Path base = Paths.get("/" + (isEclipseHelpPlugin ? symbolicName : "web_site_to_validate"));

        final Path docZipPath = dir.resolve("doc.zip");
        final ZipFile docZip = isEclipseHelpPlugin && Files.exists(docZipPath)
                               ? new ZipFile(docZipPath.toFile())
                               : null;
        final String version = computeVersion(dir);
        final String title = symbolicName == null
        		             ? dir.toString()
        		             : symbolicName + (version == null || version.isEmpty() ? "" : " " + version);
        final Website website = new Website(docZip, dir, title, symbolicName, base);
        // doc.zip
        if (docZip != null) {
            @SuppressWarnings("rawtypes")
            final Enumeration zipEntries = docZip.entries();
            final Set<String> entryNames = new HashSet<>();
            while (zipEntries.hasMoreElements()) {
                final ZipEntry entry = (ZipEntry) zipEntries.nextElement();

                // duplicate entry?
                if (!entryNames.add(entry.getName())) {
                	website.issues.add(new DuplicateZipEntryIssue(null, entry.getName()));
                }

                if (entry.isDirectory()) continue;
                website.docZipFiles.put(base.resolve(entry.getName()), entry);
            }
        }

        // other files
        Files.walk(dir)
             .filter(Files::isRegularFile)
             .filter(path -> docZip == null || !path.equals(docZipPath))
             .forEach(file -> website.otherFiles.add(base.resolve(dir.relativize(file))));

        // known Infocenter images
        if (isEclipseHelpPlugin) {
            website.otherFiles.add(Paths.get("/help/advanced/images/topic.gif"));
            website.otherFiles.add(Paths.get("/help/advanced/images/container_topic.gif"));
            website.otherFiles.add(Paths.get("/help/advanced/images/toc_open.gif"));
        }

        return website;
    }

    @Override
    public Iterator<Path> iterator() {
        Set<Path> files = new HashSet<>(otherFiles);
        files.addAll(docZipFiles.keySet());
        return files.iterator();
    }

    public InputStream getContent(Path file) throws IOException {
        ZipEntry zipEntry = docZipFiles.get(file);
        if (zipEntry != null) return docZip.getInputStream(zipEntry);
        return Files.newInputStream(dir.resolve(base.relativize(file)));
    }

    public boolean exists(Path file) {
        return docZipFiles.containsKey(file) || otherFiles.contains(file);
    }

    public String pathToString(Path file) {
        return base.relativize(file).toString();
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public String getTitle() {
    	return title;
    }

    public List<Issue> getIssues() {
    	return issues;
    }

    private static String computeSymbolicName(Path pluginDir) throws IOException {
        final Manifest manifest = manifest(pluginDir);
        if (manifest == null) return null;
        final String symbolicName = manifest.getMainAttributes().getValue("Bundle-SymbolicName");
        if (symbolicName == null) return null;
        final int end = symbolicName.indexOf(';');
        return end < 0 ? symbolicName : symbolicName.substring(0, end);
    }

    private static String computeVersion(Path pluginDir) throws IOException {
    	final Manifest manifest = manifest(pluginDir);
    	return manifest == null ? null : manifest.getMainAttributes().getValue("Bundle-Version");
    }

    private static Manifest manifest(Path pluginDir) throws IOException {
    	final Path manifestPath = pluginDir.resolve("META-INF/MANIFEST.MF");
    	return !Files.exists(manifestPath) ? null : new Manifest(Files.newInputStream(manifestPath));
    }

}
