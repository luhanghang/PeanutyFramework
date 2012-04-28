package peanuty.framework.util;

import java.io.*;
import java.util.*;

import org.jdom.*;
import jxl.*;
import jxl.write.*;
import peanuty.framework.base.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Tong Fang PC</p>
 *
 * @author Lu Hang
 * @version 1.0
 */

public class ExcelHandler
        extends BaseXMLBean {

    public String exportFileName;
    private WritableWorkbook workbook;
    private WritableSheet sheet;
    private int sheetCount = 1;
    //private List exportItems;
    private Element objectRoot;
    public final static String SAVEPATH = Config.getWebAppPath() + "/WEB-INF/export/";

    /**
     * Structure Function
     *
     * @param objectName Object Name
     */
    public ExcelHandler(String objectName) {
        this.getroot("db2excel.xml");
        this.setObjectRoot(objectName);
    }

    private boolean createFile() {
        try {
            this.workbook = Workbook.createWorkbook(new File(this.exportFileName));
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean createSheet(String sheetName) {
        this.sheet = this.workbook.createSheet(sheetName, this.sheetCount++);
        return true;
    }

    private void setObjectRoot(String objectName) {
        this.objectRoot = XMLHandler.getElementByAttribute(this.root.getChildren(
                "object"),
                "name", objectName);
    }

    public static String getExcelLabel(Element exportItem) {
        return exportItem.getAttributeValue("excelLabel");
    }

    public static String getDbField(Element exportItem) {
        return exportItem.getAttributeValue("dbField");
    }

    public WritableWorkbook getWorkbook() {
        return workbook;
    }

    public WritableSheet getSheet() {
        return sheet;
    }

    public List getExportItems() {
        return objectRoot.getChildren("exportitem");
    }

    public Element getObjectRoot() {
        return objectRoot;
    }

    /*
    private void setObjectRoot(Element objectRoot) {
        this.objectRoot = objectRoot;
    }
    */

    public boolean write() throws Throwable {
        workbook.write();
        workbook.close();
        return true;
    }

    /**
     * Export Map to Excel sheet according db2excel.xml
     *
     * @param exportFileName filename
     * @param dbsrc          record result Map
     * @param title          Title for datasheet
     * @return Aways return true;
     * @throws Throwable throwable
     */
    public boolean export(String exportFileName, Map dbsrc, String title) throws
            Throwable {
        if (!exportFileName.endsWith(".xls")) {
            exportFileName += ".xls";
        }
        this.exportFileName = SAVEPATH +
                exportFileName;
        if(!createFile() || !createSheet(objectRoot.getAttributeValue("sheetname"))) return false;

        if (title != null) {
            sheet.addCell(new Label(1, 1, title));
        }

        List exportitems = getExportItems();
        for (int i = 0; i < exportitems.size(); i++) {
            sheet.addCell(new Label(i + 1, 3,
                    getExcelLabel((Element) exportitems.get(i))));
        }

        for (int i = 0; i < dbsrc.size(); i++) {
            Map item = (Map) dbsrc.get("ROW" + i);
            for (int j = 0; j < exportitems.size(); j++) {
                String dbfield = getDbField((Element) exportitems.get(j));
                int l = dbfield.length();
                char datatype = dbfield.charAt(l - 1);
                dbfield = dbfield.substring(0, l - 1);
                String dbitem = DataFilter.show(item, dbfield);
                switch (datatype) {
                    case 'T':
                        sheet.addCell(new Label(j + 1, i + 4, dbitem));
                        break;
                    case 'N':
                        sheet.addCell(new jxl.write.Number(j + 1, i + 4,
                                Double.parseDouble(dbitem)));
                        break;
                    case 'D':
                        sheet.addCell(new DateTime(j + 1, i + 4,
                                (Date) item.get(dbfield),
                                new WritableCellFormat(new DateFormat("yyyy-mm-dd"))));
                        break;
                    default:
                        break;
                }

            }
        }
        write();
        return true;
    }
}
