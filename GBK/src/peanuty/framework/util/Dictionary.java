package peanuty.framework.util;

import peanuty.framework.base.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Dictionary extends BaseDBBean {
    public Dictionary(){
    }

    final static public String NAME = "item";
    final static public String KEY = "name";
    final static public String VALUE = "value";

    static private Map<String,Map<String,Map<String,Object>>> dict;
    static private Map<String,Map<String,Object>> infoList;

    private void initDictionary() throws Exception{
        dict = new HashMap<String,Map<String,Map<String,Object>>>();
        Map<String,Map<String,Object>> data = this.listALL();
        for(int i = 0; i < data.size(); i++){
            Map<String,Object> item = data.get("ROW" + i);
            String name = item.get(NAME) + "";
            Map<String,Map<String,Object>> record = dict.get(name);
            if(record == null){
                record = new HashMap<String,Map<String,Object>>();
                dict.put(name, record);
            }
            record.put("ROW" + record.size(), item);
        }
        infoList = new HashMap<String,Map<String,Object>>();
    }

    static {
        try {
            new Dictionary().initDictionary();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    static public Map<String,Map<String,Object>> getList(String name){
        return dict.get(name);
    }

    private boolean _add(String[] name,String[] key, String[] value){
        try {
            List<String> sqlList = new ArrayList<String>();
            for(int i = 0; i < name.length; i++){
                this._reqH.clear();
                this._reqH.put(NAME + "T",name[i]);
                this._reqH.put(KEY + "T",key[i]);
                this._reqH.put(VALUE + "T",value[i]);
                sqlList.add(sql(this._insert()));
            }

            if(this.dbConnection.execSql(sqlList)){
                this.initDictionary();
                return true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean _remove(String[] name,String[] key){
        try {
            List<String> sqlList = new ArrayList<String>();
            for(int i = 0; i < name.length; i++){
                this._reqH.clear();
                this._reqH.put(NAME,name[i]);
                this._reqH.put(KEY,key[i]);
                sqlList.add(sql(this._delete()));
            }

            if(this.dbConnection.execSql(sqlList)){
                this.initDictionary();
                return true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean _update(String[] name,String[] key, String[] value) {
        try {
            List<String> sqlList = new ArrayList<String>();
            for(int i = 0; i < name.length; i++){
                this._reqH.clear();
                this._reqH.put(NAME,name[i]);
                this._reqH.put(KEY,key[i]);
                this._reqH.put(VALUE + "T",value[i]);
                sqlList.add(sql(this._update()));
            }

            if(this.dbConnection.execSql(sqlList)){
                this.initDictionary();
                return true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean add(String[] name, String[] key, String[] value){
        return new Dictionary()._add(name,key,value);
    }

    public static boolean add(String name, String key, String value){
        String[] n = {name};
        String[] k = {key};
        String[] v = {value};
        return new Dictionary()._add(n,k,v);
    }

    public static boolean update(String[] name,String[] key, String[] value){
        return new Dictionary()._update(name,key,value);
    }

    public static boolean update(String name,String key, String value){
        String[] n = {name};
        String[] k = {key};
        String[] v = {value};
        return new Dictionary()._update(n,k,v);
    }

    public static boolean remove(String[] name,String[] key){
        return new Dictionary()._remove(name,key);
    }

    public static boolean remove(String name,String key){
        String[] n = {name};
        String[] k = {key};
        return new Dictionary()._remove(n,k);
    }

    public static Map<String,Object> getInfo(String item){
        Map<String,Object> info = infoList.get(item);
        if(info == null || info.isEmpty()){
            info = new HashMap<String,Object>();
            Map<String, Map<String,Object>> list = getList(item);
            if(list != null) {
                for(int i = 0 ; i < list.size(); i++){
                    Map<String,Object> inf = list.get("ROW" + i);
                    info.put(inf.get(KEY) + "", inf.get(VALUE) + "");
                }
            }
        }
        return info;
    }
}
