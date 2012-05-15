package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

public class MatcherConfiguration {

	private String parameterName;
	private String clazz;
	private Object value;

	public MatcherConfiguration(String parameterName, String clazz,	Object value) {
		super();
		this.parameterName = parameterName;
		this.clazz = clazz;
		this.value = value;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
