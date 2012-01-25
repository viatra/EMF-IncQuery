package org.eclipse.viatra2.patternlanguage.emf.tests;

import org.junit.Ignore;
import org.junit.Test;
public class ParserTest extends AbstractEMFPatternLanguageTest{
	
	private static final String ANNOTATION_RULE = "Annotation";
	private static final String PATTERN_COMPOSITION_RULE = "PatternCompositionConstraint";
	private static final String PATH_EXPRESSION_RULE = "PathExpressionConstraint";
	private static final String PATTERN_RULE = "Pattern";

	@Test
	public void emptyPatternBody() {
		testParserRuleErrors("pattern emptyPattern() = {}", PATTERN_RULE, "did not match");
	}
	
	@Test
	public void simplePattern() {
		testParserRule("pattern simplePattern(Name) = {Pattern(Name);}", PATTERN_RULE);
	}
	@Test@Ignore(value="Injective patterns not supported in RETE")
	public void injectivePattern() {
		testParserRule("injective pattern simplePattern(Name) = {Pattern(Name);}", PATTERN_RULE);
	}
	@Test
	public void orPattern() {
		testParserRule("pattern simplePattern(Name) = {Pattern(Name);} or {Pattern(name);}", PATTERN_RULE);		
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
		testParserRule("EClass.eIDAttribute.changeable(Name, true)", PATH_EXPRESSION_RULE);
	}
	@Test
	public void indexing() {
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
		testParserRuleErrors("find pattern()", PATTERN_COMPOSITION_RULE, "mismatched input");
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
		testParserRule("@Optional@Test pattern name(A) = {Pattern(A);}", PATTERN_RULE);
	}
}
