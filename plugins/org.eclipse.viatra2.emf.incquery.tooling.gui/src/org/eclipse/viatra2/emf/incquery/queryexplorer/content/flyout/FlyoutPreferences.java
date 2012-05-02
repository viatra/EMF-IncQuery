package org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout;

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
