package lk.ijse.dep9.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.db.ConnectionPool;

import java.io.IOException;

@WebServlet(name = "ServletTemporary", value = "/release")
public class ServletTemporary extends HttpServlet2 {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ConnectionPool pool =(ConnectionPool) getServletContext().getAttribute("pool");
        pool.releaseAllConnections();

    }


}
