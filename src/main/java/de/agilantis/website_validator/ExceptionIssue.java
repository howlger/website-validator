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

public class ExceptionIssue extends Issue {

	private final String message;

	public ExceptionIssue(Path location, Exception e) {
		super(location);
		message = e.getMessage();
	}

	@Override
	public String getType() {
		return "Unexpected exception";
	}

	@Override
	public String getDescription(Function<Path, String> pathToString) {
		return message;
	}

}
