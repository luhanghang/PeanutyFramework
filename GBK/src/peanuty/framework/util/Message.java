package peanuty.framework.util;

import java.util.*;

import org.jdom.*;
import peanuty.framework.base.*;

public class Message
    extends peanuty.framework.base.BaseXMLBean {

    private final static Message MESSAGE = new Message();
    private final static String REPLACE_SYMBOL = "\\x7b\\x3f\\x7d"; //{?}

    private Message() {
        super();
    }

    protected void init() {
        getroot("Messages.xml");
    }

    /**
     * Get Message String
     * @param messageName Message Name defined by Messages.xml
     * @return Message String
     */
    public static String getMessage(String messageName) {
        Element message = XMLHandler.getElementByAttribute(MESSAGE.root.getChildren(
            "Message"), "name", messageName);
        return message == null? messageName:message.getTextTrim();
    }

    /**
     * Get Message String
     * @param messageName Message Name defined by Messages.xml
     * @param replaceStr Replace {?} with replaceStr
     * @return Message String
     */
    public static String getMessage(String messageName,String replaceStr) {
        Element message = XMLHandler.getElementByAttribute(MESSAGE.root.getChildren(
            "Message"), "name", messageName);
        return message.getTextTrim() .replaceFirst(REPLACE_SYMBOL,replaceStr);
    }

    /**
     * Get Message String
     * @param messageName Message Name defined by Messages.xml
     * @param replaceStrs Replace {?}.. with the strings in collection
     * @return Message String
     */
    public static String getMessage(String messageName,Collection replaceStrs) {
        String message = getMessage(messageName);
        Object[] replaceStrsArray = replaceStrs.toArray();
        for(Object str: replaceStrsArray){
            message = message.replaceAll(REPLACE_SYMBOL, (String)str);
        }
        return message;
    }
}
