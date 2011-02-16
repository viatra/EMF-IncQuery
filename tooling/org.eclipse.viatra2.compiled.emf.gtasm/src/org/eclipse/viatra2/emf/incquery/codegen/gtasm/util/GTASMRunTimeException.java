/**
 *
 */
package org.eclipse.viatra2.emf.incquery.codegen.gtasm.util;

/**
 * @author akinator
 *
 */
public class GTASMRunTimeException extends Exception {

	private static final long serialVersionUID = 2886035279240134284L;
	public static String PARAM_NOT_SUITABLE_WITH_NO = "The type of the parameters are not suitable for the operation! Parameter number: ";
	public static String CONVERSION_FAILED = "Can mot convert the term to the designated type";
	public static String CONVERT_NULL_PARAMETER = "Can not convert null to the designated type";
	public static String RELATIONAL_PARAM_UNSUITABLE = "The parameters are not acceptable by the operation";



	public GTASMRunTimeException(String s) {
		super(s);
	}

}
