package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;

public class CheckStateListener implements ICheckStateListener {

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		Object object = event.getElement();
		if (event.getChecked()) {
			//register
		}
		else {
			//unregister
		}
	}

}
