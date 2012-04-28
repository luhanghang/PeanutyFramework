/**
 * 
 */
package peanuty.framework.base.message;

import peanuty.framework.base.*;

import java.util.*;
import javax.naming.*;
import javax.jms.*;

public class MessageSenderQueue extends BaseBean {

	private final static String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";

	private String JMS_FACTORY;

	private String QUEUE;

	private String URL;

	protected QueueConnectionFactory qconFactory;

	protected QueueConnection qcon;

	protected QueueSession qsession;

	protected QueueSender qsender;

	protected javax.jms.Queue queue;

	public MessageSenderQueue(String jmsFactory, String queue, String url) throws Exception{
		this.JMS_FACTORY = jmsFactory;
		this.QUEUE = queue;
		this.URL = url;
		init(getInitialContext(url), queue);
	}

	protected TextMessage msg;

	private void init(Context ctx, String queueName) throws NamingException,
			JMSException {

		qconFactory = (QueueConnectionFactory) ctx.lookup(JMS_FACTORY);
		qcon = qconFactory.createQueueConnection();
		qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = (javax.jms.Queue) ctx.lookup(queueName);
		qsender = qsession.createSender(queue);
		msg = qsession.createTextMessage();
		qcon.start();
	}

	private void sendMessage(String message) throws JMSException {
		try {
			InitialContext ctx = getInitialContext(URL);
			init(ctx, QUEUE);
			send(message);
		} catch (Exception ex) {
			throw new JMSException(ex.getMessage());
		} finally {
			close();
		}
	}

	public void send(String value) throws JMSException {
		msg.setText(value);
		qsender.send(msg);
	}

	public void close() throws JMSException {
		qsender.close();
		qsession.close();
		qcon.close();
	}

	private static InitialContext getInitialContext(String url)
			throws NamingException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, url);
		env.put("weblogic.jndi.createIntermediateContexts", Boolean.TRUE);
		return new InitialContext(env);
	}

	public static void main(String[] args) throws Exception {
		//InitialContext ic = getInitialContext(URL);
		MessageSenderQueue sender = new MessageSenderQueue(args[0],args[1],args[2]);
		//sender.init(ic, QUEUE);
		sender.send("test1");
		sender.send("test2");
		sender.send("test3");
		sender.send("test4");
		sender.send("test5");
		
		sender.close();
	}
}
