/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.gui.dialog;

import java.util.Collection;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Mark Czotter
 *
 */
public class PatternMatchDialogContentProvider implements ITreeContentProvider {

	private IncQueryMatcher<? extends IPatternMatch> matcher;
	private Collection<? extends IPatternMatch> matches;
	
	public PatternMatchDialogContentProvider(
			IncQueryMatcher<? extends IPatternMatch> matcher,
			Collection<? extends IPatternMatch> matches) {
		this.matcher = matcher;
		this.matches = matches;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		this.matcher = null;
		this.matches = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null && newInput instanceof IncQueryMatcher<?> && newInput != oldInput) {
			this.matcher = (IncQueryMatcher<?>) newInput;
			this.matches = matcher.getAllMatches();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IncQueryMatcher<?>) {
			return matches.toArray();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IncQueryMatcher<?>) {
			return matches.toArray();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof IPatternMatch) {
			return matcher;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IncQueryMatcher<?>) {
			return !matches.isEmpty();
		}
		return false;
	}

}
