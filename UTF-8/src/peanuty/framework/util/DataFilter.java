package peanuty.framework.util;

import java.util.*;

public class DataFilter {

    /**
     * Get value from Map (if value == null will return "");
     *
     * @param h   src Map
     * @param tag tag name
     * @return value
     */
    public static String show(Map h, String tag) {
        String ret = "";
        if (null != h) {
            ret = null == h.get(tag) ? "" : (h.get(tag) + "");
        }
        return ret;

    }

    /**
     * Get value from Map (if value == null will return "");
     * @param h   src Map
     * @param tag tag name
     * @return value
     */
    //TODO to modify
    public static String showToDB(Map h, String tag) {
        String ret = "";
        if (null != h) {
            if(null != h.get(tag)){
                ret = h.get(tag) + "";
                if(tag.endsWith("T")){
                    ret = ret.replaceAll("'","''");       
                }
            }
        }
        return ret;
    }

    /**
     * Get value from Map (if value == null will return "");
     *
     * @param h   src Map
     * @param tag tag name
     * @return value
     */
    //TODO to modify
    public static String showFromDB(Map h, String tag) {
        String ret = "";
        if (null != h) {
            ret = null == h.get(tag) ? "" : (h.get(tag) + "");
        }
        return ret;
    }

    /**
     * Get value from Map (if value == null will return "");
     *
     * @param h   src Map
     * @param tag tag name
     * @return value
     */
    //TODO to modify
    public static String wohs(Map h, String tag) {
        String ret = "";
        if (null != h) {
            ret = null == h.get(tag) ? "" : (h.get(tag) + "");
        }
        return ret;
    }
}
