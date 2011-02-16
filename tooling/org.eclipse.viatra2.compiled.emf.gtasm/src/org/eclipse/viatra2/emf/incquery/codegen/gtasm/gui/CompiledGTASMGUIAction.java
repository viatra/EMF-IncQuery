/**
 *
 */
package org.eclipse.viatra2.emf.incquery.codegen.gtasm.gui;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.codegen.gtasm.GTASMCompiler;
import org.eclipse.viatra2.emf.incquery.codegen.term.SerializedTerm;
import org.eclipse.viatra2.emf.incquery.codegen.term.TermEvaluator;
import org.eclipse.viatra2.frameworkgui.actions.AbstractFrameworkGUIAction;
import org.eclipse.viatra2.frameworkgui.content.transformation.TransformationContent.MachineDummy;
import org.eclipse.viatra2.frameworkgui.views.FrameworkTreeView;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.compoundRules.LetRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Machine;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.VariableDefinition;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.terms.Term;
/**
 * @author akinator
 *
 */
public class CompiledGTASMGUIAction extends AbstractFrameworkGUIAction{

	StringBuffer result;

	public CompiledGTASMGUIAction() {
		setText("Test Compiled Version");
		setToolTipText("Compiled Code generator for Viatra2");

	}


	public CompiledGTASMGUIAction(FrameworkTreeView part) {
		this();
		setupInternals(part);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}


	@Override
	public void run() {
		refreshSelection();
		try
		{
			Object c = getFirstSelected();
			if (c instanceof MachineDummy)
			{
				Machine m = ((MachineDummy)c).getMachine();
				Iterator<EObject> iter = m.getMainRule().eAllContents();
				while(iter.hasNext())
				{
					EObject obj = iter.next();
					if(obj instanceof LetRule){
						LetRule l = (LetRule)obj;
						for (Object variableDefinition : l.getDefinitions()) {
							Term t = ((VariableDefinition)variableDefinition).getValue();
							SerializedTerm st = TermEvaluator.evaluate(t,GTASMCompiler.getInstance().getUsedVariables());
							System.out.println(st.getTerm().toString());
							GTASMCompiler.getInstance().getUsedVariables().put(((VariableDefinition)variableDefinition).getVariable(),st.getType());
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			//iFT.showMessage(e.getMessage());
			e.printStackTrace();
		}
	}
}
