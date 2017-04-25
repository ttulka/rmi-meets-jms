package cz.net21.ttulka.rmimeetsjms;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;

import cz.net21.ttulka.rmimeetsjms.envelope.CallReply;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest;

/**
 * Class responsible for calling a remote method via JMS
 * 
 * @author ttulka
 *
 */
public class RemoteServiceConsumer implements AutoCloseable {
		
	private final QueueConnection connection;
	private final QueueSession session;
	
	private final MessageProducer producer;	
	private final QueueRequestor requestor;
	
	private final Object serviceProxy;
	
	/**
	 * Constructor initializes the JMS connection
	 * 
	 * @param connectionFactory JMS connection factory
	 * @param queue	Destination queue
	 */
	public RemoteServiceConsumer(final QueueConnectionFactory connectionFactory, final Queue queue, 
			final Class<?> serviceClazz) throws JMSException {	
		
		connection = connectionFactory.createQueueConnection();
		
		boolean transacted = false;
		session = connection.createQueueSession(transacted, Session.AUTO_ACKNOWLEDGE);
				
		producer = session.createProducer(queue);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		requestor = new QueueRequestor(session, queue);
				
		connection.start();
		
		serviceProxy = Proxy.newProxyInstance(
				serviceClazz.getClassLoader(), 
				new Class[] { serviceClazz },
				new ServiceProxy(this)
		);
	}

	/**
	 * Closes all the JMS resources
	 */
	public void close() throws Exception {
		if (producer != null) {
			producer.close();
		}
		if (requestor != null) {
			requestor.close();
		}
		if (session != null) {
			session.close();
		}
		if (connection != null) {
			connection.close();
		}
	}
	
	/**
	 * Getter for the remove service proxy
	 * 
	 * @return remove service proxy
	 */
	public Object getService() {
		return serviceProxy;
	}
	
	/**
	 * Send an asynchronous request via JMS
	 *  
	 * @param encodedMsg encoded object to send
	 * @throws JMSException
	 */
	void request(CallRequest request) throws JMSException {
		ObjectMessage msg = session.createObjectMessage(request);
		producer.send(msg);
	}
	
	/**
	 * Send an synchronous request and wait for a reply via JMS
	 * 
	 * @param encodedMsg encoded object to send
	 * @return response object
	 * @throws JMSException
	 */
	@SuppressWarnings("unchecked")
	CallReply<Integer> requestReply(CallRequest request) throws JMSException {		
		ObjectMessage msg = session.createObjectMessage(request);
		
		Message reply = requestor.request(msg);
		
		if (reply instanceof ObjectMessage) {
			final ObjectMessage objectMessage = (ObjectMessage)reply;
			final Serializable obj = objectMessage.getObject();
			
			if (obj instanceof CallReply) {
				return (CallReply<Integer>)obj;
			} else {
				throw new IllegalArgumentException("Received message is not type of CallReply");
			}		    
		}
		else {
			throw new IllegalArgumentException("Wrong type of the response message was received.");
		}		
	}
}
