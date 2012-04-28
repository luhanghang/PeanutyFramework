package peanuty.framework.base;

import javax.servlet.*;
import javax.servlet.http.*;

public abstract class BaseServlet
        extends javax.servlet.http.HttpServlet {
    public BaseServlet() {
        super();
    }

    protected void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            String page) throws Throwable {
        log("nextpage-->" + page);
        RequestDispatcher requestdispatcher =
                getServletContext().getRequestDispatcher(page);
        requestdispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws javax.servlet.
            ServletException, java.io.IOException {
        try {
            execute(request, response);
        }
        catch (Throwable e) {
            this.log(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws javax.servlet.
            ServletException, java.io.IOException {
        try {
            execute(request, response);
        }
        catch (Throwable e) {
            this.log(e.getMessage());
            e.printStackTrace();
        }
    }

    public abstract void execute(HttpServletRequest req,
                                 HttpServletResponse res) throws Throwable;

    public void log(Object obj) {
        Config.getLogInstance().write("(S) " + obj);
    }

    public void log(String obj) {
        Config.getLogInstance().write("(S) " + obj);
    }
}
