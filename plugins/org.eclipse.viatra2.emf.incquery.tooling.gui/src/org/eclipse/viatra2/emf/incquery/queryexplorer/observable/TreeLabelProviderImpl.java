package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.viatra2.emf.incquery.queryexplorer.DatabindingUIPluginActivator;
import org.osgi.framework.Bundle;

/**
 * Custom tree label provider implementation.
 * The class is responsible to generate labels and images for the elements in the tree viewer.
 * 
 * @author Tamas Szabo
 *
 */
@SuppressWarnings("restriction")
public class TreeLabelProviderImpl extends StyledCellLabelProvider {

	private IMapChangeListener mapChangeListener;
	private Map<Class<?>, Image> imageCache;
	
	public TreeLabelProviderImpl(IObservableMap[] attributeMaps) {
		this.imageCache = new HashMap<Class<?>, Image>();
		
		mapChangeListener = new IMapChangeListener() {
			public void handleMapChange(MapChangeEvent event) {
				Set<?> affectedElements = event.diff.getChangedKeys();
				if (!affectedElements.isEmpty()) {
					LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
							TreeLabelProviderImpl.this,
							affectedElements.toArray());
					fireLabelProviderChanged(newEvent);
				}
			}
		};

		for (int i = 0; i < attributeMaps.length; i++) {
			attributeMaps[i].addMapChangeListener(mapChangeListener);
		}
	}

	@Override
	public void update(ViewerCell cell) {
		if (cell.getElement() instanceof PatternMatcherRoot) {
			PatternMatcherRoot pm = (PatternMatcherRoot) cell.getElement();
			StyledString styledString = new StyledString(pm.getText());
			cell.setText(styledString.getString());
			cell.setImage(getImage(pm));
			cell.setStyleRanges(styledString.getStyleRanges());
		} 
		else if (cell.getElement() instanceof PatternMatcher) {
			PatternMatcher pm = (PatternMatcher) cell.getElement();
			String label = pm.getText();
			StyledString styledString = new StyledString(label); 
			cell.setText(styledString.getString());
			cell.setImage(getImage(pm));
			cell.setStyleRanges(styledString.getStyleRanges());
		} 
		else if (cell.getElement() instanceof PatternMatch) {
			PatternMatch pm = (PatternMatch) cell.getElement();
			StyledString styledString = new StyledString(pm.getText(), null);
			cell.setText(styledString.getString());
			cell.setImage(getImage(pm));
			cell.setStyleRanges(styledString.getStyleRanges());
		}
	}

	private Image getImage(Object element) {
		Bundle bundle = Platform.getBundle(DatabindingUIPluginActivator.PLUGIN_ID);

		if (imageCache.containsKey(element.getClass())) {
			return imageCache.get(element.getClass());
		}
		else if (element instanceof PatternMatcherRoot) {
			URL fullPathString = BundleUtility.find(bundle, "icons/root.gif");
			Image img = ImageDescriptor.createFromURL(fullPathString).createImage();
			imageCache.put(PatternMatcherRoot.class, img);
			return img;
		}
		else if (element instanceof PatternMatcher) {
			URL fullPathString = BundleUtility.find(bundle, "icons/matcher.gif");
			Image img = ImageDescriptor.createFromURL(fullPathString).createImage();
			imageCache.put(PatternMatcher.class, img);
			return img;
		} 
		else if (element instanceof PatternMatch) {
			URL fullPathString = BundleUtility.find(bundle, "icons/match.gif");
			Image img = ImageDescriptor.createFromURL(fullPathString).createImage();
			imageCache.put(PatternMatch.class, img);
			return img;
		} 
		else {
			return null;
		}
	}
}
