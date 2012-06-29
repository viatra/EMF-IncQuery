package org.eclipse.viatra2.emf.incquery.base.logging;

public final class SysErrLogger implements EMFIncQueryRuntimeLogger {
	@Override
	public void logDebug(String message) {
		System.err.println("[DEBUG] " + message);
	}

	@Override
	public void logError(String message) {
		System.err.println("[ERROR] " + message);
	}

	@Override
	public void logError(String message, Throwable cause) {
		System.err.println("[ERROR] " + message);
		cause.printStackTrace();
	}

	@Override
	public void logWarning(String message) {
		System.err.println("[WARNING] " + message);
	}

	@Override
	public void logWarning(String message, Throwable cause) {
		System.err.println("[WARNING] " + message);
		cause.printStackTrace();
	}
}