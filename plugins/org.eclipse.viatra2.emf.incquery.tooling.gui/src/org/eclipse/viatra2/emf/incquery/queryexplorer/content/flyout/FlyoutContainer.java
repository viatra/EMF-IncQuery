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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

public class FlyoutContainer extends Composite {
	private static final int RESIZE_WIDTH = 5;
	private static final int TITLE_LINES = 30;
	private static final int TITLE_MARGIN = 5;
	private static final Font TITLE_FONT = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
	private static final Image PIN = loadImage(IncQueryGUIPlugin.ICON_PIN);
	private static final Image ARROW_LEFT = loadImage(IncQueryGUIPlugin.ICON_ARROW_LEFT);
	private static final Image ARROW_RIGHT = loadImage(IncQueryGUIPlugin.ICON_ARROW_RIGHT);
	private static final Image ARROW_TOP = loadImage(IncQueryGUIPlugin.ICON_ARROW_TOP);
	private static final Image ARROW_BOTTOM = loadImage(IncQueryGUIPlugin.ICON_ARROW_BOTTOM);

	private FlyoutControlComposite flyoutControl;
	private int titleWidth;
	private int titleHeight;
	private Image titleImage;
	private Image titleImageRotated;
	private boolean isResizable;
	private boolean stateHover;
	private Image backImage;
	
	private static Image loadImage(String key) {
		ImageRegistry registry = IncQueryGUIPlugin.getDefault().getImageRegistry();
		Image image = registry.get(key);
		return image;
	}
	
	public FlyoutContainer(FlyoutControlComposite flyoutControl, int style) {
		super(flyoutControl, style);
		this.flyoutControl = flyoutControl;
		configureMenu();
		updateTitleImage("title");
		// add listeners
		addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				if (titleImage != null) {
					titleImage.dispose();
					titleImageRotated.dispose();
					titleImage = null;
					titleImageRotated = null;
				}
				if (backImage != null) {
					backImage.dispose();
					backImage = null;
				}
			}
		});
		{
			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					layout();
				}
			};
			addListener(SWT.Move, listener);
			addListener(SWT.Resize, listener);
		}
		addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				handlePaint(event.gc);
			}
		});
		// mouse listeners
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				if (event.button == 1) {
					handle_mouseDown(event);
				}
			}

			@Override
			public void mouseUp(MouseEvent event) {
				if (event.button == 1) {
					handle_mouseUp(event);
				}
			}
		});
		addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				stateHover = false;
				redraw();
				setCursor(null);
			}

			@Override
			public void mouseHover(MouseEvent e) {
				handle_mouseHover();
			}
		});
		addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent event) {
				handle_mouseMove(event);
			}
		});
	}

	private void handle_mouseDown(MouseEvent event) {
		if (stateHover) {
			int state = flyoutControl.getPreferences().getState();
			if (state == IFlyoutPreferences.STATE_OPEN) {
				state = IFlyoutPreferences.STATE_COLLAPSED;
			} else {
				state = IFlyoutPreferences.STATE_OPEN;
			}
			flyoutControl.getPreferences().setState(state);
			redraw();
			flyoutControl.layout();
		} else if (getCursor() == ICursorConstants.SIZEWE
				|| getCursor() == ICursorConstants.SIZENS) {
			isResizable = true;
		} else if (getCursor() == ICursorConstants.SIZEALL) {
			handleDocking();
		}
	}

	private void handle_mouseUp(MouseEvent event) {
		if (isResizable) {
			isResizable = false;
			handle_mouseMove(event);
		}
	}

	private void handle_mouseMove(MouseEvent event) {
		if (isResizable) {
			// prepare width
			int width;
			if (flyoutControl.isHorizontal()) {
				width = getSize().x;
			} else {
				width = getSize().y;
			}
			// prepare new width
			int newWidth = width;
			if (flyoutControl.isWest()) {
				newWidth = event.x + RESIZE_WIDTH / 2;
			} else if (flyoutControl.isEast()) {
				newWidth = width - event.x + RESIZE_WIDTH / 2;
			} else if (flyoutControl.isNorth()) {
				newWidth = event.y + RESIZE_WIDTH / 2;
			} else if (flyoutControl.isSouth()) {
				newWidth = width - event.y + RESIZE_WIDTH / 2;
			}
			// update width
			if (newWidth != width) {
				flyoutControl.getPreferences().setWidth(newWidth);
				redraw();
				flyoutControl.layout();
			}
		} else {
			Rectangle clientArea = getClientArea();
			boolean inside = clientArea.contains(event.x, event.y);
			int x = event.x;
			int y = event.y;
			if (inside) {
				// check for state
				{
					boolean oldStateHover = stateHover;
					if (flyoutControl.isEast()) {
						stateHover = x > clientArea.width - titleHeight
								&& y < titleHeight;
					} else {
						stateHover = x < titleHeight
								&& y < titleHeight;
					}
					if (stateHover != oldStateHover) {
						redraw();
					}
					if (stateHover) {
						setCursor(null);
						return;
					}
				}
				// check for resize band
				if (isOpenExpanded()) {
					if (flyoutControl.isWest() && x >= clientArea.width - RESIZE_WIDTH) {
						setCursor(ICursorConstants.SIZEWE);
					} else if (flyoutControl.isEast() && x <= RESIZE_WIDTH) {
						setCursor(ICursorConstants.SIZEWE);
					} else if (flyoutControl.isNorth()
							&& y >= clientArea.height - RESIZE_WIDTH) {
						setCursor(ICursorConstants.SIZENS);
					} else if (flyoutControl.isSouth() && y <= RESIZE_WIDTH) {
						setCursor(ICursorConstants.SIZENS);
					} else {
						setCursor(null);
					}
				}
				// check for docking
				if (getCursor() == null) {
					setCursor(ICursorConstants.SIZEALL);
				}
			} else {
				setCursor(null);
			}
		}
	}

	private void handle_mouseHover() {
		if (flyoutControl.getPreferences().getState() == IFlyoutPreferences.STATE_COLLAPSED	&& !stateHover) {
			flyoutControl.getPreferences().setState(IFlyoutPreferences.STATE_EXPANDED);
			
			flyoutControl.layout();
			// add listeners
			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					if (event.type == SWT.Dispose) {
						getDisplay().removeFilter(SWT.MouseMove, this);
					} else {
						Point p = ((Control) event.widget).toDisplay(
								event.x, event.y);
						// during resize mouse can be temporary outside of
						// flyout - ignore
						if (isResizable) {
							return;
						}
						// mouse in in flyout container - ignore
						if (getClientArea().contains(toControl(p.x, p.y))) {
							return;
						}
						// mouse is in full container - collapse
						if (flyoutControl.getClientArea().contains(
								flyoutControl.toControl(p.x, p.y))) {
							getDisplay().removeFilter(SWT.MouseMove, this);
							// it is possible, that user restored (OPEN)
							// flyout, so collapse only if we still in
							// expand state
							if (flyoutControl.getPreferences().getState() == IFlyoutPreferences.STATE_EXPANDED) {
								flyoutControl.getPreferences().setState(IFlyoutPreferences.STATE_COLLAPSED);
								flyoutControl.layout();
							}
						}
					}
				}
			};
			addListener(SWT.Dispose, listener);
			getDisplay().addFilter(SWT.MouseMove, listener);
		}
	}

	private void handleDocking() {
		final int width = flyoutControl.getPreferences().getWidth();
		final int oldDockLocation = flyoutControl.getDockLocation();
		final int[] newDockLocation = new int[] { oldDockLocation };
		final Tracker dockingTracker = new Tracker(flyoutControl, SWT.NONE);
		dockingTracker.setRectangles(new Rectangle[] { getBounds() });
		dockingTracker.setStippled(true);
		dockingTracker.addListener(SWT.Move, new Listener() {
			public void handleEvent(Event event2) {
				Rectangle clientArea = flyoutControl.getClientArea();
				Point location = flyoutControl.toControl(event2.x, event2.y);
				int h3 = clientArea.height / 3;
				// check locations
				if (location.y < h3
						&& flyoutControl.isValidDockLocation(IFlyoutPreferences.DOCK_NORTH)) {
					dockingTracker
							.setRectangles(new Rectangle[] { new Rectangle(
									0, 0, clientArea.width, width) });
					newDockLocation[0] = IFlyoutPreferences.DOCK_NORTH;
				} else if (location.y > 2 * h3
						&& flyoutControl.isValidDockLocation(IFlyoutPreferences.DOCK_SOUTH)) {
					dockingTracker
							.setRectangles(new Rectangle[] { new Rectangle(
									0, clientArea.height - width,
									clientArea.width, width) });
					newDockLocation[0] = IFlyoutPreferences.DOCK_SOUTH;
				} else if (location.x < clientArea.width / 2
						&& flyoutControl.isValidDockLocation(IFlyoutPreferences.DOCK_WEST)) {
					dockingTracker
							.setRectangles(new Rectangle[] { new Rectangle(
									0, 0, width, clientArea.height) });
					newDockLocation[0] = IFlyoutPreferences.DOCK_WEST;
				} else if (flyoutControl.isValidDockLocation(IFlyoutPreferences.DOCK_EAST)) {
					dockingTracker
							.setRectangles(new Rectangle[] { new Rectangle(
									clientArea.width - width, 0, width,
									clientArea.height) });
					newDockLocation[0] = IFlyoutPreferences.DOCK_EAST;
				} else {
					dockingTracker
							.setRectangles(new Rectangle[] { getBounds() });
					newDockLocation[0] = oldDockLocation;
				}
			}
		});
		// start tracking
		if (dockingTracker.open()) {
			flyoutControl.setDockLocation(newDockLocation[0]);
		}
		// dispose tracker
		dockingTracker.dispose();
	}

	public Control getControl() {
		Control[] children = getChildren();
		return children.length == 1 ? children[0] : null;
	}

	public void setTitleText(String text) {
		updateTitleImage(text);
	}

	@Override
	public void layout() {
		Control control = getControl();
		if (control == null) {
			return;
		}
		
		Rectangle clientArea = getClientArea();
		if (isOpenExpanded()) {
			if (flyoutControl.isWest()) {
				int y = titleHeight;
				control.setBounds(0, y, clientArea.width - RESIZE_WIDTH,
						clientArea.height - y);
			} else if (flyoutControl.isEast()) {
				int y = titleHeight;
				control.setBounds(RESIZE_WIDTH, y, clientArea.width
						- RESIZE_WIDTH, clientArea.height - y);
			} else if (flyoutControl.isNorth()) {
				int y = titleHeight;
				control.setBounds(0, y, clientArea.width, clientArea.height
						- y - RESIZE_WIDTH);
			} else if (flyoutControl.isSouth()) {
				int y = RESIZE_WIDTH + titleHeight;
				control.setBounds(0, y, clientArea.width, clientArea.height
						- y);
			}
		} else {
			control.setBounds(0, 0, 0, 0);
		}
	}

	private void handlePaint(GC paintGC) {
		Rectangle clientArea = getClientArea();
		// prepare back image
		GC gc;
		{
			if (backImage == null
					|| !backImage.getBounds().equals(clientArea)) {
				if (backImage != null) {
					backImage.dispose();
				}
				backImage = new Image(getDisplay(), clientArea.width,
						clientArea.height);
			}
			// prepare GC
			gc = new GC(backImage);
			gc.setBackground(paintGC.getBackground());
			gc.setForeground(paintGC.getForeground());
			gc.fillRectangle(clientArea);
		}
		//
		if (isOpenExpanded()) {
			// draw header
			{
				// draw title
				if (flyoutControl.isWest()) {
					drawStateImage(gc, 0, 0);
					gc.drawImage(titleImage, titleHeight, 0);
				} else if (flyoutControl.isEast()) {
					int x = clientArea.width - titleHeight;
					drawStateImage(gc, x, 0);
					gc.drawImage(titleImage, x - titleWidth, 0);
				} else if (flyoutControl.isNorth()) {
					drawStateImage(gc, 0, 0);
					gc.drawImage(titleImage, titleHeight, 0);
				} else if (flyoutControl.isSouth()) {
					int y = RESIZE_WIDTH;
					drawStateImage(gc, 0, y);
					gc.drawImage(titleImage, titleHeight, y);
				}
			}
			// draw resize band
			drawResizeBand(gc);
		} else {
			if (flyoutControl.isHorizontal()) {
				drawStateImage(gc, 0, 0);
				gc.drawImage(titleImageRotated, 0, titleHeight);
			} else {
				drawStateImage(gc, 0, 0);
				gc.drawImage(titleImage, titleHeight, 0);
			}
			DrawUtils.drawHighlightRectangle(gc, 0, 0, clientArea.width,
					clientArea.height);
		}
		// flush back image
		{
			gc.dispose();
			paintGC.drawImage(backImage, 0, 0);
		}
	}

	private void drawStateImage(GC gc, int x, int y) {
		DrawUtils.drawImageCHCV(gc, getStateImage(), x, y, titleHeight,
				titleHeight);
		if (stateHover) {
			DrawUtils.drawHighlightRectangle(gc, x, y, titleHeight,
					titleHeight);
		}
	}

	private Image getStateImage() {
		int location = flyoutControl.getDockLocation();
		int state = flyoutControl.getPreferences().getState();
		if (state == IFlyoutPreferences.STATE_OPEN) {
			switch (location) {
			case IFlyoutPreferences.DOCK_WEST:
				return ARROW_LEFT;
			case IFlyoutPreferences.DOCK_EAST:
				return ARROW_RIGHT;
			case IFlyoutPreferences.DOCK_NORTH:
				return ARROW_TOP;
			case IFlyoutPreferences.DOCK_SOUTH:
				return ARROW_BOTTOM;
			}
		} else if (state == IFlyoutPreferences.STATE_EXPANDED) {
			return PIN;
		} else {
			switch (location) {
			case IFlyoutPreferences.DOCK_WEST:
				return ARROW_RIGHT;
			case IFlyoutPreferences.DOCK_EAST:
				return ARROW_LEFT;
			case IFlyoutPreferences.DOCK_NORTH:
				return ARROW_BOTTOM;
			case IFlyoutPreferences.DOCK_SOUTH:
				return ARROW_TOP;
			}
		}
		
		return null;
	}

	private void drawResizeBand(GC gc) {
		Rectangle clientArea = getClientArea();
		// prepare locations
		int x, y, width, height;
		if (flyoutControl.isHorizontal()) {
			if (flyoutControl.isWest()) {
				x = clientArea.width - RESIZE_WIDTH;
			} else {
				x = 0;
			}
			y = 0;
			width = RESIZE_WIDTH;
			height = clientArea.height;
		} else {
			x = 0;
			if (flyoutControl.isNorth()) {
				y = clientArea.height - RESIZE_WIDTH;
			} else {
				y = 0;
			}
			width = clientArea.width;
			height = RESIZE_WIDTH;
		}
		// draw band
		DrawUtils.drawHighlightRectangle(gc, x, y, width, height);
	}

	private boolean isOpenExpanded() {
		int state = flyoutControl.getPreferences().getState();
		return state == IFlyoutPreferences.STATE_OPEN || state == IFlyoutPreferences.STATE_EXPANDED;
	}

	private void updateTitleImage(String text) {
		// prepare size of text
		Point textSize;
		{
			GC gc = new GC(this);
			gc.setFont(TITLE_FONT);
			textSize = gc.textExtent(text);
			gc.dispose();
		}
		// dispose existing image
		if (titleImage != null) {
			titleImage.dispose();
			titleImageRotated.dispose();
		}
		// prepare new image
		{
			titleWidth = textSize.x + 2 * TITLE_LINES + 4 * TITLE_MARGIN;
			titleHeight = textSize.y;
			titleImage = new Image(getDisplay(), titleWidth, titleHeight);
			GC gc = new GC(titleImage);
			try {
				gc.setBackground(getBackground());
				gc.fillRectangle(0, 0, titleWidth, titleHeight);
				int x = 0;
				gc.setForeground(FlyoutConstants.BLACK);
				gc.setFont(TITLE_FONT);
				gc.drawText(text, x, 0);
				
			} finally {
				gc.dispose();
			}
		}
		// prepare rotated image
		titleImageRotated = DrawUtils.createRotatedImage(titleImage);
	}

	private void configureMenu() {
		final MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuMgr) {
				addDockActions();
			}

			private void addDockActions() {
				MenuManager dockManager = new MenuManager("menuManager");
				addDockAction(dockManager, "west",
						IFlyoutPreferences.DOCK_WEST);
				addDockAction(dockManager, "east",
						IFlyoutPreferences.DOCK_EAST);
				addDockAction(dockManager, "top",
						IFlyoutPreferences.DOCK_NORTH);
				addDockAction(dockManager, "bottom",
						IFlyoutPreferences.DOCK_SOUTH);
				manager.add(dockManager);
			}

			private void addDockAction(MenuManager dockManager,
					String text, int location) {
				if ((flyoutControl.getValidDockLocations() & location) != 0) {
					dockManager.add(new DockAction(flyoutControl, text, location));
				}
			}
		});
		// set menu
		setMenu(manager.createContextMenu(this));
		// dispose it later
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				manager.dispose();
			}
		});
	}
	
	public int getTitleHeight() {
		return titleHeight;
	}
	
	public int getTitleWidth() {
		return titleWidth;
	}
}
