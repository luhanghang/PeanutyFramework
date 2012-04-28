package peanuty.framework.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.jdom.*;
import peanuty.framework.base.*;

public class BaseTagLib extends TagSupport {

    private Element root = XMLHandler.openXML("tagstyle.xml");

    protected String getContextRoot(PageContext pc) {
        return Config.getContextRoot();
    }

    protected String getCSSClass(String tag) {
        Element item = XMLHandler.getElementByAttribute(root.getChild("css").
            getChildren("class"), "tag", tag);
        if (item == null) {
            return "";
        }
        return " class=\"" + item.getText() + "\"";
    }

    protected static void log(Object obj) {
        if (Config.getDebugMode()) {
            System.out.println("(taglib) " + obj);
        }
    }

    protected static void log(String str) {
        if (Config.getDebugMode()) {
            System.out.println("(taglib) " + str);
        }
    }

    protected void write(String s) {
        try {
            pageContext.getOut().write(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
