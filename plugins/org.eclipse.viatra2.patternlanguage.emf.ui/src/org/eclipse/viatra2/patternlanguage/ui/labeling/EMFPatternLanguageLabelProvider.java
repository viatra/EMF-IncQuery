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
package org.eclipse.viatra2.patternlanguage.ui.labeling;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.IntValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCall;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class EMFPatternLanguageLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public EMFPatternLanguageLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	String text(PatternModel model) {
		return "Pattern Model";
	}
	
	String text(PackageImport ele) {
		String name = (ele.getEPackage() != null) ? ele.getEPackage().getName() : "«package»";
		return String.format("import %s", name);
	}
	
	String text(Pattern pattern) {
		return String.format("pattern %s/%d", pattern.getName(), pattern.getParameters().size());
	}
	
	String text(PatternBody ele) {
		return String.format("body #%d", ((Pattern)ele.eContainer()).getBodies().indexOf(ele) + 1);
	}
	
	String text(EClassifierConstraint constraint) {
		String typename = ((ClassType)constraint.getType()).getClassname().getName();
		return String.format("%s (%s)", typename, constraint.getVar().getVar());
	}
	
	String text(PatternCompositionConstraint constraint) {
		String modifiers = (constraint.isNegative()) ? "neg " : "";
		return String.format("find %s%s", modifiers, text(constraint.getCall()));
	}
	
	String text(PatternCall call) {
		return String.format("%s/%d", call.getPatternRef().getName(), call.getParameters().size());
	}
	
	String text(PathExpressionConstraint constraint) {
		String typename = ((ClassType)constraint.getHead().getType()).getClassname().getName();
		String varName = (constraint.getHead().getSrc() != null) ? constraint.getHead().getSrc().getVar() : "«type»";
		return String.format("%s (%s)", typename, varName);
	}
	
	String text(PathExpressionTail tail) {
		EStructuralFeature refname = ((ReferenceType)tail.getType()).getRefname();
		String type = (refname != null) ? refname.getName() : "«type»";
		String varName = "";
		if (tail.getTail() == null) {
			PathExpressionHead head = EMFPatternLanguageScopeHelper.getExpressionHead(tail);
			varName = String.format("(%s)",getValueText(head.getDst()));
		}
		return String.format("%s %s",type, varName);
	}

//	String text(ComputationValue computation) {
//		
//	}
	
	String getValueText(ValueReference ref) {
		if (ref instanceof VariableValue) {
			return ((VariableValue) ref).getValue().getVar();
		} else if (ref instanceof IntValue) {
			return Integer.toString(((IntValue) ref).getValue());
		} else if (ref instanceof StringValue) {
			return ((StringValue) ref).getValue();
		}
		return null;
	}
	
/*
	//Labels and icons can be computed like this:
	
	String text(MyModel ele) {
	  return "my "+ele.getName();
	}
	 
    String image(MyModel ele) {
      return "MyModel.gif";
    }
*/
}
