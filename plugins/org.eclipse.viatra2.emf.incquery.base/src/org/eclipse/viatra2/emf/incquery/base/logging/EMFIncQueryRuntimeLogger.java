/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.logging;

/** 
 * Interface for logging errors that happen during run-time execution.
 * @author Bergmann Gábor
 *
 */
public interface EMFIncQueryRuntimeLogger {
	public void logDebug(String message);
	public void logError(String message);
	public void logError(String message, Throwable cause);
	public void logWarning(String message);
	public void logWarning(String message, Throwable cause);
}