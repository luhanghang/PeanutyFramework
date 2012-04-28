package peanuty.framework.base;

import javax.servlet.http.*;

public interface EventLogInterface {
	/**
	 * 
	 * @param action Action for log
	 * @param object Object for log
	 * @param session Http Session
	 * @param result action result
	 * @return the result
	 */
	public boolean writeLog(String action, String object, HttpSession session, boolean result);
}
