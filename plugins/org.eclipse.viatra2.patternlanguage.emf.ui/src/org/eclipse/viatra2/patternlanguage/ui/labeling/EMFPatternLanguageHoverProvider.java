/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *   Oszkár Semeráth - Adding type information to variables and variable references
 *******************************************************************************/

package org.eclipse.viatra2.patternlanguage.ui.labeling;

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.xtext.xbase.ui.hover.XbaseHoverProvider;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class EMFPatternLanguageHoverProvider extends XbaseHoverProvider {

	@Inject
	IEiqGenmodelProvider genmodelProvider;
	
	private PatternBody getBody(VariableReference variableReference)
	{
		EObject object = variableReference;
		do
		{
			object = object.eContainer();
		}
		while(!(object instanceof PatternBody));
		return (PatternBody) object;
	}
	
	@Override
	protected String getHoverInfoAsHtml(EObject call, EObject objectToView, IRegion hoverRegion) {
		if (objectToView instanceof PackageImport) {
			PackageImport packageImport = (PackageImport) objectToView;
			GenPackage genPackage = genmodelProvider.findGenPackage(packageImport, packageImport.getEPackage());
			if (genPackage != null) {
				return String.format(
					"<b>Genmodel found</b>: %s<br/><b>Package uri</b>: %s",
					genPackage.eResource().getURI().toString(), genPackage
							.getEcorePackage().eResource().getURI().toString());
			} 
		}
		else if(objectToView instanceof Variable)
		{
			Variable variable = (Variable) objectToView;
			return String.format("Parameter <b>%s: ez egy variable</b>",variable.getName());
		}
		else if(objectToView instanceof VariableReference)
		{
			VariableReference variableReference = (VariableReference) objectToView;

			PatternBody body = getBody(variableReference);
			body.getVariables();
			Pattern pat = (Pattern) body.eContainer();
			int bodyCount = pat.getBodies().indexOf(body);
			Variable variable = variableReference.getVariable();
			if(variable!=null)
			{
				return String.format("Variable <b>%s: van variable a #%s body-ban</b>",variable.getName(),bodyCount);
			}
			else return String.format("Variable <b>%s: nincs varable a  #%s body-ban</b>",variableReference.getVar(),bodyCount);
		}
		return super.getHoverInfoAsHtml(call, objectToView, hoverRegion);
	}

	@Override
	protected boolean hasHover(EObject o) {
		return (o instanceof PackageImport) ||
			   (o instanceof Variable) ||
			   (o instanceof VariableReference) ||
			   super.hasHover(o);
	}

}
