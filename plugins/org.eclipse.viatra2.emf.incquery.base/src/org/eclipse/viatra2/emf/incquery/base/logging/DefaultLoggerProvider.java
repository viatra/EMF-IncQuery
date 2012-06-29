/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gábor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.base.logging;

import org.eclipse.viatra2.emf.incquery.base.IncQueryBasePlugin;

/**
 * Provides a static default logger.
 * If running in the Eclipse plug-in environment, the default logger will use the Eclipse logging mechanism, otherwise it will forward to SysErr. 
 * 
 * @author Bergmann Gábor
 *
 */
public class DefaultLoggerProvider {
	private static EMFIncQueryRuntimeLogger defaultLogger;
	
	/**
	 * Returns the default logger
	 */
	public static EMFIncQueryRuntimeLogger getDefaultLogger() {
		if(defaultLogger == null) {
			defaultLogger = createLogger();
		}
		return defaultLogger;
	}	
	
	/**
	 * Creates a new logger instance
	 */
	private static EMFIncQueryRuntimeLogger createLogger() {
		final IncQueryBasePlugin plugin = IncQueryBasePlugin.getDefault();
		EMFIncQueryRuntimeLogger newLogger;
		if (plugin !=null) 
			newLogger = new EclipsePluginLogger(plugin.getLog(),IncQueryBasePlugin.PLUGIN_ID); 
		else newLogger = new SysErrLogger();
		return newLogger;
	}
	
	

}
