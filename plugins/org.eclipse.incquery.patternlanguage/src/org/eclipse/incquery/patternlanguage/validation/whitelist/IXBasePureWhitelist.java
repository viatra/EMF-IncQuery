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

import java.util.Set;

/**
 * The interface used by the org.eclipse.incquery.patternlanguage.purewhitelist extension point. Whitelisted packages
 * and classes can be defined in it with their fully qualified names.
 */
public interface IXBasePureWhitelist {

    /**
     * @return a set of whitelisted packages fully qualified names.
     */
    public Set<String> getWhitelistedPackages();

    /**
     * @return a set of whitelisted classes fully qualified names.
     */
    public Set<String> getWhitelistedClasses();

}
