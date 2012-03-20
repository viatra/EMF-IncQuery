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
package org.eclipse.viatra2.patternlanguage.formatting;

import org.eclipse.viatra2.patternlanguage.core.services.PatternLanguageGrammarAccess.PatternBodyElements;
import org.eclipse.viatra2.patternlanguage.services.EMFPatternLanguageGrammarAccess;
import org.eclipse.viatra2.patternlanguage.services.EMFPatternLanguageGrammarAccess.EMFPatternModelElements;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

/**
 * Formatting rules for the EMF pattern language.
 */
public class EMFPatternLanguageFormatter extends AbstractDeclarativeFormatter {
	
	@Override
	protected void configureFormatting(FormattingConfig c) {
		// Preserve newlines around comments
		EMFPatternLanguageGrammarAccess grammar = (EMFPatternLanguageGrammarAccess) getGrammarAccess();
		c.setLinewrap(0, 1, 2).before(grammar.getSL_COMMENTRule());
		c.setLinewrap(0, 1, 2).before(grammar.getML_COMMENTRule());
		c.setLinewrap(0, 1, 1).after(grammar.getML_COMMENTRule());
		
		for (Keyword keyword : grammar.findKeywords(".")) {
			c.setNoSpace().before(keyword);
			c.setNoSpace().after(keyword);
		}
		for (Keyword keyword : grammar.findKeywords(":")) {
			c.setSpace(" ").before(keyword);
			c.setSpace(" ").after(keyword);
		}
		for (Keyword keyword : grammar.findKeywords("::")) {
			c.setSpace(" ").before(keyword);
			c.setNoSpace().after(keyword);
		}
		for (Keyword keyword : grammar.findKeywords(",")) {
			c.setNoSpace().before(keyword);
			c.setSpace(" ").after(keyword);
		}
		for (Keyword keyword : grammar.findKeywords("(", ")")) {
			c.setNoSpace().before(keyword);
			c.setNoSpace().after(keyword);
		}
		
		EMFPatternModelElements patternModelAccess = grammar.getEMFPatternModelAccess();
		c.setLinewrap(2).after(patternModelAccess.getPackageNameAssignment_1_1());
		c.setLinewrap(1).after(patternModelAccess.getImportPackagesAssignment_2());
		c.setLinewrap().before(patternModelAccess.getPatternsAssignment_3());
		
		PatternBodyElements patternBodyAccess = grammar.getPatternBodyAccess();
		c.setLinewrap(1, 1, 2).before(patternBodyAccess.getConstraintsAssignment_3_0());
		c.setLinewrap(1, 1, 2).before(patternBodyAccess.getRightCurlyBracketKeyword_4());
		c.setLinewrap(2).after(patternBodyAccess.getRightCurlyBracketKeyword_4());
		c.setIndentationIncrement().after(patternBodyAccess.getLeftCurlyBracketKeyword_2());
		c.setIndentationDecrement().before(patternBodyAccess.getRightCurlyBracketKeyword_4());
	}
}
