package peanuty.framework.util;

import peanuty.framework.base.Config;
import peanuty.framework.base.DBConn;

import java.util.Map;

public class PageMan implements java.io.Serializable {
	public int curPage = 1;

	public int maxPage = 1;

	public int maxRow = 1;

	public int rowsPerPage = Config.getPageManRowsPerPage();

	public Map dataHash;

    private DBConn dbConnection;
    public static final String ROWS_PER_PAGE = "rowsPerPage";
    public static final String PMAN_DATA = "PmanData";
    public static final String CUR_PAGE = "CurPage";
    public static final String SIZE = "Size";
    public static final String JUMP_PAGE = "jumpPage";
    public static final String SQL = "SQL";

    public int getCurPage() {
		return this.curPage;
	}

	public int getMaxPage() {
		return this.maxPage;
	}

	public int getRowsPerPage() {
		return this.rowsPerPage;
	}

	private void setCurPage(int newCurPage) {
		this.curPage = newCurPage;
	}

	private void setMaxPage(int newMaxPage) {
		this.maxPage = newMaxPage;
	}

	public int getMaxRow() {
		return this.maxRow;
	}

	private void setMaxRow(int newMaxRow) {
		this.maxRow = newMaxRow;
	}

	public PageMan(Map<String,String> reqH) {
        try {
            this.dbConnection = DBConn.getInstance();
            init(reqH);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public PageMan(){
        this.dbConnection = DBConn.getInstance();
    }

    public void init(Map<String,String> reqH) throws Exception{
        if (reqH.get(ROWS_PER_PAGE) != null) {
            this.setRowsPerPage(Integer.parseInt(reqH.get(ROWS_PER_PAGE) + ""));
        }
        if (reqH.get(PMAN_DATA) == null) {
            this.initByHash(reqH);
        } else {
            recomposePageMan(reqH);
        }
    }

    public PageMan(Map<String,String> reqH, DBConn dbConnection) {
        try {
            this.dbConnection = dbConnection;
            init(reqH);
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    private void recomposePageMan(Map reqH) {
		try {
			this.setCurPage(Integer.parseInt(reqH.get(CUR_PAGE) + ""));
			this.setMaxRow(Integer.parseInt(reqH.get(SIZE) + ""));
			this.setMaxPageByMaxRow();
			//int startPos = (this.getCurPage() - 1) * this.getRowsPerPage() + 1;
			//int endPos = this.getCurPage() * this.getRowsPerPage() + 1;

			this.setDataHash((Map)reqH.get(PMAN_DATA));
		} catch (Throwable e) {
			System.out.println("(?) PagnMan initialize error !");
			e.printStackTrace();
		}
	}

	public java.util.Map<String,Map<String,Object>> getDataHash() {
		return this.dataHash;
	}

	private void setDataHash(Map newDataHash) {
		this.dataHash = newDataHash;
	}

	private void setMaxPageByMaxRow() {
		if (this.getMaxRow() % this.getRowsPerPage() == 0) {
			this.setMaxPage(this.getMaxRow() / this.getRowsPerPage());
		} else {
			this.setMaxPage(this.getMaxRow() / this.getRowsPerPage() + 1);
		}

	}

	private void initByHash(Map reqH) throws Exception{
		if (reqH.get(JUMP_PAGE) != null) {
			try {
				this.setCurPage(Integer.parseInt(reqH.get(JUMP_PAGE) + ""));
			} catch (Exception e) {
				this.setCurPage(1);
			}
		}
		String sql = (String) reqH.get(SQL);
        int SIZE = this.dbConnection.getResultSize(sql);
		this.setMaxRow(SIZE);
		this.setMaxPageByMaxRow();

		if(this.dbConnection.isORACLE()){
			this.initByHashORACLE(sql);
			return;
		}

		if(this.dbConnection.isMYSQL()){
			this.initByHashMYSQL(sql);
			return;
		}

		if(this.dbConnection.isMSSQL()){
			this.initByHashMSSQL(sql);
			return;
		}

		this.initByHashDefault(sql);
	}

	private void initByHashDefault(String sql) {
		try {
			int startPos = (this.getCurPage() - 1) * this.getRowsPerPage() + 1;
			int endPos = this.getCurPage() * this.getRowsPerPage() + 1;

			this.setDataHash(this.dbConnection.getResultList(sql, startPos, endPos));
		} catch (Throwable e) {
			System.out.println("(?) PagnMan initialize error !");
			e.printStackTrace();
		}
	}

	private void initByHashMYSQL(String sql) {
		try {
			int startPos = (this.getCurPage() - 1) * this.getRowsPerPage();
			sql = "(" + sql + ") limit " + startPos + "," + this.rowsPerPage;
			this.setDataHash(this.dbConnection.getResultList(sql));
		} catch (Throwable e) {
			System.out.println("(?) PagnMan initialize error !");
			e.printStackTrace();
		}
	}

	private void initByHashMSSQL(String sql) {
		this.initByHashDefault(sql);
	}

	private void initByHashORACLE(String sql) {
		this.initByHashDefault(sql);
	}

	public void setRowsPerPage(int newInt) {
		this.rowsPerPage = newInt;
	}
}