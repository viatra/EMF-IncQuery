package org.eclipse.viatra2.emf.incquery.gui.wizards.internal;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ObjectsListLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element != null && element instanceof ObjectParameter) {
			ObjectParameter parameter = (ObjectParameter) element;
			if (columnIndex == 0) {
				return parameter.getParameterName();
			}
			else {
				return parameter.getObject() != null ? parameter.getObject().eClass().toString() : "";
			}
		}
		return null;
	}

}
