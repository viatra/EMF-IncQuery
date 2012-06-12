package org.eclipse.viatra2.patternlanguage.ui.highlight;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;
import org.eclipse.xtext.xbase.ui.highlighting.XbaseHighlightingConfiguration;

@SuppressWarnings("restriction")
public class EMFPatternLanguageHighlightingConfiguration extends
		XbaseHighlightingConfiguration {

	public static final String METAMODEL_REFERENCE = "incquery.metamodel.reference";
	
	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		acceptor.acceptDefaultHighlighting(METAMODEL_REFERENCE, "EMF type reference", metamodelReference());
		
		super.configure(acceptor);
	}

	public TextStyle metamodelReference(){
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(0, 26, 171));
		return textStyle;
	}
}
