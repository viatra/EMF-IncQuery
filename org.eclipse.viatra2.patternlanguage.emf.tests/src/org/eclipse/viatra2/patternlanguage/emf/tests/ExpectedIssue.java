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
package org.eclipse.viatra2.patternlanguage.emf.tests;

import org.eclipse.xtext.nodemodel.INode;

/**
 * An ExpectedIssue stores a partial error message and a line number to identify
 * a parser error message.
 * 
 */
public class ExpectedIssue {

	String desc;
	int line;

	public ExpectedIssue(String desc, int line) {
		this.desc = desc;
		this.line = line;
	}

	public String getDesc() {
		return desc;
	}

	public int getLine() {
		return line;
	}

	
	/**
	 * Decides whether the expected issue matches an error node
	 * @param node
	 * @return
	 */
	public boolean matchesErrorNode(INode node) {
		return (node.getStartLine() == line && node.getSyntaxErrorMessage()
				.getMessage().toLowerCase().contains(desc.toLowerCase()));
	}
}
