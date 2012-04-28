package peanuty.framework.base.log;

/**
 * Created by IntelliJ IDEA.
 * User: luhang
 * Date: Jan 4, 2007
 * Time: 10:25:36 AM
 */
public interface LogInterface {
    public void write(Object ob);
    public void write(int i);
    public void write(boolean b);
    public void write(float f);
    public void write(double b);
    public void write(char c);
    public void write(char[] c);
    public void write(long l);
}
