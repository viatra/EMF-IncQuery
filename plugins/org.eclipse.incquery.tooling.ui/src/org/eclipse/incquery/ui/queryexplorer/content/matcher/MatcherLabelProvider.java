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

package org.eclipse.incquery.ui.queryexplorer.content.matcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.incquery.ui.IncQueryGUIPlugin;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class MatcherLabelProvider extends ColumnLabelProvider {

	private List<ILabelProviderListener> listeners;
	
	public MatcherLabelProvider() {
		listeners = new ArrayList<ILabelProviderListener>();
	}
	
	@Override
	public void dispose() {
				
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		if (property.matches("text")) {
			return true;
		}
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		this.listeners.remove(listener);
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public Image getImage(Object element) {
		ImageRegistry imageRegistry = IncQueryGUIPlugin.getDefault().getImageRegistry();

		if (element instanceof ObservablePatternMatcherRoot) {
			ObservablePatternMatcherRoot root = (ObservablePatternMatcherRoot) element;
			if (root.isTainted()) {
				return imageRegistry.get(IncQueryGUIPlugin.ICON_ERROR);
			}
			else {
				return imageRegistry.get(IncQueryGUIPlugin.ICON_ROOT);
			}
		}
		else if (element instanceof ObservablePatternMatcher) {
			if (((ObservablePatternMatcher) element).isCreated()) {
				return imageRegistry.get(IncQueryGUIPlugin.ICON_MATCHER);
			} 
			else {
				return imageRegistry.get(IncQueryGUIPlugin.ICON_ERROR);
			}
		} 
		else if (element instanceof ObservablePatternMatch) {
			return imageRegistry.get(IncQueryGUIPlugin.ICON_MATCH);
		} 
		else {
			return null;
		}
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ObservablePatternMatcherRoot) {
			return ((ObservablePatternMatcherRoot) element).getText();
		}
		else if (element instanceof ObservablePatternMatcher) {
			return ((ObservablePatternMatcher) element).getText();
		}
		else if (element instanceof ObservablePatternMatch) {
			return ((ObservablePatternMatch) element).getText();
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		Display display = Display.getCurrent();
		if (element instanceof ObservablePatternMatcher) {
			ObservablePatternMatcher matcher = (ObservablePatternMatcher) element;
//			if (((ObservablePatternMatcher) element).getMatches().size() == 0) {
//				return display.getSystemColor(SWT.COLOR_GRAY);
//			}
			if (matcher.isGenerated()) {
				return display.getSystemColor(SWT.COLOR_DARK_GRAY);
			}
		}
		return display.getSystemColor(SWT.COLOR_BLACK);
	}
}
