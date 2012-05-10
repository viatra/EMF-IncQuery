package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;

public class ModelElementListDialogLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	
	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof EObject) {
			EObject eObj = (EObject) element;
			URI uri = eObj.eClass().eResource().getURI();
			AdapterFactory af = DatabindingUtil.getAdapterFactory(uri);
			if (af != null) {
				AdapterFactoryLabelProvider aflp = new AdapterFactoryLabelProvider(af);
				return aflp.getText(eObj);
			}
		}
		return element.toString();
	}

}
