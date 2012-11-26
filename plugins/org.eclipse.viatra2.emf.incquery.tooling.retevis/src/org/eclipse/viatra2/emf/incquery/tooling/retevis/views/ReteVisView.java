package org.eclipse.viatra2.emf.incquery.tooling.retevis.views;


import org.eclipse.gef4.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.tooling.retevis.theme.ColorTheme;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.ReteBoundary;

/**
 * 
 * @author istvanrath
 *
 */
public class ReteVisView extends ViewPart implements IZoomableWorkbenchPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.viatra2.emf.incquery.tooling.retevis.views.ReteVisView";

	private GraphViewer graphViewer;
    private ColorTheme theme;

	 @Override
	  public AbstractZoomableViewer getZoomableViewer() {
	    return graphViewer;
	  }
	
	/**
	 * The constructor.
	 */
	public ReteVisView() { }

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		// initialize Zest viewer
		graphViewer = new GraphViewer(parent, SWT.BORDER);
        graphViewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		graphViewer.setContentProvider(new ZestReteContentProvider());
		ZestReteLabelProvider labelProvider = new ZestReteLabelProvider();
        Display display = parent.getDisplay();
        theme = new ColorTheme(display);
        labelProvider.setColors(theme);
        graphViewer.setLabelProvider(labelProvider);	    
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		site.getPage().addSelectionListener(new ISelectionListener() {
			
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					Object o = sel.getFirstElement();
					if (o!=null && o instanceof ObservablePatternMatcher) {
						ObservablePatternMatcher pm = (ObservablePatternMatcher) o;
						//String patternFqn = pl.getFullPatternNamePrefix()+"."+pl.getPatternNameFragment();
						try {
							ReteBoundary rb = pm.getMatcher().getEngine().getReteEngine().getBoundary();
							((ZestReteLabelProvider)graphViewer.getLabelProvider()).setRb( rb );
							//graphViewer.setInput( pm.getMatcher().getEngine().getReteEngine().getBoundary() );
                            SugiyamaLayoutAlgorithm sugiyamaAlgorithm = new SugiyamaLayoutAlgorithm();
                            HorizontalShiftAlgorithm shiftAlgorithm = new HorizontalShiftAlgorithm();
                            graphViewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(new LayoutAlgorithm[] {
                                    sugiyamaAlgorithm, shiftAlgorithm }));
                            // graphViewer.setLayoutAlgorithm(new TreeLayoutAlgorithm());
							//graphViewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
							//graphViewer.setLayoutAlgorithm(new RadialLayoutAlgorithm());
							//graphViewer.setLayoutAlgorithm(new SpaceTreeLayoutAlgorithm());
                            graphViewer.setInput(rb.getHeadContainer());
                            // graphViewer.applyLayout();
                            // graphViewer.refresh();
						} catch (IncQueryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});	
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		// treeViewer.getControl().setFocus();
	}

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        if (theme != null) {
            theme.dispose();
        }
        super.dispose();
    }
	
	
	
}