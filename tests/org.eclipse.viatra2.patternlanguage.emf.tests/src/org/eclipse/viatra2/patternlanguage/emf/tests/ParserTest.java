package org.eclipse.viatra2.patternlanguage.emf.tests;

import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.core.validation.IssueCodes;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class ParserTest {

	private static final String ANNOTATION_RULE = "Annotation";
	private static final String PATTERN_COMPOSITION_RULE = "PatternCompositionConstraint";
	private static final String PATH_EXPRESSION_RULE = "PathExpressionConstraint";
	private static final String PATTERN_RULE = "Pattern";

	@Inject
	private ParseHelper<PatternModel> patternParseHelper;

	@Inject
	private IGrammarAccess grammarAccess;
	@Inject
	private IParser parser;

	@SuppressWarnings("unchecked")
	protected void testParserRule(String text, String rulename) {
		testParserRule(text, rulename, Collections.EMPTY_SET);
	}
	
	protected void testParserRule(String text, String rulename, ExpectedIssue issue) {
		testParserRule(text, rulename, ImmutableSet.of(issue));
	}

	protected void testParserRule(String text, String rulename,
			Collection<ExpectedIssue> issues) {
		HashSet<ExpectedIssue> issueSet = new HashSet<ExpectedIssue>(issues);
		Grammar grammar = grammarAccess.getGrammar();
		ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(grammar,
				rulename);
		IParseResult result = parser.parse(rule, new StringReader(text));
		for (INode node : result.getSyntaxErrors()) {
			ExpectedIssue issue = null;
			Iterator<ExpectedIssue> it = issueSet.iterator();
			while (it.hasNext()) {
				issue = it.next();
				if (issue.matchesErrorNode(node)) {
					it.remove();
					break;
				}
				fail(String.format("Unexpected error message '%s' in line %d",
						node.getSyntaxErrorMessage().getMessage(),
						node.getStartLine()));
			}
		}
		if (!issueSet.isEmpty()) {
			ExpectedIssue issue = issueSet.iterator().next();
			fail(String.format(
					"Expected error message '%s' not found in line %d",
					issue.desc, issue.line));
		}
	}

	@Inject
	private ValidationTestHelper helper;

	@Test
	public void emptyPatternBody() throws Exception {
		PatternModel model = patternParseHelper
				.parse("pattern emptyPattern() = {}");
		helper.assertError(model, PatternLanguagePackage.Literals.PATTERN_BODY,
				IssueCodes.PATTERN_BODY_EMPTY, "empty");
		// testParserRuleErrors("pattern emptyPattern() = {}", PATTERN_RULE,
		// "did not match");
	}

	@Test
	public void simplePattern() {
		testParserRule("pattern simplePattern(p) = {Pattern(p);}", PATTERN_RULE);
	}

	@Test
	public void typeParameter() {
		testParserRule("pattern simplePattern(p : Pattern) = {Pattern(p);}",
				PATTERN_RULE);
	}
	@Test
	public void privatePattern() {
		testParserRule("private pattern simplePattern(p : Pattern) = {Pattern(p);}",
				PATTERN_RULE);
	}

	@Test
	@Ignore(value = "Injective patterns not supported in RETE")
	public void injectivePattern() {
		testParserRule(
				"injective pattern simplePattern(Name) = {Pattern(Name);}",
				PATTERN_RULE);
	}
	@Test
	@Ignore(value = "Injective patterns not supported in RETE")
	public void privateInjectivePattern() {
		testParserRule(
				"private injective pattern simplePattern(Name) = {Pattern(Name);}",
				PATTERN_RULE);
	}
	@Test
	@Ignore(value = "Injective patterns not supported in RETE")
	public void injectivePrivatePattern() {
		testParserRule(
				"injective private pattern simplePattern(Name) = {Pattern(Name);}",
				PATTERN_RULE);
	}

	@Test
	public void orPattern() {
		testParserRule(
				"pattern simplePattern(Name) = {Pattern(Name);} or {Pattern(name);}",
				PATTERN_RULE);
	}

	@Test
	public void floatAttribute() {
		testParserRule("EClass.name(Name, 2.2)", PATH_EXPRESSION_RULE);
	}

	@Test
	public void booleanAttribute() {
		testParserRule("EClass.name(Name, true)", PATH_EXPRESSION_RULE);
		testParserRule("EClass.name(Name, false)", PATH_EXPRESSION_RULE);
	}

	@Test
	public void intAttribute() {
		testParserRule("EClass.name(Name, 2)", PATH_EXPRESSION_RULE);
	}

	@Test
	public void stringAttribute() {
		testParserRule("EClass.name(Name, \"constant\")", PATH_EXPRESSION_RULE);
	}

	@Test
	public void enumAttribute() {
		testParserRule("EClass.name(Name, ::in)", PATH_EXPRESSION_RULE);
	}

	@Test
	public void variableAttribute() {
		testParserRule("EClass.name(Name, in)", PATH_EXPRESSION_RULE);
	}

	@Test
	public void expressionChain() {
		testParserRule("EClass.eIDAttribute.changeable(Name, true)",
				PATH_EXPRESSION_RULE);
	}

	@Test
	public void indexing() throws Exception {
		//PatternModel constraint = patternParseHelper
		//		.parse("Pattern.parameters[1](Name, in)");
		//helper.assertNoErrors(constraint);
		testParserRule("Pattern.parameters[1](Name, in)", PATH_EXPRESSION_RULE);
	}

	@Test
	public void transitiveClosure() {
		testParserRule("Pattern.parameters*(Name, in)", PATH_EXPRESSION_RULE);
	}

	@Test
	public void patternCall() {
		testParserRule("find a(Name, in)", PATTERN_COMPOSITION_RULE);
	}

	@Test
	public void negPatternCall() {
		testParserRule("neg find a(Name)", PATTERN_COMPOSITION_RULE);
	}

	@Test
	public void patternKeywordAsName() {
		testParserRule("find pattern()", PATTERN_COMPOSITION_RULE,
				new ExpectedIssue("mismatched input", 1));
	}

	@Test
	public void annotationNoParam() {
		testParserRule("@Optional", ANNOTATION_RULE);
	}

	@Test
	public void annotationOneParam() {
		testParserRule("@Optional(param1=1)", ANNOTATION_RULE);
	}

	@Test
	public void annotationTwoParams() {
		testParserRule("@Optional(param1=\"1\",param2=true)", ANNOTATION_RULE);
	}

	@Test
	public void multipleAnnotations() {
		testParserRule("@Optional@Test pattern name(A) = {Pattern(A);}",
				PATTERN_RULE);
	}
}
