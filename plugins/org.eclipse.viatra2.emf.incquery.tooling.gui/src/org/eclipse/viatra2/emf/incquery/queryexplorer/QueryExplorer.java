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
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail.TableViewerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.FlyoutControlComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.FlyoutPreferences;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.IFlyoutPreferences;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherContentProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherLabelProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatch;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcher;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.CheckStateListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DoubleClickListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.ModelEditorPartListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.ResourceChangeListener;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * MatchSetViewer is used to display the match sets for those matchers which are annotated with PatternUI. 
 * 
 * @author Tamas Szabo
 *
 */
public class QueryExplorer extends ViewPart {

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
		
	@Inject
	Injector injector;
	
	@Inject
	TableViewerUtil tableViewerUtil;
	
	public QueryExplorer() {
		
	}
	
	public MatcherTreeViewerRoot getMatcherTreeViewerRoot() {
		return matcherTreeViewerRoot;
	}
	
	public static QueryExplorer getInstance() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IViewPart form = page.findView(ID);
	    	return (QueryExplorer) form;
		}
		return null;
	}
	
	public TreeViewer getMatcherTreeViewer() {
		return matcherTreeViewer;
	}
	
	public void clearTableViewer() {
		if (tableViewer.getContentProvider() != null) {
			tableViewer.setInput(null);
		}
	}
	
	public void createPartControl(Composite parent) {
		IFlyoutPreferences rightFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_EAST, IFlyoutPreferences.STATE_COLLAPSED, 300);
		FlyoutControlComposite rightFlyoutControlComposite = new FlyoutControlComposite(parent, SWT.NONE, rightFlyoutPreferences);
		rightFlyoutControlComposite.setTitleText("Observer view");
		rightFlyoutControlComposite.setValidDockLocations(IFlyoutPreferences.DOCK_EAST);
		
		IFlyoutPreferences leftFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_WEST, IFlyoutPreferences.STATE_COLLAPSED, 100);
		FlyoutControlComposite leftFlyoutControlComposite = new FlyoutControlComposite(rightFlyoutControlComposite.getClientParent(), SWT.NONE, leftFlyoutPreferences);
		leftFlyoutControlComposite.setTitleText("Registered patterns");
		leftFlyoutControlComposite.setValidDockLocations(IFlyoutPreferences.DOCK_WEST);
		
		matcherTreeViewer = new TreeViewer(leftFlyoutControlComposite.getClientParent());
		tableViewer = new TableViewer(rightFlyoutControlComposite.getFlyoutParent(), SWT.FULL_SELECTION);
		
		Table table = new Table(leftFlyoutControlComposite.getFlyoutParent(), SWT.CHECK | SWT.BORDER);
        TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(100));
        table.setLayout(layout);
		
		patternsViewer = new CheckboxTableViewer(table);
		patternsViewer.addSelectionChangedListener(new PatternsViewerSelectionChangedListener());
		patternsViewer.addCheckStateListener(new CheckStateListener());
		
		//matcherTreeViewer configuration
		matcherTreeViewer.setContentProvider(matcherContentProvider);
		matcherTreeViewer.setLabelProvider(matcherLabelProvider);
		matcherTreeViewer.setInput(matcherTreeViewerRoot);
		
		IObservableValue selection = ViewersObservables.observeSingleSelection(matcherTreeViewer);
		selection.addValueChangeListener(new MatcherTreeViewerSelectionChangeListener());
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
		//createColumns(tableViewer);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		//tableViewer.setContentProvider(new ObservableListContentProvider());
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
		
		getSite().setSelectionProvider(matcherTreeViewer);
		
		initFileListener();
	}

	private void fillContextMenu(IMenuManager mgr) {
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	public void setFocus() {
		matcherTreeViewer.getControl().setFocus();
	}
	
	private class PatternsViewerSelectionChangedListener implements ISelectionChangedListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			System.out.println(event.getSelection());
			
//			Pattern pattern = PatternRegistry.getInstance().getPatternByFqn("");
//			for (ObservablePatternMatcherRoot root : matcherTreeViewerRoot.getRoots()) {
//				root.unregisterPattern(pattern);
//			}
		}
	}
	
	private class MatcherTreeViewerSelectionChangeListener implements IValueChangeListener {
		
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			Object value = event.getObservableValue().getValue();
			
			if (value instanceof ObservablePatternMatcher) {
				ObservablePatternMatcher observableMatcher = (ObservablePatternMatcher) value;	
				tableViewerUtil.prepareTableViewerForMatcherConfiguration(observableMatcher, tableViewer);
			}
			else if (value instanceof ObservablePatternMatch) {
				ObservablePatternMatch match = (ObservablePatternMatch) value;
				tableViewerUtil.prepareTableViewerForObservableInput(match, tableViewer);
			}
			else {
				tableViewerUtil.clearTableViewerColumns(tableViewer);
				clearTableViewer();
			}
		}
		
	}
	
	private void initFileListener() {
		IResourceChangeListener listener = new ResourceChangeListener(injector);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.PRE_BUILD);
	}
	
	public ModelEditorPartListener getModelPartListener() {
		return modelPartListener;
	}
	
	public CheckboxTableViewer getPatternsViewer() {
		return patternsViewer;
	}
}
