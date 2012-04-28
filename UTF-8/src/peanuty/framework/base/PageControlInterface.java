package peanuty.framework.base;

public interface PageControlInterface {
    abstract public boolean perform(javax.servlet.http.HttpServletRequest req,javax.servlet.http.HttpServletResponse res);
    abstract public String responseText(javax.servlet.http.HttpServletRequest req);
    abstract public String getPrivileges(javax.servlet.http.HttpServletRequest req);
}