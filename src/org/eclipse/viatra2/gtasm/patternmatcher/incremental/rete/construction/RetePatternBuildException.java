/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction;


/**
 * A problem has occured during the construction of the RETE net.
 * 
 * @author Bergmann Gábor
 *
 */
public class RetePatternBuildException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2424538150959407887L;

	private Object patternDescription;
	private String templateMessage;
	private String[] templateContext;
	
	/**
	 * @param message The template of the exception message
	 * @param context The data elements to be used to instantiate the template. Can be null if no context parameter is defined
	 * @param patternDescription the PatternDescription where the exception occurred
	 */
	public RetePatternBuildException(String message, String[] context, Object patternDescription) {
		super(bind(message, context));		
		this.patternDescription = patternDescription;
		this.templateMessage = message;
		this.templateContext = context;
	}
	
	/**
	 * @param message The template of the exception message
	 * @param context The data elements to be used to instantiate the template. Can be null if no context parameter is defined
	 * @param patternDescription the PatternDescription where the exception occurred
	 */
	public RetePatternBuildException(String message, String[] context, Object patternDescription, Throwable cause) {
		super(bind(message, context), cause);		
		this.patternDescription = patternDescription;
		this.templateMessage = message;
		this.templateContext = context;
	}

	public Object getPatternDescription() {
		return patternDescription;
	}

	public String getTemplateMessage() {
		return templateMessage;
	}

	public String[] getTemplateContext() {
		return templateContext;
	}

	/**
	 * Binding the '{n}' (n = 1..N) strings to contextual conditions in 'context'
	 * @param context : array of context-sensitive Strings
	 */
	private static String bind(String message, String[] context) {
		String additionalError = "";
		
		if(context == null) return message;
		
		for(int i = 0; i<context.length; i++){
			message = message.replace("{"+(i+1)+"}", context[i]!= null? context[i] : "<<null>>");
			//error handling in case there is a null value in the context array
			if(context[i] == null)
				additionalError = "[INTERNAL ERROR] A name value in the GTASM model is null. \n\n";
		}
		return additionalError+message;
	}
}
