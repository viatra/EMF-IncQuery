/*******************************************************************************
 * Copyright (c) 2010-2012, Okrosa, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Okrosa - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.CheckConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.Constraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.xtext.xbase.XExpression;

public class CheckExpressionUtil {

    /**
     * Returns a unique string name for the given xexpression + pattern combination. The format is FQN_(pattern body
     * number)_(expression number).
     * 
     * @param pattern
     *            {@link Pattern}
     * @param xExpression
     *            {@link XExpression}
     * @return {@link String}
     */
    public static String getExpressionUniqueID(Pattern pattern, XExpression xExpression) {
        return CorePatternLanguageHelper.getFullyQualifiedName(pattern) + "_"
                + getExpressionUniqueNameInPattern(pattern, xExpression);
    }

    /**
     * Returns a unique string tag for the given xexpression + pattern combination. The format is (pattern body
     * number)_(expression number).
     * 
     * @param pattern
     *            {@link Pattern}
     * @param xExpression
     *            {@link XExpression}
     * @return {@link String}
     */
    public static String getExpressionUniqueNameInPattern(Pattern pattern, XExpression xExpression) {
        int patternBodyNumber = 0;
        for (PatternBody patternBody : pattern.getBodies()) {
            patternBodyNumber++;
            int checkConstraintNumber = 0;
            for (Constraint constraint : patternBody.getConstraints()) {
                if (constraint instanceof CheckConstraint) {
                    CheckConstraint checkConstraint = (CheckConstraint) constraint;
                    checkConstraintNumber++;
                    if (xExpression.equals(checkConstraint.getExpression())) {
                        return patternBodyNumber + "_" + checkConstraintNumber;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the containing IFile, if the pattern has a valid resource.
     * 
     * @param pattern
     *            {@link Pattern}
     * @return {@link IFile}
     */
    public static IFile getIFile(Pattern pattern) {
        if (pattern != null) {
            Resource resource = pattern.eResource();
            if (resource != null) {
                URI uri = resource.getURI();
                uri = resource.getResourceSet().getURIConverter().normalize(uri);
                String scheme = uri.scheme();
                if ("platform".equals(scheme) && uri.segmentCount() > 1 && "resource".equals(uri.segment(0))) {
                    StringBuffer platformResourcePath = new StringBuffer();
                    for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
                        platformResourcePath.append('/');
                        platformResourcePath.append(uri.segment(j));
                    }
                    return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformResourcePath.toString()));
                }
            }
        }
        return null;
    }

}
