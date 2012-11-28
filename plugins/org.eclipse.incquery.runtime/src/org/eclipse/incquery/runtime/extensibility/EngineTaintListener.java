/*******************************************************************************
 * Copyright (c) 2010-2012, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.extensibility;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.incquery.runtime.api.IncQueryEngine;

/**
 * Listens for the event of the engine becoming tainted.
 * 
 * Attach this listener to the logger of the engine as an appender. 
 * Do not forget to remove when losing interest or when engine is disposed. 
 * 
 * @author Bergmann Gabor
 * @see IncQueryEngine#isTainted()
 */
public abstract class EngineTaintListener extends AppenderSkeleton {
	public static final Level TRESHOLD = Level.FATAL;

	/**
	 * This callback will be alerted at most once, when the engine becomes tainted.
	 */
	public abstract void engineBecameTainted();
	
	private boolean noTaintDetectedYet = true;

	@Override
	public boolean requiresLayout() {return false;}

	@Override
	public void close() {}

	@Override
	protected void append(LoggingEvent event) {
		if (event.getLevel().isGreaterOrEqual(TRESHOLD) && noTaintDetectedYet) {
			noTaintDetectedYet = false;
			engineBecameTainted();
		}					
	}
}