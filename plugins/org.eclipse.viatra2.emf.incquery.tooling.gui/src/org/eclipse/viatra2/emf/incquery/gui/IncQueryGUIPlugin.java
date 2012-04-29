package org.eclipse.viatra2.emf.incquery.gui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class IncQueryGUIPlugin extends AbstractUIPlugin {

	/**
	 *  The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.eclipse.viatra2.emf.incquery.tooling.gui";
	
	public static final String ICON_ROOT = "navigator_root";
	public static final String ICON_MATCHER = "matcher";
	public static final String ICON_MATCH = "match";
	public static final String ICON_ERROR = "error";
	public static final String ICON_ARROW_RIGHT = "arrow_right";
	public static final String ICON_ARROW_LEFT = "arrow_left";
	public static final String ICON_PIN = "pin";
	public static final String ICON_ARROW_TOP = "arrow_top";
	public static final String ICON_ARROW_BOTTOM = "arrow_bottom";

	// The shared instance
	private static IncQueryGUIPlugin plugin;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IncQueryGUIPlugin getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		@SuppressWarnings("unused")
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		reg.put(ICON_ROOT, imageDescriptorFromPlugin(PLUGIN_ID, "icons/root.gif"));
		reg.put(ICON_MATCHER, imageDescriptorFromPlugin(PLUGIN_ID, "icons/matcher.gif"));
		reg.put(ICON_MATCH, imageDescriptorFromPlugin(PLUGIN_ID, "icons/match.gif"));
		reg.put(ICON_ERROR, imageDescriptorFromPlugin(PLUGIN_ID, "icons/error.gif"));
		reg.put(ICON_PIN, imageDescriptorFromPlugin(PLUGIN_ID, "icons/pin.gif"));
		reg.put(ICON_ARROW_RIGHT, imageDescriptorFromPlugin(PLUGIN_ID, "icons/arrow_right.gif"));
		reg.put(ICON_ARROW_LEFT, imageDescriptorFromPlugin(PLUGIN_ID, "icons/arrow_left.gif"));
		reg.put(ICON_ARROW_TOP, imageDescriptorFromPlugin(PLUGIN_ID, "icons/arrow_top.gif"));
		reg.put(ICON_ARROW_BOTTOM, imageDescriptorFromPlugin(PLUGIN_ID, "icons/arrow_bottom.gif"));
	}

}
