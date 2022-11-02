package lk.ijse.dep9.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ServletTemp", value = {"/temp/*","*.php"})
public class ServletTemp extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try(PrintWriter out=response.getWriter()){
//            out.println("<style>{font-weight:2rem}</style>");
            out.printf("<p> Request URI  %s</p>",request.getRequestURI());
            out.printf("<p> Request URL %s</p>",request.getRequestURL());
            out.printf("<p> Servlet Path %s</p>",request.getServletPath());
            out.printf("<p> Request URI %s</p>",request.getContextPath());
            out.printf("<p> Path Info %s</p>",request.getPathInfo());
        }
    }


}
