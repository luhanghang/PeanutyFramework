package peanuty.framework.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import peanuty.framework.base.Config;

import java.io.*;

public class HtmlGenerator {
    static public String getHtmlSrc(String url) {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod get = new MyGetMethod(url);
        String res;
        try {
            client.executeMethod(get);
            res = new String(get.getResponseBody(), Config.getRequestEncoding());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            get.releaseConnection();
        }
        return res.trim();
    }

    static public InputStream getHtmlStream(String url){
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod get = new MyGetMethod(url);
        InputStream in;
        try {
            client.executeMethod(get);
            in = get.getResponseBodyAsStream();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            get.releaseConnection();
        }
        return in;
    }

    static public boolean saveHtml(String url,String fileName) {
        try {
            FileOutputStream fo = new FileOutputStream(fileName);//建立文件输出流
//            InputStream isr = getHtmlStream(url);
//            byte[] resultBytes = new byte[1024];
//            int rcount = isr.read(resultBytes, 0, 1024);
//            while (rcount != -1) {
//                fo.write(resultBytes, 0, rcount);
//                rcount = isr.read(resultBytes, 0, 1024);
//            }
            fo.write(getHtmlSrc(url).getBytes(Config.getRequestEncoding()));
            fo.close();
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

class MyGetMethod extends GetMethod {
    public MyGetMethod(String url) {
        super(url);
    }

    @Override
    public String getRequestCharSet() {
        //return super.getRequestCharSet();
        return Config.getRequestEncoding();
    }
}
