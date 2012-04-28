package peanuty.framework.base;

import java.util.*;

public class DBConnection {

    public static final DBConn DBC = DBConn.getInstance();

    /**
     * Return a query result by using Map
     * @param s sql String
     * @return Map result like {ROW0={field0=value0,field1=value1,...},ROW1={field0=value0,field1=value1,...},...}
     * @throws Exception All Exception
     */
    public static Map getResultList(String s) throws Exception{
		return DBC.getResultList(s);
	}

    /**
     * Return a giving scope query result by using Map
     * <br/> for a example:
     * <br/> String s = "select * from example";
     * <br/> Map result = DBConnection.getResultList(s, 50,60);
     * <br/> will return the record result from row 50th to 60th 
     * @param s sql string
     * @param startPos postion from
     * @param endPos postion end
     * @return Map result like {ROW0={field0=value0,field1=value1,...},ROW1={field0=value0,field1=value1,...},...}, All field names will be converted to lower case
     * @throws Exception All exceptions
     */
    public static Map getResultList(String s, int startPos, int endPos) throws Exception{
		return DBC.getResultList(s,startPos,endPos);
	}

    /**
     * Return a query result by using XML
     * <br/> result looks like:
     * <br/> &lt;Items&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;Item&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;row&gt;0&lt;/row&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;field0&gt;value0&lt;/field0&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;field1&gt;value1&lt;/field1&gt;
     * <br/> ...
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;/Item&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;Item&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;row&gt;1&lt;/row&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;field0&gt;value0&lt;/field0&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;field1&gt;value1&lt;/field1&gt;
     * <br/> ...
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;/Item&gt;
     * <br/> ...
     * <br/> &lt;/Items&gt;
     * @param s sql string
     * @return XML result (All field names will be converted to lower case)
     * @throws Exception All exceptions
     */
    public static String getResultListXML(String s) throws Exception{
		return DBC.getResultListXML(s);
	}

    /**
     * Return a query result by using XML
     * <br/> result looks like:
     * <br/> &lt;Items&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;Item row=0 field0=value0 field1=value1 .../&gt;
     * <br/> &nbsp;&nbsp;&nbsp;&nbsp;
     * &lt;Item row=1 field0=value0 field1=value1 .../&gt;
     * <br/> ...
     * <br/> &lt;/Items&gt;
     * @param s sql string
     * @return XML result (All field names will be converted to lower case)
     * @throws Exception All exceptions
     */
    public static String getXMLResultList(String s) throws Exception{
		return DBC.getXMLResultList(s);
	}

    /**
     * Return a giving scope query result by using XML
     * <br/> for a example:
     * <br/> String s = "select * from example";
     * <br/> Map result = DBConnection.getResultListXML(s, 50,60);
     * <br/> will return the record result from row 50th to 60th
     * @param s sql string
     * @param startPos postion from
     * @param endPos postion end
     * @return XML result
     * @throws Exception All exceptions
     */
    public static String getResultListXML(String s, int startPos, int endPos) throws Exception{
		return DBC.getResultListXML(s, startPos, endPos);
	}

    /**
     * Return a giving scope query result by using XML
     * <br/> for a example:
     * <br/> String s = "select * from example";
     * <br/> Map result = DBConnection.getXMLResultList(s, 50,60);
     * <br/> will return the record result from row 50th to 60th
     * @param s sql string
     * @param startPos postion from
     * @param endPos postion end
     * @return XML result
     * @throws Exception All exceptions
     */
    public static String getXMLResultList(String s, int startPos, int endPos) throws Exception{
		return DBC.getXMLResultList(s, startPos, endPos);
	}

    /**
     * Return the record result size of a giving sql string
     * @param s sql string
     * @return Record size
     * @throws Exception  All exceptions
     */
    public static int getResultSize(String s) throws Exception{
		return DBC.getResultSize(s);
	}

    /**
     * Return the value of the giving sequency name (ONLY FOR ORACLE)
     * @param seq Sequency name
     * @param next true-means get next value, false-means get current value
     * @return The ID
     * @throws Exception All Exceptions
     */
    public static String createID(String seq, boolean next) throws Exception{
		return DBC.createID(seq,next);
	}

    /**
     * Execute a sql command
     * @param sql sql string
     * @return The result
     * @throws Exception All exceptions
     */
    public static boolean execSql(String sql) throws Exception{
		return DBC.execSql(sql);
	}

    /**
     * Get the first row record of the result
     * @param sql sql string
     * @return First row record
     * @throws Exception All exceptions
     */
    public static Map getRow0(String sql) throws Exception{
		return DBC.getRow0(sql);
	}

    /**
     * Execute a batch of sql commands
     * @param sqlH sql commands like {ROW0=sql0,ROW1=sql1,....}
     * @return The result
     * @throws Exception All exceptions
     */
    public static boolean execSql(Map<String,String> sqlH) throws Exception{
		return DBC.execSql(sqlH);
	}

    /**
     * Execute a batch of sql commands
     * @param sqlL sql commands giving by List
     * @return The result
     * @throws Exception All exceptions
     */
    public static boolean execSql(List sqlL) throws Exception{
		return DBC.execSql(sqlL);
	}

    /**
     * Update a clob field (Oracle only)
     * @param sql sql string like "select {field} from tablename for update"
     * @param content Clob field content
     * @return the result
     * @throws Exception All exceptions
     */
    public static boolean updateClob(String sql, String content) throws Exception{
		return DBC.updateClob(sql,content);
	}

    /**
     * Update one or more clob fields (Oracle only)
     * @param sql sql string like "select field0, field1, ... from tablename for update"
     * @param content clobs' content
     * @return The Result
     * @throws Exception All exceptions
     */
    public static boolean updateClob(String sql, String[] content) throws Exception{
		return DBC.updateClob(sql,content);
	}

    /**
     * Execute a batch of sql commands with clob fields (Oracle only)
     * @param sqlgroups Collection, item type should be java.util.Map<String,String,String[]> like this {update=update tablename set ...,select=select field0, field1,... from tablename for update, clobcontents={contentForField0, contentForField1...}}
     * @return The result
     * @throws Exception All exceptions
     */
    public static boolean updateClob(Collection sqlgroups) throws Exception{
	    return DBC.updateClob(sqlgroups);
    }

    /**
     * Get clob field value (Oracle only)
     * @param clobField Clob Field Object
     * @return String List, every item per line
     * @throws Exception All exceptions
     */
    public static List getClobValue(Object clobField) throws Exception{
		return DBC.getClobValue(clobField);
	}

    /**
     * Get clob field string (Oracle only)
     * @param clobField Clob Field Object
     * @return String String
     * @throws Exception All exceptions
     */
    public static String getClobString(Object clobField) throws Exception{
		return DBC.getClobString(clobField);
	}

    /**
     * Get clob string from a record result map
     * @param records Record result
     * @param tag Clob field tag
     * @return Clob stirng
     * @throws Exception All exceptions
     */
    public static String showClob(Map<String,Map<String,Object>> records, String tag) throws Exception{
        return DBC.showClob(records,tag);
    }

    /**
     * Get the database character encoding      <br/>
     * Defined at &lt;dbconfig default="true" dataSource="jdbc/exampleDS" type="oracle" encoding="gbk" sequencyTable="t_sequency" sequencyCacheSize="20"/&gt; of "WEB-INF/xml/sql.xml"
     * @return database character encoding
     */
    synchronized public String getDataBaseEncoding(){
        return DBC.getDataBaseEncoding();
    }

    /**
     * Get the database type string<br/>
     * Defined at &lt;dbconfig default="true" dataSource="jdbc/exampleDS" type="oracle" encoding="gbk" sequencyTable="t_sequency" sequencyCacheSize="20"/&gt; of "WEB-INF/xml/sql.xml"
     * @return database type string
     */
    synchronized public String getDataBaseType(){
        return DBC.getDataBaseType();
    }

    /**
     * Get sequency next value
     * @param seqName Sequency name
     * @return Next value
     * @throws Exception All exceptions
     */
    synchronized public String getSequencyNextValue(String seqName) throws Exception{
		return DBC.getSequencyNextValue(seqName);
	}

    /**
     * Get sequency current value
     * @param seqName Sequency name
     * @return Current value
     * @throws Exception All exceptions
     */
    synchronized public String getSequencyCurrentValue(String seqName) throws Exception{
		return DBC.getSequencyCurrentValue(seqName);
	}

    static public boolean isORACLE(){
        return DBC.isORACLE();
    }

    static public boolean isMYSQL(){
        return DBC.isMYSQL();
    }

    static public boolean isMSSQL(){
        return DBC.isMSSQL();
    }

    /**
     * Get sql current date string
     * @return Oracle-"sysdate", Sql Server-"getdate()", MySql-"now()", Others-"getdate()"
     */
    static public String getSysDate() {
        return DBC.getSysDate();
    }
}