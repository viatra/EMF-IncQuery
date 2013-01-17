package org.eclipse.incquery.tooling.ui.patternregistry.views;

import org.eclipse.incquery.runtime.patternregistry.PatternInfo;
import org.eclipse.incquery.runtime.patternregistry.PatternRegistry;
import org.eclipse.incquery.runtime.patternregistry.listeners.IPatternRegistryListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class PatternRegistryViewPart extends ViewPart {

    private Label label;

    public PatternRegistryViewPart() {
        super();
    }

    @Override
    public void setFocus() {
        label.setFocus();
    }

    @Override
    public void createPartControl(Composite parent) {
        label = new Label(parent, 0);
        updateLabelText();

        PatternRegistry.INSTANCE.registerListener(new IPatternRegistryListener() {
            @Override
            public void patternRemoved(PatternInfo patternInfo) {
                updateLabelText();
            }

            @Override
            public void patternAdded(PatternInfo patternInfo) {
                updateLabelText();
            }
        });
    }

    private void updateLabelText() {
        String labelText = "";
        for (PatternInfo patternInfo : PatternRegistry.INSTANCE.getAllPatternInfosInAspect()) {
            labelText = labelText.concat(patternInfo.getId() + "\n");
        }
        label.setText(labelText);
    }

}
