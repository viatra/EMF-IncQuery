/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.single;

import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;

/**
 * This node filters patterns according to equalities and inequalities of
 * elements. The 'subject' element is asserted to be different from the elements
 * given by the inequalityMask.
 * 
 * 
 * @author Gabor Bergmann
 * 
 */
public class InequalityFilterNode extends FilterNode {

	int subjectIndex;
	TupleMask inequalityMask;

	/**
	 * @param reteContainer
	 * @param subjectIndex
	 *            the index of the element that should be compared.
	 * @param inequalityMask
	 *            the indices of elements that should be different from the
	 *            subjectIndex.
	 */
	public InequalityFilterNode(ReteContainer reteContainer, int subject,
			TupleMask inequalityMask) {
		super(reteContainer);
		this.subjectIndex = subject;
		this.inequalityMask = inequalityMask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.FilterNode#check(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	@Override
	public boolean check(Tuple ps) {
		Object subject = ps.get(subjectIndex);
		for (int ineq : inequalityMask.indices) {
			if (subject.equals(ps.get(ineq)))
				return false;
		}
		return true;
	}

}
