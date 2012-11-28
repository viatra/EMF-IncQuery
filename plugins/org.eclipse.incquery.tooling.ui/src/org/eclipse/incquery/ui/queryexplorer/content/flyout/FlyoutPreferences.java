/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.ui.queryexplorer.content.flyout;

public final class FlyoutPreferences implements IFlyoutPreferences {
	private int dockLocation;
	private int state;
	private int width;

	public FlyoutPreferences(int dockLocation, int state, int width) {
		super();
		this.dockLocation = dockLocation;
		this.state = state;
		this.width = width;
	}

	public int getDockLocation() {
		return dockLocation;
	}

	public void setDockLocation(int dockLocation) {
		this.dockLocation = dockLocation;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
