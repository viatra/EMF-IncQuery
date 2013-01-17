package org.eclipse.incquery.tooling.ui.patternregistry.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.incquery.runtime.patternregistry.PatternRegistry;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

public class RegisterHandlersUtil {

    public static void registerSingleFile(IFile file, IResourceSetProvider resourceSetProvider) {
        if (file != null && resourceSetProvider != null && file.getName().endsWith(".eiq")) {
            ResourceSet resourceSet = resourceSetProvider.get(file.getProject());
            Resource resource = resourceSet.getResource(
                    URI.createPlatformPluginURI(file.getFullPath().toOSString(), false), true);
            EObject eObject = resource.getContents().get(0);
            if (eObject instanceof PatternModel) {
                PatternModel patternModel = (PatternModel) eObject;
                for (Pattern pattern : patternModel.getPatterns()) {
                    PatternRegistry.INSTANCE.addPatternToRegistry(pattern);
                }
            }
        }
    }

}
