package peanuty.framework.base.message;

import peanuty.framework.base.*;

import java.util.*;
import javax.naming.*;
import javax.jms.*;

public class MessageSenderTopic extends BaseBean {
	public final static String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";

	public final static String JMS_FACTORY = "jms.mms.connectionFactory.Topic";

	public final static String TOPIC = "jms.mms.Topic";

	public final static String URL = "t3://202.108.205.248";

	protected TopicConnectionFactory tconFactory;

	protected TopicConnection tcon;

	protected TopicSession tsession;

	protected TopicPublisher tpublisher;

	protected Topic topic;

	protected TextMessage msg;

	public void init(Context ctx, String topicName) throws NamingException,
			JMSException {
		tconFactory = (TopicConnectionFactory) ctx.lookup(JMS_FACTORY);
		tcon = tconFactory.createTopicConnection();
		tsession = tcon.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		topic = (Topic) ctx.lookup(topicName);
		tpublisher = tsession.createPublisher(topic);
		msg = tsession.createTextMessage();
		tcon.start();
	}

	public void sendMessage(String message) throws JMSException {
		try {
			InitialContext ctx = getInitialContext(URL);
			init(ctx, TOPIC);
			send(message);
		} catch (Exception ex) {
			throw new JMSException(ex.getMessage());
		} finally {
			close();
		}
	}

	public void send(String value) throws JMSException {
		msg.setText(value);
		tpublisher.publish(msg);
	}

	public void close() throws JMSException {
		tpublisher.close();
		tsession.close();
		tcon.close();
	}

	protected static InitialContext getInitialContext(String url)
			throws NamingException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, url);
		env.put("weblogic.jndi.createIntermediateContexts", Boolean.TRUE);
		return new InitialContext(env);
	}

	public static void main(String[] args) throws Exception {
		InitialContext ic = getInitialContext(URL);
		MessageSenderTopic sender = new MessageSenderTopic();
		sender.init(ic, TOPIC);
		sender.send("test - TOPIC2");
		sender.close();
	}
}
