package org.eclipse.viatra2.emf.incquery.base.exception;


public class IncQueryBaseException extends Exception
{
	
	private static final long serialVersionUID = -5145445047912938251L;
	
	public static String EMPTY_REF_LIST = "At least one EReference must be provided!";
	public static String INVALID_EMFROOT = "Emf navigation helper can only be attached on the contents of an EMF EObject, Resource, or ResourceSet.";
	
	public IncQueryBaseException(String s) {
		super(s);
	}

}
