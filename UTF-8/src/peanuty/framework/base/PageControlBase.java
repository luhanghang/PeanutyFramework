package peanuty.framework.base;

import peanuty.framework.util.DataConvert;
import peanuty.framework.util.DataFilter;
import peanuty.framework.util.Message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * PageControl Foundation Class
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Tong Fang PC</p>
 * @author Lu Hang
 * @version 1.0
 */
public abstract class PageControlBase
        extends BaseBean
    implements PageControlInterface {

    public PageControlBase() {
        //init();
    }

    public final static String LOGINUSERSTR = "loginUser";
    public final static String PRIVILEGESTR = "privilege";
    public final static String MESSAGESTR = "message";
    public final static String ALLPRIVILEGE = "all";

    protected HttpServletRequest req;
    protected Map<String,String> _reqH = new HashMap<String,String>();
    private String _action;
    //private Object _me;

    public final static String TRUE = "true";
    public final static String FALSE = "false";
    protected HttpServletResponse res;

    public boolean perform(HttpServletRequest Req, HttpServletResponse res) {
        this.res = res;
        initReq(Req);
        this._action = _("action");
        boolean result = false;
        try {
            result = ( (Boolean) this.getClass().getMethod(_action).invoke(this));
        }
        catch (NoSuchMethodException e) {
            return true;
        }
        catch (ClassCastException e){
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String responseText(HttpServletRequest Req){
    	initReq(Req);
        this._action = _("action");
        try {
            return (String) this.getClass().getMethod(this._action).invoke(this);
        }
        catch (NoSuchMethodException e) {
            return "no such method:" + this._action;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }

    public void initReq(HttpServletRequest Req){
    	 this.req = Req;
         this._reqH.putAll(DataConvert.ReqToHash(req));
    }

    public void setReqH(Map<String,String> reqH){
    	this._reqH = reqH;
    }

    //protected abstract void init();

//    public void setMe(Object me) {
//        this._me = me;
//    }

    protected String _(String tag) {
        return DataFilter.show(this._reqH, tag);
    }

    protected String toDB(String tag) {
        return DataFilter.showToDB(this._reqH, tag);
    }

    public String getPrivileges(javax.servlet.http.HttpServletRequest Req) {
    	Map loginuser = (Map) Req.getSession().getAttribute(LOGINUSERSTR);
		if (loginuser == null || loginuser.get(PRIVILEGESTR) == null) {
			return "nonloginuser";
		}
		return loginuser.get(PRIVILEGESTR).toString();
    }

    protected void setReturnMessage(String messageName) {
        this.req.getSession().setAttribute(MESSAGESTR, Message.getMessage(messageName));
    }

    protected void setReturnMessage(String messageName, String replaceStr) {
        this.req.getSession().setAttribute(MESSAGESTR,
                                      Message.getMessage(messageName, replaceStr));
    }

    protected void setReturnMessage(String messageName, Collection replaceStrs) {
        this.req.getSession().setAttribute(MESSAGESTR,
                                      Message.getMessage(messageName, replaceStrs));
    }

    protected Map getLoginuser() {
        return (Map) this.req.getSession().getAttribute(LOGINUSERSTR);
    }
}