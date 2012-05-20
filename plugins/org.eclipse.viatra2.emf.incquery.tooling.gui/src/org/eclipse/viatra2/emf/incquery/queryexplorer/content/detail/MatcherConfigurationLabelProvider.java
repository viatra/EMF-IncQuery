package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;

public final class MatcherConfigurationLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		MatcherConfiguration mc = (MatcherConfiguration) element;
		switch (columnIndex) {
		case 0:
			return mc.getParameterName();
		case 1:
			if (mc.getFilter() == null) {
				return "";
			}
			else if (mc.getFilter() instanceof EObject) {
				EObject eObj = (EObject) mc.getFilter();
				URI uri = eObj.eClass().eResource().getURI();
				AdapterFactoryLabelProvider lp = DatabindingUtil.getAdapterFactoryLabelProvider(uri);
				if (lp != null) {
					return lp.getText(eObj);
				}
			}
			else {
				return mc.getFilter().toString();
			}
		case 2:
			return mc.getClazz();
		default:
			return "";
		}
	}

}