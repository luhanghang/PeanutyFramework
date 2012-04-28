package peanuty.framework.base;

import oracle.sql.CLOB;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import peanuty.framework.util.DataConvert;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import java.io.BufferedReader;
import java.io.Writer;
import java.sql.*;
import java.util.*;

public class DBConn {

    final static public String ORACLE = "oracle";
    final static public String MSSQL = "mssql";
    final static public String MYSQL = "mysql";
    final public static Map<String,String> SYSDATE = new HashMap<String,String>();
    static {
        SYSDATE.put(ORACLE,"sysdate");
        SYSDATE.put(MSSQL,"getdate()");
        SYSDATE.put(MYSQL,"now()");
    }

    private static final Map<String,DBConn> instancePool = new HashMap<String,DBConn>();
      
    private DBConn(String dataSource){
        this.dataSource = dataSource;
        sequencyCache = new HashMap<String,int[]>();
    }

    /**
     * Get a DBConn Instance by using the default datasource defined in sql.xml
     * @return A DBConn Instance
     */
    public static DBConn getInstance(){
        return getInstance(DEFAULTDATASOURCE);
    }

    /**
     * Get a DBConn Instance by using the giving datasource
     * @param dbSource Data source string
     * @return A DBConn Instance
     */
    public static DBConn getInstance(String dbSource){
        synchronized(instancePool){
            DBConn dc = instancePool.get(dbSource);
            if(dc == null){
                dc = new DBConn(dbSource);
                instancePool.put(dbSource, dc);
            }
            return dc;
        }
    }

    private final static String DEFAULTDATASOURCE = Config.getDefaultDataSource();
    private String dataSource;

    /**
     * Return a query result by using Map
     * @param s sql String
     * @return Map result like {ROW0={field0=value0,field1=value1,...},ROW1={field0=value0,field1=value1,...},...}
     * @throws Exception All Exception
     */
    public Map<String, Map<String,Object>> getResultList(String s) throws Exception{
		return this.getResultList(s, null);
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
    public Map<String,Map<String,Object>> getResultList(String s, int startPos, int endPos) throws Exception{
		return this.getResultList(s, startPos, endPos, null);
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
    public String getResultListXML(String s) throws Exception{
		StringBuffer xml = new StringBuffer();
		xml.append("<Items>");

		int j = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		log("getResultList->" + s);

		try {
			conn = this.getConnection();
			if (conn == null) {
				log("Connection Failed");
			} else {
				try {
					stmt = conn.createStatement();
				} catch (java.sql.SQLException cursorE) {
					if (cursorE.getMessage().trim().startsWith("ORA-01000")) {
						log("get statement 2nd time");
						conn.close();
						conn = this.getConnection();
						stmt = conn.createStatement();
					}
                    throw cursorE;
                }
				if (stmt == null) {
					log("Stmt Failed");
				}
				rs = stmt.executeQuery(s);

				if (rs == null) {
					log(" RS is null");
				} else {
					ResultSetMetaData metadata = rs.getMetaData();
					int colCount = metadata.getColumnCount();
					while (rs.next()) {
						xml.append("<Item><row>");
						xml.append(j);
						xml.append("</row>");
						for (int i = 1; i <= colCount; i++) {
							String colLabel = metadata.getColumnLabel(i)
									.toLowerCase();
							Object colValue = rs.getObject(i);
							if (colValue != null) {
								xml.append("<");
								xml.append(colLabel);
								xml.append(">");
								xml.append(DataConvert.escapeXMLTags(colValue + ""));
								xml.append("</");
								xml.append(colLabel);
								xml.append(">");
							}
						}
						xml.append("</Item>");
						++j;
					}
				}
				// log("size=" + lst.size());
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			// release the resource !!!
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
                    ex.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		xml.append("</Items>");
		return xml.toString();
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
    public String getXMLResultList(String s) throws Exception{
		StringBuffer xml = new StringBuffer();
		xml.append("<Items>");

		int j = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		log("getResultList->" + s);

		try {
			conn = this.getConnection();
			if (conn == null) {
				log("Connection Failed");
			} else {
				try {
					stmt = conn.createStatement();
				} catch (Exception ex) {
					if (ex.getMessage().trim().startsWith("ORA-01000")) {
						log("get statement 2nd time");
						conn.close();
						conn = this.getConnection();
						stmt = conn.createStatement();
					}
                    throw ex;
                }
				if (stmt == null) {
					log("Stmt Failed");
				}
				rs = stmt.executeQuery(s);

				if (rs == null) {
					log(" RS is null");
				} else {
					ResultSetMetaData metadata = rs.getMetaData();
					int colCount = metadata.getColumnCount();
					while (rs.next()) {
						xml.append("<Item row=\"");
						xml.append(j);
						xml.append("\"");
						for (int i = 1; i <= colCount; i++) {
							String colLabel = metadata.getColumnLabel(i)
									.toLowerCase();
							Object colValue = rs.getObject(i);
							if (colValue != null) {
								xml.append(" ");
								xml.append(colLabel);
								xml.append("=\"");
								xml.append(DataConvert.escapeXMLTags(colValue + ""));
								xml.append("\"");
							}
						}
						xml.append("/>");
						++j;
					}
				}
				// log("size=" + lst.size());
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			// release the resource !!!
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		xml.append("</Items>");
		return xml.toString();
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
    public String getResultListXML(String s, int startPos, int endPos) throws Exception{
		log("sql->" + s);
		StringBuffer xml = new StringBuffer();
		xml.append("<Items>");
		int j = 0;
		int rowcnt = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = this.getConnection();
			if (conn == null) {
				log("Connection Failed");
			} else {
				try {
					stmt = conn.createStatement();
				} catch (java.sql.SQLException cursorE) {
					log("Get Statement error:" + cursorE.getMessage());
					if (cursorE.getMessage().trim().startsWith("ORA-01000")) {
						log("get statement 2nd time");
						conn.close();
						conn = this.getConnection();
						stmt = conn.createStatement();
					}
                    throw cursorE;
                }
				if (stmt == null) {
					log("Stmt Failed");
				}
				rs = stmt.executeQuery(s);

				if (rs == null) {
					log(" RS is null");
				} else {
					ResultSetMetaData metadata = rs.getMetaData();
					int colCount = metadata.getColumnCount();
					while (rs.next()) {
						rowcnt++;
						if (rowcnt >= startPos && rowcnt < endPos) {
							xml.append("<Item><row>");
							xml.append(j);
							xml.append("</row>");
							for (int i = 1; i <= colCount; i++) {
								String colLabel = metadata.getColumnLabel(i)
										.toLowerCase();

								Object colValue = rs.getObject(i);
								if (colValue == null) {
								} else {
									xml.append("<");
									xml.append(colLabel);
									xml.append(">");
									xml.append(DataConvert.escapeXMLTags(colValue + ""));
									xml.append("</");
									xml.append(colLabel);
									xml.append(">");
								}
							}
							++j;
							xml.append("</Item>");
						} // while
					}
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			// release the resource !!!
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		xml.append("</Items>");
		return xml.toString();
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
    public String getXMLResultList(String s, int startPos, int endPos) throws Exception{
		log("sql->" + s);
		StringBuffer xml = new StringBuffer();
		xml.append("<Items>");
		int j = 0;
		int rowcnt = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = this.getConnection();
			if (conn == null) {
				log("Connection Failed");
			} else {
				try {
					stmt = conn.createStatement();
				} catch (java.sql.SQLException cursorE) {
					if (cursorE.getMessage().trim().startsWith("ORA-01000")) {
						log("get statement 2nd time");
						conn.close();
						conn = this.getConnection();
						stmt = conn.createStatement();
					}
                    throw cursorE;
                }
				if (stmt == null) {
					log("Stmt Failed");
				}
				rs = stmt.executeQuery(s);

				if (rs == null) {
					log(" RS is null");
				} else {
					ResultSetMetaData metadata = rs.getMetaData();
					int colCount = metadata.getColumnCount();
					while (rs.next()) {
						rowcnt++;
						if (rowcnt >= startPos && rowcnt < endPos) {
							xml.append("<Item row=\"");
							xml.append(j);
							xml.append("\"");
							for (int i = 1; i <= colCount; i++) {
								String colLabel = metadata.getColumnLabel(i)
										.toLowerCase();
								Object colValue = rs.getObject(i);
								if (colValue == null) {
								} else {
									xml.append(" ");
									xml.append(colLabel);
									xml.append("=\"");
									xml.append(DataConvert.escapeXMLTags(colValue + ""));
									xml.append("\"");
								}
							}
							++j;
							xml.append("/>");
						} // while
					}
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			// release the resource !!!
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		xml.append("</Items>");
		return xml.toString();
	}


    /*
    private static Object getField(String sql, String fieldName, String row) throws Exception{
		Object ret = null;
		if ((sql == null) || (fieldName == null) || (row == null)) {
			return null;
		}
		Map r = getRow(sql, row);
		if (r != null) {
			ret = r.get(fieldName);
		}
		return ret;
	}
	*/

	private java.sql.Connection getConnection() throws Exception {
		java.sql.Connection conn = null;
		Context initCtx = null;

		try {
			initCtx = new InitialContext();
            javax.sql.DataSource ds;

			if (Config.getServerType().equalsIgnoreCase("WEBLOGIC")) {
				ds = (javax.sql.DataSource) initCtx.lookup(this.dataSource);
			} else {
				ds = (javax.sql.DataSource) initCtx.lookup("java:comp/env/"
						+ this.dataSource);
			}

			conn = ds.getConnection();
			if (conn == null) {
				log("(?) (DB) Connection Failed!");
			}
		} catch (NamingException e) {
			log("(?) (DB) Name serivice failed when try to get Pool connection");
			throw e;
			// a failure occurred
		} finally {
			try {
				// envCtx.close();
                if(null != initCtx){
                    initCtx.close();
                }
            } catch (Exception e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	private Map<String,Map<String,Object>> getResultList(String s, Vector vTableName) throws Exception{
		Map<String,Map<String,Object>> lst = new HashMap<String,Map<String,Object>>();
		int j = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		log("getResultList->" + s);

		try {
			conn = this.getConnection();
			if (conn == null) {
				log("Connection Failed");
			} else {
				try {
					stmt = conn.createStatement();
				} catch (java.sql.SQLException cursorE) {
					if (cursorE.getMessage().trim().startsWith("ORA-01000")) {
						log("get statement 2nd time");
						conn.close();
						conn = this.getConnection();
						stmt = conn.createStatement();
					}
                    throw cursorE;
                }
				if (stmt == null) {
					log("Stmt Failed");
				}
				rs = stmt.executeQuery(s);

				if (rs == null) {
					log(" RS is null");
				} else {
					ResultSetMetaData metadata = rs.getMetaData();
					int colCount = metadata.getColumnCount();
					while (rs.next()) {
                        Map<String,Object> row = new HashMap<String,Object>();
						String rowStr = "ROW" + j;

						for (int i = 1; i <= colCount; i++) {
							String colLabel = metadata.getColumnLabel(i)
									.toLowerCase();
							String tableName = "";
							if (vTableName != null) {
								int vSize = vTableName.size();
								if ((i - 1) < vSize) {
									tableName = vTableName.get(i - 1) + ".";
								}
							}
							Object colValue = rs.getObject(i);
							if (colValue == null) {
								// do not put into hash
							} else {
								String tag = tableName + colLabel;
								if (row.containsKey(tag)) {
									tag = tag + "_" + i;
								}
								row.put(tag, colValue);
							}
						}
						++j;
						lst.put(rowStr, row);
					}
				}
				// log("size=" + lst.size());
			}
		} catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
		} finally {
			// release the resource !!!
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return lst;
	}

    private Map<String,Map<String,Object>> getResultList(String s, int startPos, int endPos,
			Vector vTableName) throws Exception{
		log("sql->" + s);
		Map<String,Map<String,Object>> lst = new HashMap<String,Map<String,Object>>();
		int j = 0;
		int rowcnt = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = this.getConnection();
			if (conn == null) {
				log("Connection Failed");
			} else {
				try {
					stmt = conn.createStatement();
				} catch (java.sql.SQLException cursorE) {
					if (cursorE.getMessage().trim().startsWith("ORA-01000")) {
						log("get statement 2nd time");
						conn.close();
						conn = this.getConnection();
						stmt = conn.createStatement();
					}
                    throw cursorE;
                }
				if (stmt == null) {
					log("Stmt Failed");
				}
				rs = stmt.executeQuery(s);

				if (rs == null) {
					log(" RS is null");
				} else {
					ResultSetMetaData metadata = rs.getMetaData();
					int colCount = metadata.getColumnCount();
					while (rs.next()) {
						rowcnt++;
						if (rowcnt >= startPos && rowcnt < endPos) {
							Map<String,Object> row = new HashMap<String,Object>();
							String rowStr = "ROW" + j;

							for (int i = 1; i <= colCount; i++) {
								String colLabel = metadata.getColumnLabel(i)
										.toLowerCase();
								String tableName = "";
								if (vTableName != null) {
									int vSize = vTableName.size();
									if ((i - 1) < vSize) {
										tableName = vTableName.get(i - 1) + ".";
									}
								}
								Object colValue = rs.getObject(i);
								if (colValue == null) {
								} else {
									String tag = tableName + colLabel;
									if (row.containsKey(tag)) {
										tag = tag + "_" + i;
									}
									row.put(tag, colValue);
								}
							}
							++j;
							lst.put(rowStr, row);
						} // while
					}
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			// release the resource !!!
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return lst;
	}

    /**
     * Return the record result size of a giving sql string
     * @param s sql string
     * @return Record size
     * @throws Exception  All exceptions
     */
    public int getResultSize(String s) throws Exception{
        StringBuffer sql = new StringBuffer("select count(1) as size from (");
        int orderIdx = s.lastIndexOf("order by") - 1;
		if(orderIdx < 0){
			orderIdx = s.length();
		}
        sql.append(s.substring(0, orderIdx)).append(") GENBYGETSIZE");
        return Integer.parseInt(this.getRow0(sql.toString()).get("size") + "");
	}

    /**
     * Return the value of the giving sequency name (ONLY FOR ORACLE)
     * @param seq Sequency name
     * @param next true-means get next value, false-means get current value
     * @return The ID
     * @throws Exception All Exceptions
     */
    public String createID(String seq, boolean next) throws Exception{
		if (next) {
			String query = "Select " + seq.toUpperCase() + ".NEXTVAL from dual";
			return this.createIDByQuery(query);
		} else {
			String query = "Select " + seq + ".CURRVAL from dual";
			return this.createIDByQuery(query);
		}
	}

	private String createIDByQuery(String query) throws Exception{
		try {
			Map<String, Object> subres = this.getRow0(query);	
            if (null != subres) {
					return subres.get(subres.keySet().iterator().next()) + "";
				}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

    /**
     * Execute a sql command
     * @param sql sql string
     * @return The result
     * @throws Exception All exceptions
     */
    public boolean execSql(String sql) throws Exception{
		Connection conn = null;
		boolean rtn = true;
		log("execSql->" + sql);
        Exception exp = null;
        try {
			conn = this.getConnection();
			if (conn == null) {
				log("Connection Failed");
				rtn = false;
			} else {
				conn.setAutoCommit(false);
				Statement stmt = conn.createStatement();
				if (stmt != null) {
					stmt.executeUpdate(sql);
				}
				conn.commit();
			}
		} catch (Exception e) {
            exp = e;
            rtn = false;
        }
		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					if (!rtn) {
						conn.rollback();
					}
					conn.close();
                    if(exp != null){
                        throw exp;
                    }
                }
			} catch (Exception ex) {
				throw ex;
			}
		}
		return rtn;
	}

	private Map<String,Object> getRow(String sql, String row) throws Exception{
		Map<String, Object> ret = null;
		if ((sql == null) || (row == null)) {
			return null;
		}
		Map<String,Map<String,Object>> rl = this.getResultList(sql);
		if (rl != null) {
			ret = rl.get(row);
		}
		return ret;
	}

    /**
     * Get the first row record of the result
     * @param sql sql string
     * @return First row record
     * @throws Exception All exceptions
     */
    public Map<String,Object> getRow0(String sql) throws Exception{
		return this.getRow(sql, "ROW0");
	}

    /**
     * Execute a batch of sql commands
     * @param sqlH sql commands like {ROW0=sql0,ROW1=sql1,....}
     * @return The result
     * @throws Exception All exceptions
     */
    public boolean execSql(Map<String,String> sqlH) throws Exception{
		Connection conn = null;
		boolean rtn = true;

		int sqlNum = sqlH.size();
        Exception exp = null;
        try {
			conn = this.getConnection();
			if (conn == null) {
				log("(?) (DB) Connection Failed");
				rtn = false;
			} else {
				conn.setAutoCommit(false);
				Statement stmt = conn.createStatement();
				int execCount = 0;
				if (stmt != null) {
					for (int i = 0; i <= sqlNum; i++) {
						String sqlString = sqlH.get("ROW" + i);
						log("(DB) ExecSql ROW" + i + "---> " + sqlString);
						if (null != sqlString) {
							int ExecSqlResultCount = stmt.executeUpdate(sqlString);
							log("(DB) ExecSqlResultCount = "
									+ ExecSqlResultCount);
						} else {
							if (sqlNum != execCount) {
								log("sqlNum = " + sqlNum + " And execCount = "
										+ execCount);
								rtn = false;
							}
							break;
						}
						execCount++;
					}
					if (rtn) {
						conn.commit();
					}
				} else {
					log("(?) (DB) Connection.ExecSql stmt Failed");
					rtn = false;
				}
			}
		} catch (Exception e) {
			rtn = false;
            exp = e;
        }
		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					if (!rtn) {
						conn.rollback();
					}
					conn.close();
                    if(exp != null){
                        throw exp;
                    }
                }
			} catch (Exception ex) {
				throw ex;
			}
		}
		return rtn;
	}

    /**
     * Execute a batch of sql commands
     * @param sqlL sql commands giving by List
     * @return The result
     * @throws Exception All exceptions
     */
    public boolean execSql(List sqlL) throws Exception{
		Connection conn = null;
		boolean rtn = true;

		int sqlNum = sqlL.size();
        Exception exp = null;
        try {
			conn = this.getConnection();
			if (conn == null) {
				log("(?) (DB) Connection Failed");
				rtn = false;
			} else {
				conn.setAutoCommit(false);
				Statement stmt = conn.createStatement();
				int execCount = 0;
				if (stmt != null) {
					for (int i = 0; i < sqlNum; i++) {
						String sqlString = (String) sqlL.get(i);
						log("(DB) ExecSql " + i + "---> " + sqlString);
						if (null != sqlString) {
							int ExecSqlResultCount = stmt.executeUpdate(sqlString);
							log("(DB) ResultCount = " + ExecSqlResultCount);
						} else {
							if (sqlNum != execCount) {
								log("sqlNum = " + sqlNum + " And execCount = "
										+ execCount);
								rtn = false;
							}
							break;
						}
						execCount++;
					}
					if (rtn) {
						conn.commit();
					}
				} else {
					log("(?) (DB) Connection.ExecSql stmt Failed");
					rtn = false;
				}
			}
		} catch (Exception e) {
			rtn = false;
            exp = e;
        }
		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					if (!rtn) {
						conn.rollback();
					}
					conn.close();
                    if(exp != null){
                        throw exp;
                    }
                }
			} catch (Exception ex) {
				throw ex;
			}
		} else {
            throw new Exception("DB Connection Failure!");
        }
		return rtn;
	}

	/*
    public static boolean execSql(String sql,
			java.io.ByteArrayInputStream bInStream, int length) throws Exception{
		Connection conn = null;
		boolean rtn = true;
        Exception exp = null;
        log(sql);

		try {
			conn = DBConnection.getConnection();
			if (conn == null) {
				log("Connection Failed");
				rtn = false;
			} else {
				conn.setAutoCommit(false);
				PreparedStatement preStmt = conn.prepareStatement(sql);
				if (preStmt != null) {
					preStmt.setBinaryStream(1, bInStream, length);
					preStmt.executeQuery();
				}
				conn.commit();
			}
		} catch (Exception e) {
			rtn = false;
            exp = e;
        }

		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					if (!rtn) {
						conn.rollback();
					}
					conn.close();
                    if(exp != null){
                        throw exp;
                    }
                }
			} catch (Exception ex) {
				throw ex;
			}
		}
		return rtn;
	}
	*/

    /**
     * Update a clob field (Oracle only)
     * @param sql sql string like "select {field} from tablename for update"
     * @param content Clob field content
     * @return the result
     * @throws Exception All exceptions
     */
    public boolean updateClob(String sql, String content) throws Exception{
		if (!this.getDataBaseType().equalsIgnoreCase("oracle")) {
			return true;
		}
		String[] cont = new String[1];
		cont[0] = content;
		return this.updateClob(sql, cont);
	}

    /**
     * Update one or more clob fields (Oracle only)
     * @param sql sql string like "select field0, field1, ... from tablename for update"
     * @param content clobs' content
     * @return The Result
     * @throws Exception All exceptions
     */
    public boolean updateClob(String sql, String[] content) throws Exception{
		if (!this.getDataBaseType().equalsIgnoreCase("oracle")) {
			return true;
		}
		Connection conn = null;
        Exception exp = null;
        boolean rtn = true;
		log(sql);

		try {
			conn = this.getConnection();
			Statement stmt = conn.createStatement();
			if (conn == null) {
				System.out
						.println("Connection Failed");
				rtn = false;
			} else {
				conn.setAutoCommit(false);
				ResultSet rs = stmt.executeQuery(sql);
				java.sql.Clob[] clobtt = new java.sql.Clob[content.length];
				if (rs.next()) {
					for (int i = 0; i < content.length; i++) {
						clobtt[i] = rs.getClob(i + 1);
					}
				}
				for (int i = 0; i < content.length; i++) {
					// Writer wr = ((weblogic.jdbc.vendor.oracle.OracleThinClob)
					// clobtt[i])
					// .getCharacterOutputStream();
					Writer wr = ((CLOB) clobtt[i]).getCharacterOutputStream();
					wr.write(content[i]);
					wr.flush();
					wr.close();
				}
				rs.close();
				conn.commit();
			}
		} catch (Exception e) {
			rtn = false;
            exp = e;
        }

		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					if (!rtn) {
						conn.rollback();
					}
					conn.close();
                    if(exp != null){
                        throw exp;
                    }
                }
			} catch (Exception ex) {
				throw ex;
			}
		} else {
            throw new Exception("DB Connection Failure!");
        }
		return rtn;
	}

    /**
     * Execute a batch of sql commands with clob fields (Oracle only)
     * @param sqlgroups Collection, item type should be java.util.Map<String,String,String[]> like this {update=update tablename set ...,select=select field0, field1,... from tablename for update, clobcontents={contentForField0, contentForField1...}}
     * @return The result
     * @throws Exception All exceptions
     */
    public boolean updateClob(Collection sqlgroups) throws Exception{
		if (!this.getDataBaseType().equalsIgnoreCase("oracle")) {
			return true;
		}
		Connection conn = null;
		boolean rtn = true;
        Exception exp = null;

        try {
			conn = this.getConnection();
			Statement stmt = conn.createStatement();
			if (conn == null) {
				log("Connection Failed");
				rtn = false;
			} else {
				conn.setAutoCommit(false);
				Iterator sqlgrps = sqlgroups.iterator();
				for (; sqlgrps.hasNext();) {
					Map sqlgroup = (Map) sqlgrps.next();
					String update = sqlgroup.get("update").toString();
					String select = sqlgroup.get("select").toString();
					String[] clobcontents = (String[]) sqlgroup
							.get("clobcontents");
					stmt.execute(update);
					ResultSet rs = stmt.executeQuery(select);
					log(select);
					java.sql.Clob[] clobtt = new java.sql.Clob[clobcontents.length];
					if (rs.next()) {
						for (int i = 0; i < clobcontents.length; i++) {
							clobtt[i] = rs.getClob(i + 1);
						}

						for (int i = 0; i < clobcontents.length; i++) {
							Writer wr = ((weblogic.jdbc.vendor.oracle.OracleThinClob) clobtt[i])
									.getCharacterOutputStream();
							wr.write(clobcontents[i]);
							wr.flush();
							wr.close();
						}
					}
					rs.close();
				}
				conn.commit();
			}
		} catch (Exception e) {
			rtn = false;
            exp = e;
        }

		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					if (!rtn) {
						conn.rollback();
					}
					conn.close();
                    if(exp != null){
                        throw exp;
                    }
                }
			} catch (Exception ex) {
				throw ex;
			}
		} else {
            throw new Exception("DB Connection Failure!");
        }
		return rtn;

	}

    /*
    public static Map getResultListSql(String s) throws Exception{
		Map<String, Map> lst = null;
		int j = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();
			if (conn == null) {
				log("Connection Failed");
			} else {
				try {
					stmt = conn.createStatement();
				} catch (java.sql.SQLException cursorE) {
					if (cursorE.getMessage().trim().startsWith("ORA-01000")) {
						log("get statement 2nd time");
						conn.close();
						conn = DBConnection.getConnection();
						stmt = conn.createStatement();
					}
                    throw cursorE;
                }
				if (stmt == null) {
					log("Stmt Failed");
				}
				rs = stmt.executeQuery(s);

				if (rs == null) {
					log(" RS is null");
				} else {
					lst = new HashMap<String,Map>();
					ResultSetMetaData metadata = rs.getMetaData();
					int colCount = metadata.getColumnCount();
					while (rs.next()) {
						HashMap<String,Object> row = new HashMap<String,Object>();
						String rowStr = "ROW" + j;

						for (int i = 1; i <= colCount; i++) {
							String colLabel = metadata.getColumnLabel(i);
							String tableName = "";

							Object colValue = rs.getObject(i);
							if (colValue == null) {
								// do not put into hash
							} else {
								String tag = tableName + colLabel;
								if (row.containsKey(tag)) {
									tag = tag + "_" + i;
								}
								row.put(tag, colValue);
							}
						}
						++j;
						lst.put(rowStr, row);
					}
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			// release the resource !!!
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return lst;
	}
	*/

    /**
     * Get clob field value (Oracle only)
     * @param clobField Clob Field Object
     * @return String List, every item per line
     * @throws Exception All exceptions
     */
    public List<String> getClobValue(Object clobField) throws Exception{
		List<String> strList = new ArrayList<String>();
		try {
			BufferedReader clobData = new BufferedReader(((Clob) clobField)
					.getCharacterStream());
            String s;
            while ((s = clobData.readLine()) != null) {
				strList.add(s);
			}
		} catch (Exception e) {
			throw e;
		}
		return strList;
	}

    /**
     * Get clob field string (Oracle only)
     * @param clobField Clob Field Object
     * @return String String
     * @throws Exception All exceptions
     */
    public String getClobString(Object clobField) throws Exception{
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader clobData = new BufferedReader(((Clob) clobField)
					.getCharacterStream());
            String s;
            while ((s = clobData.readLine()) != null) {
				sb.append(s);
			}
		} catch (Exception e) {
			throw e;
		}
		return sb.toString();
	}

    /**
     * Get clob string from a record result map
     * @param records Record result
     * @param tag Clob field tag
     * @return Clob stirng
     * @throws Exception All exceptions
     */
    public String showClob(Map<String,Map<String,Object>> records, String tag) throws Exception{
        if (null == records) {
            return null;
        }
        Object obj = records.get(tag);
        if (null == obj) {
            return null;
        }
        java.sql.Clob clob = (java.sql.Clob) obj;
        String ret;
        try {
            ret = clob.getSubString( (long) 1, (int) clob.length());
        }
        catch (Exception e) {
            throw e;
        }
        return ret;
    }

    private static void log(String msg) {
        if(Config.logSql()){
            Config.getLogInstance().write("<DBConnection>" + msg);
        }
    }

    /*---------------------------------DBConfig-----------------------------------*/
    private String de = null;
    private String dbSeqTbName = null;
    private int dbSeqCacheSize = -1;
    private String dt = null;
    private Element dbElement = null;

    synchronized private Element getDBElement() {
        if (this.dbElement == null || Config.getDebugMode()) {
                try {
                    this.dbElement = (Element)XPath.selectSingleNode(Config.getSqlElement(),"//dbconfig[@dataSource='" + this.dataSource + "']");
                }
                catch(Exception e){
                    e.printStackTrace();
                }
               //XMLHandler.getElementByAttribute(XMLHandler.openXML("sql.xml").getChildren("dbconfig"), "default", "true");
            }
        return this.dbElement;
    }
    /**
     * Get the database character encoding      <br/>
     * Defined at &lt;dbconfig default="true" dataSource="jdbc/exampleDS" type="oracle" encoding="gbk" sequencyTable="t_sequency" sequencyCacheSize="20"/&gt; of "WEB-INF/xml/sql.xml"
     * @return database character encoding
     */
    synchronized public String getDataBaseEncoding(){
        if (this.de == null || Config.getDebugMode()) {
            this.de = this.getDBElement().getAttributeValue("encoding");
        }
        if(this.de == null || this.de.equals("")){
            this.de = "UTF-8";
        }
        return this.de;
    }

    /**
     * Get the database type string<br/>
     * Defined at &lt;dbconfig default="true" dataSource="jdbc/exampleDS" type="oracle" encoding="gbk" sequencyTable="t_sequency" sequencyCacheSize="20"/&gt; of "WEB-INF/xml/sql.xml"
     * @return database type string
     */
    synchronized public String getDataBaseType(){
        if (this.dt == null || Config.getDebugMode()) {
            this.dt = this.getDBElement().getAttributeValue("type").toLowerCase();
        }
        return this.dt;
    }

    /**
     * Get the name of sequency table which will generate the unique id key of each table
     * <br/>the table contains 2 columns - "name(varchar(100))" and "currval(int)"
     * <br/>Defined at &lt;dbconfig default="true" dataSource="jdbc/exampleDS" type="oracle" encoding="gbk" sequencyTable="t_sequency" sequencyCacheSize="20"/&gt; of "WEB-INF/xml/sql.xml"
     * @return database character encoding
     */
    synchronized private String getSequencyTableName(){
        if (this.dbSeqTbName == null || Config.getDebugMode()) {
            this.dbSeqTbName = this.getDBElement().getAttributeValue("sequencyTable");
        }
        return this.dbSeqTbName;
    }

    /**
     * Get the cache size of the sequency
     * <br/>Defined at &lt;dbconfig default="true" dataSource="jdbc/exampleDS" type="oracle" encoding="gbk" sequencyTable="t_sequency" sequencyCacheSize="20"/&gt; of "WEB-INF/xml/sql.xml"
     * @return database character encoding
     */
    synchronized private int getSequencyCacheSize() {
        if (this.dbSeqCacheSize == -1 || Config.getDebugMode()) {
            try {
                String size = this.getDBElement().getAttributeValue("sequencyCacheSize");
                this.dbSeqCacheSize = Integer.parseInt(size);
            }
            catch(Exception e){
                this.dbSeqCacheSize = 0;
            }
        }
        return this.dbSeqCacheSize;
    }

    /*-----------------------------DBConfig End---------------------------------------*/

    final private Map<String,int[]> sequencyCache;

    /**
     * Get sequency next value
     * @param seqName Sequency name
     * @return Next value
     * @throws Exception All exceptions
     */
    public String getSequencyNextValue(String seqName) throws Exception{
		if(this.getSequencyCacheSize() == 0){ //no cache
			return this.getSequencyNextValueWithoutCache(seqName);
		}
		return ++this.getSequencyCacheValue(seqName)[0] + "";
	}

    /**
     * Get sequency current value
     * @param seqName Sequency name
     * @return Current value
     * @throws Exception All exceptions
     */
    public String getSequencyCurrentValue(String seqName) throws Exception{
		if(this.getSequencyCacheSize() == 0){
			return this.getSequencyCurrValueWithoutCache(seqName);
		}
		return this.getSequencyCacheValue(seqName)[0] + "";
	}

	private int[] getSequencyCacheValue(String seqName) throws Exception{
        synchronized(this.sequencyCache){
            int[] cacheValue = this.sequencyCache.get(seqName);
            if(cacheValue == null || cacheValue[0] == cacheValue[1]){
                initSequencyCache(seqName);
                cacheValue = this.sequencyCache.get(seqName);
            }
		return cacheValue;
        }
    }

	private void initSequencyCache(String seqName) throws Exception{
		int curVal = Integer.parseInt(this.getSequencyCurrValueWithoutCache(seqName));
		String seqTbName = this.getSequencyTableName();
		if(this.execSql("update " + seqTbName + " set currval = currval + " + this.getSequencyCacheSize() + " where name = '" + seqName + "'")){
			int[] value = {curVal, curVal + this.getSequencyCacheSize()};
			this.sequencyCache.put(seqName, value);
			return;
		}
		throw new Exception("Getting sequency no. failure because of database disconnection!");
	}

	synchronized private String getSequencyNextValueWithoutCache(String sequencyName) throws Exception{
		String curVal = this.getSequencyCurrValueWithoutCache(sequencyName);
		if(this.execSql("update " + this.getSequencyTableName() + " set currval = currval + 1 where name = '" + sequencyName + "'")){
			return (Integer.parseInt(curVal) + 1) + "";
		}
		throw new Exception("Getting sequency no. failure because of database disconnection!");
	}

	synchronized private String getSequencyCurrValueWithoutCache(String sequencyName) throws Exception{
		String seqTbName = this.getSequencyTableName();
		Map<String,Object> result = this.getRow0("select currval from " + seqTbName + " where name = '" + sequencyName + "'");
		if(result != null){
			return result.get("currval") + "";
		}
		if(this.execSql("insert into " + seqTbName + " values ('" + sequencyName + "', 0)")){
			return "0";
		}
		throw new Exception("Initializing sequency no. failure because of database disconnection!");
	}

    public boolean isORACLE(){
        return this.getDataBaseType().equalsIgnoreCase(ORACLE);
    }

    public boolean isMYSQL(){
        return this.getDataBaseType().equalsIgnoreCase(MYSQL);
    }

    public boolean isMSSQL(){
        return this.getDataBaseType().equalsIgnoreCase(MSSQL);
    }

    /**
     * Get sql current date string
     * @return Oracle-"sysdate", Sql Server-"getdate()", MySql-"now()", Others-"getdate()"
     */
    public String getSysDate() {
		String date = SYSDATE.get(this.getDataBaseType());
        if(date == null){
            return "getdate()";
        }
        return date;
    }
}