package cz.net21.ttulka.rmimeetsjms.envelope;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author ttulka
 *
 */
public class CallRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String methodName;
	
	private Class<?> returnType;
	
	private List<CallParameter> parameters = new LinkedList<>();
	
	public static class CallParameter implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private Class<?> clazz;
		private Object value;
		
		public CallParameter() {
			super();
		}
		
		public <T> CallParameter(Class<T> clazz, T value) {
			this();
			this.clazz = clazz;
			this.value = value;
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CallParameter other = (CallParameter) obj;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "CallParameter [clazz=" + clazz + ", value=" + value + "]";
		}
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public List<CallParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<CallParameter> parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(CallParameter parameter) {
		this.parameters.add(parameter);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CallRequest other = (CallRequest) obj;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CallRequest [methodName=" + methodName + ", returnType=" + returnType + ", parameters=" + parameters + "]";
	}
}
