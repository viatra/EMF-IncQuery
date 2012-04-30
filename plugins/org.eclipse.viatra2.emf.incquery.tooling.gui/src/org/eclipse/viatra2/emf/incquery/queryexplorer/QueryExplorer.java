package org.eclipse.viatra2.emf.incquery.queryexplorer;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapter;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.FlyoutControlComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.FlyoutPreferences;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.IFlyoutPreferences;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherContentProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherLabelProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.PatternMatch;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.observable.DetailElement;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.observable.DetailObserver;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.CheckStateListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DoubleClickListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.FileEditorPartListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.ModelEditorPartListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.ResourceChangeListener;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * MatchSetViewer is used to display the match sets for those matchers which are annotated with PatternUI. 
 * 
 * @author Tamas Szabo
 *
 */
public class QueryExplorer extends ViewPart {
	public QueryExplorer() {
	}

	public static final String ID = "org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer";
	private TableViewer tableViewer;
	private CheckboxTableViewer patternsViewer;
	
	//matcher tree viewer
	private TreeViewer matcherTreeViewer;
	private MatcherContentProvider matcherContentProvider = new MatcherContentProvider();
	private MatcherLabelProvider matcherLabelProvider = new MatcherLabelProvider();
	private MatcherTreeViewerRoot matcherTreeViewerRoot = new MatcherTreeViewerRoot();
	
	//observable view
	private ModelEditorPartListener modelPartListener = new ModelEditorPartListener();
	private FileEditorPartListener filePartListener = new FileEditorPartListener();
	private static QueryExplorer instance;
		
	@Inject
	Injector injector;
	
	public MatcherTreeViewerRoot getMatcherTreeViewerRoot() {
		return matcherTreeViewerRoot;
	}
	
	public static QueryExplorer getInstance() {
		if (instance == null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	        IViewPart form = page.findView(ID);
	        instance = (QueryExplorer) form;
		}
        return instance;
	}
	
	public TreeViewer getMatcherTreeViewer() {
		return matcherTreeViewer;
	}
	
	public void clearTableViewer() {
		tableViewer.setInput(null);
	}
	
	public void createPartControl(Composite parent) {
		IFlyoutPreferences rightFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_EAST, IFlyoutPreferences.STATE_COLLAPSED, 400);
		FlyoutControlComposite rightFlyoutControlComposite = new FlyoutControlComposite(parent, SWT.NONE, rightFlyoutPreferences);
		rightFlyoutControlComposite.setTitleText("Observer view");
		rightFlyoutControlComposite.setValidDockLocations(IFlyoutPreferences.DOCK_EAST);
		
		IFlyoutPreferences leftFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_WEST, IFlyoutPreferences.STATE_COLLAPSED, 150);
		FlyoutControlComposite leftFlyoutControlComposite = new FlyoutControlComposite(rightFlyoutControlComposite.getClientParent(), SWT.NONE, leftFlyoutPreferences);
		leftFlyoutControlComposite.setTitleText("Registered patterns");
		leftFlyoutControlComposite.setValidDockLocations(IFlyoutPreferences.DOCK_WEST);
		
		matcherTreeViewer = new TreeViewer(leftFlyoutControlComposite.getClientParent());
		tableViewer = new TableViewer(rightFlyoutControlComposite.getFlyoutParent());
		
		Table table = new Table(leftFlyoutControlComposite.getFlyoutParent(), SWT.CHECK | SWT.BORDER);
        TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(100));
        table.setLayout(layout);
		
		patternsViewer = new CheckboxTableViewer(table);
		patternsViewer.addCheckStateListener(new CheckStateListener());
		
		//matcherTreeViewer configuration
		matcherTreeViewer.setContentProvider(matcherContentProvider);
		matcherTreeViewer.setLabelProvider(matcherLabelProvider);
		matcherTreeViewer.setInput(matcherTreeViewerRoot);
		
		IObservableValue selection = ViewersObservables.observeSingleSelection(matcherTreeViewer);
		selection.addValueChangeListener(new SelectionChangeListener());
		matcherTreeViewer.addDoubleClickListener(new DoubleClickListener());
		
		//patternsViewer configuration
		patternsViewer.setContentProvider(new ObservableListContentProvider());
		IObservableList list = BeansObservables.observeList(PatternRegistry.getInstance(), "patternNames", String.class);
		patternsViewer.setInput(list);
		
		// Create menu manager.
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
        	public void menuAboutToShow(IMenuManager mgr) {
        		fillContextMenu(mgr);
            }
        });
           
        // Create menu.
        Menu menu = menuMgr.createContextMenu(matcherTreeViewer.getControl());
        
		matcherTreeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu("org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer.treeViewerMenu", menuMgr, matcherTreeViewer);
		
		//tableView configuration
		createColumns(tableViewer);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setContentProvider(new ObservableListContentProvider());
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
		
		getSite().setSelectionProvider(matcherTreeViewer);
		
		initFileListener();
		initFileEditorListener();
	}

	private void fillContextMenu(IMenuManager mgr) {
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void createColumns(TableViewer viewer) {
		String[] titles = { "Parameter", "Value" };

		//parameter
		TableViewerColumn col = createTableViewerColumn(viewer, titles[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				DetailElement de = (DetailElement) element;
				return de.getKey();
			}
		});

		// value
		col = createTableViewerColumn(viewer, titles[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				DetailElement de = (DetailElement) element;
				return de.getValue();
			}
		});
	}
	
	/**
	 * Creates a column for the table viewer with the given parameters.
	 * 
	 * @param viewer the viewer to create the column for
	 * @param title the title of the column
	 * @param index the index of the column
	 * @return the column object
	 */
	private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int index) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE, index);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		column.setWidth(200);
		return viewerColumn;

	}

	public void setFocus() {
		matcherTreeViewer.getControl().setFocus();
	}
	
	private class SelectionChangeListener implements IValueChangeListener {
		
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			Object value = event.getObservableValue().getValue();
			
			if (value instanceof PatternMatch) {
				PatternMatch pm = (PatternMatch) value;
				DatabindingAdapter<IPatternMatch> databindableMatcher = 
						DatabindingUtil.getDatabindingAdapter(pm.getPatternMatch().patternName(), pm.getParent().isGenerated());
				
				if (databindableMatcher == null) {
					tableViewer.setInput(null);
				}
				else {
					DetailObserver observer = new DetailObserver(databindableMatcher, pm);
					tableViewer.setInput(observer);
					//ViewerSupport.bind(tableViewer, observer, new LabelPropety());
				}
			}
			else {
				tableViewer.setInput(null);
			}
		}
		
	}
	
	private void initFileEditorListener() {
		IPartService service = (IPartService) getSite().getService(IPartService.class);
		service.addPartListener(filePartListener);
	}
	
	private void initFileListener() {
		IResourceChangeListener listener = new ResourceChangeListener(injector);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.PRE_BUILD);
	}
	
	public ModelEditorPartListener getModelPartListener() {
		return modelPartListener;
	}
	
	public FileEditorPartListener getFilePartListener() {
		return filePartListener;
	}
	
	public TableViewer getPatternsViewer() {
		return patternsViewer;
	}
}
