package peanuty.framework.base;

import org.jdom.Element;
import org.jdom.xpath.XPath;
import peanuty.framework.base.log.LogInterface;
import peanuty.framework.base.log.LogSimple;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base configuration infomation class which based on the file "WEB-INF/xml/config.xml"
 * <br/>
 * The file looked like this:
 * <br/>
 * &lt;config&gt;
 * <br/>
 * &lt;pageman rowsPerPage="16"/&gt;
 * <br/>
 * &lt;webserver type="tomcat" requestEncoding="UTF-8"/&gt;
 * <br/>
 * &lt;log classname="peanuty.framework.base.log.LogSimple"/&gt;
 * <br/>
 * &lt;/config&gt;
 */

public class Config {
    private Config() {
    }

    /**
     * To get the root path of the web application.
     *
     * @return The root path of the web application
     */
    synchronized public static String getWebAppPath() {
        if (webappPath == null) {
            URL url = Config.class.getResource("Config.class");
            String filePath = url.getFile();
            int beginPos = 0;
            if (filePath.startsWith("file:/")) {
                beginPos = 6;
            }
            webappPath = filePath.substring(beginPos, filePath.indexOf("/WEB-INF/"));
            if (getDebugMode()) {
                System.out.println("WEBAPP-PATH->" + webappPath);
            }
        }
        return webappPath;
    }

    /**
     * To get the context root string
     * <br/>
     * Defined at &lt;context-root&gt;/contextroot&lt;/context-root&gt; of "WEB-INF/xml/config.xml"
     * @return Context root string
     */
    synchronized public static String getContextRoot() {
        if (contextroot == null || getDebugMode()) {
            try {
                contextroot = getConfigElement().getChildText("context-root");
            }
            catch (Exception e) {
                //do nothing
            }
            if (contextroot == null || contextroot.trim().equals("")) {
                contextroot = "";
            }
        }
        return contextroot;
    }

    /**
     * To get the web server request encoding string
     * <br/>
     * Defined at &lt;webserver type="tomcat" requestEncoding="UTF-8"/&gt; of "WEB-INF/xml/config.xml"
     * @return the web server request encoding string
     */
    synchronized public static String getRequestEncoding() {
        if (re == null || getDebugMode()) {
            try {
                re = getConfigElement().getChild("webserver").getAttributeValue("requestEncoding");
            }
            catch (Exception e) {
                //do nothing
            }
            if(re == null || re.equals("")){
                re = "UTF-8";
            }
        }
        return re;
    }

    /**
     * Get default datasource string
     * <br/>Defined at &lt;dbconfig default="true" dataSource="jdbc/exampleDS" type="oracle" encoding="gbk" sequencyTable="t_sequency" sequencyCacheSize="20"/&gt; of "WEB-INF/xml/sql.xml"
     * @return default datasource string
     */
    synchronized public static String getDefaultDataSource() {
        if (ds == null || getDebugMode()) {
            ds = getDefaultDBElement().getAttributeValue("dataSource");
        }
        return ds;
    }

    private static Element sqlElement;
    synchronized static public Element getSqlElement(){
        if(sqlElement == null || getDebugMode()){
            sqlElement = XMLHandler.openXML("sql.xml");
        }
        return sqlElement;
    }

    private static Boolean sqlLog;
    synchronized public static boolean logSql(){
        if(sqlLog == null || getDebugMode()){
            String text = getSqlElement().getChildText("log");
            sqlLog = text != null && (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("on"));
        }
        return sqlLog;
    }

    synchronized private static Element getDefaultDBElement() {
        if (dbElement == null || getDebugMode()) {
            try {
                dbElement = (Element)XPath.selectSingleNode(getSqlElement(),"//dbconfig[@default='true']");
            }
            catch(Exception e){
                e.printStackTrace();
            }
                    //XMLHandler.getElementByAttribute(XMLHandler.openXML("sql.xml").getChildren("dbconfig"), "default", "true");
        }
        return dbElement;
    }

    /*
    synchronized public static String getPortalURL() {
        if (portalURL == null || getDebugMode()) {
            portalURL = getConfigElement().getChild("portal")
                    .getAttributeValue("url");
        }
        return portalURL;
    }
    */

    /*
    private final static String WEBSERVICEURI = "/services/ServerBean";

    synchronized public static String getWebServiceURL(String webserviceName) {
        if (wsList == null || getDebugMode()) {
            wsList = getConfigElement().getChild("webservices").getChildren("webservice");
        }
        return XMLHandler.getElementByAttribute(wsList, "name", webserviceName).getAttributeValue("url") + WEBSERVICEURI;
    }
    */

    /**
     * To get the web server type
     * <br/>Defined at &lt;webserver type="tomcat" requestEncoding="UTF-8"/&gt; of "WEB-INF/xml/config.xml"
     * @return the web server type
     */
    synchronized public static String getServerType() {
        if (serverType == null || getDebugMode()) {
            serverType = getConfigElement().getChild("webserver").getAttributeValue("type");
        }
        return serverType;
    }

    /**
     * Get Debug Mode
     * <br/>If debug mode is set to true, all configuration value (based on the xml files under the directory "WEB-INF/xml") changes
     * <br/>will become effective without rebooting application server, if the debug mode is false, the application will get a better performance.
     * <br/>The debug mode is defined at "debug=true[|false]" in the file "WEB-INF/classes/config.properties". Debug mode would be false if the file doesn't exist. 
     * @return Debug mode
     */
    synchronized public static boolean getDebugMode() {
        if (debug == null) {
            try {
                debug = Boolean.valueOf(ResourceBundle.getBundle("config").getString(
                        "debug"));
            } catch (Exception e) {
                debug = false;
            }
        }
        return debug;
    }

    /**
     * Get the rows per page which used by PageMan
     * <br/>
     * Defined at &lt;pageman rowsPerPage="16"/&gt; in "WEB-INF/xml/config.xml"
     * @return rows per page
     */
    synchronized public static int getPageManRowsPerPage() {
        try {
            if (pageManRowsPerPage == 0 || getDebugMode()) {
                pageManRowsPerPage = new Integer(getConfigElement().getChild(
                        "pageman").getAttributeValue("rowsPerPage"));
            }

        } catch (Exception e) {
            pageManRowsPerPage = 20;
        }
        return pageManRowsPerPage;
    }

    synchronized public static Element getConfigElement() {
        if (Config == null || getDebugMode()) {
            Config = XMLHandler.openXML("config.xml");
        }
        return Config;
    }

    /*
    synchronized public static Map getJMSConfig(String id) {
        if (getDebugMode() || JMSConfig == null || JMSConfig.get(id) == null) {
            if (JMSConfig == null) {
                JMSConfig = new HashMap();
            }
            JMSConfig.put(id, XMLHandler.getElementByAttribute(Config.getChildren("JMSConfig"), "id", id));
        }
        return XMLHandler.ElementToHash((Element) JMSConfig.get(id), null);
    }
    */

    /**
     * Get the instance of your own log class
     * @return the instance of your own log class
     */
    synchronized public static LogInterface getLogInstance() {
        if (li == null) {
            try {
                String logClassName = getConfigElement().getChild("log").getAttributeValue("className");
                li = (LogInterface) Class.forName(logClassName).newInstance();
            }
            catch (Exception e) {
                li = new LogSimple();
            }
        }
        return li;
    }

    private static String ds = null;

    private static String re = null;

    private static String contextroot = null;

    //private static String syscode = null;

    private static String webappPath = null;

    private static Boolean debug = null;

    private static Element Config;

    private static Element dbElement;

    //private static String portalURL = null;

    //private static List wsList = null;

    private static int pageManRowsPerPage = 0;

    private static String serverType = null;

    //private static Map JMSConfig;

    private static LogInterface li;
}
