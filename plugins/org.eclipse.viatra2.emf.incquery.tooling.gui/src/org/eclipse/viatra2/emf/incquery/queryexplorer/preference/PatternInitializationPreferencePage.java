package org.eclipse.viatra2.emf.incquery.queryexplorer.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

public class PatternInitializationPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public PatternInitializationPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		IPreferenceStore store = IncQueryGUIPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor formatOnSave = new BooleanFieldEditor(PreferenceConstants.WILDCARD_MODE, "&Wildcard mode", getFieldEditorParent());
		addField(formatOnSave);
	}

}
