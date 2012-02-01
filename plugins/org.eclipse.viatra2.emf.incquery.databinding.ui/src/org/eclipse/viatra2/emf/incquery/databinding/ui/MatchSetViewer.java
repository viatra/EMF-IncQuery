package org.eclipse.viatra2.emf.incquery.databinding.ui;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanListProperty;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapter;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.DetailElement;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.DetailObservable;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.PatternMatch;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.TreeFactoryImpl;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.TreeLabelProviderImpl;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.TreeStructureAdvisorImpl;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.databinding.ui.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;

/**
 * MatchSetViewer is used to display the match sets for those matchers which are annotated with PatternUI. 
 * 
 * @author Tamas Szabo
 *
 */
public class MatchSetViewer extends ViewPart {

	public static final String ID = "org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer";
	public static TreeViewer treeViewer;
	public static TableViewer tableViewer;
	public static ViewerRoot viewerRoot = new ViewerRoot();
	
	public MatchSetViewer() {
		
	}

	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent);
		tableViewer = new TableViewer(parent);
		
		//treeViewer configuration
		ObservableListTreeContentProvider cp = new ObservableListTreeContentProvider(
				new TreeFactoryImpl(), new TreeStructureAdvisorImpl());
		treeViewer.setContentProvider(cp);
		
		IObservableSet set = cp.getKnownElements();
		IObservableMap[] map = new IObservableMap[1];
		
		map[0] = BeanProperties.value("text", String.class).observeDetail(set);

		treeViewer.setLabelProvider(new TreeLabelProviderImpl(map));
		
		IBeanListProperty rootsProp = BeanProperties.list(ViewerRoot.class, "roots", PatternMatcherRoot.class);
		IObservableList rootsObservableList = rootsProp.observe(viewerRoot);
		treeViewer.setInput(rootsObservableList);
		
		IObservableValue selection = ViewersObservables.observeSingleSelection(treeViewer);
		selection.addValueChangeListener(new SelectionChangleListener());
		
		// Create menu manager.
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
        	public void menuAboutToShow(IMenuManager mgr) {
        		fillContextMenu(mgr);
            }
        });
           
           // Create menu.
        Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
        
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu("org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer.treeViewerMenu", menuMgr, treeViewer);
		
		//tableView configuration
		createColumns(parent, tableViewer);
		final Table table = tableViewer.getTable();
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
		
		getSite().setSelectionProvider(treeViewer);
	}

	private void fillContextMenu(IMenuManager mgr) {
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void createColumns(Composite parent, TableViewer viewer) {
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
	 * Creates a column for the table viewer with the given paramters.
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
		treeViewer.getControl().setFocus();
	}
	
	private class SelectionChangleListener implements IValueChangeListener {

		public SelectionChangleListener() {
			
		}
		
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			Object value = event.getObservableValue().getValue();
			
			if (value != null && value instanceof PatternMatch) {
				PatternMatch pm = (PatternMatch) value;
				DatabindingAdapter<IPatternSignature> databindableMatcher = DatabindingUtil.getDatabindingAdapter(pm.getSignature().patternName());
				
				if (databindableMatcher == null) {
					tableViewer.setInput(null);
				}
				else {
					DetailObservable observer = new DetailObservable(databindableMatcher, pm);
					tableViewer.setInput(observer);
					//ViewerSupport.bind(tableViewer, observer, new LabelPropety());
				}
			}
			else {
				tableViewer.setInput(null);
			}
		}
		
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
}