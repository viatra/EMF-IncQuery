/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.core.scoping

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.resource.EObjectDescription
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.scoping.impl.MapBasedScope
import org.eclipse.xtext.xbase.scoping.LocalVariableScopeContext
import org.eclipse.xtext.xbase.scoping.XbaseScopeProvider

class PatternLanguageScopeProvider extends XbaseScopeProvider {
	
	override IScope createLocalVarScope(IScope parent, LocalVariableScopeContext scopeContext) {
		val parentScope = super.createLocalVarScope(parent, scopeContext)
		switch context: scopeContext.context {
			PatternBody : {
				val descriptions = context.variables.map(e | e.createIEObjectDescription())
				return MapBasedScope::createScope(
						super.createLocalVarScope(parentScope, scopeContext), descriptions);
			}
			Pattern : {
				val descriptions = context.parameters.map(e | e.createIEObjectDescription())
				return MapBasedScope::createScope(parentScope, descriptions);
					
			}
		}
		return parentScope
	}
	
	def createIEObjectDescription(Variable parameter) {
		var name = if (parameter.name != null)  QualifiedName::^create(parameter.name) else QualifiedName::EMPTY
		EObjectDescription::^create(name, parameter, null);
	}
}
