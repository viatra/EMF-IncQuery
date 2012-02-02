package org.eclipse.viatra2.emf.incquery.gui.handlers;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.core.genmodel.GenModelHelper;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryProjectSupport;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;

/**
 * Handler class to invoke the import of ECore metamodels into the VPML modelspace file.
 * @author Zoltan Ujhelyi, Istvan Rath
 *
 */
public class FillVPMLHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		VPMLCreatorJob job = new VPMLCreatorJob("Creating models", selection, event);
		job.setUser(true);
		job.schedule();
		return null;
	}

	class VPMLCreatorJob extends Job{

		public VPMLCreatorJob(String name, IStructuredSelection selection, ExecutionEvent event) {
			super(name);
			this.selection = selection;
			this.event = event;
		}
		
		IStructuredSelection selection;
		ExecutionEvent event;
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IncQueryGenmodel) {
				IncQueryGenmodel iqModel = (IncQueryGenmodel) firstElement;
				IEditorInput editorInput = HandlerUtil.getActiveEditor(event).getEditorInput();
				IFile file = (IFile) editorInput.getAdapter(IFile.class);
				if (file != null) {
					IProject project = file.getProject();
					monitor.beginTask("Repopulating VPM with referenced EMF models", 100);
					return doFillVPML(monitor, iqModel, project);
				}
			} else {
				IProject project;

				if (firstElement instanceof IProject) {
					project = (IProject) firstElement;
				} else {
					IFile iFile = (IFile)firstElement;
					project = iFile.getProject();
				}
				monitor.beginTask("Repopulating VPM with referenced EMF models", 110);
				monitor.subTask("Loading IncQuery generator model");
				IncQueryGenmodel iqModel = GenModelHelper.parseGenModel(project);
				monitor.worked(10);
				if (iqModel!=null) {
					return doFillVPML(monitor, iqModel, project);
				}
				else {
					monitor.done();
					return new Status(Status.ERROR, IncQueryGUIPlugin.PLUGIN_ID, 
							"Error loading IncQuery genmodel file");
				}

			}

			return new Status(Status.ERROR, IncQueryGUIPlugin.PLUGIN_ID, 
					"Error loading referenced EMF models: cannot interpret input; " 
					+ "issue command on EMF-IncQuery project, generator or root element ofgenerator file.");	
		}

		private IStatus doFillVPML(IProgressMonitor monitor, IncQueryGenmodel iqModel, IProject project) {
			monitor.subTask("Loading Ecore models"); //, iqModel.getEcoreModel().size());
			for (EcoreModel ecore : iqModel.getEcoreModel()) {
				GenModel genModel = ecore.getModels();
				String modelPluginID = genModel.getModelPluginID();
				//EPackage ecorePackage = genModel.getEcoreGenPackage().getRootGenPackage().getEcorePackage();
				List<EPackage> packages = new ArrayList<EPackage>();
				for (GenPackage genPackage : genModel.getGenPackages()) {
					packages.add(genPackage.getEcorePackage());
				}
				try {
					IncQueryProjectSupport.fillVPMLContent(packages, project, modelPluginID);
					monitor.worked(100);
				} 
				catch (Exception e) {
					String msg = e.getMessage();
					if (msg==null) {
						msg = "An exception was caught while importing the ECore metamodel.";
					}
					msg+="\nPlease validate your ECore file, and make sure you follow the ECore design guidelines on http://viatra.inf.mit.bme.hu/incquery.\n";
					monitor.done();
					Status status = new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, msg, e);
					return status;
				}

			}
			monitor.done();
			return Status.OK_STATUS;
		}
		
	}
}
