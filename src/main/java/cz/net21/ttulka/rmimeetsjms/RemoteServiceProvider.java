package cz.net21.ttulka.rmimeetsjms;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import cz.net21.ttulka.rmimeetsjms.envelope.CallReply;
import cz.net21.ttulka.rmimeetsjms.envelope.CallRequest;

/**
 * Class responsible for invoking and replying a remote method via JMS
 * 
 * @author ttulka
 *
 */
public class RemoteServiceProvider implements AutoCloseable {
	
	protected static final Logger LOG = LoggerFactory.getLogger(RemoteServiceProvider.class);
	
	private final ServiceAdapter serviceAdapter;
	
	private final Connection connection;
	private final Session session;
	
	private final MessageConsumer consumer;
	
	/**
	 * Constructor initializes the JMS connection
	 * 
	 * @param connectionFactory
	 * @param queue
	 * @throws JMSException
	 */
	public RemoteServiceProvider(final ConnectionFactory connectionFactory, final Destination queue,
			final Object serviceImpl) throws JMSException {
		serviceAdapter = new ServiceAdapter(serviceImpl);
		
		connection = connectionFactory.createConnection();
		
		boolean transacted = false;
		session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
				
		consumer = session.createConsumer(queue);
		MessageListener listener = new Replier();
		consumer.setMessageListener(listener);
				
		connection.start();
	}

	/**
	 * Closes all the JMS resources
	 */
	public void close() throws Exception {
		if (consumer != null) {
			consumer.close();
		}
		if (session != null) {
			session.close();
		}
		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * Implementation of the message listener
	 */
	private class Replier implements MessageListener {
		
		public void onMessage(Message message) {
			try {
				if (message instanceof ObjectMessage) {
					final ObjectMessage objectMessage = (ObjectMessage)message;
					final Serializable obj = objectMessage.getObject();
					
					if (obj instanceof CallRequest) {					
						final CallRequest request = (CallRequest)obj;
						
						if (message.getJMSReplyTo() != null) {
							
							final CallReply<?> reply = serviceAdapter.callService(request);
							
							Destination replyDestination = message.getJMSReplyTo();
							MessageProducer replyProducer = session.createProducer(replyDestination);
	
							ObjectMessage replyMessage = session.createObjectMessage(reply);
							replyMessage.setJMSCorrelationID(message.getJMSMessageID());
							replyProducer.send(replyMessage);						
						} 
						else {
							serviceAdapter.callService(request);
						}	
					} else {
						throw new IllegalArgumentException("Received message is not type of CallReply");
					}
				}
			} catch (JMSException e) {
				LOG.error("Error occured while consuming a message.", e);
				throw new RuntimeException(e);
			}
		}
	}
}
