package org.eclipse.incquery.tooling.ui.retevis.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.gef4.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.rete.boundary.ReteBoundary;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.PConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.index.Indexer;
import org.eclipse.incquery.runtime.rete.index.IndexerWithMemory;
import org.eclipse.incquery.runtime.rete.matcher.RetePatternMatcher;
import org.eclipse.incquery.runtime.rete.misc.ConstantNode;
import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.runtime.rete.network.Production;
import org.eclipse.incquery.runtime.rete.remote.Address;
import org.eclipse.incquery.runtime.rete.single.UniquenessEnforcerNode;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.tooling.ui.retevis.theme.ColorTheme;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;

public class ZestReteLabelProvider extends LabelProvider implements IEntityStyleProvider {

    private final int INDEXER_ID = 0;
    private final int RETEMATCHER_ID = 1;
    private final int INPUT_ID = 2;

    private ReteBoundary rb;
    private ColorTheme theme;

    /**
     * Sets the colors of the indexer and rete matcher nodes
     * 
     * @param indexerColor
     * @param reteMatcherColor
     */
    public void setColors(ColorTheme theme) {
        this.theme = theme;

    }

    public ReteBoundary getRb() {
        return rb;
    }

    public void setRb(ReteBoundary rb) {
        this.rb = rb;
        // initialize reverse traceability information
        resetReverseMap();
        for (Object _o : rb.getAllProductionNodes()) {
            Node productionNode = rb.getHeadContainer().resolveLocal((Address<?>) _o);
            if (productionNode != null && productionNode instanceof Production) {
                initalizeReverseMap((Production) productionNode);
            }
        }
    }

    @Override
    public String getText(Object element) {
        if (element instanceof Node) {
            Node n = (Node) element;
            Class<?> namedClass = n.getClass();
            String simpleName;
            do {
                simpleName = namedClass.getSimpleName();
                namedClass = namedClass.getSuperclass();
            } while (simpleName == null || simpleName.isEmpty());
            String s = "" + simpleName;
            if (n instanceof UniquenessEnforcerNode) {
                // print tuplememory statistics
                UniquenessEnforcerNode un = (UniquenessEnforcerNode) n;

                if (un.getParents().isEmpty() && un.getTag() instanceof ENamedElement) {
                    s += " : " + ((ENamedElement) un.getTag()).getName() + " : ";

                }
                s += " [" + (un).getMemory().size() + "]";

            }
            if (n instanceof IndexerWithMemory) {
                s += "[" + ((IndexerWithMemory) n).getMemory().getSize() + "]";
            }
            if (!(n instanceof UniquenessEnforcerNode || n instanceof ConstantNode)) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n");
                for (Stub st : getStubsForNode(n)) {
                    sb.append("<");
                    Tuple variablesTuple = st.getVariablesTuple();
                    for (Object obj : variablesTuple.getElements()) {
                        if (obj instanceof PVariable) {
                            Object nameObj = ((PVariable) obj).getName();
                            if (nameObj instanceof Variable) {
                                sb.append(((Variable) nameObj).getName());
                            }
                        }
                        sb.append("; ");
                    }
                    sb.append(">  ");
                    s += sb.toString();
                }
            }
            return s;
        }
        return "!";
        // return s+super.getText(element);
    }

    @Override
    public IFigure getTooltip(Object entity) {
        if (entity instanceof Node) {
            Node n = (Node) entity;
            String s = "";

            for (Stub st : getStubsForNode(n)) {
                s += getEnforcedConstraints(st);
            }

            FlowPage fp = new FlowPage();

            TextFlow nameTf = new TextFlow();
            // nameTf.setFont(fontRegistry.get("default"));
            TextFlow infoTf = new TextFlow();
            // infoTf.setFont(fontRegistry.get("code"));

            nameTf.setText(n.toString());
            String info = "";// "\n";
            info += "Stubs:\n" + s;// +"\n";
            infoTf.setText(info);
            if (entity instanceof RetePatternMatcher) {
                if (((Node) entity).getTag() instanceof Pattern) {
                    Pattern pattern = (Pattern) ((Node) entity).getTag();
                    nameTf.setText(pattern.getName());
                    fp.add(nameTf);
                }
            } else if (entity instanceof ConstantNode) {
                ConstantNode node = (ConstantNode) entity;
                ArrayList<Tuple> arrayList = new ArrayList<Tuple>();
                node.pullInto(arrayList);
                StringBuilder sb = new StringBuilder();
                for (Tuple tuple : arrayList) {
                    sb.append(tuple.toString() + "\n");
                }
                nameTf.setText(sb.toString());
                fp.add(nameTf);
            }
            fp.add(infoTf);
            return fp;
        }
        return null;
    }

    // useful only for production nodes
    private static String getEnforcedConstraints(Stub st) {
        String s = "";
        for (Object _pc : st.getAllEnforcedConstraints()) {
            PConstraint pc = (PConstraint) _pc;
            s += "\t[" + pc.getClass().getSimpleName() + "]:";
            for (Object _v : pc.getAffectedVariables()) {
                PVariable v = (PVariable) _v;
                s += "{" + v.getName() + "}";
            }
            s += "\n";
        }
        return s;
    }

    private Collection<Stub<Address<?>>> getStubsForNode(Node n) {
        Collection<Stub<Address<?>>> r = reverseMap.get(n);
        if (r != null)
            return r;
        else
            return Collections.EMPTY_SET;
    }

    Map<Node, Collection<Stub<Address<?>>>> reverseMap;// = new HashMap<Node, Collection<Stub<Address<?>>>>();

    private void resetReverseMap() {
        reverseMap = new HashMap<Node, Collection<Stub<Address<?>>>>();
    }

    private void initalizeReverseMap(Production prod) {
        for (Object _stubOfProd : rb.getParentStubsOfReceiver(new Address<Node>(prod))) {
            Stub stubOfProd = (Stub) _stubOfProd;
            for (Stub<Address<?>> s : getAllParentStubs(stubOfProd)) {
                Address<Node> address = (Address<Node>) s.getHandle();
                Node n = rb.getHeadContainer().resolveLocal(address);
                Collection<Stub<Address<?>>> t = reverseMap.get(n);
                if (t == null) {
                    t = new HashSet<Stub<Address<?>>>();
                }
                t.add(s);
                reverseMap.put(n, t);
            }
        }
    }

    private static Collection<Stub<Address<?>>> getAllParentStubs(Stub<Address<?>> st) {
        if (st != null) {
            Vector<Stub<Address<?>>> v = new Vector<Stub<Address<?>>>();
            v.add(st);
            v.addAll(getAllParentStubs(st.getPrimaryParentStub()));
            v.addAll(getAllParentStubs(st.getSecondaryParentStub()));
            return v;
        } else
            return Collections.EMPTY_LIST;
    }

    @Override
    public Color getNodeHighlightColor(Object entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getBorderColor(Object entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getBorderHighlightColor(Object entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getBorderWidth(Object entity) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Color getBackgroundColour(Object entity) {
        if (entity instanceof Indexer) {
            return theme.getNodeColor(INDEXER_ID);
        } else if (entity instanceof RetePatternMatcher) {
            return theme.getNodeColor(RETEMATCHER_ID);
        } else if (entity instanceof UniquenessEnforcerNode) {
            UniquenessEnforcerNode inputNode = (UniquenessEnforcerNode) entity;
            if (inputNode.getParents().isEmpty()) {
                return theme.getNodeColor(INPUT_ID);
            }
        }
        return null;
    }

    @Override
    public Color getForegroundColour(Object entity) {
        if (entity instanceof Indexer) {
            return theme.getTextColor(INDEXER_ID);
        } else if (entity instanceof RetePatternMatcher) {
            return theme.getTextColor(RETEMATCHER_ID);
        } else if (entity instanceof UniquenessEnforcerNode) {
            UniquenessEnforcerNode inputNode = (UniquenessEnforcerNode) entity;
            if (inputNode.getParents().isEmpty()) {
                return theme.getTextColor(INPUT_ID);
            }
        }
        return null;
    }

    @Override
    public boolean fisheyeNode(Object entity) {
        // TODO Auto-generated method stub
        return true;
    }

}
