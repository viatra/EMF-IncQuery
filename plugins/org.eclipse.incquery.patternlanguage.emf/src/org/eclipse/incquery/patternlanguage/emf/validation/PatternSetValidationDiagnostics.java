/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.validation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.Sets;

/**
 * Stateless validator for a set of patterns.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public final class PatternSetValidationDiagnostics implements IAcceptor<Issue> {
    Set<Issue> foundErrors = Sets.newHashSet();
    Set<Issue> foundWarnings = Sets.newHashSet();

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.xtext.util.IAcceptor#accept(java.lang.Object)
     */
    @Override
    public void accept(Issue issue) {
        switch (issue.getSeverity()) {
        case ERROR:
            foundErrors.add(issue);
            break;
        case WARNING:
            foundWarnings.add(issue);
            break;
        case INFO:
            break;
        }
    }

    public PatternValidationStatus getStatus() {
        if (!foundErrors.isEmpty()) {
            return PatternValidationStatus.ERROR;
        } else if (!foundWarnings.isEmpty()) {
            return PatternValidationStatus.WARNING;
        } else {
            return PatternValidationStatus.OK;
        }
    }

    public Set<Issue> getAllErrors() {
        return Sets.newHashSet(foundErrors);
    }

    public Set<Issue> getAllWarnings() {
        return Sets.newHashSet(foundWarnings);
    }

    public void logErrors(Logger logger) {
        for (Issue diag : foundErrors) {
            logger.error(stringRepresentation(diag));
        }
    }

    public void logAllMessages(Logger logger) {
        logErrors(logger);
        for (Issue diag : foundWarnings) {
            logger.warn(stringRepresentation(diag));
        }
    }

    private String stringRepresentation(Issue issue) {
        return String.format("[%s] %s", issue.getSeverity().toString(), issue.getMessage());
    }

}