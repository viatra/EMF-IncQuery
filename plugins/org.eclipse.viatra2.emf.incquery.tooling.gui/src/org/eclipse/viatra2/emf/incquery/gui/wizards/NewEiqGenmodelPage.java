package org.eclipse.viatra2.emf.incquery.gui.wizards;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class NewEiqGenmodelPage extends WizardPage {
	//private DataBindingContext m_bindingContext;
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());
	private Tree referencedGenmodels;
	private TreeViewer genModelViewer;
	private Button btnInitializeGeneratorModel;
	private Button addGenmodel;
	
	private ResourceSet set;
	private List<GenModel> selectedGenmodels = Lists.newArrayList();
	private boolean displayCreateComposite;

	/**
	 * Create the wizard.
	 */
	public NewEiqGenmodelPage(boolean displayCreateComposite) {
		super("wizardPage");
		this.displayCreateComposite = displayCreateComposite;
		setTitle("EMF-IncQuery Generator model");
		setDescription("Set up a generator model used for code generation.");
		set = new ResourceSetImpl();
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		if (displayCreateComposite) {
			Section sctnGenmodel = formToolkit.createSection(container,
					Section.EXPANDED | Section.TITLE_BAR);
			sctnGenmodel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));
			formToolkit.paintBordersFor(sctnGenmodel);
			sctnGenmodel.setText("Genmodel");

			Composite composite = formToolkit.createComposite(sctnGenmodel,
					SWT.NONE);
			formToolkit.paintBordersFor(composite);
			sctnGenmodel.setClient(composite);
			composite.setLayout(new GridLayout(3, false));
			new Label(composite, SWT.NONE);
			new Label(composite, SWT.NONE);

			btnInitializeGeneratorModel = formToolkit.createButton(composite,
					"Initialize Generator Model", SWT.CHECK);
			new Label(composite, SWT.NONE);

			formToolkit.createLabel(composite, "Filename", SWT.NONE);

			formToolkit.createLabel(composite,		"generator.eiqgen", SWT.NONE);
		}
		
		Section sctnReferencedEmfGenerator = formToolkit.createSection(
				container, Section.EXPANDED | Section.TITLE_BAR);
		sctnReferencedEmfGenerator.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(sctnReferencedEmfGenerator);
		sctnReferencedEmfGenerator.setText("Referenced EMF Generator models");

		referencedGenmodels = formToolkit.createTree(
				sctnReferencedEmfGenerator, SWT.NONE);
		referencedGenmodels.setEnabled(false);
		formToolkit.paintBordersFor(referencedGenmodels);
		sctnReferencedEmfGenerator.setClient(referencedGenmodels);
		referencedGenmodels.setHeaderVisible(true);
		referencedGenmodels.setLinesVisible(true);
		
		genModelViewer = new TreeViewer(referencedGenmodels);
		genModelViewer.setContentProvider(new ITreeContentProvider() {
			
			List<GenModel> genmodels;
			
			@SuppressWarnings("unchecked")
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				if (newInput instanceof List<?>) {
					genmodels = (List<GenModel>) newInput;
				}
			}
			
			@Override
			public void dispose() {	}
			
			@Override
			public boolean hasChildren(Object element) {
				return element instanceof GenModel && !((GenModel)element).getGenPackages().isEmpty();
			}
			
			@Override
			public Object getParent(Object element) {
				if (element instanceof GenPackage) {
					return ((GenPackage) element).getGenModel();
				}
				return null;
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return genmodels.toArray(new GenModel[genmodels.size()]);
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof GenModel) {
					List<GenPackage> packages = ((GenModel) parentElement).getGenPackages();
					return packages.toArray(new GenPackage[packages.size()]);
				}
				return null;
			}
		});
		genModelViewer.setLabelProvider(new LabelProvider() {
			
		});
		TreeViewerColumn resourcePathColumn = new TreeViewerColumn(genModelViewer, SWT.LEFT);
		resourcePathColumn.setLabelProvider(new ColumnLabelProvider() {
			
			@Override
			public String getText(Object element) {
				if (element instanceof GenModel) {
					return ((GenModel) element).eResource().getURI().toPlatformString(true);
				} else if (element instanceof GenPackage) {
					return ((GenPackage) element).getNSURI();
				}
				return super.getText(element);
			}
		});
		TreeViewerColumn packageURIColumn = new TreeViewerColumn(genModelViewer, SWT.LEFT);
		packageURIColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof GenModel) {
					return String.format("Plug-in dependency: %s",((GenModel) element).getModelPluginID());
				} else if (element instanceof GenPackage) {
					return "";
				}
				return super.getText(element);
			}
			
		});
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(50, true));
		tableLayout.addColumnData(new ColumnWeightData(50, true));
		referencedGenmodels.setLayout(tableLayout);
		
		genModelViewer.setInput(selectedGenmodels);

		Composite composite_1 = formToolkit.createComposite(
				sctnReferencedEmfGenerator, SWT.NONE);
		formToolkit.paintBordersFor(composite_1);
		sctnReferencedEmfGenerator.setTextClient(composite_1);
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));

		addGenmodel = formToolkit.createButton(composite_1, "Add", SWT.NONE);
		addGenmodel.setEnabled(false);
		addGenmodel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectGenmodelFromWorkspace();
			}
		});
		if (displayCreateComposite) {
		/*m_bindingContext = */initDataBindings();
		}
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeSelectionBtnInitializeGeneratorModelObserveWidget = WidgetProperties
				.selection().observe(btnInitializeGeneratorModel);
		IObservableValue enabledReferencedGenmodelsObserveValue = PojoProperties
				.value("enabled").observe(referencedGenmodels);
		bindingContext.bindValue(
				observeSelectionBtnInitializeGeneratorModelObserveWidget,
				enabledReferencedGenmodelsObserveValue, null, null);
		//
		IObservableValue observeEnabledAddGenmodelObserveWidget = WidgetProperties
				.enabled().observe(addGenmodel);
		IObservableValue observeSelectionBtnInitializeGeneratorModelObserveWidget_1 = WidgetProperties
				.selection().observe(btnInitializeGeneratorModel);
		bindingContext.bindValue(observeEnabledAddGenmodelObserveWidget,
				observeSelectionBtnInitializeGeneratorModelObserveWidget_1,
				null, null);
		//
		return bindingContext;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			if (displayCreateComposite) {
				btnInitializeGeneratorModel.setSelection(true);
			}
			addGenmodel.setEnabled(true);
			referencedGenmodels.setEnabled(true);
		}
		super.setVisible(visible);
	}

	public void selectGenmodelFromWorkspace() {
		ViewerFilter genmodelFileFilter = new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IFile) {
					return "genmodel".equals(((IFile) element).getFileExtension());
				}
				return true;
			}
		};
		IFile[] files = WorkspaceResourceDialog
				.openFileSelection(
						getShell(),
						"Select EMF Generator model",
						"Select EMF generator model(s) to add to the initialized EMF-IncQuery generator model",
						true, null, ImmutableList.of(genmodelFileFilter));
		for (IFile file : files) {
			URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
			Resource res = set.getResource(uri, true);
			for (EObject obj : res.getContents()) {
				if (obj instanceof GenModel) {
					selectedGenmodels.add((GenModel) obj);
				}
			}
			genModelViewer.refresh();
		}
	}

	public List<GenModel> getSelectedGenmodels() {
		return selectedGenmodels;
	}
	
	public boolean isCreateGenmodelChecked() {
		return !displayCreateComposite || btnInitializeGeneratorModel.getSelection();
	}
}
