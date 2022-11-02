package lk.ijse.dep9.api.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Locale;

public class HttpServlet2 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getMethod().equalsIgnoreCase("PATCH")){
            doPatch(req, resp);
        }else {
            super.service(req,resp);
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
