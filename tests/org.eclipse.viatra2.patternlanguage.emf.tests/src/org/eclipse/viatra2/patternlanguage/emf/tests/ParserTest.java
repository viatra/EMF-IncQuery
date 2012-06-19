package org.eclipse.viatra2.patternlanguage.emf.tests;

import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
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
	private static final String COMPARE_CONSTRAINT_RULE = "CompareConstraint";
	private static final String PATTERN_COMPOSITION_RULE = "PatternCompositionConstraint";
	private static final String PATH_EXPRESSION_RULE = "PathExpressionConstraint";
	private static final String PATTERN_RULE = "Pattern";

	@Inject
	private IGrammarAccess grammarAccess;
	@Inject
	private IParser parser;

	protected void testParserRule(String text, String rulename) {
		testParserRule(text, rulename, Collections.<ExpectedIssue>emptySet());
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

	@Test
	public void emptyPatternBody() throws Exception {
		// Parser allows empty pattern body - only validation checks for it
		testParserRule("pattern emptyPattern() = {}", PATTERN_RULE);
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
	public void namedBodies() {
		testParserRule(
				"pattern simplePattern(Name) = body1 {Pattern(Name);} or body2 {Pattern(name);}",
				PATTERN_RULE);
	}

	@Test
	public void floatAttribute() {
		testParserRule("EClass.name(Name, 2.2)", PATH_EXPRESSION_RULE);
	}
	@Test
	public void negativeFloatAttribute() {
		testParserRule("EClass.name(Name, -2.2)", PATH_EXPRESSION_RULE);
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
	public void negativeIntAttribute() {
		testParserRule("EClass.name(Name, -2)", PATH_EXPRESSION_RULE);
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
	public void equality() throws Exception {
		testParserRule("A == B", COMPARE_CONSTRAINT_RULE);
	}
	@Test
	public void equalityConstant() throws Exception {
		testParserRule("A == 1", COMPARE_CONSTRAINT_RULE);
	}
	@Test
	public void inequality() throws Exception {
		testParserRule("A != B", COMPARE_CONSTRAINT_RULE);
	}
	@Test
	public void inequalityConstant() throws Exception {
		testParserRule("A != 1", COMPARE_CONSTRAINT_RULE);
	}
	
	@Test
	public void patternCall() {
		testParserRule("find a(Name, in)", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void patternCallStringConstantParameter() {
		testParserRule("find a(Name, \"in\")", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void patternCallIntConstantParameter() {
		testParserRule("find a(Name, 2)", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void patternCallEnumConstantParameter() {
		testParserRule("find a(Name, ::in)", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void patternCallQualifiedEnumConstantParameter() {
		testParserRule("find a(Name, eenum::in)", PATTERN_COMPOSITION_RULE);
	}

	@Test
	public void negPatternCall() {
		testParserRule("neg find a(Name)", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void negPatternCallWithNamedSingleUseParameter() {
		testParserRule("neg find a(_Name)", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void negPatternCallWithUnnamedSingleUseParameter() {
		testParserRule("neg find a(_)", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void negPatternCallStringConstantParameter() {
		testParserRule("neg find a(Name, \"in\")", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void negPatternCallIntConstantParameter() {
		testParserRule("neg find a(Name, 2)", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void negPatternCallEnumConstantParameter() {
		testParserRule("neg find a(Name, ::in)", PATTERN_COMPOSITION_RULE);
	}
	@Test
	public void negPatternCallQualifiedEnumConstantParameter() {
		testParserRule("neg find a(Name, enum::in)", PATTERN_COMPOSITION_RULE);
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
	@Test
	public void multipleSameAnnotations() {
		testParserRule("@Optional@Optional pattern name(A) = {Pattern(A);}",
				PATTERN_RULE);
	}
}
