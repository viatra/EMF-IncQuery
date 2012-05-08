package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

public class MatcherConfiguration {

	private String parameterName;
	private Class<?> clazz;
	private Object value;

	public MatcherConfiguration(String parameterName, Class<?> clazz,
			Object value) {
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

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
