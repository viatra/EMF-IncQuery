package org.eclipse.viatra2.emf.incquery.matchsetviewer.observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.list.AbstractObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapter;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;

/**
 * The class is used to provide input for the tableviewer for a given PatternMatch.
 * All of the declared ObservableValues will be present in the table for the match.
 * The generated DatabindableMatcher class is used to get IObservableValues for the given parameters.
 * 
 * @author Tamas Szabo
 *
 */
public class DetailObservable extends AbstractObservableList {

	private PatternMatch patternMatch;
	private List<DetailElement> details;
	private ValueChangeListener listener;
	private Map<IObservableValue, DetailElement> valueMap;
	
	public DetailObservable(DatabindingAdapter<IPatternSignature> databindableMatcher, PatternMatch pm) {
		this.patternMatch = pm;
		this.details = new ArrayList<DetailElement>();
		this.valueMap = new HashMap<IObservableValue, DetailElement>();
		this.listener = new ValueChangeListener();
		for (String param : databindableMatcher.getParameterNames()) {
			IObservableValue ov = databindableMatcher.getObservableParameter(patternMatch.getSignature(), param);
			ov.addValueChangeListener(listener);
			Object value = ov.getValue();
			String data = "";
			if (value == null) {
				data = null;
			}
			else if (value instanceof Collection<?>) {
				data = "Collection";
			}
			else {
				data = value.toString();
			}
			
			DetailElement de = new DetailElement(param, data);
			addDetail(ov, de, -1);
		}
	}
	
	private void addDetail(IObservableValue ov, DetailElement de, int index) {
		if (index == -1) {
			this.details.add(de);
			this.valueMap.put(ov, de);
			fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(this.details.size(), true, de)));
		}
		else {
			this.details.add(index, de);
			this.valueMap.put(ov, de);
			fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(index, true, de)));
		}
	}
	
	private void removeDetail(IObservableValue ov, DetailElement de, int index) {
		this.details.remove(index);
		this.valueMap.remove(ov);
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(index, false, de)));
	}
	
	@Override
	public Object getElementType() {
		return DetailElement.class;
	}

	@Override
	protected int doGetSize() {
		return this.details.size();
	}

	@Override
	public Object get(int index) {
		return this.details.get(index);
	}
	
	/**
	 * Used to observ changes in the observed values. 
	 * 
	 * @author Tamas Szabo
	 *
	 */
	private class ValueChangeListener implements IValueChangeListener {

		@Override
		public void handleValueChange(ValueChangeEvent event) {
			IObservableValue ov = event.getObservableValue();
			Object value = ov.getValue();
			DetailElement de = valueMap.get(ov);
			int index = findElement(de);
			removeDetail(ov, de, index);
			DetailElement newDe = null;
			
			String data = "";
			if (value == null) {
				data = null;
			}
			else if (value instanceof Collection<?>) {
				data = "Collection";
			}
			else {
				data = value.toString();
			}

			newDe = new DetailElement(de.getKey(), data);
			
			addDetail(ov, newDe, index);
		}
		
	}

	/**
	 * Find a given element is the details list.
	 * 
	 * @param de element to be found
	 * @return the index of the element if it is present in the detials list, or -1 if not
	 */
	private int findElement(DetailElement de) {
		int i = 0;
		for (DetailElement e : details) {
			if (e.equals(de)) return i;
			i ++;
		}
		return -1;
	}
}
