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
package org.eclipse.incquery.tooling.ui.queryexplorer.content.flyout;

public interface IFlyoutPreferences {

    int DOCK_WEST = 1;
    int DOCK_EAST = 2;
    int DOCK_NORTH = 4;
    int DOCK_SOUTH = 8;

    int STATE_OPEN = 0;
    int STATE_COLLAPSED = 1;
    int STATE_EXPANDED = 2;

    int getDockLocation();

    int getState();

    int getWidth();

    void setDockLocation(int location);

    void setState(int state);

    void setWidth(int width);
}
