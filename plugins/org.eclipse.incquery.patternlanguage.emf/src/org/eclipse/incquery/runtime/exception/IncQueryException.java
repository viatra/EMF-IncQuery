/*******************************************************************************
 * Copyright (c) 2004-2010 Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.exception;

import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;

public class IncQueryException extends Exception {

    private static final long serialVersionUID = -74252748358355750L;

    public static final String PARAM_NOT_SUITABLE_WITH_NO = "The type of the parameters are not suitable for the operation. Parameter number: ";
    public static final String CONVERSION_FAILED = "Could not convert the term to the designated type";
    public static final String CONVERT_NULL_PARAMETER = "Could not convert null to the designated type";
    public static final String RELATIONAL_PARAM_UNSUITABLE = "The parameters are not acceptable by the operation";
    public static final String PATTERN_MATCHER_PROBLEM = "The following error occurred during the preparation of an EMF-IncQuery pattern matcher";
    public static final String GETNAME_FAILED = "Could not get 'name' attribute of the result";

    public static final String INVALID_EMFROOT = "Incremental query engine can only be attached on the contents of an EMF EObject, Resource, or ResourceSet. Received instead: ";
    public static final String INVALID_EMFROOT_SHORT = "Invalid EMF model root";
    // public static final String EMF_MODEL_PROCESSING_ERROR = "Error while processing the EMF model";

    private final String shortMessage;

    public IncQueryException(String s, String shortMessage) {
        super(s);
        this.shortMessage = shortMessage;
    }

    public IncQueryException(RetePatternBuildException e) {
        super(PATTERN_MATCHER_PROBLEM + ": " + e.getMessage(), e);
        this.shortMessage = e.getShortMessage();
    }

    public IncQueryException(String s, String shortMessage, Exception e) {
        super(s + ": " + e.getMessage(), e);
        this.shortMessage = shortMessage;
    }

    public String getShortMessage() {
        return shortMessage;
    }

}
