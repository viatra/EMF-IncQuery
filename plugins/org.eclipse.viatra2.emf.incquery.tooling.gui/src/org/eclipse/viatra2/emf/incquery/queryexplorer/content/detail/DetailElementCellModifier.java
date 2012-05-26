package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import org.eclipse.jface.viewers.ICellModifier;

/**
 * This is a basic implementation of the ICellModifier interface used for pattern matches. 
 * Note that this class is necessary because of the 'two-sided' table viewer used in the Details/Filters view. 
 * 
 * @author Tamas Szabo
 *
 */
public class DetailElementCellModifier implements ICellModifier {

	@Override
	public boolean canModify(Object element, String property) {
		return false;
	}

	@Override
	public Object getValue(Object element, String property) {
		return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		
	}
}
