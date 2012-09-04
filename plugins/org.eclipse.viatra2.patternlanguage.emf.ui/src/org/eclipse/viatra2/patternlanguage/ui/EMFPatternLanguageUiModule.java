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
package org.eclipse.viatra2.patternlanguage.ui;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.EMFPatternLanguageBuilderParticipant;
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.GenModelMetamodelProviderService;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.EMFPatternJvmModelAssociator;
import org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel.EMFPatternLanguageJvmModelInferrer;
import org.eclipse.viatra2.emf.incquery.tooling.generator.types.GenModelBasedTypeProvider;
import org.eclipse.viatra2.emf.incquery.typesearch.EMFPatternTypeProviderBySearch;
import org.eclipse.viatra2.patternlanguage.scoping.IMetamodelProvider;
import org.eclipse.viatra2.patternlanguage.ui.feedback.GeneratorMarkerFeedback;
import org.eclipse.viatra2.patternlanguage.ui.highlight.EMFPatternLanguageHighlightingCalculator;
import org.eclipse.viatra2.patternlanguage.ui.highlight.EMFPatternLanguageHighlightingConfiguration;
import org.eclipse.viatra2.patternlanguage.ui.labeling.EMFPatternLanguageHoverProvider;
import org.eclipse.viatra2.patternlanguage.ui.validation.GenmodelBasedEMFPatternLanguageJavaValidator;
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.editor.contentassist.XtextContentAssistProcessor;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;
import org.eclipse.xtext.xbase.jvmmodel.ILogicalContainerProvider;
import org.eclipse.xtext.xbase.jvmmodel.JvmModelAssociator;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the IDE.
 */
public class EMFPatternLanguageUiModule extends org.eclipse.viatra2.patternlanguage.ui.AbstractEMFPatternLanguageUiModule {
	private static final String loggerRoot = "org.eclipse.viatra2.emf.incquery";

	public EMFPatternLanguageUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	@Provides
	@Singleton
	Logger provideLoggerImplementation() {
		Logger logger = Logger.getLogger(loggerRoot);
		logger.setAdditivity(false);
		logger.addAppender(new ConsoleAppender());
		logger.addAppender(new EclipseLogAppender());
		return logger;
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
	
	@Override
	public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
		return EMFPatternLanguageHighlightingCalculator.class;
	}

	@Override
	public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
		return EMFPatternLanguageHighlightingConfiguration.class;
	}
	
	public Class<? extends IMetamodelProvider> bindIMetamodelProvider() {
		return GenModelMetamodelProviderService.class;
	}

	public Class<? extends IEiqGenmodelProvider> bindIEiqGenmodelProvider() {
		return GenModelMetamodelProviderService.class;
	}
	
	public Class<? extends ITypeProvider> bindITypeProvider() {
		//return GenModelBasedTypeProvider.class;
		return EMFPatternTypeProviderBySearch.class;
	}
	
	@Override
	public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider() {
		return EMFPatternLanguageHoverProvider.class;
	}
	
	public Class<? extends IErrorFeedback> bindIErrorFeedback(){
		return GeneratorMarkerFeedback.class;
	}
	

	public Class<? extends ILogicalContainerProvider> bindILogicalContainerProvider() {
		return EMFPatternJvmModelAssociator.class;
	}
	
	public Class<? extends JvmModelAssociator> bindJvmModelAssociator() {
		return EMFPatternJvmModelAssociator.class;
	}
	
	@SingletonBinding(eager = true)
	public Class<? extends EMFPatternLanguageJavaValidator> bindEMFPatternLanguageJavaValidator() {
		return GenmodelBasedEMFPatternLanguageJavaValidator.class;
	}
}
