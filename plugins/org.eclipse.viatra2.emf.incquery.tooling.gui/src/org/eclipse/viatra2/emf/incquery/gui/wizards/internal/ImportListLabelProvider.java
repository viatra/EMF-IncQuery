package org.eclipse.viatra2.emf.incquery.gui.wizards.internal;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

public class ImportListLabelProvider implements ILabelProvider {

	private ImageRegistry imageRegistry;
	
	public ImportListLabelProvider() {
		imageRegistry = IncQueryGUIPlugin.getDefault().getImageRegistry();
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {}

	@Override
	public Image getImage(Object element) {
		return imageRegistry.get(IncQueryGUIPlugin.ICON_EPACKAGE);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Resource) {
			return ((Resource) element).getURI().toString();
		}
		return null;
	}

}
