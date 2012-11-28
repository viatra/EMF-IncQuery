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
package org.eclipse.incquery.patternlanguage.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.annotations.PatternAnnotationProvider;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import com.google.common.base.Predicates;
import com.google.inject.Inject;

/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class PatternLanguageProposalProvider extends AbstractPatternLanguageProposalProvider {

	@Inject
	private PatternAnnotationProvider annotationProvider;
	@Inject
	private IScopeProvider scopeProvider;
	@Inject
	private ReferenceProposalCreator crossReferenceProposalCreator;
	
	@Override
    public void complete_Annotation(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		for (String annotationName : annotationProvider.getAllAnnotationNames()) {
			String prefixedName = String.format("@%s", annotationName);
			String prefix = context.getPrefix();
			ContentAssistContext modifiedContext = context;
			INode lastNode = context.getLastCompleteNode();
			if ("".equals(prefix)
					&& lastNode.getSemanticElement() instanceof Annotation) {
				Annotation previousNode = (Annotation) lastNode
						.getSemanticElement();
				String annotationPrefix = previousNode.getName();
				if (previousNode.getParameters().isEmpty()
						&& !annotationProvider.getAllAnnotationNames()
								.contains(annotationPrefix)) {
					modifiedContext = context
							.copy()
							.setReplaceRegion(
									new Region(lastNode.getOffset(), lastNode
											.getLength() + prefix.length()))
							.toContext();
					prefixedName = annotationName;
				}
			}
			ICompletionProposal proposal = createCompletionProposal(
					prefixedName, prefixedName, null, modifiedContext);
			if (proposal instanceof ConfigurableCompletionProposal) {
				((ConfigurableCompletionProposal) proposal)
						.setAdditionalProposalInfo(annotationProvider
								.getAnnotationObject(annotationName));
				((ConfigurableCompletionProposal) proposal)
						.setHover(getHover());
			}
			acceptor.accept(proposal);
		}
	}

	@Override
    public void complete_AnnotationParameter(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		if (model instanceof Annotation) {
			Annotation annotation = (Annotation) model;
			for (String paramName : annotationProvider.getAnnotationParameters(annotation.getName())){
				String outputName = String.format("%s = ", paramName);
				ICompletionProposal proposal = createCompletionProposal(outputName, paramName, null, context);
				if (proposal instanceof ConfigurableCompletionProposal) {
					((ConfigurableCompletionProposal) proposal)
							.setAdditionalProposalInfo(annotationProvider
									.getAnnotationParameter(
											annotation.getName(), paramName));
					((ConfigurableCompletionProposal) proposal)
							.setHover(getHover());
				}
				acceptor.accept(proposal);
			}
		}
	}

	@Override
	public void complete_VariableReference(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		IScope scope = scopeProvider.getScope(model, PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VARIABLE);
		crossReferenceProposalCreator.lookupCrossReference(scope, model,
				PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VARIABLE,
				acceptor, Predicates.<IEObjectDescription> alwaysTrue(),
				getProposalFactory(ruleCall.getRule().getName(), context));
		

	}
}
