package peanuty.framework.util;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class HttpXML {

    public static String FALSE = "false";

    /**
     * Post XML to the giving url
     *
     * @param url url to post
     * @param xml xml string
     * @return repsonse text
     * @throws Exception All exceptions
     */
    public static String postXML(String url, String xml) throws Exception {
        URL u = new URL(url);
        URLConnection con = u.openConnection();
        con.setReadTimeout(10000);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "text/xml");

        PrintWriter post = new PrintWriter(new OutputStreamWriter(con
                .getOutputStream()), true);
        post.println(xml);

        InputStreamReader isr = new InputStreamReader(con.getInputStream());
        char[] resultBytes = new char[1024];
        int rcount = isr.read(resultBytes, 0, 1024);
        StringBuffer soapResponse = new StringBuffer();
        while (rcount != -1) {
            soapResponse = soapResponse.append(resultBytes, 0, rcount);
            rcount = isr.read(resultBytes, 0, 1024);
        }
        return soapResponse.toString();
    }
}
