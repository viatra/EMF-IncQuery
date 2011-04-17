package org.eclipse.viatra2.emf.incquery.validation.ui.actions;


import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.viatra2.emf.incquery.validation.core.ValidationUtil;

public class InitializeValidationAction implements IViewActionDelegate, IEditorActionDelegate {

//	private NamedElement selectedElement=null;
	private Object selectedElement=null;

	
	/**
	 * Reference to the currently active editor.
	 */
	private IEditorPart editor;
	/**
	 * Constructor for CreateMarkerAction.
	 */
	public InitializeValidationAction() {
		super();
	}

	public void init(IViewPart view){}
	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.editor = targetPart.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();

	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		this.editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		// set the color of all elements to default
//		if (editor instanceof MultiPageEditor) {

//			final TreeOutlinePage outline = (TreeOutlinePage) ((MultiDiagramEditor) editor)
//					.getAdapter(IContentOutlinePage.class);
			//outline.setDefault(); // <- re-set colours
		
//			final FileEditorInput f = (FileEditorInput) ((MultiPageEditor)editor).getEditorInput();
			final FileEditorInput f = (FileEditorInput) (editor).getEditorInput();
			if (selectedElement instanceof EObject) {
				Resource eResource = ((EObject)selectedElement).eResource();
				IFile file = f.getFile();
				
				try {
					ValidationUtil.initValidators(eResource, file);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
	}


	
//	private Collection<InheritanceDiamondDTO> _processNewMatches(Collection<InheritanceDiamondDTO> dtos, TreeOutlinePage outline, IFile f) throws CoreException {
//		Vector<InheritanceDiamondDTO> processed = new Vector<InheritanceDiamondDTO>();
//		for (InheritanceDiamondDTO diamond : dtos) {
//			System.out.println("New match:"+toStringDTO(diamond));
//			processed.add(diamond);
//		}
//		return processed;
//	}
//	
//	private Collection<InheritanceDiamondDTO> _processLostMatches(Collection<InheritanceDiamondDTO> dtos, TreeOutlinePage outline, IFile f) throws CoreException {
//		Vector<InheritanceDiamondDTO> processed = new Vector<InheritanceDiamondDTO>();
//		for (InheritanceDiamondDTO diamond : dtos) {
//			System.out.println("Lost match:"+toStringDTO(diamond));
//			processed.add(diamond);
//		}
//		return processed;
//	}

	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	//	action.setEnabled(false);
		//look for selected Element
		Object e=((IStructuredSelection)selection).getFirstElement();
		this.selectedElement=e;
		//this is element an elementlink to an UML element?
//		if (e instanceof EditPart ){
//			Element element = ((IUMLElementEditPart)e).getUmlElement();
//			//this is a named Element?
//			if (element instanceof NamedElement){
//				action.setEnabled(true);
//				selectedElement= (NamedElement)element;
//			}
////			if (element instanceof org.eclipse.papyrus.umlutils.NamedElement){
////				action.setEnabled(true);
////				selectedElement= ((org.eclipse.papyrus.umlutils.NamedElement)element).getUml2NamedElement();
////			}
//			
//		}
	}
	public void setActiveEditor(IAction action, IEditorPart targetEditor){

	}
}
