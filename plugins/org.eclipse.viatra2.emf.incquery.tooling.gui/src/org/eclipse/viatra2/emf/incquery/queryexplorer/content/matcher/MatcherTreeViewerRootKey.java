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

package org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.ui.IEditorPart;

/**
 * the class is used to join an IEditorPart instance and a ResourceSet instance.
 * Such a key will be used to map the PatternMatcherRoot elements in the ViewerRoot.
 * 
 * @author Tamas Szabo
 *
 */
public class MatcherTreeViewerRootKey {

	private IEditorPart editorPart;
	private Notifier notifier;
	private IncQueryEngine engine;

	public MatcherTreeViewerRootKey(IEditorPart editor, Notifier notifier) {
		super();
		this.editorPart = editor;
		this.notifier = notifier;
	}

	public IEditorPart getEditorPart() {
		return editorPart;
	}

	public void setEditorPart(IEditorPart editor) {
		this.editorPart = editor;
	}

	public Notifier getNotifier() {
		return notifier;
	}

	public void setNotifier(Notifier notifier) {
		this.notifier = notifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		else {
			MatcherTreeViewerRootKey key = (MatcherTreeViewerRootKey) obj;
			if (key.getEditorPart().equals(editorPart) && key.getNotifier().equals(notifier)) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	@Override
	public int hashCode() {
		int hash = 1;
        hash = hash * 17 + editorPart.hashCode();
        hash = hash * 17 + notifier.hashCode();
        return hash;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		
		int i = 0;
		
		if (notifier instanceof ResourceSet) {
			ResourceSet rs = (ResourceSet) notifier;
			
			for (Resource r : rs.getResources()) {
				sb.append(r.getURI().toString());
				if (i != rs.getResources().size()-1) {
					sb.append(", ");
				}
			}
		}
		else if (notifier instanceof Resource) {
			sb.append(((Resource) notifier).getURI().toString());
		}
		else {
			sb.append(notifier.toString());
		}
		
		sb.append("]");
		sb.append("[");
		sb.append(editorPart.getEditorSite().getId());
		sb.append("]");
		return sb.toString();
	}

  /**
   * @return the engine
   */
  public IncQueryEngine getEngine() {
    return engine;
  }

  /**
   * @param engine the engine to set
   */
  public void setEngine(IncQueryEngine engine) {
    this.engine = engine;
  }

}
