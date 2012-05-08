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
package org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public final class FlyoutControlComposite extends Composite {

	private final IFlyoutPreferences preferences;
	private final FlyoutContainer flyoutContainer;
	private int minWidth = 100;
	private int validDockLocations = -1;

	public FlyoutControlComposite(Composite parent, int style,
			IFlyoutPreferences preferences) {
		super(parent, style);
		this.preferences = preferences;

		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				if (getShell().getMinimized()) {
					return;
				}
				layout();
			}
		});
		
		flyoutContainer = new FlyoutContainer(this, SWT.NO_BACKGROUND);
	}

	public Composite getFlyoutParent() {
		return flyoutContainer;
	}

	public Composite getClientParent() {
		return this;
	}

	public void setValidDockLocations(int validDockLocations) {
		this.validDockLocations = validDockLocations;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public void setTitleText(String text) {
		flyoutContainer.setTitleText(text);
	}

	@Override
	public void layout() {
		Rectangle clientArea = getClientArea();
		int state = preferences.getState();
		Control client = getChildren()[1];

		if (clientArea.width == 0 || clientArea.height == 0) {
			return;
		}

		if (flyoutContainer.getControl() == null) {
			flyoutContainer.setBounds(0, 0, 0, 0);
			client.setBounds(clientArea);
			return;
		}
		// prepare width to display
		int width;
		int offset;
		if (state == IFlyoutPreferences.STATE_OPEN) {
			width = preferences.getWidth();
			// limit maximum value
			if (isHorizontal()) {
				width = Math.min(clientArea.width / 2, width);
			} else {
				width = Math.min(clientArea.height / 2, width);
			}
			// limit minimum value
			width = Math.max(width, minWidth);
			width = Math.max(width, 2 * flyoutContainer.getTitleHeight()
					+ flyoutContainer.getTitleWidth());
			// remember actual width
			preferences.setWidth(width);
			//
			offset = width;
		} else if (state == IFlyoutPreferences.STATE_EXPANDED) {
			offset = flyoutContainer.getTitleHeight();
			width = preferences.getWidth();
		} else {
			width = flyoutContainer.getTitleHeight();
			offset = width;
		}
		// change bounds for flyout container and client control
		{
			if (isWest()) {
				flyoutContainer.setBounds(0, 0, width, clientArea.height);
				client.setBounds(offset, 0, clientArea.width - offset,
						clientArea.height);
			} else if (isEast()) {
				flyoutContainer.setBounds(clientArea.width - width, 0, width,
						clientArea.height);
				client.setBounds(0, 0, clientArea.width - offset,
						clientArea.height);
			} else if (isNorth()) {
				flyoutContainer.setBounds(0, 0, clientArea.width, width);
				client.setBounds(0, offset, clientArea.width, clientArea.height
						- offset);
			} else if (isSouth()) {
				flyoutContainer.setBounds(0, clientArea.height - width,
						clientArea.width, width);
				client.setBounds(0, 0, clientArea.width, clientArea.height
						- offset);
			}
		}
	}

	public boolean isHorizontal() {
		return isWest() || isEast();
	}

	public boolean isWest() {
		return getDockLocation() == IFlyoutPreferences.DOCK_WEST;
	}

	public boolean isEast() {
		return getDockLocation() == IFlyoutPreferences.DOCK_EAST;
	}

	public boolean isNorth() {
		return getDockLocation() == IFlyoutPreferences.DOCK_NORTH;
	}

	public boolean isSouth() {
		return getDockLocation() == IFlyoutPreferences.DOCK_SOUTH;
	}

	public boolean isValidDockLocation(int location) {
		return (location & validDockLocations) == location;
	}

	public int getDockLocation() {
		return preferences.getDockLocation();
	}
	
	public int getValidDockLocations() {
		return validDockLocations;
	}

	public void setDockLocation(int dockLocation) {
		preferences.setDockLocation(dockLocation);
		layout();
	}
	
	public IFlyoutPreferences getPreferences() {
		return preferences;
	}
}
