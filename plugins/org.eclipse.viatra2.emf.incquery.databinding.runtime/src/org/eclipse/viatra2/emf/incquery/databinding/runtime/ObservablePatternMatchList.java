/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.databinding.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.list.AbstractObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;

/**
 * This observable list provides read-only access to 
 * the result set of an {@link IncQueryMatcher}. The elements of the list are
 * instances of {@link IPatternMatch}.<br>
 * <br>
 * This implementation registers a {@link DeltaMonitor} for the matcher
 * and updates its contents when the matcher's results change. This observable
 * list shall be disposed in order to unregister the delta monitor.<br>
 * <br>
 * This class can be used as-is, but clients may subclass it to override the
 * protected method "createCacheList".<br>
 * 
 * @author balazs.grill
 */
public class ObservablePatternMatchList extends AbstractObservableList {

	private final DeltaMonitor<? extends IPatternMatch> deltaMonitor;
	private final IncQueryMatcher<? extends IPatternMatch> matcher;
	private final List<IPatternMatch> cache;
	
	private final Runnable updateCallback = new Runnable() {
		
		@Override
		public void run() {
			update(deltaMonitor.matchFoundEvents, deltaMonitor.matchLostEvents);
			deltaMonitor.clear();
		}
	};

	private void update(final Collection<? extends IPatternMatch> additions, final Collection<? extends IPatternMatch> removals){
		synchronized (cache) {
			final List<ListDiffEntry> entries = new ArrayList<ListDiffEntry>(additions.size()+removals.size());

			for(final IPatternMatch remove : removals){
				final int index = cache.indexOf(remove);
				entries.add(Diffs.createListDiffEntry(index, false, remove));
				if (index != -1) cache.remove(index);
			}
			for(final IPatternMatch add : additions){
				final int index = cache.size();
				entries.add(Diffs.createListDiffEntry(index, true, add));
				cache.add(add);
			}
			fireListChange(new ListDiff() {

				@Override
				public ListDiffEntry[] getDifferences() {
					return entries.toArray(new ListDiffEntry[entries.size()]);
				}
			});
		}
	}
	
	/**
	 * Create an observable list of the results of the given matcher.
	 * 
	 * @param matcher The matcher to be watched by this observable list.
	 */
	public ObservablePatternMatchList(IncQueryMatcher<? extends IPatternMatch> matcher) {
		this.matcher = matcher;
		cache = createCacheList();
		deltaMonitor = matcher.newDeltaMonitor(true);
		matcher.addCallbackAfterUpdates(updateCallback);
		updateCallback.run();
	}

	@Override
	public synchronized void dispose() {
		matcher.removeCallbackAfterUpdates(updateCallback);
		super.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.observable.list.IObservableList#getElementType()
	 */
	@Override
	public Object getElementType() {
		return IPatternMatch.class;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.observable.list.AbstractObservableList#doGetSize()
	 */
	@Override
	protected int doGetSize() {
		synchronized (cache) {
			return cache.size();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public Object get(int arg0) {
		synchronized (cache) {
			return cache.get(arg0);
		}
	}

	/**
	 * Create a list instance for caching the current value set. Clients may
	 * override this method to provide the best performance for their use cases.
	 * The default implementation creates an {@link ArrayList}.
	 * 
	 * @return a list instance
	 */
	protected List<IPatternMatch> createCacheList(){
		return new ArrayList<IPatternMatch>();
	}
	
}
