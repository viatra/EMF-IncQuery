/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.ui.queryexplorer.preference;

import org.eclipse.incquery.tooling.ui.IncQueryGUIPlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
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

public class PatternInitializationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String WILDCARD_MODE_DESCRIPTION = "&In wildcard mode, every aspect of the EMF model is automatically indexed, "
            + "as opposed to only indexing model elements and features relevant to the "
            + "currently registered patterns; thus patterns can be registered and "
            + "unregistered without re-traversing the model. This is typically useful "
            + "during query development. Turn off wildcard mode to decrease the memory "
            + "usage while working with very large models.";

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    protected Control createContents(Composite parent) {
        final IPreferenceStore store = IncQueryGUIPlugin.getDefault().getPreferenceStore();
        Composite control = new Composite(parent, SWT.NONE);
        Label wildcardDescriptionLabel = new Label(control, SWT.NONE | SWT.WRAP);
        wildcardDescriptionLabel.setText(WILDCARD_MODE_DESCRIPTION);
        final GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.widthHint = 200;
        wildcardDescriptionLabel.setLayoutData(layoutData);
        final BooleanFieldEditor wildcardModeEditor = new BooleanFieldEditor(PreferenceConstants.WILDCARD_MODE,
                "&Wildcard mode", control);
        wildcardModeEditor.setPreferenceStore(IncQueryGUIPlugin.getDefault().getPreferenceStore());
        wildcardModeEditor.load();
        wildcardModeEditor.setPropertyChangeListener(new IPropertyChangeListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                store.setValue(PreferenceConstants.WILDCARD_MODE, wildcardModeEditor.getBooleanValue());
                // the mentioned replace method did not work for me
                IncQueryGUIPlugin.getDefault().savePluginPreferences();
            }
        });
        return control;
    }
}
