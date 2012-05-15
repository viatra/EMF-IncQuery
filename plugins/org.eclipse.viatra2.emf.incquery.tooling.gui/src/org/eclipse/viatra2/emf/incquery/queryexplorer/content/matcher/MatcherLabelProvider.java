package org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

public class MatcherLabelProvider implements ILabelProvider {

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
			return imageRegistry.get(IncQueryGUIPlugin.ICON_ROOT);
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

	
}
