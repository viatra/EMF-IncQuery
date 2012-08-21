/*******************************************************************************
 * Copyright (c) 2010-2012, Oszkár Semeráth, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Oszkár Semeráth - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.ui.labeling;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.typeinference.queryanalysis.QueryAnalysisProviderOnPattern;
import org.eclipse.viatra2.emf.incquery.typeinference.typeanalysis.EMFPatternTypeProviderByInference;
import org.eclipse.viatra2.emf.incquery.typeinference.typeerrors.ErrorReasonProvider;
import org.eclipse.viatra2.emf.incquery.typeinference.typeerrors.TypeReason;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.types.EMFPatternTypeProvider;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil;

/**
 * @author Oszkár Semeráth
 *
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageTypeHover {
	
	ErrorReasonProvider errorReasonProvider;
	ITypeProvider typeProvider;
	
	public EMFPatternLanguageTypeHover(ITypeProvider typeProvider) {
		super();
		this.errorReasonProvider = new ErrorReasonProvider();
		this.typeProvider = typeProvider;
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
	protected Boolean IsUnsatisfiableVariable(PatternBody body, Variable variable)
	{
		if(this.typeProvider instanceof EMFPatternTypeProviderByInference)
		{
			return ((EMFPatternTypeProviderByInference)(this.typeProvider)).isUnsatisfiableVariable(body, variable);
		}
		else return null;
	}
	protected Boolean IsTooGeneralVariable(PatternBody body, Variable variable)
	{
		if(this.typeProvider instanceof EMFPatternTypeProviderByInference)
		{
			return ((EMFPatternTypeProviderByInference)(this.typeProvider)).isTooGeneralVariable(body, variable);
		}
		else return null;
	}
	
	public String getVariableTypeHover(EObject objectToView, Variable variable)
	{
		System.out.println("TypeProvider: " + this.typeProvider);
		PatternBody body = QueryAnalysisProviderOnPattern.getPatternBody(objectToView);
		if(body == null)
		{
			JvmTypeReference type = this.getType(variable);
			String typeString =
				type!=null ?
					String.format("%s -<br/>%s",type.getSimpleName(),type.getQualifiedName()) :
					"unknown";
			return String.format("parameter <b>%s: %s</b>",
				variable.getName(),	typeString);
		}
		else
		{
			Pattern pat = (Pattern) body.eContainer();
			int bodyCount = pat.getBodies().indexOf(body) + 1;
			JvmTypeReference type = this.getType(body,variable);
			String typeString = null;
			if(type!=null) typeString =
					String.format("%s -<br/>%s", type.getSimpleName(),	type.getQualifiedName());
			else if(this.IsUnsatisfiableVariable(body, variable)==true)
			{
				typeString = "unsatisfiable";
				Collection<TypeReason<Object>> errorReasons = this.errorReasonProvider.getReasonOfUnsat(body, variable);
				if(errorReasons!=null)
				{
					typeString+="</b>, because:";
					for(TypeReason<Object> errorReason : errorReasons)
					{
						
						 EMFPatternLanguageJvmModelInferrerUtil util =
								new EMFPatternLanguageJvmModelInferrerUtil();
						 typeString+="<br/>"+errorReason.getType().getName()+" - "+
								util.serialize((EObject) errorReason.getReason());
					}
					typeString+="<b>";
				}
			}
			else if(this.IsTooGeneralVariable(body, variable)==true)
			{
				typeString = "too general type</b>, because:" +
						"It doesn't have any type constraint in #"+bodyCount+" body.<b>";
			}
			else typeString="unknown...";
			return String.format("variable in #%s body <b>%s: %s</b>",
				bodyCount,	variable.getName(),	typeString);
		}
	}
}
