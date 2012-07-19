package org.eclipse.viatra2.emf.incquery.queryexplorer.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

public class PatternInitializationPreferencePage extends PreferencePage
		implements IWorkbenchPreferencePage {

	private static final String WILDCARD_MODE_DESCRIPTION = "&Description: Turn off wildcard mode to decrease the memory usage\nwhile working with very large models during query development.";

	public PatternInitializationPreferencePage() {
		
	}

	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected Control createContents(Composite parent) {
		
		Composite control = new Composite(parent, SWT.NONE);
		Label label = new Label(control, SWT.NONE | SWT.TOP);
		label.setText(WILDCARD_MODE_DESCRIPTION);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		final BooleanFieldEditor wildcardModeEditor = new BooleanFieldEditor(PreferenceConstants.WILDCARD_MODE, "&Wildcard mode", control);
		wildcardModeEditor.setPreferenceStore(IncQueryGUIPlugin.getDefault().getPreferenceStore());
		wildcardModeEditor.load();
		wildcardModeEditor.setPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				IncQueryGUIPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.WILDCARD_MODE, wildcardModeEditor.getBooleanValue());
			}
		});
		return control;
	}
}
