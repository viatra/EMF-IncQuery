/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.util.ContentModel;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.util.EMFContentModel;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;

import com.google.inject.Inject;

public class LoadEiqModelHandler extends LoadModelHandler {

	@Inject
	DatabindingUtil dbUtil;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			IFile file = (IFile) HandlerUtil.getActiveEditorInput(event).getAdapter(IFile.class);
			if (file != null) {
				MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(HandlerUtil.getActiveEditor(event),	dbUtil.parseEPM(file));
				ContentModel contentModel = new EMFContentModel(key);
				QueryExplorer.contentModelMap.put(key, contentModel);
				contentModel.loadModel();
			}
		} catch (Exception e) {
			throw new ExecutionException("Cannot load pattern model", e);
		}
		return null;
	}
}
