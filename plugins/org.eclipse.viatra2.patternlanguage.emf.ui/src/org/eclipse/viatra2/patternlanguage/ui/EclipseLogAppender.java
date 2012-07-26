/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.ui;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * Updated Eclipse LogAppender based on the implementation in org.eclipse.xtext.logger bundle.
 * @author Peter Friese - Initial contribution and API
 * @author Sven Efftinge
 * @author Knut Wannheden - Refactored handling when used in non OSGi environment
 * @author Zoltan Ujhelyi - updated for the use of EMF-IncQuery
 */
public class EclipseLogAppender extends AppenderSkeleton {

	private static final String LOG_PATTERN = "%m%n";

	private static final String BUNDLE_NAME = EMFPatternLanguageUIActivator.getInstance().getBundle().getSymbolicName();

	private boolean initialized;
	private ILog log;

	public EclipseLogAppender() {
		super();
		layout = new PatternLayout(LOG_PATTERN);
	}

	public EclipseLogAppender(boolean isActive) {
		super(isActive);
		layout = new PatternLayout(LOG_PATTERN);		
	}

	private synchronized void ensureInitialized() {
		if (!initialized) {
			log = Platform.getLog(Platform.getBundle(BUNDLE_NAME));
			initialized = true;
		}
	}

	private ILog getLog() {
		ensureInitialized();
		return log;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (isDoLog(event.getLevel())) {
			String logString = layout.format(event);
			
			ILog myLog = getLog();
			if (myLog != null) {
				String loggerName = event.getLoggerName();
				int severity = mapLevel(event.getLevel());
				final Throwable throwable = event.getThrowableInformation() != null ? event.getThrowableInformation()
						.getThrowable() : null;
				IStatus status = createStatus(severity, loggerName, logString, throwable);
				getLog().log(status);
			} 
		}
	}

	private boolean isDoLog(Level level) {
		return level.toInt() >= Priority.WARN_INT;
	}

	private int mapLevel(Level level) {
		switch (level.toInt()) {
			case Priority.DEBUG_INT:
			case Priority.INFO_INT:
				return IStatus.INFO;

			case Priority.WARN_INT:
				return IStatus.WARNING;

			case Priority.ERROR_INT:
			case Priority.FATAL_INT:
				return IStatus.ERROR;

			default:
				return IStatus.INFO;
		}
	}

	private IStatus createStatus(int severity, String loggerName, String message, Throwable throwable) {
		return new Status(severity, BUNDLE_NAME, message, throwable);
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return true;
	}

}
