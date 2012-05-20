package org.eclipse.viatra2.patternlanguage.ui.builder;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.viatra2.patternlanguage.ui.EMFPatternLanguageUIActivator;

public class LoggingUtil {

	private static ILog log = EMFPatternLanguageUIActivator.getInstance().getLog();
	public static final String PLUGIN_ID = EMFPatternLanguageUIActivator.ORG_ECLIPSE_VIATRA2_PATTERNLANGUAGE_EMFPATTERNLANGUAGE;
	
	private static IStatus createStatus(String message, int severity, Throwable exception) {
		return new Status(severity, PLUGIN_ID, message, exception);
	}
	
	public static void error(String message) {
		log.log(createStatus(message, IStatus.ERROR, null));
	}
	
	public static void error(String message, Throwable exception) {
		log.log(createStatus(message, IStatus.ERROR, exception));
	}

	public static void warning(String message, Exception exception) {
		log.log(createStatus(message, IStatus.WARNING, exception));
	}
	
}
