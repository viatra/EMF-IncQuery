/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer;
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociator;

import com.google.inject.Inject;

/**
 * @author Mark Czotter
 *
 */
@SuppressWarnings("restriction")
public class AbstractEMFPatternLanguageJvmModelInferrer extends
		AbstractModelInferrer {

	@Inject 
	private PatternGroupClassInferrer groupClassInferrer;
	@Inject 
	private IJvmModelAssociator associator;
	
	/* (non-Javadoc)
	 * @see org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer#_infer(org.eclipse.emf.ecore.EObject, org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor, boolean)
	 */
	@Override
	public void _infer(EObject e, IJvmDeclaredTypeAcceptor acceptor,
			boolean preIndexingPhase) {
		super._infer(e, acceptor, preIndexingPhase);
		if (e instanceof PatternModel) {
			inferGroupClass((PatternModel) e, acceptor, preIndexingPhase);			
		}
	}

	/**
	 * Infers a group Class from a {@link PatternModel}
	 */
	private void inferGroupClass(PatternModel model, IJvmDeclaredTypeAcceptor acceptor,
			boolean preIndexingPhase) {
		try {
			final JvmGenericType groupClass = groupClassInferrer.inferPatternGroup(model);
			associator.associatePrimary(model, groupClass);
			acceptor.accept(groupClass);
		} catch (Exception e) {
			IncQueryEngine.getDefaultLogger().logError("Exception during Jvm model infer for: " + model, e);
		}
	}
	
}
