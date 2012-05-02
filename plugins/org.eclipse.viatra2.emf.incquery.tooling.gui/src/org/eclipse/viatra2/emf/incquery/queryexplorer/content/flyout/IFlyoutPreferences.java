package org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout;

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
