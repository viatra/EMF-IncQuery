package org.eclipse.viatra2.patternlanguage.ui.highlight;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XNumberLiteral;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotation;
import org.eclipse.xtext.xbase.ui.highlighting.XbaseHighlightingCalculator;

@SuppressWarnings("restriction")
public class EMFPatternLanguageHighlightingCalculator extends
		XbaseHighlightingCalculator {

	@Override
	protected void searchAndHighlightElements(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
		TreeIterator<EObject> iterator = resource.getAllContents();
		while (iterator.hasNext()) {
			EObject object = iterator.next();
			if (object instanceof XAbstractFeatureCall) {
				computeFeatureCallHighlighting((XAbstractFeatureCall) object, acceptor);
			} else if (object instanceof XNumberLiteral) {
			// Handle XAnnotation in a special way because we want the @ highlighted too
				highlightNumberLiterals((XNumberLiteral) object, acceptor);
			} else if (object instanceof XAnnotation) {
				highlightAnnotation((XAnnotation) object, acceptor);
			} else if (object instanceof ClassType  || object instanceof ReferenceType) {
				ICompositeNode node = NodeModelUtils.findActualNodeFor(object);
				highlightNode(node, EMFPatternLanguageHighlightingConfiguration.METAMODEL_REFERENCE, acceptor);
			} else {
				computeReferencedJvmTypeHighlighting(acceptor, object);
			}
		}
	}
	
}
