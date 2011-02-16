/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.compiled.emf.patternmatcher.mapping;

import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Informs the ViatraEMF runtime about the structure and mappings of an EMF metamodel
 * @author Bergmann Gábor
 *
 */
public interface ViatraEMFMetamodelAdvisor {

	void addEClassifiersByFQN(Map<String, EClassifier> eClassifiersByFQN);
	void addEStructuralFeaturesByFQN(Map<String, EStructuralFeature> eStructuralFeaturesByFQN);
}
