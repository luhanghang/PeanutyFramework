package peanuty.framework.base;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import java.util.*;

/**
 * <p/>
 * Title: BaseDBBean
 * </p>
 * <p/>
 * Description: Base Class based on DB operation
 * <br/>All Class based on database should extends this class
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 *
 * @author Lu Hang
 * @version 1.0
 */

public abstract class BaseDBBean extends PageControlBase {
    protected String TABLE; //binded tablename
    private String TABLEINIT;
    protected String TABLEALIAS;

    public String PK; //pk field name
    public String PKInsert;
    public int PKTYPE; //pk field type
    protected String ORDERBY; //default order field
    protected String SORTBY; //default order

    private static final String SQLFILE = "sql.xml";
    protected static final int PT_NUMBER = 0;
    protected static final int PT_TEXT = 1;
    protected static final String DESC = " desc";
    protected static final String ACES = "";

    protected DBConn dbConnection;

    public BaseDBBean() {
        String[] tmp = this.getClass().getName().split("\\.");
        this.TABLEALIAS = tmp[tmp.length - 1];
        this.bindTable(this.TABLEALIAS);
    }

    protected void bindTable(String tableAlias){
        this.tableInit(this.getTable(tableAlias));
    }

    /**
     * Init the tablename of the Instance to bind
     *
     * @param tablename Table to bind
     */
    protected void tableInit(String tablename) {
        try {
            this.TABLE = tablename;
            this.TABLEINIT = tablename;
            this.PK = "id"; //pk field name
            this.PKTYPE = PT_NUMBER; //pk field type
            this.ORDERBY = PK; //default order field
            this.SORTBY = ACES; //default order
            Element sqlroot = getSqlXML();
            //Element table = XMLHandler.getElementByAttribute(sqlroot.getChildren(),"name", tablename);
            Element table = (Element) XPath.selectSingleNode(sqlroot, "//table[@name='" + tablename + "'']");
            if (table == null) {
                throw new Exception("Table:" + tablename + " not defined in sql.xml");
            }
            Attribute pkAttr = table.getAttribute("pk");
            if (pkAttr != null) {
                this.PK = pkAttr.getValue();
                if (table.getAttributeValue("pktype").equals("text")) {
                    this.PKTYPE = PT_TEXT;
                } else {
                    this.PKTYPE = PT_NUMBER;
                }
                this.ORDERBY = this.PK;
            }
            Attribute sortAttr = table.getAttribute("defaultSortBy");
            if (sortAttr != null) {
                this.SORTBY = sortAttr.getValue();
            }
            this.PKInsert = this.getPKInsert();
            this.dbConnection = this.getDBConnection(table);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTableName() {
        return this.TABLE;
    }

    private DBConn getDBConnection(Element tableElement) {
        Element dbElement = tableElement.getParentElement();
        String dataSource = dbElement.getAttributeValue("dataSource");
        return DBConn.getInstance(dataSource);
    }

    //protected abstract void init();

    /**
     * Compose sql string
     *
     * @param sql Src sql string to be composed
     * @return Sql string
     * @throws Exception All exceptions
     */
    protected String sql(String sql) throws Exception {
        sql = this.convertSql(this.convertCondition(sql), this.TABLE);
        this.TABLE = this.TABLEINIT;
        //sql = new String(convertsql(convertCondition(sql)).getBytes("GBK"), "8859_1");
        return sql;
    }

    protected String sql(String sql, String table) throws Exception {
        sql = this.convertSql(this.convertCondition(sql), table);
        return sql;
    }

    private String convertSql(String sql, String table) {
        if (sql.indexOf("{") == -1) {
            sql = sql.replaceAll("TABLE", table);
            sql = sql.replaceAll("TABLE", this.TABLE);
            sql = sql.replaceAll("lUHANGJINHUA", "{");
            sql = sql.replaceAll("LUHANGJINHUa", "}");
            sql = sql.replaceAll("TlAuBhLaEng", "TABLE");
            sql = sql.replaceAll("luHANgJinHUa", "\\\\");
            sql = sql.replaceAll("LuanGjINHUA", "\\$");
            if (this.dbConnection.getDataBaseType().equalsIgnoreCase("mysql")) {
                sql = sql.replaceAll("\\\\", "\\\\\\\\");
            }
            return sql;
        }
        int split = sql.indexOf('}') + 1;
        String pre = sql.substring(0, split);
        String post = sql.substring(split, sql.length());
        String value = pre.substring(pre.indexOf('{') + 1, pre.length() - 1);
        sql = pre.replaceAll("\\x7b" + value + "\\x7d", __(value)) + post;
        // \\x7b"{", \\x7d"}";
        return this.convertSql(sql, table);
    }

    private String convertCondition(String sql) {
        int a = sql.indexOf("[");
        int b = sql.indexOf("where");
        if (a <= b) {
            return sql;
        }
        int begin = sql.indexOf('[');
        int end = sql.indexOf(']');
        String condition = sql.substring(begin + 1, end);
        String field = condition.substring(condition.indexOf('{') + 1,
                condition.indexOf('}'));
        if (this._(field).equals("")) {
            sql = sql.substring(0, begin)
                    + sql.substring(end + 1, sql.length());
        } else {
            sql = sql.replaceFirst("\\x5b", "").replaceFirst("\\x5d", "");
        }
        return this.convertCondition(sql);
    }

    /**
     * Return the result of "select * from TABLENAME order by ORDERBY SORT"
     *
     * @return Map result
     * @throws Exception All exceptions
     */
    public Map<String, Map<String, Object>> commonlist() throws Exception {
        return this.dbConnection.getResultList(sql(_commonList()));
    }

    /**
     * Return the common list sql string as "select * from TABLENAME order by ORDERBY SORT"
     *
     * @return "select * from TABLENAME order by ORDERBY SORT"
     */
    public String _commonList() {
        return "Select * from TABLE order by " + this.ORDERBY + " " + this.SORTBY;
    }

    /**
     * Return the result of "select * from TABLENAME"
     *
     * @return Map result
     * @throws Exception All exceptions
     */
    public Map<String, Map<String, Object>> listALL() throws Exception {
        return this.dbConnection.getResultList(sql(_listALL()));
    }

    /**
     * Return the sql string as "select * from TABLENAME"
     *
     * @return "select * from TABLENAME"
     */
    public String _listALL() {
        return "Select * from TABLE";
    }

    private String whereClause;

    private String addWhereClause() {
        if (this.whereClause == null) {
            StringBuffer w = new StringBuffer();
            w.append(" where 1 = 1");
            String[] pks = this.PK.split(",");
            for (String pk : pks) {
                char type = pk.charAt(pk.length() - 1);
                String p = pk.substring(0, pk.length() - 1);
                switch (type) {
                    case'T':
                        w.append(" and ").append(p).append("=");
                        w.append("'{").append(p).append("}'");
                        break;
                    case'N':
                        w.append(" and ").append(p).append("=");
                        w.append("{").append(p).append("}");
                        break;
                    default:
                        w.append(" and ").append(pk).append("=");
                        if (this.PKTYPE == PT_TEXT) {
                            w.append("'{").append(pk).append("}'");
                        } else {
                            w.append("{").append(pk).append("}");
                        }
                        break;
                }
            }
            this.whereClause = w.toString();
        }
        return this.whereClause;
    }


    /**
     * Delete all data ("delete from TABLE");
     *
     * @return the result
     * @throws Exception All exceptions
     */
    protected boolean emptyTable() throws Exception {
        return this.dbConnection.execSql(sql("delete from TABLE"));
    }

    /**
     * Truncate table ("truncate TABLE")
     *
     * @return the result
     * @throws Exception All exceptions
     */
    protected boolean truncateTable() throws Exception {
        return this.dbConnection.execSql(sql("truncate TABLE"));
    }

    /**
     * Return Compose sql string for delete a record
     *
     * @return the string "delete from TABLE where 1 = 1 [ and pk1={pk1} and pk2={pk2} ...]"
     */
    protected String _delete() {
        return "delete from TABLE" + this.addWhereClause();
    }

    protected String _multiDelete(){
        this.collectIds();
        return "delete from TABLE where " + this.PK + " in ({ids})";
    }

    protected void collectIds(){
        if(_("ids").equals("")){
            String[] ids = this.req.getParameterValues(this.PK);
            StringBuffer sbIds = new StringBuffer();
            for(String id : ids){
                sbIds.append(id).append(",");
            }
            sbIds.deleteCharAt(sbIds.length() - 1);
            this._reqH.put("ids", sbIds.toString());
        }
    }

    protected boolean multiDelete() throws Exception{
        return this.dbConnection.execSql(this.sql(this._multiDelete()));
    }

    protected String _disable() {
        return "update TABLE set enabled = 0" + this.addWhereClause();
    }

    protected String _enable() {
        return "update TABLE set enabled = 1" + this.addWhereClause();
    }


    /**
     * Find a record ("select * from TABLE where 1 = 1 [ and pk1={pk1} and pk2={pk2} ...]")
     *
     * @return the Map record
     * @throws Exception All exceptions
     */
    public Map<String, Object> find() throws Exception {
        String sql = "Select * from TABLE" + this.addWhereClause();
        return this.dbConnection.getRow0(this.sql(sql));
    }

    public String _find() throws Exception {
        return "Select * from TABLE" + this.addWhereClause();
    }


    /**
     * Insert a new record
     *
     * @return the result
     * @throws Exception All exceptions
     */
    public boolean insert() throws Exception {
        String sql = this.sql(this._insert());
        if (this.clobs == null) {
            return this.dbConnection.execSql(sql);
        }
        return this.dbConnection.execSql(sql) && this.updateClob();
    }

    /**
     * Update clob fields
     *
     * @return the result
     * @throws Exception All exceptions
     */
    protected boolean updateClob() throws Exception {
        if (this.clobs == null || this.dbConnection.isORACLE()) {
            return true;
        }
        String[] clobContent = new String[this.clobs.size()];
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        for (int i = 0; i < this.clobs.size(); i++) {
            String field = this.clobs.get(i);
            sql.append(field).append(",");
            clobContent[i] = _(field + "C");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" from ").append(this.TABLE).append(" where ").append(this.PK).append(" = ");
        if (this.PKTYPE == PT_NUMBER) {
            sql.append("[{").append(this.PK).append("N}][{").append(this.PK).append("}]");
        } else {
            sql.append("['{").append(this.PK).append("T}']['{").append(this.PK).append("}']");
        }
        sql.append(" for update");
        this.clobs = null;
        return this.dbConnection.updateClob(this.sql(sql.toString()), clobContent);
    }

    /**
     * Update a record
     *
     * @return the result
     * @throws Exception All exceptions
     */
    public boolean update() throws Exception {
        String sql = this.sql(this._update());
        if (this.clobs == null && !this.dbConnection.isORACLE()) {
            return this.dbConnection.execSql(sql);
        }
        return this.dbConnection.execSql(sql) && this.updateClob();
    }


    /**
     * Delete a record
     *
     * @return the result
     * @throws Exception All exceptions
     */
    public boolean delete() throws Exception {
        return this.dbConnection.execSql(this.sql(this._delete()));
    }

    public boolean disable() throws Exception {
        return DBConnection.execSql(sql(_disable()));
    }

    public boolean enable() throws Exception {
        return DBConnection.execSql(sql(_enable()));
    }

    private List<String> clobs;

    /**
     * Compose sql string for inserting a record
     *
     * @return Insert string
     */
    protected String _insert() {
        int i = 0;
        StringBuffer name = new StringBuffer();
        StringBuffer value = new StringBuffer();
        Set tags = this._reqH.keySet();
        String field, tag;
        for (Object tagObj : tags) {
            tag = (String) tagObj;
            int l = tag.length();
            if (l <= 1) {
                continue;
            }
            char c = tag.charAt(--l);
            field = tag.substring(0, l);
            switch (c) {
                case'T':
                    name.append(field).append(",");
                    value.append("'{").append(tag).append("}',");
                    break;
                case'N':
                    name.append(field).append(",");
                    value.append("{").append(tag).append("},");
                    break;
                case'D':
                    name.append(field).append(",");
                    if (this.dbConnection.isORACLE()) {
                        value.append("to_date('").append(_(tag)).append("','YYYY-MM-DD'),"); // oracle
                    } else {
                        value.append("'{").append(tag).append("}',"); // mysql & SQL Server ...
                    }
                    break;
                case'C': // Clob
                    if (this.dbConnection.isORACLE()) {
                        name.append(field).append(",");
                        value.append("empty_clob(),");
                        if (this.clobs == null) {
                            this.clobs = new ArrayList<String>();
                        }
                        this.clobs.add(i++, field);
                    } else {
                        name.append(field).append(",");
                        value.append("'{").append(tag).append("}',");
                    }
                    break;
                default: // do nothing
                    break;
            }
        }
        name.deleteCharAt(name.length() - 1);
        value.deleteCharAt(value.length() - 1);
        StringBuffer sql = new StringBuffer("insert into TABLE (");
        sql.append(name);
        sql.append(") values (");
        sql.append(value);
        sql.append(")");
        return sql.toString();
    }

    /**
     * Compose sql string for updating a record
     *
     * @return update string
     */
    protected String _update() {
        int i = 0;
        Set tags = this._reqH.keySet();
        String field;
        String tag;
        StringBuffer setvalues = new StringBuffer(" set ");
        for (Object tagObj : tags) {
            tag = (String) tagObj;
            int l = tag.length();
            if (l <= 1) {
                continue;
            }
            char c = tag.charAt(--l);
            field = tag.substring(0, l);
            switch (c) {
                case'T':
                    setvalues.append(field).append("='{").append(tag).append("}',");
                    break;
                case'N':
                    setvalues.append(field).append("={").append(tag).append("},");
                    break;
                case'D':
                    if (this.dbConnection.isORACLE()) {
                        setvalues.append(field).append("=to_date('").append(_(tag)).append("','YYYY-MM-DD'),"); // oracle
                    } else {
                        setvalues.append(field).append("='{").append(tag).append("}',"); // mysql & SQL
                    }
                    break;
                case'C':
                    if (this.dbConnection.isORACLE()) {
                        setvalues.append(field).append("=empty_clob(),"); // Oracle
                        if (this.clobs == null) {
                            this.clobs = new ArrayList<String>();
                        }
                        this.clobs.add(i++, field);
                    } else {
                        setvalues.append(field).append("='{").append(tag).append("}',");
                    }
                    break;
                default:
                    break;
            }
        }
        setvalues.deleteCharAt(setvalues.length() - 1);
        StringBuffer sql = new StringBuffer("update TABLE");
        sql.append(setvalues);
        sql.append(this.addWhereClause());
        return sql.toString();
    }

    private static Element sqlXML;

    private static Element getSqlXML() {
        if (sqlXML == null || Config.getDebugMode()) {
            sqlXML = XMLHandler.openXML(SQLFILE);
        }
        return sqlXML;
    }

    /**
     * Get a sql statement from sql.xml
     *
     * @param statementname Statement name
     * @return sql string
     */
    protected String getSql(String statementname) {
        try {
            Element sqlroot = getSqlXML();
            Element statement = (Element) XPath.selectSingleNode(sqlroot, "//statement[@name='" + statementname + "']");
            if (statement == null) {
                return null;
            }
            String table = statement.getParentElement().getAttributeValue("name");
            return this.sql(statement.getTextTrim(), table);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get sql statements from sql.xml which statement name is statementname
     *
     * @param statementname Statement name
     * @return sql strings
     * @throws Exception All exceptions
     */
    protected List<String> getSqlList(String statementname) throws Exception {
        Element sqlroot = getSqlXML();
        List statements = XPath.selectNodes(sqlroot, "//statement[@name='" + statementname + "']");

        if (statements == null) {
            return null;
        }
        List<String> sqls = new ArrayList<String>();
        for (Object item : statements) {
            Element statement = (Element) item;
            String table = statement.getParentElement().getAttributeValue("name");
            sqls.add(this.sql(statement.getTextTrim(), table));
        }
        return sqls;
    }

    protected String getTable(String alias) {
        try {
            Element root = getSqlXML();
            Element table = (Element) XPath.selectSingleNode(root, "//table[@alias='" + alias + "']");
            if (table == null) {
                throw new NullPointerException();
            }
            //return XMLHandler.getElementByAttribute(root.getChildren("table"), "alias", alias).getAttributeValue("name");
            return table.getAttributeValue("name");
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            log("Table Alias:" + alias + " not defined in sql.xml");
        }
        return null;
    }

    /**
     * Compose sql string for calling a storage procedure
     *
     * @param spName storage procedure
     * @param paras  parameters
     * @return sql string
     */
    protected String callStorageProcedure(String spName, Collection paras) {
        StringBuffer sql = new StringBuffer();
        sql.append("{call ");
        sql.append(spName);
        if (paras != null && paras.size() > 0) {
            sql.append("(");
            Iterator parasIt = paras.iterator();
            boolean hasPara = false;
            for (; parasIt.hasNext();) {
                hasPara = true;
                sql.append("'");
                sql.append((String) parasIt.next());
                sql.append("',");
            }
            if (hasPara) {
                sql.delete(sql.length() - 1, sql.length());
            }
            sql.append(")");
        }
        sql.append("}");
        // log(sql);
        return sql.toString();
    }

    private String __(String tag) {
        String value = toDB(tag);
        value = value.replaceAll("\\x7b", "lUHANGJINHUA");
        value = value.replaceAll("\\x7d", "LUHANGJINHUa");
        value = value.replaceAll("TABLE", "TlAuBhLaEng");
        value = value.replaceAll("\\\\", "luHANgJinHUa");
        value = value.replaceAll("\\$", "LuanGjINHUA");
        return value;
    }

    protected String getSysDate() {
        return this.dbConnection.getSysDate();
    }

    private String getPKInsert() {
        return this.PK + (this.PKTYPE == PT_NUMBER ? "N" : "T");
	}
}