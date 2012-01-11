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
package org.eclipse.viatra2.patternlanguage.ui.outline;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;

import com.google.common.collect.Iterables;

/**
 * customization of the default outline structure
 * 
 */
public class EMFPatternLanguageOutlineTreeProvider extends
		DefaultOutlineTreeProvider {

	protected void _createChildren(DocumentRootNode parentNode,
			PatternModel model) {
		for (EObject element : Iterables.concat(
				model.getImportPackages(), model.getPatterns())) {
			createNode(parentNode, element);
		}
	}
	
	protected void _createChildren(IOutlineNode parentNode, Pattern model) {
		if (model.getBodies().size() == 1) {
			_createChildren(parentNode, model.getBodies().get(0));
		} else {
			for (PatternBody body : model.getBodies()) {
				createNode(parentNode, body);
			}
		}
	}
	
	protected void _createChildren(IOutlineNode parentNode, EClassConstraint constraint) {
		// By leaving this method empty, the EClass Constraint will not have any children in the outline view
	}
	
	protected void _createChildren(IOutlineNode parentNode, PathExpressionConstraint constraint) {
		PathExpressionHead head = constraint.getHead();
		if (head.getTail() != null) {
			createNode(parentNode, head.getTail());
		}
	}
	
	protected void _createChildren(IOutlineNode parentNode, PathExpressionTail tail) {
		if (tail.getTail() != null) {
			createNode(parentNode, tail.getTail());
		}
	}
	
	protected void _createChildren(IOutlineNode parentNode, PatternCompositionConstraint constraint) {
		// By leaving this method empty, the Pattern Composition Constraint will not have any children in the outline view
	}
}
