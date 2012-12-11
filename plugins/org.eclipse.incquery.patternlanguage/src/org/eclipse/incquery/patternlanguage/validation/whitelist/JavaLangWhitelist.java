/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.validation.whitelist;

import java.util.HashSet;
import java.util.Set;

/**
 * A basic implementation for the org.eclipse.incquery.patternlanguage.purewhitelist extension point. All side-effect
 * free parts of the java core language should be inserted here.
 */
public class JavaLangWhitelist implements IXBasePureWhitelist {

    @Override
    public Set<String> getWhitelistedPackages() {
        Set<String> resultSet = new HashSet<String>();
        return resultSet;
    }

    @Override
    public Set<String> getWhitelistedClasses() {
        Set<String> resultSet = new HashSet<String>();
        resultSet.add("java.lang.Math");
        return resultSet;
    }

}
