/*******************************************************************************
 * Copyright (c) 2011, Istvan Rath, Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.core.codegen.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Java keywords.
 * @author Istvan Rath
 *
 */
public class JavaKeywords {

	/*
	 * Source: http://download.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
	 */
	
	static final String[] keywords = new String[]{"abstract",
	"continue",	"for",	"new",	"switch",
	"assert",	"default",	"goto",	"package",	"synchronized",
	"boolean",	"do",	"if",	"private",	"this",
	"break",	"double",	"implements",	"protected",	"throw",
	"byte",	"else",	"import",	"public",	"throws",
	"case",	"enum",	"instanceof",	"return",	"transient",
	"catch",	"extends",	"int",	"short",	"try",
	"char",	"final",	"interface",	"static",	"void",
	"class",	"finally",	"long",	"strictfp",	"volatile",
	"const"	,"float"	,"native",	"super",	"while", "true", "false", "null"
	};
	
	static Set<String> keywordSet = new HashSet<String>();
	
	static {
		keywordSet.addAll( Arrays.asList(keywords));
	}
	
	/**
	 * True, if s is a reserver Java keyword
	 * @param s
	 * @return
	 */
	public static final boolean isJavaKeyword(String s) {
		return keywordSet.contains(s);
	}
	
}
