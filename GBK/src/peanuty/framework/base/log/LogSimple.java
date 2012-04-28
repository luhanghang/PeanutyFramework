package peanuty.framework.base.log;

import peanuty.framework.base.Config;
import peanuty.framework.util.Common;

/**
 * A simple log tool which just print the log information onto the console.
 * You can write your own log class to implements peanuty.framework.base.log.LogInterface and give the class name
 * to the attribute value of the node '//Log' of 'WEB/INF/xml/config.xml'
 * <br/>example:
 * <br/> ...
 * <br/>&lt;Log className="your.log.className"&gt;
 * <br/>...
 * <br/>
 * User: luhang
 * Date: Jan 4, 2007
 * Time: 10:34:56 AM
 */

public class LogSimple implements LogInterface{
    public void write(Object ob){
        if(Config.getDebugMode()) {
            System.out.println("[" + Common.now() + "] " + ob);
        }
    }

    public void write(int i){}
    public void write(boolean b){}
    public void write(float f){}
    public void write(double b){}
    public void write(char c){}
    public void write(char[] c){}
    public void write(long l){}
}
