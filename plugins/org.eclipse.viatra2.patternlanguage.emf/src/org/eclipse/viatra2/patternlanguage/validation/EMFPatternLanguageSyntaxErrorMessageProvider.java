package org.eclipse.viatra2.patternlanguage.validation;

import org.antlr.runtime.MismatchedTokenException;
import org.eclipse.viatra2.patternlanguage.services.EMFPatternLanguageGrammarAccess;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.antlr.SyntaxErrorMessageProvider;

import com.google.inject.Inject;

public class EMFPatternLanguageSyntaxErrorMessageProvider extends
		SyntaxErrorMessageProvider {

	@Inject
	EMFPatternLanguageGrammarAccess grammar;

	@Override
	public SyntaxErrorMessage getSyntaxErrorMessage(IParserErrorContext context) {
		if (context.getRecognitionException() instanceof MismatchedTokenException) {
			MismatchedTokenException exception = (MismatchedTokenException) context
					.getRecognitionException();
			if (exception.expecting >= 0 && exception.getUnexpectedType() >= 0) {
				String expectingTokenTypeName = context.getTokenNames()[exception.expecting];
				String unexpectedTokenTypeName = context.getTokenNames()[exception
						.getUnexpectedType()];
				if ("RULE_ID".equals(expectingTokenTypeName)
						&& Character
								.isJavaIdentifierStart(unexpectedTokenTypeName
										.replace("'", "").charAt(0))) {
					return new SyntaxErrorMessage(
							"Keywords of the query language are to be prefixed with the ^ character when used as an identifier",
							EMFIssueCodes.IDENTIFIER_AS_KEYWORD);
				}
			}
		}
		return super.getSyntaxErrorMessage(context);
	}

}
