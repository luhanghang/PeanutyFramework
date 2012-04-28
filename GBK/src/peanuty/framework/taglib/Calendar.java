package peanuty.framework.taglib;


public class Calendar
    extends BaseTagLib {

    private String name;
    private String value;
    private String style;
    private String classname;
    private String extra;
    private String id;

    private boolean hasvalue = false;
    private boolean readonly = true;

    public final void setName(String name) {
        this.name = name;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public final void setValue(String value) {
        this.value = value;
        hasvalue = true;
    }

    public void setStyle(String style) {
        this.style = style;
    }
    
    public void setExtra(String extra) {
        this.extra = extra;
    }


    public void setClassname(String cssclass) {
        this.classname = cssclass;
    }

    public int doEndTag() {
        write(composeCalendarString());
        return EVAL_PAGE;
    }

    private String composeCalendarString() {
        if (!hasvalue) {
            value = pageContext.getRequest().getParameter(name) == null ? "" :
                pageContext.getRequest().getParameter(name);
        }
        hasvalue = false;

        StringBuffer s = new StringBuffer();
        s.append("<!-- generated by webapp.framework.taglib.Calendar -->\n");
        s.append("<input ");
        if(id != null)  s.append("id=\"").append(id).append("\"");
        s.append(" name='").append(name);
        s.append("' type='text' size='10'");
        if(classname == null){
            s.append(getCSSClass("calendar"));
        } else {
            s.append(" class='").append(classname).append("'");
        }
        s.append(" value='");
        s.append(value);
        s.append("'");
        if(readonly){
            s.append(" readonly");
        }
        if(extra != null){
        	s.append(" ").append(extra);
        }
        s.append(" style=\"cursor:pointer;");
        if (style != null) {
            s.append(style);
        }
        s.append("\" onclick=\"setday(this, this);\">");
        return s.toString();
    }
}