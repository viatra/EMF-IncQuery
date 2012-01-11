package org.eclipse.viatra2.patternlanguage.emf.tests;

import static org.junit.Assert.assertNotNull;

import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public class BasicTest {

	@Inject
	private ParseHelper<PatternModel> parseHelper;
	
	@Test
	public void testPattern() throws Exception {
		PatternModel model = parseHelper.parse("pattern emptyPattern() = {}");
		assertNotNull(model);
	}
}
