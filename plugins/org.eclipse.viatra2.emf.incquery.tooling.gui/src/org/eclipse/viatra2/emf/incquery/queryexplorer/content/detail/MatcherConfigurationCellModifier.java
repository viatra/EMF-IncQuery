package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;

public class MatcherConfigurationCellModifier implements ICellModifier {
	
	private TableViewer viewer;
	
	public MatcherConfigurationCellModifier(TableViewer viewer) {
		this.viewer = viewer;
	}
	
	@Override
	public boolean canModify(Object element, String property) {
		if (property.equalsIgnoreCase("value")) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public Object getValue(Object element, String property) {
		MatcherConfiguration conf = (MatcherConfiguration) element;
		if (property.equalsIgnoreCase("value")) {
			if (conf.getValue() instanceof EObject) {
				EObject eObj = (EObject) conf.getValue();
				URI uri = eObj.eClass().eResource().getURI();
				AdapterFactoryLabelProvider lp = DatabindingUtil.getAdapterFactoryLabelProvider(uri);
				if (lp != null) {
					return lp.getText(eObj);
				}
			}
			return conf.getValue();
		}
		else if (property.equalsIgnoreCase("class")) {
			return conf.getClazz();
		}
		else if (property.equalsIgnoreCase("parameter")) {
			return conf.getParameterName();
		}
		return "";
	}

	@Override
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
	         element = ((Item) element).getData();
	    }
		MatcherConfiguration conf = (MatcherConfiguration) element;
		
		if (conf != null && property.equalsIgnoreCase("value")) {
			conf.setValue(value);
			viewer.update(conf, null);
		}
	}

}
