package peanuty.framework.taglib;

import peanuty.framework.util.DataFilter;

import javax.servlet.jsp.JspException;
import java.util.Map;

public class Select
    extends BaseTagLib {

    private String name;
    private Map<String,Map<String,Object>> src;
    private String opVal;
    private String opTxt;
    private String selectedVal;
    private String hasEmptyOption = "true";
    private String emptyOptionTxt="";
    private String emptyOptionVal="";
    private String classname;
    private String id;

    public final void setId(String id) {
        this.id = id;
    }

    public final void setClassname(String cssClass) {
        this.classname = cssClass;
    }

    public void setHasEmptyOption(String hasEmptyOption) {
        this.hasEmptyOption = hasEmptyOption;
    }

    public void setEmptyOptionTxt(String emptyOptionTxt) {
        this.emptyOptionTxt = emptyOptionTxt;
    }

    public void setEmptyOptionVal(String emptyOptionVal) {
        this.emptyOptionVal = emptyOptionVal;
    }

    private String style = null;
    private String extra;

    public final void setName(String name) {
        this.name = name;
    }

    public final void setSrc(Map<String,Map<String,Object>> src) {
        this.src = src;
    }

    public final void setOpVal(String opVal) {
        this.opVal = opVal;
    }

    public final void setOpTxt(String opTxt) {
        this.opTxt = opTxt;
    }

    public final void setSelectedVal(String selectedVal) {
        this.selectedVal = selectedVal;
    }

    public final int doEndTag() throws JspException {
        write(composeSelectString());
        return EVAL_PAGE;
    }
    
    public final void setExtra(String extra){
        this.extra = extra;
    }

    private String composeSelectString() {
        StringBuffer s = new StringBuffer();
        s.append("<!-- generated by webapp.framework.taglib.Select -->\n");
        s.append("<Select");
        if(id != null) s.append(" id='").append(id).append("'");
        s.append(" name = '").append(name).append("'");
        if(this.classname == null) {
            s.append(getCSSClass("Select"));
        } else {
            s.append(" class='").append(this.classname).append("'");
        }
        if(style != null){
            s.append(" style = '").append(style).append("'");
        }
        if(extra != null){
            s.append(" ");
            s.append(extra);
        }
        s.append(">\n");
        if(this.hasEmptyOption.equalsIgnoreCase("true")){
            s.append("<option value='").append(this.emptyOptionVal).append("' selected=\"true\">").append(this.emptyOptionTxt).append("</option>\n");
        }
        for (int i = 0; i < src.size(); i++) {
            Map item = src.get("ROW" + i);
            s.append("<option value = '").append(DataFilter.show(item, opVal)).append("'");
            if (DataFilter.show(item, opVal).equals(selectedVal)) {
                s.append(" selected=\"true\" ");
            }
            s.append(">");
            s.append(DataFilter.show(item, opTxt));
            s.append("</option>\n");
        }
        s.append("</Select>\n");
        return s.toString();
    }
    public void setStyle(String style) {
        this.style = style;
    }
}                                                                                     
