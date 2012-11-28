/*******************************************************************************
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *    Tamas Szabo - code extensions, modifications
 *******************************************************************************/
package org.eclipse.incquery.ui.queryexplorer.content.flyout;

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
