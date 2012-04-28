package peanuty.framework.base.message;

import javax.jms.*;
import javax.naming.*;
import java.util.*;

public class ReceiverQueue implements javax.jms.MessageListener, MessageReceiver {

	public static void main(String[] args) throws Exception {
		new ReceiverQueue("jms.mms.connectionFactory.Queue", "jms.mms.Queue","t3://localhost", null);
		while (true) {
			Thread.sleep(10000);
		}
	}
	
	private MessageReceiver mr;
	
	public ReceiverQueue(String factoryName, String QueueName, String url, MessageReceiver mr) throws Exception {
		this.mr = mr;
		InitialContext jndiContext = getInitialContext(url);
		QueueConnectionFactory factory = (QueueConnectionFactory) jndiContext
				.lookup(factoryName);

		javax.jms.Queue Queue = (javax.jms.Queue) jndiContext.lookup(QueueName);
		QueueConnection connect = factory.createQueueConnection();

		QueueSession session = connect.createQueueSession(false,
				Session.AUTO_ACKNOWLEDGE);
		QueueReceiver receiver = session.createReceiver(Queue);
		receiver.setMessageListener(this);

		connect.start();
		System.out.println("Queue Receiver Thread Started...");
	}

	public void onMessage(Message message) {
		this.mr.onMessageDo(message);
	}
	
	public Object onMessageDo(Message message){
		try {
			TextMessage textMsg = (TextMessage) message;
			String text = textMsg.getText();
			System.out.println("\n RESERVATION RECIEVED:\n" + text);
		} catch (JMSException jmsE) {
			jmsE.printStackTrace();
		}
		return null;
	}

	private static InitialContext getInitialContext(String url) throws Exception{
		Properties env = new Properties();
		//env.put(Context.SECURITY_PRINCIPAL, "guest");
		//env.put(Context.SECURITY_CREDENTIALS, "guest");
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"weblogic.jndi.WLInitialContextFactory");
		env.put(Context.PROVIDER_URL, url);
		return new InitialContext(env);
	}
}