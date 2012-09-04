/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.util;

import org.eclipse.core.runtime.ILog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;

public abstract class ContentModel {

	protected MatcherTreeViewerRootKey key;
	protected IWorkbenchPage workbenchPage;
	protected ILog logger = IncQueryGUIPlugin.getDefault().getLog(); 
	
	public ContentModel(MatcherTreeViewerRootKey key) {
		this.key = key;
		this.logger = IncQueryGUIPlugin.getDefault().getLog(); 
		this.workbenchPage = key.getEditor().getSite().getPage();
	}
	
	public abstract void loadModel();
	
	public abstract void unloadModel();
	
	public abstract void showLocation(Object[] locationObjects);
	
}
