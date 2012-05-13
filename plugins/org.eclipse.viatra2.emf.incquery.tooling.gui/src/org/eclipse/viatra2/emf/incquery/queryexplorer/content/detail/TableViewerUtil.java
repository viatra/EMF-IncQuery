package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import java.util.HashSet;
import java.util.Set;

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
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("restriction")
@Singleton
public class TableViewerUtil {
	
	@Inject
	ITypeProvider typeProvider;
	
	private static Set<String> primitiveTypes = new HashSet<String>();
	
	public TableViewerUtil() {
		primitiveTypes.add(Boolean.class.getName());
		primitiveTypes.add(Character.class.getName());
		primitiveTypes.add(Byte.class.getName());
		primitiveTypes.add(Short.class.getName());
		primitiveTypes.add(Integer.class.getName());
		primitiveTypes.add(Long.class.getName());
		primitiveTypes.add(Float.class.getName());
		primitiveTypes.add(Double.class.getName());
		primitiveTypes.add(Void.class.getName());
		primitiveTypes.add(String.class.getName());
	}
	
	public static boolean isPrimitiveType(String fqn) {
		return primitiveTypes.contains(fqn);
	}

	public void prepareTableViewerForObservableInput(ObservablePatternMatch match, TableViewer viewer) {
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
	
	public void prepareTableViewerForMatcherConfiguration(ObservablePatternMatcher observableMatcher, TableViewer viewer) {
		clearTableViewerColumns(viewer);
		String[] titles = { "Parameter", "Class", "Value" };
		createColumns(viewer, titles);
		viewer.setUseHashlookup(true);
		viewer.setColumnProperties(titles);
		viewer.setContentProvider(new MatcherConfigurationContentProvider());
		viewer.setLabelProvider(new MatcherConfigurationLabelProvider());
		viewer.setCellModifier(new MatcherConfigurationCellModifier(viewer));
		
		Table table = viewer.getTable();
		CellEditor[] editors = new CellEditor[titles.length];

		editors[0] = new TextCellEditor(table);
		editors[1] = new TextCellEditor(table);
		editors[2] = new ModelElementCellEditor(table, observableMatcher);
		
		viewer.setCellEditors(editors);
		
		Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(observableMatcher.getPatternName());
		MatcherConfiguration[] input = new MatcherConfiguration[pattern.getParameters().size()];
		for (int i = 0;i<pattern.getParameters().size();i++) {
			Variable var = pattern.getParameters().get(i);
			String name = var.getName();
			JvmTypeReference ref = typeProvider.getTypeForIdentifiable(var);
			String clazz = ref.getType().getQualifiedName();
			input[i] = new MatcherConfiguration(name, clazz, "");
		}	
		
		viewer.setInput(input);
	}
	
	public void clearTableViewerColumns(TableViewer viewer) {
				
		if (viewer.getContentProvider() != null) {
			viewer.setInput(null);
		}
		while (viewer.getTable().getColumnCount() > 0 ) {
			viewer.getTable().getColumns()[ 0 ].dispose();
		}
		
		viewer.refresh();
	}
	
	private void createColumns(TableViewer viewer, String[] titles) {
		for (int i = 0;i<titles.length;i++) {
			createTableViewerColumn(viewer, titles[i], i);
		}
	}
	
	private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int index) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE, index);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		column.setWidth(200);
		return viewerColumn;
	}
	
	public static Object createValue(String classFqn, String value) {
		classFqn = classFqn.toLowerCase();
		
		if (value.matches("")) {
			return null;
		}
		else if (Boolean.class.getName().toLowerCase().matches(classFqn)) {
			return new Boolean(value.toLowerCase());
		}
		else if (Character.class.getName().toLowerCase().matches(classFqn)) {
			return new Character(value.charAt(0));
		}
		else if (Byte.class.getName().toLowerCase().matches(classFqn)) {
			return new Byte(value);
		}
		else if (Short.class.getName().toLowerCase().matches(classFqn)) {
			return new Short(value);
		}
		else if (Integer.class.getName().toLowerCase().matches(classFqn)) {
			return new Integer(value);
		}
		else if (Long.class.getName().toLowerCase().matches(classFqn)) {
			return new Long(value);
		}
		else if (Float.class.getName().toLowerCase().matches(classFqn)) {
			return new Float(value);
		}
		else if (Double.class.getName().toLowerCase().matches(classFqn)) {
			return new Double(value);
		}
		else if (String.class.getName().toLowerCase().matches(classFqn)) {
			return value;
		}
		else {
			return null;
		}
	}
	
	public static boolean isValidValue(String classFqn, String value) {
		classFqn = classFqn.toLowerCase();
		
		if (Boolean.class.getName().toLowerCase().matches(classFqn)) {
			if (value.toLowerCase().matches("true") || value.toLowerCase().matches("false")) {
				return true;
			}
			else {
				return false;
			}
		}
		else if (Character.class.getName().toLowerCase().matches(classFqn)) {
			return true;
		}
		else if (Byte.class.getName().toLowerCase().matches(classFqn)) {
			return value.matches("[0-9]*");
		}
		else if (Short.class.getName().toLowerCase().matches(classFqn)) {
			return value.matches("[0-9]*");
		}
		else if (Integer.class.getName().toLowerCase().matches(classFqn)) {
			return value.matches("[0-9]*");
		}
		else if (Long.class.getName().toLowerCase().matches(classFqn)) {
			return value.matches("[0-9]*");
		}
		else if (Float.class.getName().toLowerCase().matches(classFqn)) {
			return value.matches("[0-9]*\\.[0-9]*");
		}
		else if (Double.class.getName().toLowerCase().matches(classFqn)) {
			return value.matches("[0-9]*\\.?[0-9]*");
		}
		else if (String.class.getName().toLowerCase().matches(classFqn)) {
			return true;
		}
		else {
			return true;
		}
	}
}
