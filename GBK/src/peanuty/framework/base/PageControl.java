package peanuty.framework.base;

import org.jdom.Element;
import peanuty.framework.util.FileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * replace of Servlet
 * <p/>
 * Title:
 * </p>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * Company: Tong Fang PC
 * </p>
 *
 * @author Lu Hang
 * @version 1.0
 */

public class PageControl extends BaseServlet {

    private final static String CONFIGFILE = "../PageControl.xml";
    private Element root;

    private Element getConfig() {
        if (root == null || Config.getDebugMode()) {
            root = XMLHandler.openXML(CONFIGFILE);
        }
        return root;
    }

    public void execute(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res) throws Throwable {

        //getHeaderDatas(req);
        req.setCharacterEncoding(Config.getRequestEncoding().toUpperCase());
        Element root = getConfig();
        String nextpage = root.getChildText("DefaultPage");
        String action = req.getParameter("action");
        String object = req.getParameter("object");
        boolean Forward = false; // forward or redirect
        // -------------------------------------------------------------------------------
        if (action != null && object != null) { //both action and object exist
            if (action.equalsIgnoreCase("download")
                    && object.equalsIgnoreCase("file")) { //download file
                startDownloadFile(req, res);
                return;
            }
            Element requiredObject = XMLHandler.getElementByAttribute(root
                    .getChildren("Object"), "Name", object);
            // -------------------------------------------------------------------------------
            if (requiredObject != null) { //object found in PageControl.xml
                Element requiredAction = XMLHandler.getElementByAttribute(
                        requiredObject.getChildren("Action"), "Operation",
                        action);
                // -------------------------------------------------------------------------------
                Forward = true;
                if (requiredAction != null
                        && checkPrivilege(getPrivileges(req), requiredAction,
                        req)) {
                    // action found and authentication passed
                    PageControlInterface bean = getBeanInstance(getBeanClassName(requiredObject));
                    //bean.setMe(bean);
                    if (getNextPageSuccess(req, requiredAction).trim().toLowerCase().equals("text")) {
                        res.setContentType("text/html; charset=" + Config.getRequestEncoding().toUpperCase());
                        //res.setHeader("Pragma","No-cache");
                        //res.setHeader("Cache-Control","no-cache");
                        //res.setDateHeader("Expires", 0);

                        java.io.PrintWriter out = res.getWriter();
                        out.println(bean.responseText(req));
                        out.close();
                        return;
                    }
                    if (getNextPageSuccess(req, requiredAction).trim().toLowerCase().equals("xml")) {
                        res.setContentType("text/xml; charset=" + Config.getRequestEncoding().toUpperCase());
                        //res.setHeader("Pragma","No-cache");
                        //res.setHeader("Cache-Control","no-cache");
                        //res.setDateHeader("Expires", 0);

                        java.io.PrintWriter out = res.getWriter();
                        out.println("<?xml version=\"1.0\" encoding=\"" + Config.getRequestEncoding() + "\"?>");
                        out.println(bean.responseText(req));
                        out.close();
                        return;
                    }
                    // -------------------------------------------------------------------------------
                    if (bean.perform(req,res)) { // invoke perform method success
                        writeEventLog(action, object, req.getSession(), true); //log
                        nextpage = getNextPageSuccess(req, requiredAction);
                        if (!getNextPageRedirectType(requiredAction)
                                .equalsIgnoreCase("forward")) {
                            Forward = false;
                        }

                        if (Forward
                                && getBeanScope(requiredAction)
                                .equalsIgnoreCase("request")) {
                            req.setAttribute(getInstanceName(requiredObject),
                                    bean);
                        } else if (getBeanScope(requiredAction)
                                .equalsIgnoreCase("session")) {
                            req.getSession().setAttribute(
                                    "session_"
                                            + getInstanceName(requiredObject),
                                    bean);
                        }
                    } else { // perform failure
                        writeEventLog(action, object, req.getSession(), false); //log
                        String failurePage = getNextPageFailure(req,
                                requiredAction);
                        if (!failurePage.equals("")) {
                            nextpage = failurePage;
                        }
                        Forward = false;
                    }
                    // -------------------------------------------------------------------------------
                }
            }
        }
        int b = nextpage.indexOf("{");
        if (b >= 0) {
            String para = nextpage.substring(b + 1, nextpage.indexOf("}"));
            String val = req.getParameter(para);
            if (null == val) {
                val = req.getAttribute(para) + "";
            }
            nextpage = nextpage.replaceFirst("\\x7b" + para + "\\x7d", val);
        }
        if (Forward) {
            try {
                forward(req, res, nextpage);
            } catch (Exception e) {
                if (Config.getDebugMode()) {
                    e.printStackTrace();
                }
                forward(req, res, root.getChildText("DefaultPage"));
            }
            return;
        }

        if (nextpage.startsWith("//")) {
            nextpage = nextpage.substring(1, nextpage.length()); // no context-path
        } else {
            nextpage = req.getContextPath() + nextpage; //context-path
        }
        res.sendRedirect(nextpage);
    }

    private String getBeanClassName(Element element) {
        return element.getAttributeValue("Class");
    }

    private String getInstanceName(Element element) {
        String Alias = element.getAttributeValue("Alias");
        if (Alias.equals("")) {
            Alias = element.getAttributeValue("Name");
        }
        return Alias;
    }

    private String getBeanScope(Element element) {
        return element.getAttributeValue("ClassScope");
    }

    private String getNextPage(javax.servlet.http.HttpServletRequest req,
                               Element element, String type) {
        String nextpage = element.getChild("NextPage").getAttributeValue(type);
        List paras = element.getChild("NextPage").getChildren("Parameter");
        String jsessionid = "";
        String Parameter = "?";
        for (Object paraObj : paras) {
            Element para = (Element) paraObj;
            if (para.getAttributeValue("For").equalsIgnoreCase(type) || para.getAttributeValue("For").equalsIgnoreCase("both")) {
                String name = para.getAttributeValue("Name");
                if (name.equals("jsessionid")) {
                    jsessionid += ";jsessionid=";
                    if (para.getAttributeValue("GetFromRequest").equalsIgnoreCase(
                            "true")) {
                        String p = req.getParameter(para
                                .getAttributeValue("Value"));
                        if (p == null) {
                            p = req.getAttribute(para.getAttributeValue("Value")) + "";
                        }
                        jsessionid += p;
                    } else {
                        jsessionid += para.getAttributeValue("Value");
                    }
                    nextpage += jsessionid;
                    continue;
                }
                Parameter += para.getAttributeValue("Name") + "=";
                if (para.getAttributeValue("GetFromRequest").equalsIgnoreCase(
                        "true")) {
                    String p = req.getParameter(para
                            .getAttributeValue("Value"));
                    if (p == null) {
                        p = req.getAttribute(para.getAttributeValue("Value")) + "";
                    }
                    Parameter += p;
                } else {
                    Parameter += para.getAttributeValue("Value");
                }
                Parameter += "&";
            }
        }
        if (!Parameter.equals("?")) { // has parameters
            nextpage += Parameter.substring(0, Parameter.length() - 1); // trim &
        }
        return nextpage;
    }

    private String getNextPageSuccess(
            javax.servlet.http.HttpServletRequest req, Element element) {
        return getNextPage(req, element, "Success");
    }

    private String getNextPageFailure(
            javax.servlet.http.HttpServletRequest req, Element element) {
        Element root = XMLHandler.openXML(CONFIGFILE);
        String nextpage = getNextPage(req, element, "Failure");
        if (nextpage.equals("")) {
            nextpage = root.getChildText("DefaultFailurePage");
        }
        return nextpage;
    }

    private String getNextPageRedirectType(Element element) {
        return element.getChild("NextPage").getAttributeValue("RedirectType");
    }

    private String getPrivilegeClass() {
        Element root = XMLHandler.openXML(CONFIGFILE);
        return root.getChildText("PrivilegeClass");
    }

    /**
     * get privileges
     *
     * @param req
     * @return
     */
    private PageControlInterface privilegeHelper;

    private String getPrivileges(javax.servlet.http.HttpServletRequest req)
            throws Throwable {
        if (privilegeHelper == null) {
            String classname = getPrivilegeClass();
            if (classname == null) {
                return "non-auth"; // no authentication
            }
            try {
                privilegeHelper = (PageControlInterface) Class.forName(
                        classname).newInstance();
            } catch (ClassNotFoundException e) {
                privilegeHelper = null;
                return "non-auth"; // no authentication
            }
        }
        return privilegeHelper.getPrivileges(req);
    }

    /*
    private String getRequiredPrivilege(Element el) {
        return el.getAttributeValue("Privilege");
    }
    */

    private boolean checkPrivilege(String privileges, Element action,
                                   HttpServletRequest req) {
        if (privileges.equalsIgnoreCase("non-auth")
                || privileges.equalsIgnoreCase(PageControlBase.ALLPRIVILEGE)) {
            return true;
        }
        String requiredPrivilege = action.getAttributeValue("Privilege");
        if (requiredPrivilege.equals("")) {
            return true;
        }
        String[] rps = requiredPrivilege.split(",");
        for (String rp : rps) {
            if (("," + privileges + ",").indexOf("," + rp + ",") >= 0) {
                return true;
            }
        }
        req.getSession().setAttribute(PageControlBase.MESSAGESTR, "Access Denied");
        return false;
    }

    private void startDownloadFile(javax.servlet.http.HttpServletRequest req,
                                   javax.servlet.http.HttpServletResponse res) throws Throwable {
        javax.servlet.http.HttpSession session = req.getSession();
        String filename = (String) session.getAttribute("filename");
        session.removeAttribute("filename");
        byte[] file = FileUpload.downloadfile(filename);
        filename = new String(filename.getBytes("GBK"), "8859_1");
        res.setHeader("content-disposition", "attachment;filename=\""
                + filename.substring(filename.lastIndexOf("/") + 1, filename
                .length()) + "\"");
        res.setContentType("application/octet-stream");
        res.setHeader("content-size", file.length + "");
        javax.servlet.ServletOutputStream out = res.getOutputStream();
        try {
            out.write(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (session.getAttribute("delete") != null) {
            FileUpload.deleteFile(filename);
        }
        try {
            res.flushBuffer();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        out.close();
    }

    /*
    private void getHeaderDatas(javax.servlet.http.HttpServletRequest req) {
        Enumeration names = req.getHeaderNames();
        this.log("----------------REQUEST HEADER BEGIN------------");
        for (; names.hasMoreElements();) {
            String name = (String) names.nextElement();
            this.log(name + ":" + req.getHeader(name));
        }
        this.log("----------------REQUEST HEADER END--------------");
    }
    */

    private PageControlInterface getBeanInstance(String className)
            throws Exception {
        //PageControlInterface instance = null;
        //synchronized (instancePool) {
        //instance = (PageControlInterface) instancePool.get(className);
        //if (instance == null) {
        return (PageControlInterface) Class.forName(className)
                .newInstance();
        //instancePool.put(className, instance);
        //}
        //}
        //return instance;
    }

    private EventLogInterface eventLogInterface;

    private void writeEventLog(String action, String object, HttpSession session, boolean result) {
        Element eventLogElement = root.getChild("EventLogInterface");
        if (eventLogElement == null) {
            this.log("No EventLogInterface");
            return;
        }
        String logFlag = eventLogElement.getAttributeValue("log");
        if (!logFlag.equalsIgnoreCase("true")) {
            this.log("EventLogInterface:false");
            return;
        }
        if (eventLogInterface == null) {
            String classname = eventLogElement.getAttributeValue("class");
            try {
                Class eventLogClass = Class.forName(classname);
                eventLogInterface = (EventLogInterface) eventLogClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                this.log("EventLogInterface Class:" + classname + " Not Found.");
                eventLogInterface = null;
                return;
            }
        }
        eventLogInterface.writeLog(action, object, session, result);
    }
}