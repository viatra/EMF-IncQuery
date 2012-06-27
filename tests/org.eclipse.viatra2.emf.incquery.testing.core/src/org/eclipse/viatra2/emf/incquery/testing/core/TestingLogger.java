/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.testing.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger;

/**
 * @author Abel Hegedus
 *
 */
public class TestingLogger implements EMFIncQueryRuntimeLogger {

	private final Map<String,StringBuilder> messages = new HashMap<String, StringBuilder>();
	private final Map<String,Throwable> errors = new HashMap<String, Throwable>();
	private final Map<String,Throwable> warnings = new HashMap<String, Throwable>();
	private final StringBuilder output = new StringBuilder();

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger#logDebug(java.lang.String)
	 */
	@Override
	public void logDebug(String message) {
		StringBuilder sb = messages.get("DEBUG");
		if(sb == null) {
			sb = new StringBuilder();
			messages.put("DEBUG", sb);
		}
		sb.append(message).append('\n');
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger#logError(java.lang.String)
	 */
	@Override
	public void logError(String message) {
		StringBuilder sb = messages.get("ERROR");
		if(sb == null) {
			sb = new StringBuilder();
			messages.put("ERROR", sb);
		}
		sb.append(message).append('\n');
		output.append(message).append('\n');
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger#logError(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void logError(String message, Throwable cause) {
		StringBuilder sb = messages.get("ERROR");
		if(sb == null) {
			sb = new StringBuilder();
			messages.put("ERROR", sb);
		}
		sb.append(message).append('\n');
		errors.put(message, cause);
		output.append(message).append('\n');
    output.append(getStackTraceAsString(cause)).append('\n');
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger#logWarning(java.lang.String)
	 */
	@Override
	public void logWarning(String message) {
		StringBuilder sb = messages.get("WARNING");
		if(sb == null) {
			sb = new StringBuilder();
			messages.put("WARNING", sb);
		}
		sb.append(message).append('\n');
		output.append(message).append('\n');
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger#logWarning(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void logWarning(String message, Throwable cause) {
		StringBuilder sb = messages.get("WARNING");
		if(sb == null) {
			sb = new StringBuilder();
			messages.put("WARNING", sb);
		}
		sb.append(message).append('\n');
		warnings.put(message, cause);
		output.append(message).append('\n');
		output.append(getStackTraceAsString(cause)).append('\n');
	}

		/**
	 * @param cause
	 * @return
	 */
	private String getStackTraceAsString(Throwable cause) {
		final Writer result = new StringWriter();
	  final PrintWriter printWriter = new PrintWriter(result);
	  cause.printStackTrace(printWriter);
		return result.toString();
	}

		/**
	 * @return the messages
	 */
	public Map<String, StringBuilder> getMessages() {
		return messages;
	}
	
	/**
	 * @return the errors
	 */
	public Map<String, Throwable> getErrors() {
		return errors;
	}
	
	/**
	 * @return the warnings
	 */
	public Map<String, Throwable> getWarnings() {
		return warnings;
	}
	
	/**
	 * @return the output
	 */
	public StringBuilder getOutput() {
		return output;
	}
	
	
}
