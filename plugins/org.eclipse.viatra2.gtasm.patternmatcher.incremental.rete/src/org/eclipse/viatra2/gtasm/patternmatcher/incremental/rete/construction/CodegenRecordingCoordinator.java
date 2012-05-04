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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;

// TODO Output contents of builderCode into files, accompany with PosMapping

/**
 * 
 * @author Bergmann Gábor
 *
 */
public class CodegenRecordingCoordinator<PatternDescription> {
	protected static final String varPrefix = "var";
	protected static final String buildablePrefix = "buildable";
	protected static final String collectorPrefix = "production";
		
	public Long nextIdentifier; /**/
	public String stubType; /**/
	public String collectorType; /**/
	public String buildableType; /**/
	HashMap<PatternDescription, StringBuilder> builderCode; /**/
	// HashMap<PatternDescription, String> collectors; /**/
	// LinkedHashSet<PatternDescription> unbuilt; /**/
	public IPatternMatcherContext<PatternDescription> targetContext; /**/

	public CodegenRecordingCoordinator(IPatternMatcherContext<PatternDescription> targetContext,
			String stubType, String collectorType, String buildableType) 
	{
		super();
		this.targetContext = targetContext;
		this.nextIdentifier = 0L;
		this.stubType = stubType;
		this.collectorType = collectorType;
		this.buildableType = buildableType;
		
		this.builderCode = new HashMap<PatternDescription, StringBuilder>();
		//this.collectors = new HashMap<PatternDescription, String>();
		//this.unbuilt = new LinkedHashSet<PatternDescription>();
		
	}
	
	String newIdentifier(String prefix){
		return prefix + "_"+(nextIdentifier++).toString();
	}

	String newVariableIdentifier(){
		return newIdentifier(varPrefix);
	}
	String newBuildableIdentifier(){
		return newIdentifier(buildablePrefix);
	}
	String newCollectorIdentifier(){
		return newIdentifier(collectorPrefix);
	}
	
	public void emitPatternBuilderLine(PatternDescription effort, String indent, String line) {
		StringBuilder sb = getBuilder(effort);
		emitLine(sb, indent, line);
	}
	
	
	StringBuilder getBuilder(PatternDescription effort) {
		if (effort == null) 
			throw new UnsupportedOperationException("Build actions must be put on the tab of a pattern");
		StringBuilder result = builderCode.get(effort);
		if (result == null) {
			result = new StringBuilder();
			builderCode.put(effort, result);
		}
		return result;
	}
	
	private void emitLine(StringBuilder where, String indent, String line) {
		where.append(indent);
		where.append(line);
		where.append(System.getProperty("line.separator"));		
	}
	
	public String getFinishedBuilderCode(PatternDescription pattern) {
		return builderCode.get(pattern).toString();
	}
	public Set<PatternDescription> getBuiltPatterns() {
		return builderCode.keySet();
	}
	
//	String allocateNewCollector(PatternDescription pattern) {
//		if (collectors.containsKey(pattern)) 
//			throw new UnsupportedOperationException("Duplicate production nodes unsupported in RETE code generation");
//		String prod = newCollectorIdentifier();
//		collectors.put(pattern, prod);
//		unbuilt.remove(pattern);
//		return prod;
//	}
	
//	/**
//	 * @pre isComplete()
//	 */
//	public void printMembers(StringBuilder where, String indent) {
//		for (String collector : collectors.values()) {
//			emitLine(where, indent, collectorType + " " + collector + ";");
//		}
//	}

//	public boolean isComplete() {
//		return unbuilt.isEmpty();
//	}
//	public PatternDescription nextUnbuilt() {
//		return unbuilt.iterator().next();	
//	}	
}
