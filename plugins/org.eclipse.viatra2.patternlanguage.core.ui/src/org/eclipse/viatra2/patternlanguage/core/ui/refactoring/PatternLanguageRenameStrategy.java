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
package org.eclipse.viatra2.patternlanguage.core.ui.refactoring;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.ui.jvmmodel.refactoring.AbstractJvmModelRenameStrategy;

/**
 * Encapsulates the model changes of a rename refactoring.  
 */
@SuppressWarnings("restriction")
public class PatternLanguageRenameStrategy extends AbstractJvmModelRenameStrategy {

	@Override
	protected void setInferredJvmElementName(String name, EObject renamedSourceElement) {
		/*
		 * TODO: rename inferred elements as you would in IJvmModelInferrer 
		 */
	}
}
