package peanuty.framework.base.message;

import javax.jms.*;

public interface MessageReceiver {
	public Object onMessageDo(Message message);
}
