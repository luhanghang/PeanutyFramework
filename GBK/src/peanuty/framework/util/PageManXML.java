package peanuty.framework.util;

import peanuty.framework.base.Config;
import peanuty.framework.base.DBConn;
import peanuty.framework.base.DBConnection;

import java.util.Map;

public class PageManXML {

    public final static DBConn DBC = DBConnection.DBC;
    public static String getResultListXML(String sql, int jumpToPage) throws Exception{
		return getResultListXML(sql, jumpToPage, DBC);
	}

	public static String getXMLResultList(String sql, int jumpToPage) throws Exception{
		return getXMLResultList(sql, jumpToPage, DBC);
	}

	public static String getResultListXML(String sql, int jumpToPage, int rowsPerPage) throws Exception{
        return getResultListXML(sql, jumpToPage, rowsPerPage, DBC);
	}

	public static String getXMLResultList(String sql, int jumpToPage, int rowsPerPage) throws Exception{
		return getXMLResultList(sql, jumpToPage, rowsPerPage, DBC);
	}

	public static String getResultListXML(Map reqH) throws Exception{
		return getResultListXML(reqH, DBC);
	}

	public static String getXMLResultList(Map reqH) throws Exception{
		return getXMLResultList(reqH,DBC);
	}

	private static int getMaxPageByMaxRow(int maxRow, int rowsPerPage) {
		if (maxRow % rowsPerPage == 0) {
			return maxRow / rowsPerPage;
		}
		return maxRow / rowsPerPage + 1;
	}  

    public static String getResultListXML(String sql, int jumpToPage, DBConn dbc) throws Exception{
		return getResultListXML(sql, jumpToPage, Config.getPageManRowsPerPage(), dbc);
	}

	public static String getXMLResultList(String sql, int jumpToPage, DBConn dbc) throws Exception{
		return getXMLResultList(sql, jumpToPage, Config.getPageManRowsPerPage(), dbc);
	}

	public static String getResultListXML(String sql, int jumpToPage, int rowsPerPage, DBConn dbc) throws Exception{
		int maxRow = dbc.getResultSize(sql);
		int maxPage = getMaxPageByMaxRow(maxRow, rowsPerPage);
		int startPos = (jumpToPage - 1) * rowsPerPage + 1;
		int endPos = jumpToPage * rowsPerPage + 1;

		String dataXML = dbc.getResultListXML(sql, startPos, endPos);

		StringBuffer xml = new StringBuffer();
		xml.append("<PageMan>");
		xml.append("<CurrentPage>");
		xml.append(jumpToPage);
		xml.append("</CurrentPage>");
		xml.append("<MaxPage>");
		xml.append(maxPage);
		xml.append("</MaxPage>");
		xml.append("<MaxRow>");
		xml.append(maxRow);
		xml.append("</MaxRow>");
		xml.append("<RowsPerPage>");
		xml.append(rowsPerPage);
		xml.append("</RowsPerPage>");
		xml.append(dataXML);
		xml.append("</PageMan>");
		return xml.toString();
	}

	public static String getXMLResultList(String sql, int jumpToPage, int rowsPerPage, DBConn dbc) throws Exception{
		int maxRow = dbc.getResultSize(sql);
		int maxPage = getMaxPageByMaxRow(maxRow, rowsPerPage);
		int startPos = (jumpToPage - 1) * rowsPerPage + 1;
		int endPos = jumpToPage * rowsPerPage + 1;

		String dataXML = dbc.getXMLResultList(sql, startPos, endPos);

		StringBuffer xml = new StringBuffer();
		xml.append("<PageMan>");
		xml.append("<CurrentPage>");
		xml.append(jumpToPage);
		xml.append("</CurrentPage>");
		xml.append("<MaxPage>");
		xml.append(maxPage);
		xml.append("</MaxPage>");
		xml.append("<MaxRow>");
		xml.append(maxRow);
		xml.append("</MaxRow>");
		xml.append("<RowsPerPage>");
		xml.append(rowsPerPage);
		xml.append("</RowsPerPage>");
		xml.append(dataXML);
		xml.append("</PageMan>");
		return xml.toString();
	}

	public static String getResultListXML(Map reqH, DBConn dbc) throws Exception{
		int jumpPage = 1;
		if (reqH.get("jumpPage") != null) {
			jumpPage = (Integer.parseInt(reqH.get("jumpPage") + ""));
		}
		if (reqH.get("rowsPerPage") != null) {
			return getResultListXML(reqH.get("SQL") + "",jumpPage, Integer.parseInt(reqH.get("rowsPerPage") + ""), dbc);
		}
		return getResultListXML(reqH.get("SQL") + "", jumpPage, dbc);
	}

	public static String getXMLResultList(Map reqH, DBConn dbc) throws Exception{
		int jumpPage = 1;
		if (reqH.get("jumpPage") != null) {
			jumpPage = Integer.parseInt(reqH.get("jumpPage") + "");
		}
		if (reqH.get("rowsPerPage") != null) {
			return getXMLResultList(reqH.get("SQL") + "",jumpPage, Integer.parseInt(reqH.get("rowsPerPage") + ""), dbc);
		}
		return getXMLResultList(reqH.get("SQL") + "", jumpPage, dbc);
	}
}