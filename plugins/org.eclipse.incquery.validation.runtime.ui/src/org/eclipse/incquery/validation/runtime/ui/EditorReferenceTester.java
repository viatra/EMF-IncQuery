/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.validation.runtime.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.incquery.validation.runtime.ValidationUtil;

/**
 * @author Abel Hegedus
 * 
 */
public class EditorReferenceTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (receiver instanceof String) {
            String editorId = (String) receiver;
            if (property.equals("hasConstraint")) {
                return ValidationUtil.isConstraintsRegisteredForEditorId(editorId);
            }
        }
        return false;
    }

}