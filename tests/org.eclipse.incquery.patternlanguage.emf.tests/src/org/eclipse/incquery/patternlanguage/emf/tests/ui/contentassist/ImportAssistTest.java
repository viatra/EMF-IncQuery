package org.eclipse.incquery.patternlanguage.emf.tests.ui.contentassist;

import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageUiInjectorProvider;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.junit4.ui.AbstractContentAssistProcessorTest;
import org.eclipse.xtext.junit4.ui.ContentAssistProcessorTestBuilder;
import org.junit.Test;

import com.google.inject.Injector;

@SuppressWarnings("restriction")
public class ImportAssistTest extends AbstractContentAssistProcessorTest {

    @Override
    protected ISetup doGetSetup() {
        return new EMFPatternLanguageStandaloneSetup() {
            @Override
            public Injector createInjector() {
                return new EMFPatternLanguageUiInjectorProvider().getInjector();
            }
        };
    }

    public int findProposalId(String expectedResult, ICompletionProposal[] proposals) {
        for (int i = 0; i < proposals.length; i++) {
            if (proposals[i].getDisplayString().contains(expectedResult)) {
                return i;
            }
        }
        return -1;
    }

    @Test
    public void testImport() throws Exception {

        ContentAssistProcessorTestBuilder processorBuilder = newBuilder().append("import \"\"").cursorBack(1);
        ICompletionProposal[] proposals = processorBuilder.computeCompletionProposals();
        int id = findProposalId("http://www.eclipse.org/incquery/patternlanguage/PatternLanguage", proposals);
        assertTrue("Package not found", id >= 0);
        proposals = processorBuilder.applyText(id, false).appendNl("").append("pattern test(p : ")
                .computeCompletionProposals();
        id = findProposalId("Pattern", proposals);
        assertTrue("Class not found", id >= 0);

    }
}
