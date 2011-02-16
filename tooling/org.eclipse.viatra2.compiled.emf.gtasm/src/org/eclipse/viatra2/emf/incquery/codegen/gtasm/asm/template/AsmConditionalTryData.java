/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.codegen.gtasm.asm.template;



/**
 * @author akinator
 *
 */
public class AsmConditionalTryData extends GTASMElementData {
	
	StringBuffer trueB, falseB;

	
	public AsmConditionalTryData(StringBuffer trueBranch, StringBuffer falseBranch) {
		super();
		trueB = trueBranch;
		falseB = falseBranch;
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
