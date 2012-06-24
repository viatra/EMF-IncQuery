package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

public class QueryExplorerFocusListener implements FocusListener {

	public QueryExplorerFocusListener() {

	}

	@Override
	public void focusGained(FocusEvent e) {
		System.out.println(e);
	}

	@Override
	public void focusLost(FocusEvent e) {
		System.out.println(e);
	}
}
