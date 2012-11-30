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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public interface FlyoutConstants {

    public static final int LEFT = 1;
    public static final int CENTER = 2;
    public static final int RIGHT = 4;
    public static final int LEFT_CENTER_RIGHT = LEFT | CENTER | RIGHT;
    public static final int TOP = 8;
    public static final int MIDDLE = 16;
    public static final int BOTTOM = 32;
    public static final int BASELINE = 64;
    public static final int TOP_MIDDLE_BOTTOM = TOP | MIDDLE | BOTTOM;

    int NONE = 0;
    int NORTH = 1 << 0;
    int SOUTH = 1 << 2;
    int WEST = 1 << 3;
    int EAST = 1 << 4;
    int NORTH_EAST = NORTH | EAST;
    int NORTH_WEST = NORTH | WEST;
    int SOUTH_EAST = SOUTH | EAST;
    int SOUTH_WEST = SOUTH | WEST;
    int NORTH_SOUTH = NORTH | SOUTH;
    int EAST_WEST = EAST | WEST;

    public static Color buttonLightest = Utils.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
    public static Color button = Utils.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    public static Color buttonDarker = Utils.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
    public static Color BLACK = new Color(null, 0, 0, 0);

    static class Utils {

        private static Color getSystemColor(final int id) {
            final Color[] color = new Color[1];
            final Display display = Display.getDefault();
            display.syncExec(new Runnable() {
                public void run() {
                    color[0] = display.getSystemColor(id);
                }
            });
            return color[0];
        }
    }
}