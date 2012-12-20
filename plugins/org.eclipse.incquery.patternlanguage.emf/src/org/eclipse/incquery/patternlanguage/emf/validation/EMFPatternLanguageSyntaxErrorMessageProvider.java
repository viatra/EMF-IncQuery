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
package org.eclipse.incquery.patternlanguage.emf.validation;

import org.antlr.runtime.MismatchedTokenException;
import org.eclipse.incquery.patternlanguage.emf.services.EMFPatternLanguageGrammarAccess;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.antlr.SyntaxErrorMessageProvider;

import com.google.inject.Inject;

public class EMFPatternLanguageSyntaxErrorMessageProvider extends SyntaxErrorMessageProvider {

    @Inject
    EMFPatternLanguageGrammarAccess grammar;

    @Override
    public SyntaxErrorMessage getSyntaxErrorMessage(IParserErrorContext context) {
        if (context.getRecognitionException() instanceof MismatchedTokenException) {
            MismatchedTokenException exception = (MismatchedTokenException) context.getRecognitionException();
            if (exception.expecting >= 0 && exception.getUnexpectedType() >= 0) {
                String expectingTokenTypeName = context.getTokenNames()[exception.expecting];
                String unexpectedTokenTypeName = context.getTokenNames()[exception.getUnexpectedType()];
                if ("RULE_ID".equals(expectingTokenTypeName) && !unexpectedTokenTypeName.startsWith("RULE_")
                        && Character.isJavaIdentifierStart(unexpectedTokenTypeName.replace("'", "").charAt(0))) {
                    return new SyntaxErrorMessage(
                            "Keywords of the query language are to be prefixed with the ^ character when used as an identifier",
                            EMFIssueCodes.IDENTIFIER_AS_KEYWORD);
                }
            }
        }
        return super.getSyntaxErrorMessage(context);
    }

}
