package peanuty.framework.base;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

/**
 * <p>Title: XMLHandler</p>
 * <p>Description: Handle the xml file under "WEB-INF/xml"</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author Lu Hang
 * @version 1.0
 */

public class XMLHandler {

    private XMLHandler() {

    }

    public static XMLHandler createNewInstance() {
        return new XMLHandler();
    }

    public final static String FILELOCATION = Config.getWebAppPath() +
                    "/WEB-INF/xml/";

    /**
     * Open a XML file
     *
     * @param XMLFileName the name of the file to be opened
     * @return the root element of this file
     */
    public static Element openXML(String XMLFileName) {
        Document doc = null;
        try {
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(new File(FILELOCATION + XMLFileName));
        }
        catch (Exception e) {
            System.out.println("XMLFile Open Failure->" + e.getMessage());
        }
        if (doc == null) {
            return null;
        }

        return doc.getRootElement();
    }

    /**
     * Open a XML from a servlet input stream
     *
     * @param in a servlet input stream
     * @return the root element of this file
     */
    public static Element openXML(javax.servlet.ServletInputStream in) {
        Document doc = null;
        try {
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(in);
        }
        catch (Exception e) {
            System.out.println("XMLFile Open Failure->" + e.getMessage());
        }
        if (doc == null) {
            return null;
        }

        return doc.getRootElement();
    }

    /**
     * write XML file
     * ----------------------------------------------
     *
     * @param XMLFileName file name to be wrote
     * @param doc         docment to be wrote
     * @return the result
     */
    public static boolean writeXML(String XMLFileName, Document doc) {
        try {
            XMLOutputter outputter = new XMLOutputter();
            FileWriter writer = new FileWriter(FILELOCATION + XMLFileName);
            outputter.output(doc, writer);
            writer.close();
            return true;
        }
        catch (Exception e) {
            Config.getLogInstance().write("Write File Failure-->" + e.getMessage());
            return false;
        }
    }

    public static void writeXML(String filename, Element root) throws Exception{
        File file = new File(XMLHandler.FILELOCATION + filename);
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        osw.write(XMLHandler.showXML(root));
        osw.flush();
    }

    /**
     * Convert node list to Map
     * <br/>&lt;item id="1" name="name1"/&gt;
     * <br/>&lt;item id="2" name="name2"/&gt;
     * <br/>...
     * <br/>will be converted to:
     * <br/>{ROW0={id=1,name=name1},ROW1={id=1,name=name2},...}
     *
     * @param nodes      The source node list
     * @param attributes Attrbute list to be converted, null means convert all attributes
     * @return java.util.Map
     */
    //TODO refactor with xquery
    public static Map nodesToMap(List nodes, List attributes) {
        Map<String, Map> nodehash = new HashMap<String, Map>();
        for (int i = 0; i < nodes.size(); i++) {
            Element child = (Element) nodes.get(i);
            nodehash.put("ROW" + i, elementToHash(child, attributes));
        }
        return nodehash;
    }

    /**
     * Convert node list to Map
     * <br/>&lt;item id="1" name="name1"/&gt;
     * <br/>&lt;item id="2" name="name2"/&gt;
     * <br/>...
     * <br/>(keyName="name")will be converted to:
     * <br/>{name1={id=1,name=name1},name2={id=1,name=name2},...}
     *
     * @param nodes      The source node list
     * @param attributes Attrbute list to be converted, null means convert all attributes
     * @param keyName    key name of the Map
     * @return java.util.Map
     */
    //TODO refactor with xquery
    public static Map nodesToMap(List nodes, List attributes, String keyName) {
        Map<String, Map> nodehash = new HashMap<String, Map>();
        for (Object child : nodes) {
            nodehash.put(((Element) child).getAttributeValue(keyName), elementToHash((Element) child, attributes));
        }
        return nodehash;
    }

    /**
     * Convert node list to Map
     *
     * @param nodes      The source node list
     * @param attrs      Attrbute list to be converted, null means convert all attributes
     * @param searchAttr filter such as {id=1, flag=true} will only convert the elements which attibute "id"'s value is "1" and "flag" value is "trueß"
     * @return java.util.Map
     */
    //TODO refactor with xquery
    public static Map nodesToMap(List nodes, List attrs, Map searchAttr) {
        List<Element> newList = new ArrayList<Element>();
        int count = 0;
        for (Object node : nodes) {
            Element child = (Element) node;
            boolean match = true;
            for (Object ob : searchAttr.keySet()) {
                if (!child.getAttributeValue((String) ob).equals(searchAttr.
                        get(ob).toString())) {
                    match = false;
                    break;
                }
            }
            if (match) {
                newList.add(count++, child);
            }
        }
        return nodesToMap(newList, attrs);
    }

    /*
    public static Map nodesToMap(List nodes, List searchItems, List attrs) {
        List newList = new ArrayList();
        int count = 0;
        for (int i = 0; i < nodes.size(); i++) {
            Element child = (Element) nodes.get(i);
            boolean match = true;
            for (int si = 0; si < searchItems.size(); si++) {
                searchItem item = (searchItem) searchItems.get(si);
                if (item.pattern.equals("%%")) {
                    match = child.getAttributeValue(item.field).indexOf(item.
                        value) >= 0;
                }
                else if (item.pattern.equals("*%")) {
                    match = child.getAttributeValue(item.field).startsWith(item.
                        value);
                }
                else if (item.pattern.equals("%*")) {
                    match = child.getAttributeValue(item.field).endsWith(item.
                        value);
                }
                else if (item.pattern.equals("")) {
                    match = child.getAttributeValue(item.field).equals(item.
                        value);
                }
                if (!match) {
                    break;
                }
            }
            if (match) {
                newList.add(count++, child);
            }
        }
        return nodesToMap(newList, attrs);
    }
    */

    /**
     * Convert Element to Map
     * <br/>&lt;item id="1" name="name1"/&gt;
     * <br/>will be converted to:
     * <br/>{id=1, name=name1}
     *
     * @param element Source Element to be converted
     * @param attrs   Attributes to be converted, null means all attributes
     * @return java.util.Map
     */
    //TODO refactor with xquery
    public static Map elementToHash(Element element, List attrs) {
        Map<String, String> item = new HashMap<String, String>();
        List Attrs = element.getAttributes();
        if (Attrs.size() > 0) {
            for (Object ob : Attrs) {
                Attribute attr = (Attribute) ob;
                if (attrs == null || attrs.contains(attr.getName())) {
                    item.put(attr.getName(), attr.getValue());
                }
            }
        } else {
            item.put(element.getName(), element.getText()); //如果没有属性取元素内容
        }
        return item;
    }

    /**
     * Get the first element according to the given attributes and their value
     *
     * @param ElementList node list
     * @param Attributes  attributes and their value such as {att1=val1, att2=val2...}
     * @return org.jdom.Element
     */
    //TODO refactor with xquery
    public static Element getElementByAttributes(List ElementList,
                                                 Map Attributes) {
        Element resultElement = null;
        for (Object ob : ElementList) {
            Element element = (Element) ob;
            boolean match = true;
            for (Object o : Attributes.keySet()) {
                String attrName = (String) o;
                try {
                    Attribute attr = element.getAttribute(attrName);
                    if (!attr.getValue().equals(Attributes.get(attrName))) {
                        match = false;
                    }
                }
                catch (Exception e) {
                    Config.getLogInstance().write("Attribute:\"" + attrName + "\" does not exist");
                }
            }
            if (match) {
                resultElement = element;
                break;
            }
        }
        return resultElement;
    }

    /**
     * Get the first element according to the given attribute and it's value
     *
     * @param ElementList node list
     * @param attrName    attribute name
     * @param attrValue   attrbute value
     * @return org.jdom.Element
     *         <p/>
     *         <br/>Example:
     *         <br/>Node list:
     *         <br/>&lt;item id="1"&gt;
     *         <br/>&lt;item id="2"&gt;
     *         <br/>...
     *         <br/>XMLHandler.getElementByAttribute(list,"id","1") will get the first element
     */
    //TODO refactor with xquery
    public static Element getElementByAttribute(List ElementList,
                                                String attrName,
                                                String attrValue) {
        Element resultElement = null;
        for (Object ob : ElementList) {
            Element element = (Element) ob;
            boolean match = false;
            try {
                Attribute attr = element.getAttribute(attrName);
                String attVal[] = attr.getValue().split(",");
                for (String val : attVal) {
                    if (val.equals(attrValue)) {
                        match = true;
                        break;
                    }
                }
            }
            catch (Exception e) {
                Config.getLogInstance().write("Attribute:\"" + attrName + "\" does not exist");
            }
            if (match) {
                resultElement = element;
                break;
            }
        }
        return resultElement;
    }

    /*
    public class searchItem {
        public String field;
        public String value;
        public String pattern; //"%%","%*","*%",""

        public searchItem(String field, String value, String pattern) {
            this.field = field;
            this.value = value;
            this.pattern = pattern;
        }
    }

    public searchItem newSearchItem(String field, String value,
                                    String pattern) {
        return new searchItem(field, value, pattern);
    }

    public static void main(String[] args){
        Map items = new HashMap();
        for(int i = 0; i < 20; i++){
        	Map item = new HashMap();
        	item.put("code", i + "");
        	item.put("desc", "&\"<>");
        	items.put("ROW" + i, item);
        }
        System.out.println(items);
        System.out.println(map2XML(items));
    }
    */

    /**
     * Convert Map to XML
     *
     * @param src source map to be converted
     * @return XML String
     */
    public static String map2XML(Map src) {
        if (src.isEmpty()) {
            return "<Items/>";
        }
        Object key;
        StringBuffer xml = new StringBuffer();
        xml.append("<Items>");

        for (int i = 0; i < src.size(); i++) {
            Map item = (Map) src.get("ROW" + i);
            Iterator keys = item.keySet().iterator();
            xml.append("<Item><row>");
            xml.append(i);
            xml.append("</row>");
            for (; keys.hasNext();) {
                key = keys.next();
                xml.append("<");
                xml.append(key);
                xml.append(">");
                xml.append(escapeXMLTag(item.get(key) + ""));
                xml.append("</");
                xml.append(key);
                xml.append(">");
            }
            xml.append("</Item>");
        }
        xml.append("</Items>");
        return xml.toString();
    }

    /**
     * Escape XML tag
     *
     * @param sourcestr source xml string
     * @return xml string with xml tag escaped
     */
    public static String escapeXMLTag(String sourcestr) {
        sourcestr = sourcestr.replaceAll("\\x26", "&amp;");
        sourcestr = sourcestr.replaceAll("\\x3c", "&lt;");
        sourcestr = sourcestr.replaceAll("\\x3e", "&gt;");
        sourcestr = sourcestr.replaceAll("\\x22", "&quot;");
        return sourcestr;
    }


    /**
     * Show xml element by string
     *
     * @param root root element to show
     * @return xml string
     */
    public static String showXML(Element root) {
        StringBuffer strbuf = new StringBuffer();
        try {
            strbuf.append("<");
            strbuf.append(root.getName());
            if (!"".equals(root.getNamespaceURI())) {
                strbuf.append("   xmlns=\"");
                strbuf.append(root.getNamespaceURI());
                strbuf.append("\"");
            }
            accessAdditionalNamespaces(root, strbuf);
            accessAttribute(root, strbuf);
            strbuf.append(">");
            accessElement(root, strbuf);
            strbuf.append("</");
            strbuf.append(root.getName());
            strbuf.append(">");
            return strbuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void accessElement(Element parent, StringBuffer strbuf) {
        List listChild = parent.getChildren();
        int iChild = listChild.size();
        for (int i = 0; i < iChild; i++) {
            Element e = (Element) listChild.get(i);
            strbuf.append(System.getProperty("line.separator"));
            strbuf.append("     <");
            strbuf.append(e.getName());

            accessAttribute(e, strbuf);
            strbuf.append(">");
            strbuf.append(e.getTextTrim());

            accessElement(e, strbuf);
            strbuf.append("</");
            strbuf.append(e.getName());
            strbuf.append(">");
        }

        if (iChild > 0) {
            strbuf.append(System.getProperty("line.separator"));
        }
    }

    private static void accessAttribute(Element e, StringBuffer strbuf) {
        List listAttributes = e.getAttributes();
        int iAttributes = listAttributes.size();
        for (int j = 0; j < iAttributes; j++) {
            Attribute attribute = (Attribute) listAttributes.get(j);
            strbuf.append("   ");
            strbuf.append(attribute.getName());
            strbuf.append("=\"");
            strbuf.append(attribute.getValue());
            strbuf.append("\"");
        }
    }

    private static void accessAdditionalNamespaces(Element e, StringBuffer strbuf) {
        List listAdditionalNamespaces = e.getAdditionalNamespaces();
        int iAttributes = listAdditionalNamespaces.size();
        for (int j = 0; j < iAttributes; j++) {
            Namespace namespace = (Namespace) listAdditionalNamespaces.get(j);
            strbuf.append("   xmlns:");
            strbuf.append(namespace.getPrefix());
            strbuf.append("=\"");
            strbuf.append(namespace.getURI());
            strbuf.append("\"");
        }
    }
}
