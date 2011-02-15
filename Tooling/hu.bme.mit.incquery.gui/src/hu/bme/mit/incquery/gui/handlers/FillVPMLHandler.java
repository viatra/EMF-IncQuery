package hu.bme.mit.incquery.gui.handlers;

import hu.bme.mit.incquery.core.project.IncQueryProjectSupport;
import hu.bme.mit.incquery.gui.IncQueryGUIPlugin;
import incquerygenmodel.EcoreModel;
import incquerygenmodel.IncQueryGenmodel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler class to invoke the import of ECore metamodels into the VPML modelspace file.
 * @author Zoltan Ujhelyi, Istvan Rath
 *
 */
public class FillVPMLHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		IEditorInput editorInput = HandlerUtil.getActiveEditor(event).getEditorInput();
		IFile file = (IFile) editorInput.getAdapter(IFile.class);
		if (file != null) {
			IProject project = file.getProject();
			VPMLCreatorJob job = new VPMLCreatorJob("Creating models", selection, project);
			job.setUser(true);
			job.schedule();
		}
		return null;
	}

	class VPMLCreatorJob extends Job{

		IStructuredSelection selection;
		IProject project;
		
		public VPMLCreatorJob(String name, IStructuredSelection selection, IProject project) {
			super(name);
			this.project = project;
			this.selection = selection;
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IncQueryGenmodel iqModel = (IncQueryGenmodel) selection.getFirstElement();
			monitor.beginTask("Loading models", iqModel.getEcoreModel().size());
			for (EcoreModel ecore : iqModel.getEcoreModel()) {
				GenModel genModel = ecore.getModels();
				String modelPluginID = genModel.getModelPluginID();
				//EPackage ecorePackage = genModel.getEcoreGenPackage().getRootGenPackage().getEcorePackage();
				for (GenPackage genPackage : genModel.getGenPackages()) {
					try {
						IncQueryProjectSupport.fillVPMLContent(genPackage.getEcorePackage(), project, modelPluginID);
						monitor.worked(1);
					} 
					catch (Exception e) {
						String msg = e.getMessage();
						if (msg==null) {
							msg = "An exception was caught while importing the ECore metamodel.";
						}
						msg+="\nPlease validate your ECore file, and make sure you follow the ECore design guidelines on http://viatra.inf.mit.bme.hu/incquery.\n";
					//	msg+="\nStack trace follows:\n";
					//	StringWriter sw = new StringWriter();
					//	e.printStackTrace(new PrintWriter( sw ));
					//	msg+=sw.toString();
						monitor.done();
						Status status = new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, msg, e);
						return status;
						/*ErrorDialog.openError(Display.getCurrent().getActiveShell(),
								"INCQuery error", 
								"An error occurred while performing the ECore import operation. Check the error log.", 
								Activator.log(e, msg ));*/
					}

				}
			}
			monitor.done();
			return Status.OK_STATUS;
		}
		
	}
}
