
package org.eclipse.viatra2.emf.incquery.tooling.generator.ui.quickfix;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.tooling.generator.ui.GenmodelProjectBasedValidation;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModification;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;

public class GeneratorModelQuickfixProvider extends DefaultQuickfixProvider {

//	@Fix(MyJavaValidator.INVALID_NAME)
//	public void capitalizeName(final Issue issue, IssueResolutionAcceptor acceptor) {
//		acceptor.accept(issue, "Capitalize name", "Capitalize the name.", "upcase.png", new IModification() {
//			public void apply(IModificationContext context) throws BadLocationException {
//				IXtextDocument xtextDocument = context.getXtextDocument();
//				String firstLetter = xtextDocument.get(issue.getOffset(), 1);
//				xtextDocument.replace(issue.getOffset(), 1, firstLetter.toUpperCase());
//			}
//		});
//	}

	@Fix(GenmodelProjectBasedValidation.GENMODEL_DEPENDENCY)
	public void addDependency(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		acceptor.accept(issue, "Add dependency",
				"Add the required bundle to the manifest.mf file.", null,
				new IModification() {

					@Override
					public void apply(IModificationContext context)
							throws CoreException, BadLocationException {
						URI uriToProblem = issue.getUriToProblem();
						if (uriToProblem.isPlatform()) {
							IWorkspaceRoot root = ResourcesPlugin
									.getWorkspace().getRoot();
							IFile file = root.getFile(new Path(uriToProblem
									.toPlatformString(true)));
							if (file.exists() && !file.isReadOnly())
								ProjectGenerationHelper
										.ensureBundleDependencies(
												file.getProject(),
												Arrays.asList(issue.getData()));
							// The following change changes the document thus
							// triggers its parsing
							IXtextDocument document = context
									.getXtextDocument();
							document.replace(issue.getOffset(), 1,
									document.get(issue.getOffset(), 1));
						}
					}
				});
	}
}
