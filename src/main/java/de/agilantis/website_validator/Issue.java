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

import java.nio.file.Path;
import java.util.function.Function;

public abstract class Issue {

	protected final Path location;

	public Issue(Path location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return toString(path -> path.toString());
	}

	public String toString(Function<Path, String> pathToString) {
		return   "[" + getType() + "] "
		       + (location == null ? "" : "'" + pathToString.apply(location) + "': ")
		       + getDescription(pathToString);
	}

	public Path getLocation() {
		return location;
	}

	public abstract String getType();

	public abstract String getDescription(Function<Path, String> pathToString);

}
