package peanuty.framework.base.message;

import javax.jms.*;
import javax.naming.*;
import java.util.*;

public class ReceiverTopic implements javax.jms.MessageListener {

	public static void main(String[] args) throws Exception {

		new ReceiverTopic("jms.mms.connectionFactory.Topic", "jms.mms.Topic");
		while (true) {
			Thread.sleep(10000);
		}
	}

	public ReceiverTopic(String factoryName, String topicName) throws Exception {

		InitialContext jndiContext = getInitialContext();
		TopicConnectionFactory factory = (TopicConnectionFactory) jndiContext
				.lookup(factoryName);

		Topic topic = (Topic) jndiContext.lookup(topicName);
		TopicConnection connect = factory.createTopicConnection();

		TopicSession session = connect.createTopicSession(false,
				Session.AUTO_ACKNOWLEDGE);
		TopicSubscriber subscriber = session.createSubscriber(topic);
		subscriber.setMessageListener(this);

		connect.start();
		System.out.println("Receiver Thread Started...");
	}

	public void onMessage(Message message) {
		try {
			TextMessage textMsg = (TextMessage) message;
			String text = textMsg.getText();
			System.out.println("\n RESERVATION RECIEVED:\n" + text);
		} catch (JMSException jmsE) {
			jmsE.printStackTrace();
		}
	}

	public static InitialContext getInitialContext() throws Exception{
		Properties env = new Properties();
		//env.put(Context.SECURITY_PRINCIPAL, "guest");
		//env.put(Context.SECURITY_CREDENTIALS, "guest");
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"weblogic.jndi.WLInitialContextFactory");
		env.put(Context.PROVIDER_URL, "t3://202.108.205.248:7001");
		return new InitialContext(env);
	}
}