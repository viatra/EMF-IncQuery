/**
 * 
 */
package org.eclipse.viatra2.emf.incquery.codegen.gtasm.util;

/**
 * @author akinator
 *
 */
public class ViatraCompiledParameter {
	
	Object value;
	String name;
	
	
	public ViatraCompiledParameter(Object v, String n){
		name = n;
		value = v;
	}
	
	public ViatraCompiledParameter(Object v){
		value = v;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	

}
