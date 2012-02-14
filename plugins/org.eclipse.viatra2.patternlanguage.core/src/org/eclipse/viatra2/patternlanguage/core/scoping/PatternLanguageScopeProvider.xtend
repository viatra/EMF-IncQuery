/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.core.scoping

import org.eclipse.xtext.xbase.scoping.XbaseScopeProvider
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.resource.EObjectDescription
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.scoping.impl.MapBasedScope
import org.eclipse.xtext.xbase.scoping.LocalVariableScopeContext
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.xtext.xbase.XVariableDeclaration

class PatternLanguageScopeProvider extends XbaseScopeProvider {
	
	override IScope createLocalVarScope(IScope parent, LocalVariableScopeContext scopeContext) {
		switch context: scopeContext.context {
			PatternBody : {
				/*val it = context.eAllContents
				val list = new ArrayList<Variable>()
				while (it.hasNext) {
					val obj = it.next
					if (obj instanceof Variable) {
     					println(obj)
						list.add(obj as Variable)
					}
					
				}
				val descriptions = list.map(e | e.createIEObjectDescription)*/
				val descriptions = context.variables.map(e | e.createIEObjectDescription())
				return MapBasedScope::createScope(
						super.createLocalVarScope(parent, scopeContext), descriptions);
			}
			Pattern : {
				val descriptions = context.parameters.map(e | e.createIEObjectDescription())
				return MapBasedScope::createScope(IScope::NULLSCOPE, descriptions);	
			}
		}
		return super.createLocalVarScope(parent, scopeContext)
	}
	
	def createIEObjectDescription(XVariableDeclaration parameter) {
		EObjectDescription::^create(QualifiedName::^create(parameter.name), parameter, null);
	}
	
	def createIEObjectDescription(Variable parameter) {
		EObjectDescription::^create(QualifiedName::^create(parameter.name), parameter, null);
	}
}