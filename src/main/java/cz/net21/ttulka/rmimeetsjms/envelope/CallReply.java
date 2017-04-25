package cz.net21.ttulka.rmimeetsjms.envelope;

import java.io.Serializable;

/**
 * 
 * @author ttulka
 *
 */
public class CallReply<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private T response;
	
	public CallReply() {
		super();
	}
	
	public CallReply(final T response) {
		this();
		this.response = response;
	}
	
	public T getResponse() {
		return response;
	}
	public void setResponse(T response) {
		this.response = response;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((response == null) ? 0 : response.hashCode());
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
		CallReply<?> other = (CallReply<?>) obj;
		if (response == null) {
			if (other.response != null)
				return false;
		} else if (!response.equals(other.response))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CallReply [response=" + response + "]";
	}
}
