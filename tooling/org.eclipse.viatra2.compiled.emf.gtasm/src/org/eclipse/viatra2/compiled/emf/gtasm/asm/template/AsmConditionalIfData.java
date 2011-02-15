/**
 * 
 */
package org.eclipse.viatra2.compiled.emf.gtasm.asm.template;

import org.eclipse.viatra2.compiled.emf.term.SerializedTerm;


/**
 * @author akinator
 *
 */
public class AsmConditionalIfData extends GTASMElementData{
	
	SerializedTerm condition;
	StringBuffer trueB, falseB;
	
	
	public AsmConditionalIfData(SerializedTerm term, StringBuffer falseBranch, StringBuffer trueBranch) {
		super();
		condition = term;
		trueB = trueBranch;
		falseB = falseBranch;
	}
	
	/**
	 * @return the condition
	 */
	public SerializedTerm getCondition() {
		return condition;
	}
	/**
	 * @param condition the condition to set
	 */
	public void setCondition(SerializedTerm condition) {
		this.condition = condition;
	}
	/**
	 * @return the falseB
	 */
	public StringBuffer getFalseB() {
		return falseB;
	}
	/**
	 * @param falseB the falseB to set
	 */
	public void setFalseB(StringBuffer falseB) {
		this.falseB = falseB;
	}
	/**
	 * @return the trueB
	 */
	public StringBuffer getTrueB() {
		return trueB;
	}
	/**
	 * @param trueB the trueB to set
	 */
	public void setTrueB(StringBuffer trueB) {
		this.trueB = trueB;
	}
	
	
	
	

}
