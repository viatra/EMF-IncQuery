/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package hu.bme.mit.incquery.core.codegen.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.gtasm.support.helper.GTASMHelper;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Machine;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPatternBody;

/**
 * Collects all GTPatterns from a framework, excluding those that are marked as ignored by their annotations.
 * @author Bergmann Gábor
 *
 */
public class PatternsCollector {
	private Set<GTPattern> collectedPatterns;

	public PatternsCollector(IFramework framework) {
		super();
		collectedPatterns = new HashSet<GTPattern>();
		
		for (Object machine : framework.getMachines()) {
			Machine gtasmMachine = (Machine) machine;
			
			// process annotation
			String machineCodegenMode = null;
			Map<String, String> annotation = 
				GTASMHelper.extractLowerCaseRuntimeAnnotation(gtasmMachine, "@CodeGeneration");
			if (annotation != null) machineCodegenMode = annotation.get("mode");
			
			// process patterns in machine
			if (! "ignore".equals(machineCodegenMode))
			{
				// TODO preconditions (?)
				for (Object patternObj : gtasmMachine.getGtPatternDefinitions()) {
					GTPattern gtPattern = (GTPattern)patternObj;
					collectPattern(gtPattern);
				}
			}	
		}
	}

	/**
	 * @param gtPattern
	 */
	private void collectPattern(GTPattern gtPattern) {
		String patternCodegenMode = null;
		Map<String, String> annotation = 
			GTASMHelper.extractLowerCaseRuntimeAnnotation(gtPattern, "@CodeGeneration");
		if (annotation != null) patternCodegenMode = annotation.get("mode");
		if (!"ignore".equals(patternCodegenMode)) {
			collectedPatterns.add(gtPattern);
			for (GTPatternBody body : gtPattern.getPatternBodies()) {
				for (GTPattern subPattern : body.getGtPatternDefinitions()) {
					collectPattern(subPattern);
				}
			}
		}
	}

	public Set<GTPattern> getCollectedPatterns() {
		return collectedPatterns;
	}
	
}
