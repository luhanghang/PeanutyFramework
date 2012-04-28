package peanuty.framework.base;

/**
 * All new application classes should extends this class
 */
public class BaseBean {
    public BaseBean() {
        super();
    }

    public void log(Object content){
        Config.getLogInstance().write("<" + this.getClass().getName() + ">" + content);
    }

    public void log(int content){
        Config.getLogInstance().write("<" + this.getClass().getName() + ">" + content);
    }
    public void log(boolean content){
        Config.getLogInstance().write("<" + this.getClass().getName() + ">" + content);
    }
    public void log(float content){
        Config.getLogInstance().write("<" + this.getClass().getName() + ">" + content);
    }
    public void log(double content){
        Config.getLogInstance().write("<" + this.getClass().getName() + ">" + content);
    }
    public void log(char content){
        Config.getLogInstance().write("<" + this.getClass().getName() + ">" + content);
    }
    public void log(char[] content){
        Config.getLogInstance().write("<" + this.getClass().getName() + ">" + content);
    }
    public void log(long content){
        Config.getLogInstance().write("<" + this.getClass().getName() + ">" + content);
    }
}
