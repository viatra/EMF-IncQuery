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
import org.eclipse.viatra2.patternlanguage.types.EMFPatternTypeProvider;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.typing.ITypeProvider;
import org.eclipse.xtext.xbase.ui.hover.XbaseHoverProvider;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class EMFPatternLanguageHoverProvider extends XbaseHoverProvider {

	@Inject
	IEiqGenmodelProvider genmodelProvider;
	
	@Inject
	ITypeProvider typeProvider;
	
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
	
	protected JvmTypeReference getType(Variable parameter)
	{
		if(this.typeProvider instanceof EMFPatternTypeProvider)
		{
			return ((EMFPatternTypeProvider)(this.typeProvider)).resolve(parameter);
		}
		else return null;
	}
	
	protected JvmTypeReference getType(PatternBody body, Variable variable)
	{
		if(this.typeProvider instanceof EMFPatternTypeProvider)
		{
			return ((EMFPatternTypeProvider)(this.typeProvider)).resolve(body,variable);
		}
		else return null;
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
			JvmTypeReference type = this.getType(variable);
			String typeString =
				type!=null ?
					String.format("%s -<br/>%s",
							type.getSimpleName(),
							type.getQualifiedName()) :
					"unknown";
			return String.format(this.typeProvider.getClass().getSimpleName()+" say: "+"parameter <b>%s: %s</b>",
				variable.getName(),
				typeString);
		}
		else if(objectToView instanceof VariableReference)
		{
			VariableReference variableReference = (VariableReference) objectToView;
			PatternBody body = getBody(variableReference);
			// To evaluate the variable references
			body.getVariables();
			Pattern pat = (Pattern) body.eContainer();
			int bodyCount = pat.getBodies().indexOf(body) + 1;
			Variable variable = variableReference.getVariable();
			JvmTypeReference type = this.getType(body,variable);
			String typeString =
					type!=null ?
						String.format("%s -<br/>%s",
							type.getSimpleName(),
							type.getQualifiedName()) :
						"unknown";
			return String.format(this.typeProvider.getClass().getSimpleName()+" say: " +"variable in #%s body <b>%s: %s</b>",
				bodyCount,
				variable.getName(),
				typeString);
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
