package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

public class MatcherConfigurationCellModifier implements ICellModifier {
	
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
		
		if (property.equalsIgnoreCase("value")) {
			conf.setValue(value);
		}
	}

}
