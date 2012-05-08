package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

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
			return mc.getClazz().getSimpleName();
		case 2:
			return mc.getValue().toString();
		default:
			return "";
		}
	}

}