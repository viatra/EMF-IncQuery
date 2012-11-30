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
package org.eclipse.incquery.runtime.util;

/**
 * An enum implementation to be used in conjuction with the XmiModelUtil.
 */
public enum XmiModelUtilRunningOptionEnum {

    /**
     * Use this if you intend to call the XmiModelUtil with workspace only URIs.
     */
    JUST_RESOURCE,

    /**
     * Use this if you intend to call the XmiModelUtil with plugin only URIs.
     */
    JUST_PLUGIN,

    /**
     * Default choice should be this. Use this if you intend to call the XmiModelUtil with both workspace and plugin
     * URIs.
     */
    BOTH;

}
