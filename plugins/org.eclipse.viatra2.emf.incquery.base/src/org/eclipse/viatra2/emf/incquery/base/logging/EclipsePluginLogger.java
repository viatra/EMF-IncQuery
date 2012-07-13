package org.eclipse.viatra2.emf.incquery.base.logging;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public final class EclipsePluginLogger implements EMFIncQueryRuntimeLogger {
	private final ILog log; 
	private final String pluginID;

	public EclipsePluginLogger(ILog log, String pluginID) {
		super();
		this.log = log;
		this.pluginID = pluginID;
	}

	@Override
	public void logDebug(String message) {
		//log.log(new Status(IStatus.INFO, pluginID, message));
	}

	@Override
	public void logError(String message) {
		log.log(new Status(IStatus.ERROR, pluginID, message));
	}

	@Override
	public void logError(String message, Throwable cause) {
		log.log(new Status(IStatus.ERROR, pluginID, message, cause));
	}

	@Override
	public void logWarning(String message) {
		log.log(new Status(IStatus.WARNING, pluginID, message));
	}

	@Override
	public void logWarning(String message, Throwable cause) {
		log.log(new Status(IStatus.WARNING, pluginID, message, cause));
	}
}