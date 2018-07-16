/*********************************************************************
* Copyright (c) 2018 Advantest Corporation and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.agilantis.website_validator.files;

import java.util.function.Function;

public enum DubiousFileType {

    // TODO __MACOSX folder

    // see e. g. https://thumbsviewer.github.io/
    WINDOWS_THUMBS("Windows thumbs cache file",
                   name ->    "thumbs.db".equals(name)
                           || "ehthumbs.db".equals(name)
                           || "ehthumbs_vista.db".equals(name)
                           || "image.db".equals(name)
                           || "video.db".equals(name)
                           || "tvthumb.db".equals(name)
                           || "musicthumbs.db".equals(name)
                           || (name.startsWith("thumbcache_") && name.endsWith(".db"))),

    OPEN_DOCUMENT_LOCK_FILE("LibreOffice/OpenOffice lock/temp file",
                            name -> name.startsWith(".~") && name.endsWith("#")),

    MICROSOFT_OFFICE_LOCK_FILE("Microsoft Office lock/temp file",
                               name -> name.startsWith("~") && name.endsWith(".tmp")),

    WORD_AUTORECOVERY("Microsoft Word auto-recovery file",
                      name -> name.endsWith(".asd"));

    private final String displayName;
    private final Function<String, Boolean> fileNameCheck;

    private DubiousFileType(String displayName, Function<String, Boolean> test) {
        this.displayName = displayName;
        this.fileNameCheck = test;
    }

    public boolean checkFile(String lowerCaseFileName) {
        return fileNameCheck.apply(lowerCaseFileName);
    }

    @Override
    public String toString() {
        return displayName;
    }

}
