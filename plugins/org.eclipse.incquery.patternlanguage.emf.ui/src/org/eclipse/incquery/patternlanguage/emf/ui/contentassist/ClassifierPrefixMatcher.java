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
package org.eclipse.incquery.patternlanguage.emf.ui.contentassist;

import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.ui.editor.contentassist.PrefixMatcher;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ClassifierPrefixMatcher extends PrefixMatcher {
    private final PrefixMatcher delegate;

    private final IQualifiedNameConverter qualifiedNameConverter;

    public ClassifierPrefixMatcher(PrefixMatcher delegate, IQualifiedNameConverter qualifiedNameConverter) {
        this.delegate = delegate;
        this.qualifiedNameConverter = qualifiedNameConverter;
    }
    
    @Override
    public boolean isCandidateMatchingPrefix(String name, String prefix) {
        if (delegate.isCandidateMatchingPrefix(name, prefix))
            return true;
        QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(name);
        QualifiedName qualifiedPrefix = qualifiedNameConverter.toQualifiedName(prefix);
        if (qualifiedName.getSegmentCount() > 1) {
            if (qualifiedPrefix.getSegmentCount() == 1)
                return delegate.isCandidateMatchingPrefix(qualifiedName.getSegment(1),
                        qualifiedPrefix.getFirstSegment());
            if (!delegate.isCandidateMatchingPrefix(qualifiedName.getFirstSegment(),
                    qualifiedPrefix.getFirstSegment()))
                return false;
            return delegate.isCandidateMatchingPrefix(qualifiedName.getSegment(1), qualifiedPrefix.getSegment(1));
        }
        return false;
    }

}
