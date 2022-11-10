package lk.ijse.dep9.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@WebFilter(filterName = "cors-filter", urlPatterns = {"/members/*", "/books/*"})
public class CorsFilter extends HttpFilter {
//    private final String[] origins = {"http://localhost", "http://http://35.200.157.92:8080","http://127.0.0.1"};  // with whom we would like to share things
    private List<String> origins;
    @Override
    public void init() throws ServletException {
        String origin = getFilterConfig().getInitParameter("origins");
        origins=Arrays.asList(origin.split(", "));

    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String requestOrigin = req.getHeader("Origin");  // obtaining the location that it came which is the origin
        for (String origin : origins) {
            if (requestOrigin.startsWith(origin)) {
                resp.setHeader("Access-Control-Allow-Origin", requestOrigin);
                break;
            }
        }

        if (req.getMethod().equalsIgnoreCase("OPTIONS")) {
//            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "POST,GET,PATCH,DELETE, HEAD, OPTIONS, PUT");
            String requestedMethod = req.getHeader("Access-Control-Request-Method");
            String requestdHeader = req.getHeader("Access-control-Request-Headers");

            // in the logical "and" it will check the right side only if the left of it is right
         if ((requestedMethod.equalsIgnoreCase("POST") || requestedMethod.equalsIgnoreCase("PATCH")) && requestdHeader.toLowerCase().contains("content-type")) {
                resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
            }
        } else {
            if (req.getMethod().equalsIgnoreCase("GET") || req.getMethod().equalsIgnoreCase("HEAD")) {
                resp.setHeader("Access-Control-Expose-Headers", "X-Total-Count");
            }
        }
        chain.doFilter(req,resp);
    }
}

//            String header = req.getHeader("Access-Control-Request-Headers");
//            if (header != null) {
//                resp.setHeader("Access-Control-Allow-Headers", header);
//                resp.setHeader("Access-Control-Expose-Header", header);
//            }
//
//        } else if (req.getMethod().equalsIgnoreCase("PATCH")) {
//            resp.setHeader("Access-Control-Allow-Origin", "*");
//            chain.doFilter(req, resp);
//        } else if (req.getMethod().equalsIgnoreCase("DELETE")) {
//            resp.setHeader("Access-Control-Allow-Origin", "*");
//            chain.doFilter(req, resp);
//        } else if (req.getMethod().equalsIgnoreCase("PUT")) {
//            resp.setHeader("Access-Control-Allow-Origin", "*");
//            chain.doFilter(req, resp);
//        } else if (req.getMethod().equalsIgnoreCase("GET")) {
//            if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
//                String q = req.getParameter("q");
//                String size = req.getParameter("size");
//                String page = req.getParameter("page");
//                if (q != null && size != null && page != null) {
//                    System.out.println(" q sixe page not null");
//                    resp.addHeader("Access-Control-Allow-Origin", "*");
//                    resp.addHeader("Access-Control-Allow-Headers", "X-Total-Count");
//                    resp.addHeader("Access-Control-Expose-Headers", "X-Total-Count");
//
//                } else if (q != null) {
//                    System.out.println("q!=null");
//                    resp.addHeader("Access-Control-Allow-Origin", "*");
//                } else if (size != null && page != null && q == null) {
//                    resp.addHeader("Access-Control-Allow-Origin", "*");
//                    resp.addHeader("Access-Control-Allow-Headers", "X-Total-Count");
//                    resp.addHeader("Access-Control-Expose-Headers", "X-Total-Count");
//
//
//                } else {
//                    resp.addHeader("Access-Control-Allow-Origin", "*");
//                }
//
//            } else {
//                resp.setHeader("Access-Control-Allow-Origin", "*");
//            }
//            chain.doFilter(req, resp);
//        }
//    }
//}
