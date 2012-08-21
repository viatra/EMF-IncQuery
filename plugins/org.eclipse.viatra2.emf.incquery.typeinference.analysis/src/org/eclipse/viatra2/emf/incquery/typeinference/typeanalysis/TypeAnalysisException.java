package org.eclipse.viatra2.emf.incquery.typeinference.typeanalysis;

import java.util.Collection;

public class TypeAnalysisException extends Exception {
	private static final long serialVersionUID = -1740770198384457774L;

	private Collection<?> resoultSet;
	
	public TypeAnalysisException(String message) {
		super(message);
	}
	
	public TypeAnalysisException(String message, Collection<?> resultSet) {
		this(message);
		this.resoultSet = resultSet;
	}

	public Collection<?> getResoultSet() {
		return resoultSet;
	}
}
