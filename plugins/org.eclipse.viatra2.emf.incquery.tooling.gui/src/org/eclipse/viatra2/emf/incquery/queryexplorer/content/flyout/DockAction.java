package org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout;

import org.eclipse.jface.action.Action;

public class DockAction extends Action {
	private final int location;
	private FlyoutControlComposite flyoutControl;

	public DockAction(FlyoutControlComposite flyoutControl, String text, int location) {
		super(text, AS_RADIO_BUTTON);
		this.flyoutControl = flyoutControl;
		this.location = location;
	}

	@Override
	public boolean isChecked() {
		return flyoutControl.getDockLocation() == location;
	}

	@Override
	public void run() {
		flyoutControl.setDockLocation(location);
	}
}
