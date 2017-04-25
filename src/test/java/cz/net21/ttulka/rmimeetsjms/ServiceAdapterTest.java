package cz.net21.ttulka.rmimeetsjms;

import org.junit.Assert;
import org.junit.Test;

import cz.net21.ttulka.rmimeetsjms.ServiceAdapter;
import cz.net21.ttulka.rmimeetsjms.envelope.CallReply;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest.CallParameter;

/**
 * Test for the ServiceAdapter
 * 
 * @author ttulka
 *
 */
public class ServiceAdapterTest {
	
	private final TestService service = new TestServiceImpl(); 
	private final ServiceAdapter adapter = new ServiceAdapter(service); 

	@Test
	public void callService1() {
		final CallRequest req = new CallRequest();
		req.setReturnType(Integer.class);
		req.setMethodName("func1");
		final Integer value = 1;
		req.addParameter(new CallParameter(Integer.class, value));
		
		CallReply<Integer> reply = adapter.callService(req);		
		Assert.assertNotNull(reply);
		
		Integer response = reply.getResponse();		
		Assert.assertNotNull(response);
		
		Integer expected = service.func1(value);
		
		Assert.assertEquals(expected, response);
	}
	
	@Test
	public void callService2() {
		final CallRequest req = new CallRequest();
		req.setReturnType(Void.TYPE);
		req.setMethodName("func2");
		final String value = "abc";
		req.addParameter(new CallParameter(String.class, value));
		
		CallReply<Integer> reply = adapter.callService(req);
		Integer response = reply.getResponse();
		
		Assert.assertNull(response);
	}
}

interface TestService {
	Integer func1(Integer i);
	void func2(String str);
}

class TestServiceImpl implements TestService {
	@Override
	public Integer func1(Integer i) {
		if (i != null) {
			return i + 1;
		}
		return 0;
	}
	@Override
	public void func2(String str) {
		System.out.println("func2: " + str);
	}	
}
