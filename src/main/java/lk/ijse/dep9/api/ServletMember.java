package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep9.api.dto.MemberDTO;
import lk.ijse.dep9.api.util.HttpServlet2;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@WebServlet(name = "ServletMember", value = "/members/*", loadOnStartup = 0)
public class ServletMember extends HttpServlet2 {

    @Resource(lookup = "java:/comp/env/jdbc/lms")
    private DataSource pool;

//    @Override
//    public void init() throws ServletException {
//        try {
//            InitialContext initialContext = new InitialContext();
//            pool = (DataSource) initialContext.lookup("jdbc/lms");
//            System.out.println(pool);
//        } catch (NamingException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            String q = request.getParameter("q");
            String size = request.getParameter("size");
            String page = request.getParameter("page");
            if (q != null && size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid size or page");
                } else {
                    searchPaginatedMembers(q, Integer.parseInt(size), Integer.parseInt(page), response);
                }
            } else if (q != null) {
                searchMembers(q, response);
            } else if (size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid size or page");
                } else {
                    loadPaginatedAllMembers(Integer.parseInt(size), Integer.parseInt(page), response);
                }
            } else {
                loadAllMembers(response);
            }

        } else {
            Matcher matcher = Pattern.compile("^/([A-Fa-f0-9]{8}-([A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12})/?$").matcher(request.getPathInfo());
            if (matcher.matches()) {
                getMemberDetails(matcher.group(1), response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Expected Valid UUId");
            }
        }
    }

    private void loadAllMembers(HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM member");
            ArrayList<MemberDTO> memberDto = new ArrayList<>();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");
                memberDto.add(new MemberDTO(id, name, address, contact));
            }
            connection.close();
//            response.addHeader("Access-Control-Allow-Origin", "*");
            Jsonb jsonb = JsonbBuilder.create();
            response.setContentType("application/json");
            jsonb.toJson(memberDto, response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println(e);
        }
    }

    private void loadPaginatedAllMembers(int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(id) AS count FROM member");
            resultSet.next();
            int totalMembers = resultSet.getInt("count");
            response.setIntHeader("X-Total-Count", totalMembers);
            PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM member LIMIT ? OFFSET ?");
            statement1.setInt(1, size);
            statement1.setInt(2, (page - 1) * size);
            resultSet = statement1.executeQuery();
            ArrayList<MemberDTO> memberDto = new ArrayList<>();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");
                memberDto.add(new MemberDTO(id, name, address, contact));
            }
            Jsonb jsonb = JsonbBuilder.create();
//            response.addHeader("Access-Control-Allow-Origin", "*");
//            response.addHeader("Access-Control-Allow-Headers", "X-Total-Count");
//            response.addHeader("Access-Control-Expose-Headers", "X-Total-Count");
            response.setContentType("application/json");
            jsonb.toJson(memberDto, response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to load data");
        }

    }

    private void searchMembers(String query, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            query = "%" + query + "%";
            statement.setString(1, query);
            statement.setString(2, query);
            statement.setString(3, query);
            statement.setString(4, query);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<MemberDTO> memberDto = new ArrayList<>();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");
                memberDto.add(new MemberDTO(id, name, address, contact));
            }
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(memberDto, response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't fetch the members");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        Matcher matcher = Pattern.compile("^/([A-Fa-f0-9]{8}-([A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12})/?$").matcher(req.getPathInfo());
        if (matcher.matches()) {
            updateMember(matcher.group(1), resp, req);

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not implemented");
        }
    }

    private void updateMember(String memberId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {

            if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
                throw new JsonbException("Invalid JSON");
            }
            MemberDTO member = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);

            if (member.getId() == null || !memberId.equalsIgnoreCase(member.getId())) {
                throw new JsonbException("ID is empty or invalid");
            } else if (member.getName() == null || !member.getName().matches("[A-Za-z0-9,.:;/\\-]+")) {
                throw new JsonbException("Name is empty or Invalid");
            } else if (member.getAddress() == null || !member.getAddress().matches("[A-Za-z0-9/,:]+")) {
                throw new JsonbException("Address is empty or Invalid");

            } else if (member.getContact() == null || !member.getContact().matches("\\d{3}-\\d{7}")) {
                throw new JsonbException("Contact is empty or Invalid");
            }

            try (Connection connection = pool.getConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE member SET name=?, address=?,contact=? WHERE id=?");
                statement.setString(1, member.getName());
                statement.setString(2, member.getAddress());
                statement.setString(3, member.getContact());
                statement.setString(4, member.getId());

                if (statement.executeUpdate() == 1) {
//                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Member does not exist");
                }


            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update the member");
            }


        } catch (JsonbException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

    }

    private void searchPaginatedMembers(String query, int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stmCount = connection.prepareStatement("SELECT COUNT(id) FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ? LIMIT ? OFFSET ?");

            query = "%" + query + "%";
            stmCount.setString(1, query);
            stmCount.setString(2, query);
            stmCount.setString(3, query);
            stmCount.setString(4, query);
            ResultSet rst = stmCount.executeQuery();
            rst.next();

            int totalMembers = rst.getInt(1);
            response.addIntHeader("X-Total-Count", totalMembers);

            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            stm.setInt(5, size);
            stm.setInt(6, (page - 1) * size);

            ResultSet rst2 = stm.executeQuery();

            ArrayList<MemberDTO> members = new ArrayList<>();

            while (rst2.next()) {
                String id = rst2.getString("id");
                String name = rst2.getString("name");
                String address = rst2.getString("address");
                String contact = rst2.getString("contact");
                MemberDTO dto = new MemberDTO(id, name, address, contact);
                members.add(dto);
            }
//            response.addHeader("Access-Control-Allow-Origin", "*");
//            response.addHeader("Access-Control-Allow-Headers", "X-Total-Count");
//            response.addHeader("Access-Control-Expose-Headers", "X-Total-Count");
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(members, response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch members");
        }
    }


//    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
////        resp.setHeader("Access-Control-Allow-Origin", "*");
////        resp.setHeader("Access-Control-Allow-Methods", "POST,GET,PATCH,DELETE, HEAD, OPTIONS, PUT");
////
////        String header = req.getHeader("Access-Control-Request-Headers");
////        if (header != null) {
////            resp.setHeader("Access-Control-Allow-Headers", header);
////            resp.setHeader("Access-Control-Expose-Header", header);
////        }
//    }

    private void getMemberDetails(String memberID, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM member WHERE id=?");
            statement.setString(1, memberID);
            ResultSet resultSet = statement.executeQuery();
//            ArrayList<MemberDTO> memberDto = new ArrayList<>();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String contact = resultSet.getString("contact");
                String address = resultSet.getString("address");
                MemberDTO memberDto=new MemberDTO(id, name, address, contact);
                response.setContentType("application/json");
//                response.setHeader("Access-Control-Allow-Origin","*");
                JsonbBuilder.create().toJson(memberDto, response.getWriter());
            }else{
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to fetch member details");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't fetch the member details");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        Matcher matcher = Pattern.compile("^/([A-Fa-f0-9]{8}-([A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12})/?$").matcher(req.getPathInfo());
        if (matcher.matches()) {
            deleteMember(matcher.group(1), resp);

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Expected Valid UUId");
        }

    }

    private void deleteMember(String memberId, HttpServletResponse response) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM member WHERE id=?");
            statement.setString(1, memberId);
            int i = statement.executeUpdate();
            if (i == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid Member ID");
            } else {
//                response.setHeader("Access-Control-Allow-Origin","*");
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);  // a success
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {

            try {
                if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
                    System.out.println(request.getPathInfo());
                    throw new JsonbException("Invalid JSON");
                }
                MemberDTO memberDTO = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);
                if (memberDTO.getName() == null || !memberDTO.getName().matches("[A-Za-z ]+")) {
                    throw new JsonbException("Name is empty or invalid");
                } else if (memberDTO.getContact() == null || !memberDTO.getContact().matches("\\d{3}-\\d{7}")) {
                    throw new JsonbException("Contcact is empty or invalid");
                } else if (memberDTO.getAddress() == null || !memberDTO.getAddress().matches("[A-Za-z0-9,.;:/\\-]+")) {
                    throw new JsonbException(" Address is invalid or empty");

                }

                try (Connection connection = pool.getConnection()) {
                    memberDTO.setId(UUID.randomUUID().toString());
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO member(id,name,address,contact) VALUES (?,?,?,?)");
                    statement.setString(1, memberDTO.getId());
                    statement.setString(2, memberDTO.getName());
                    statement.setString(3, memberDTO.getAddress());
                    statement.setString(4, memberDTO.getContact());

                    int i = statement.executeUpdate();
                    if (i == 1) {
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.setContentType("application/json");
//                        response.setHeader("Access-Control-Allow-Origin", "*");
                        JsonbBuilder.create().toJson(memberDTO, response.getWriter());
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save the customer");
                }

            } catch (JsonbException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }
}
