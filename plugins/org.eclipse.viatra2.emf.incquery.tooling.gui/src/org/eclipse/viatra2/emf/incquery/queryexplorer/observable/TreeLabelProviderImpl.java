package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import java.util.Set;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

/**
 * Custom tree label provider implementation.
 * The class is responsible to generate labels and images for the elements in the tree viewer.
 * 
 * @author Tamas Szabo
 *
 */
public class TreeLabelProviderImpl extends StyledCellLabelProvider {

	private IMapChangeListener mapChangeListener;
	
	public TreeLabelProviderImpl(IObservableMap[] attributeMaps) {
		
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
		ImageRegistry imageRegistry = IncQueryGUIPlugin.getDefault().getImageRegistry();


		if (element instanceof PatternMatcherRoot) {
			return imageRegistry.get(IncQueryGUIPlugin.ICON_ROOT);
		}
		else if (element instanceof PatternMatcher) {
			if (((PatternMatcher) element).isCreated()) {
				return imageRegistry.get(IncQueryGUIPlugin.ICON_MATCHER);
			} else {
				return imageRegistry.get(IncQueryGUIPlugin.ICON_ERROR);
			}
		} 
		else if (element instanceof PatternMatch) {
			return imageRegistry.get(IncQueryGUIPlugin.ICON_MATCH);
		} 
		else {
			return null;
		}
	}
}
