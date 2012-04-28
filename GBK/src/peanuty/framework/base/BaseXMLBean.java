package peanuty.framework.base;

import org.jdom.Element;
import peanuty.framework.util.DataFilter;

public abstract class BaseXMLBean
        extends PageControlBase {

    public BaseXMLBean(){
        init();
    }

    abstract protected void init();
    private String filename;
    protected Element root;

    protected void getroot(String filename){
        this.filename = filename;
        root = XMLHandler.openXML(filename);
    }

    protected String _(String tag) {
        return DataFilter.wohs(_reqH, tag);
    }

    protected boolean writeXML(){
        return XMLHandler.writeXML(filename,root.getDocument());
    }
}