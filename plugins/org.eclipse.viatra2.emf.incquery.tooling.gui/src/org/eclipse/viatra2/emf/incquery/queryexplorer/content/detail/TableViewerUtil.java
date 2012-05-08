package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapter;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatch;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcher;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public class TableViewerUtil {

	public static void prepareTableViewerForObservableInput(ObservablePatternMatch match, TableViewer viewer) {
		clearTableViewerColumns(viewer);
		String[] titles = { "Parameter", "Value" };
		createColumns(viewer, titles);
		viewer.setContentProvider(new ObservableListContentProvider());
		viewer.setLabelProvider(new DetailElementLabelProvider());
		
		DatabindingAdapter<IPatternMatch> databindableMatcher = 
				DatabindingUtil.getDatabindingAdapter(match.getPatternMatch().patternName(), match.getParent().isGenerated());
		
		if (databindableMatcher == null) {
			viewer.setInput(null);
		}
		else {
			DetailObserver observer = new DetailObserver(databindableMatcher, match);
			viewer.setInput(observer);
		}
	}
	
	public static void prepareTableViewerForMatcherConfiguration(ObservablePatternMatcher observableMatcher, TableViewer viewer) {
		clearTableViewerColumns(viewer);
		String[] titles = { "Parameter", "Class", "Value" };
		createColumns(viewer, titles);
		viewer.setUseHashlookup(true);
		viewer.setColumnProperties(titles);
		viewer.setContentProvider(new MatcherConfigurationContentProvider());
		viewer.setLabelProvider(new MatcherConfigurationLabelProvider());
		viewer.setCellModifier(new MatcherConfigurationCellModifier());
		
		Table table = viewer.getTable();
		CellEditor[] editors = new CellEditor[titles.length];

		editors[0] = new TextCellEditor(table);
		editors[1] = new TextCellEditor(table);
		editors[2] = new ModelElementCellEditor(table, observableMatcher.getParent().getNotifier());
		
		viewer.setCellEditors(editors);
		
		String[] parameterNames = observableMatcher.getMatcher().getParameterNames();
		MatcherConfiguration[] input = new MatcherConfiguration[parameterNames.length];
		for (int i = 0;i<parameterNames.length;i++) {
			input[i] = new MatcherConfiguration(parameterNames[i], Integer.class, "");
		}
		
		viewer.setInput(input);
	}
	
	public static void clearTableViewerColumns(TableViewer viewer) {
				
		if (viewer.getContentProvider() != null) {
			viewer.setInput(null);
		}
		while (viewer.getTable().getColumnCount() > 0 ) {
			viewer.getTable().getColumns()[ 0 ].dispose();
		}
		
		viewer.refresh();
	}
	
	private static void createColumns(TableViewer viewer, String[] titles) {
		for (int i = 0;i<titles.length;i++) {
			createTableViewerColumn(viewer, titles[i], i);
		}
	}
	
	private static TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int index) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE, index);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		column.setWidth(200);
		return viewerColumn;
	}
}
