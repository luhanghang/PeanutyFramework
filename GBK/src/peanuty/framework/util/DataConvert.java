package peanuty.framework.util;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

public class DataConvert {

    /**
     * Convert byte to int
     * @param b byte to be converted
     * @return int
     */
    public static int byte2int(byte b) {
		if (b < 0) {
			return (int) b + 0x100;
		}
		return b;
	}

    /**
     * Convert byte array to Hex String
     * @param bArray byte array to be converted
     * @return Hex String
     */
    public static String toHex(byte bArray[]) {
		StringBuffer stringbuffer = new StringBuffer(bArray.length * 2);
		for (byte b: bArray) {
			if ((b & 0xff) < 16) {
				stringbuffer.append("0");
			}
			stringbuffer.append(Long.toString(b & 0xff, 16));
		}
		return stringbuffer.toString();
	}

    /**
     * Convert request parameters to Map
     * @param request Http request
     * @return Map
     */
    public static Map<String,String> ReqToHash(HttpServletRequest request) {
		Map<String,String> h = new HashMap<String,String>();
		Enumeration eReqTags = request.getParameterNames();
		String rTag;
		String rVal;
		for(;eReqTags.hasMoreElements();) {
			rTag = eReqTags.nextElement().toString();
			rVal = request.getParameter(rTag);
			if(rVal.trim().equals("")){
				continue;
			}
			rVal = rVal.replaceAll("ANDandAND", "&");
			rVal = rVal.replaceAll("ppppCCCeNNt", "%");
			// rVal = escapeComma(rVal);
			h.put(rTag, rVal);
		}
		return h;
	}

    /**
     * Escape Html tags
     * @param sourcestr Source String
     * @return Escaped String
     */
    public static String escapeHTMLTags(String sourcestr) {
		if (sourcestr == null) {
			return "";
		}
		sourcestr = sourcestr.replaceAll("\\x26", "&amp;");
		sourcestr = sourcestr.replaceAll("\\x3c", "&lt;");
		sourcestr = sourcestr.replaceAll("\\x3e", "&gt;");
		sourcestr = sourcestr.replaceAll("\\x22", "&quot;");
		sourcestr = sourcestr.replaceAll("\\x0d", "<br>");
		sourcestr = sourcestr.replaceAll("\\x09", "&nbsp;&nbsp;&nbsp;&nbsp;");
		sourcestr = sourcestr.replaceAll("\\x20", "&nbsp;");
		// sourcestr = sourcestr.replaceAll("\\x22","&quot;");
		return sourcestr;
	}

	/**
     * Escape Xml tags
     * @param sourcestr Source String
     * @return Escaped String
     */
    public static String escapeXMLTags(String sourcestr) {
		if (sourcestr == null) {
			return "";
		}
		sourcestr = sourcestr.replaceAll("\\x26", "&amp;");
		sourcestr = sourcestr.replaceAll("\\x3c", "&lt;");
		sourcestr = sourcestr.replaceAll("\\x3e", "&gt;");
		sourcestr = sourcestr.replaceAll("\\x22", "&quot;");
		return sourcestr;
	}


    /**
     * Escape quote character
     * @param sourcestr Source String
     * @return Escaped String
     */
    public static String escapeQuote(String sourcestr) {
		if (sourcestr == null) {
			return "";
		}
		sourcestr = sourcestr.replaceAll("\\x22", "&quot;");
		return sourcestr;
	}

    /**
     * Replace ' with ''
     * @param sourcestr Source String
     * @return Replaced String
     */
    public static String escapeComma(String sourcestr) {
		return sourcestr.replaceAll("'", "''");
	}

    /**
     * Hash Map Sort
     * @param orgHash Source Map like {ROW0={key1=value1,key2=value2...},ROW1={key1=value1,key2=value2...}...}
     * @param orderBy Key for order
     * @param asce true for asce false for desc
     * @return Sorted Map like {ROW0={key1=value1,key2=value2...},ROW1={key1=value1,key2=value2...}...}
     */
    public static Map mapSort(Map<String,Map<String,Object>> orgHash, String orderBy, boolean asce) {
		Map<String,Map<String,Object>> newHash = new HashMap<String,Map<String,Object>>();
		List<String> orderList = new ArrayList<String>();
		for (int x = 0; x < orgHash.size(); x++) {
			orderList.add(orgHash.get("ROW" + x).get(orderBy)	+ "!" + x);
		}
		Collections.sort(orderList);

		if (asce) {
            int x = 0;
            for (String key: orderList) {
				int keyLength = key.length();
				newHash.put("ROW" + (x++), orgHash.get("ROW"
						+ key.substring(key.lastIndexOf("!") + 1, keyLength)));
			}
		} else {
            int x = 0;
            for (String key: orderList) {
				int keyLength = key.length();
				newHash.put("ROW" + (orderList.size() - 1 - (x++)), orgHash.get("ROW" + key.substring(key.lastIndexOf("!") + 1,
										keyLength)));
			}
		}
		return newHash;
	}


    /**
     * Return the result of 2 double values
     * @param d1 double value1
     * @param d2 double value2
     * @return result
     */
    public static double doubleMultiply(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return b1.multiply(b2).doubleValue();
	}

    /**
     * Padding use giving char
     * <br/> Example: padding("7","0",3) will return "700"
     * @param src Src String
     * @param charfill char for filling
     * @param length String length after padding
     * @return new string
     */
	public static String padding(String src, String charfill, int length) {
        for (int i = src.length(); i < length; i++) {
            src += charfill;			
		}
		return src;
	}

	/**
     * Padding use giving char
     * <br/> Example: padding("0",0,"7") will return "007"
     * @param charfill char for filling
     * @param length String length after padding
     * @param src Src String
     * @return new string
     */
    public static String padding(String charfill, int length, String src) {
		for (int i = src.length(); i < length; i++) {
			src = charfill + src;
		}
		return src;
	}

    /**
     * Convert Record result Map to javascript array
     * @param list Recorld Result Map like {ROW0={key1=value1,key2=value2...},ROW1={key1=value1,key2=value2...}...}
     * @param nameTag nameTag in the Map
     * @param valueTag  value Tag in the Map
     * @param arrayName javascript array name
     * @return javascript string
     * <br/>Example
     * <br/> list = {ROW0={name=name1,value=value1}, ROW1={name=name2,value=value2}, ROW2={name=name3,value=value3}}
     * <br/> DataConvert.Map2JsArray(list, "name", "value", "list") will return
     * <br/>"
     * <br/> var list = new Array();
     * <br/> list['name1'] = 'value1';
     * <br/> list['name2'] = 'value2';
     * <br/> list['name3'] = 'value3'; 
     * <br/>"
     */
    public static String Map2JsArray(Map<String,Map<String,Object>> list, String nameTag, String valueTag, String arrayName){
        StringBuffer array = new StringBuffer();
        array.append("var ").append(arrayName).append(" = new Array();\n");
        for(int i = 0; i < list.size(); i++){
            Map<String,Object> item = list.get("ROW" + i);
            array.append(arrayName).append("['").append(item.get(nameTag)).append("'] = '").append(item.get(valueTag)).append("';\n");
        }
        return array.toString();
    }

    public static String Map2JsArray2D(Map<String,Map<String,Map<String,Object>>> list,String nameTag, String valueTag, String arrayName){
        StringBuffer array = new StringBuffer();
        array.append("var ").append(arrayName).append(" = new Array();\n");
        Set keySet = list.keySet();
        Iterator itKey = keySet.iterator();
        for(;itKey.hasNext();){
            String key = itKey.next() + "";
            array.append(arrayName).append("['").append(key).append("'] = ").append("new Array();\n");
            Map<String,Map<String,Object>> items = list.get(key);
            for(int i = 0; i < items.size(); i++){
                Map item = items.get("ROW" + i);
                array.append(arrayName).append("['").append(key).append("']['").append(item.get(nameTag)).append("'] = '").append(item.get(valueTag)).append("';\n");
            }
        }
        return array.toString();
    }

    public static String Map2JsJasonArray2D(Map<String,Map<String,Map<String,Object>>> list,String nameTag, String valueTag, String arrayName){
        StringBuffer array = new StringBuffer();
        array.append("var ").append(arrayName).append(" = new Array();\n");
        Set keySet = list.keySet();
        Iterator itKey = keySet.iterator();
        for(;itKey.hasNext();){
            String key = itKey.next() + "";
            array.append(arrayName).append("['").append(key).append("'] = ").append("new Array();\n");
            Map<String,Map<String,Object>> items = list.get(key);
            for(int i = 0; i < items.size(); i++){
                Map item = items.get("ROW" + i);
                array.append(arrayName).append("['").append(key).append("'][").append(i).append("] = {'name':'").append(item.get(nameTag)).append("','value':'").append(item.get(valueTag)).append("'};\n");
            }
        }
        return array.toString();
    }

    /**
     * Find in the giving map where exists the giving key's value equals the giving value
     * @param src Src map likes {ROW0={key0=value0,key1=value1...},ROW1={key0=somevalue,key1=somevalue...}...}
     * @param key key to find
     * @param value value to find
     * @return A map including the key = value likes {ROW0={key0=value0,key1=value1...},ROW1={key0=somevalue,key1=somevalue...}...}
     * <br/>Excample:
     * <br/>Map src = {ROW0={name=luhang,gender=male},ROW1={name=flora,gender=female}, ROW2={name=peanuty,gender=female}}
     * <br/>mapSearch(src,"gender","female") will returns
     * <br/>{ROW0={name=flora,gender=female}, ROW1={name=peanuty,gender=female}} 
     */
    public static Map<String,Map<String,Object>> mapSearch(Map<String,Map<String,Object>> src, String key, String value){
        if(src == null) return null;
        Map<String,Map<String,Object>> result = new HashMap<String,Map<String,Object>>();
        int index = 0;
        for(int i = 0; i < src.size(); i++){
            Map<String,Object> item = src.get("ROW" + i);
            if(value.equals(item.get(key))){
                result.put("ROW" + (index++), item);
            }
        }
        return result;
    }

    public static String Map2XML(Map<String,Map<String,Object>> src){
        if(src == null) return "<Items/>";
        StringBuffer xml = new StringBuffer();
        xml.append("<Items>");
        for(int i = 0 ; i < src.size(); i++){
            Map<String,Object> item = src.get("ROW" + i);
            xml.append("<Item><row>").append(i).append("</row>");
            Iterator keys = item.keySet().iterator();
            for(;keys.hasNext();){
                String key = (String)keys.next();
                xml.append("<").append(key).append(">").append(item.get(key)).append("</").append(key).append(">");
            }
            xml.append("</Item>");
        }
        xml.append("</Items>");
        return xml.toString();
    }

    public static String Map2XMLWizAttribute(Map<String,Map<String,Object>> src){
        if(src == null) return "<Items/>";
        StringBuffer xml = new StringBuffer();
        xml.append("<Items?");
        for(int i = 0 ; i < src.size(); i++){
            Map<String,Object> item = src.get("ROW" + i);
            xml.append("<Item row=\"").append(i).append("\"");
            Iterator keys = item.keySet().iterator();
            for(;keys.hasNext();){
                String key = (String)keys.next();
                xml.append(" ").append(key).append("=\"").append(item.get(key)).append("\"");
            }
            xml.append("/>");
        }
        xml.append("</Items");
        return xml.toString();
    }

    public static String Map2TreeData(Map<String,Map<String,Object>> map,String codeTag, String rootTag, String descriptionAttrTag, String rootDescription){
        if(map.isEmpty()){
            return null;
        }
        SortedSet<Integer> s = new TreeSet<Integer>();
        for(int i = 0 ; i < map.size(); i++){
            Map<String,Object> item = map.get("ROW" + i);
            String code = item.get(codeTag) + "";
            int codeLength = code.length();
            s.add(codeLength);
        }
        Integer[] level = s.toArray(new Integer[s.size()]);
        Map<Integer,Integer> m = new HashMap<Integer,Integer>();
        for(int i = 0; i < level.length; i++){
            m.put(level[i], i);
        }
        StringBuffer treeData = new StringBuffer();
        treeData.append("<").append(rootTag).append(" ").append(descriptionAttrTag).append("=\"").append(rootDescription).append("\">");
        for(int i = 0 ; i < map.size(); i++){
            Map<String,Object> item = map.get("ROW" + i);
            treeData.append("<Item");
            Iterator keys = item.keySet().iterator();
            for(;keys.hasNext();){
                String key = keys.next() + "";
                treeData.append(" ").append(key).append("=\"").append(item.get(key)).append("\"");
            }

            Map<String,Object> itemNext = map.get("ROW" + (i + 1));
            int codeLength = (item.get(codeTag) + "").length();
            if(itemNext == null){
                treeData.append("/>");
                for(int x = 0; x < m.get(codeLength); x++){
                    treeData.append("</Item>");    
                }
                break;
            }

            int codeNextLength = (itemNext.get(codeTag) + "").length();
            if(codeLength < codeNextLength){
                treeData.append(">");
                continue;
            }
            treeData.append("/>");
            if(codeLength == codeNextLength){
                continue;
            }

            for(int x = m.get(codeLength); x > m.get(codeNextLength); x--){
                treeData.append("</Item>");
            }
        }
        treeData.append("</").append(rootTag).append(">");
        return treeData.toString();
    }

    public static Map<String,String> MapList2KeyValue(Map<String,Map<String,Object>> src, String keyTag, String valueTag){
        Map<String,String> keyValue = new HashMap<String,String>();
        if(src == null || src.isEmpty()) return keyValue;
        for(int i = 0; i < src.size(); i++){
            Map<String,Object> item = src.get("ROW" + i);
            keyValue.put(item.get(keyTag) + "",item.get(valueTag) + "");            
        }
        return keyValue;
    }
}