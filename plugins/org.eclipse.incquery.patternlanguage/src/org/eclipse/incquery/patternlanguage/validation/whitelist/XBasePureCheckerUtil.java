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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.xtext.common.types.JvmAnnotationReference;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.xbase.lib.Pure;

import com.google.inject.Inject;

/**
 * A utility class for checking the "purity" of the JvmOperations in XBase check expressions. It checks the @Pure
 * annotation and the whitelists as well.
 */
public class XBasePureCheckerUtil {

    @Inject
    private static Logger logger;

    public static boolean isImpureElement(JvmOperation jvmOperation) {
        // First, check if it is tagged with the @Pure annotation
        if (!jvmOperation.getAnnotations().isEmpty()) {
            for (JvmAnnotationReference jvmAnnotationReference : jvmOperation.getAnnotations()) {
                if (Pure.class.getSimpleName().equals(jvmAnnotationReference.getAnnotation().getSimpleName())) {
                    return false;
                }
            }
        }

        // Second, check if it is on the whitelist
        String qualifiedName = jvmOperation.getQualifiedName();
        qualifiedName = qualifiedName.replace("." + jvmOperation.getSimpleName(), "");
        IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                "org.eclipse.incquery.patternlanguage.purewhitelist");
        for (IConfigurationElement configurationElement : configurationElements) {
            Object object = null;
            try {
                object = configurationElement.createExecutableExtension("whitelistClass");
            } catch (CoreException coreException) {
                logger.error("Whitelist extension point initialization failed.", coreException);
            }
            if (object != null && object instanceof IXBasePureWhitelist) {
                IXBasePureWhitelist xbasePureWhitelist = (IXBasePureWhitelist) object;
                if (xbasePureWhitelist.getWhitelistedClasses().contains(qualifiedName)) {
                    return false;
                }
                for (String whitelistedPackageName : xbasePureWhitelist.getWhitelistedPackages()) {
                    if (qualifiedName.startsWith(whitelistedPackageName + ".")) {
                        String refactoredQualifiedName = qualifiedName.replace(whitelistedPackageName + ".", "");
                        if (!refactoredQualifiedName.contains(".")) {
                            return false;
                        }
                    }
                }
            }
        }

        // Neither option resulted false, so we return with true.
        return true;
    }

}
