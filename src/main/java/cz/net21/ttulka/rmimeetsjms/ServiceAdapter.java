package cz.net21.ttulka.rmimeetsjms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.net21.ttulka.rmimeetsjms.envelope.CallReply;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest.CallParameter;

/**
 * Class for invocation a method from the request
 * 
 * @author ttulka
 *
 */
class ServiceAdapter {
	
	protected static final Logger LOG = LoggerFactory.getLogger(ServiceAdapter.class);
	
	private final Object service;
	
	private final Method[] methods;
	
	public ServiceAdapter(final Object service) {
		super();
		this.service = service;
		
		methods = service.getClass().getMethods();
	}

	public <T> CallReply<T> callService(CallRequest request) {
		
		Method requestedMethod = null;
		
		for (Method m : methods) {
			// by the name
			if (m.getName().equals(request.getMethodName())) {
				// by the return type
				if (m.getReturnType().equals(request.getReturnType())
						|| (m.getReturnType().equals(Void.TYPE) && request.getReturnType().equals(Void.class))) {
					// by the parameters
					Class<?>[] params = m.getParameterTypes();
					if (params.length == request.getParameters().size()) {
						boolean paramsOk = true;
						int i = 0;
						for (CallParameter p : request.getParameters()) {
							Class<?> clazz = params[i ++];
							if (!clazz.equals(p.getClazz())) {
								paramsOk = false;
								break;
							}
						}
						// we have the winner!
						if (paramsOk) {
							requestedMethod = m;
							break;
						}
					}					
				}
			}
		}
		
		if (requestedMethod != null) {
			try {
				// prepare parameters
				Object[] params = new Object[request.getParameters().size()];
				for (int i = 0; i < request.getParameters().size(); i ++) {
					params[i] = request.getParameters().get(i).getValue();
				}
				// call the method
				@SuppressWarnings("unchecked")
				T res = (T)requestedMethod.invoke(service, params);
				
				// return as a reply
				return new CallReply<T>(res);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOG.error("Error occured while calling a service.", e);
			}
		}
		
		return null;
	}
}
