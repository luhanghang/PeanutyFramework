package peanuty.framework.taglib;

import java.util.Map;
import peanuty.framework.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Tong Fang PC</p>
 * @author Lu Hang
 * @version 1.0
 */

public class Radio
    extends BaseTagLib {

    private String name;
    private Map src;
    private String radioVal;
    private String radioTxt;
    private String checkedVal;

    public void setName(String name) {
        this.name = name;
    }

    public void setSrc(Map src) {
        this.src = src;
    }

    public void setRadioVal(String radioVal) {
        this.radioVal = radioVal;
    }

    public void setRadioTxt(String radioTxt) {
        this.radioTxt = radioTxt;
    }

    public void setCheckedVal(String checkedVal) {
        this.checkedVal = checkedVal;
    }

    public int doEndTag(){
        write(composeRadioString());
        return EVAL_PAGE;
    }

    private String composeRadioString(){
        StringBuffer s = new StringBuffer();
        s.append("<!-- generated by webapp.framework.taglib.Radio -->\n");
        for (int i = 0; i < src.size(); i++) {
            Map item = (Map) src.get("ROW" + i);
            s.append("<input type=\"Radio\" name=");
            s.append(name);
            s.append(" value=\"");
            s.append(DataFilter.show(item, radioVal));
            s.append("\"");
            s.append(getCSSClass("Radio"));
            if (checkedVal.equals(DataFilter.show(item, radioVal))) {
                s.append(" checked");
            }
            s.append(">&nbsp;");
            s.append(DataFilter.show(item, radioTxt));
            s.append("&nbsp;\n");
        }
        return s.toString();
    }
}