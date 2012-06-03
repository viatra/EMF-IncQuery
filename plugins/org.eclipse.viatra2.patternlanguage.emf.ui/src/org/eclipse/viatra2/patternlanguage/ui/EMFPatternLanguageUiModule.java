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
package org.eclipse.viatra2.patternlanguage.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.viatra2.patternlanguage.ui.builder.EMFPatternLanguageBuilderParticipant;
import org.eclipse.viatra2.patternlanguage.ui.highlight.EMFPatternLanguageHighlightingConfiguration;
import org.eclipse.viatra2.patternlanguage.ui.highlight.EMFPatternLanguageHighlightingCalculator;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.ui.editor.contentassist.XtextContentAssistProcessor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;
import org.eclipse.xtext.xbase.ui.highlighting.XbaseHighlightingCalculator;
import org.eclipse.xtext.xbase.ui.highlighting.XbaseHighlightingConfiguration;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the IDE.
 */
public class EMFPatternLanguageUiModule extends org.eclipse.viatra2.patternlanguage.ui.AbstractEMFPatternLanguageUiModule {
	public EMFPatternLanguageUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		binder.bind(String.class)
				.annotatedWith(
						Names.named(XtextContentAssistProcessor.COMPLETION_AUTO_ACTIVATION_CHARS))
				.toInstance(".,");
	}
	
	/*
	 * Registering model inferrer from the tooling.generator project
	 */
	public Class<? extends IJvmModelInferrer> bindIJvmModelInferrer() {
		return EMFPatternLanguageJvmModelInferrer.class;
	}
	
	@Override
	public Class<? extends IXtextBuilderParticipant> bindIXtextBuilderParticipant() {
		return EMFPatternLanguageBuilderParticipant.class;
	}
	
	// contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
	public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
		return EMFPatternLanguageHighlightingCalculator.class;
	}

	// contributed by org.eclipse.xtext.generator.xbase.XbaseGeneratorFragment
	public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
		return EMFPatternLanguageHighlightingConfiguration.class;
	}
	
}
