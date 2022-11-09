package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep9.api.dto.BookDTO;
import lk.ijse.dep9.api.util.HttpServlet2;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "ServletBook", value = "/books/*", loadOnStartup = 0)
public class ServletBook extends HttpServlet2 {
    @Resource(lookup = "java:/comp/env/jdbc/lms")
    private DataSource pool;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            String q = request.getParameter("q");
            String page = request.getParameter("page");
            String size = request.getParameter("size");
            if (q != null && size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid size or page input");
                } else {
                    searchPaginatedBooks(q, Integer.parseInt(page), Integer.parseInt(size), response);
                }

            } else if (q != null) {
                searchBooks(q, response);
            } else if (size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid size or page input");
                } else {
                    loadPaginatedBooks(Integer.parseInt(page), Integer.parseInt(size), response);
                }

            } else {
                loadAllBooks(response);
            }

        } else {
            Matcher matcher = Pattern.compile("^/([0-9]{3}-){3}[0-9]{3}/?$").matcher(request.getPathInfo());
            if (matcher.matches()) {
                getBookDetails(matcher.group(1), response);

            } else {
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            }
        }

    }

    private void getBookDetails(String isbn, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM book WHERE isbn=?");
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String is = resultSet.getString("isbn");
                String name = resultSet.getString("title");
                String author = resultSet.getString("author");
                int copies = Integer.parseInt(resultSet.getString("copies"));
                BookDTO book = new BookDTO(is, name, author, copies);
                response.setContentType("application/json");
                response.setHeader("Access-Control-Allow-Origin", "*");
                JsonbBuilder.create().toJson(book, response.getWriter());
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch the book details");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't load the book details");
        }
    }

    private void searchPaginatedBooks(String query, int page, int size, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? LIMIT ? OFFSET ?");
            PreparedStatement statementCount = connection.prepareStatement("SELECT COUNT(isbn) FROM book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ?");

            query="%"+query+"%";
            statementCount.setString(1,query);
            statementCount.setString(2,query);
            statementCount.setString(3,query);
            ResultSet resultSet1 = statement.executeQuery();
            resultSet1.next();

            query="%"+query+"%";
            statement.setString(1,query);
            statement.setString(2,query);
            statement.setString(3,query);
            statement.setInt(4,size);
            statement.setInt(5,(page-1)*size);
            ResultSet resultSet= statement.executeQuery();

            int totalBooks = resultSet.getInt(1);
            response.addIntHeader("X-Total-books",totalBooks);

            ArrayList<BookDTO> bookDTOS = new ArrayList<>();

            while (resultSet.next()){
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int copies = resultSet.getInt("copies");
                bookDTOS.add(new BookDTO(isbn,title,author,copies));

            }
            response.addHeader("Access-Control-Allow-Origin","*");
            response.addHeader("Access-Control-Allow-Header","X-Total-Count");
            response.addHeader("Access-Control-Expose-Header","X-Total-Count");
            JsonbBuilder.create().toJson(bookDTOS,response.getWriter());

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private void searchBooks(String query, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? ");
            query="%"+query+"%";
            statement.setString(1,query);
            statement.setString(2,query);
            statement.setString(3,query);
            statement.setString(4,query);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<BookDTO> books=new ArrayList<>();
            while (resultSet.next()){
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int copies = resultSet.getInt("copies");
                BookDTO bookDTO = new BookDTO(isbn, title, author, copies);
                books.add(bookDTO);
            }
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(books,response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }

    }

    private void loadAllBooks(HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM book");
            ArrayList<BookDTO> bookDTOS = new ArrayList<>();
            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int copies = resultSet.getInt("copies");

                BookDTO book = new BookDTO(isbn, title, author, copies);
                bookDTOS.add(book);
            }
            connection.close();
            response.addHeader("Access-Controll-Allow-Origin", "*");
            response.setContentType("application/json");
            Jsonb jsonb = JsonbBuilder.create();
            jsonb.toJson(bookDTOS, response.getWriter());


        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }

    }

    private void loadPaginatedBooks(int page, int size, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            Statement statementCount = connection.createStatement();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM book LIMIT ? OFFSET ?");
            String sql = "SELECT COUNT(isbn) FROM book";

            ResultSet resultSet = statementCount.executeQuery(sql);
            resultSet.next();

            int totalBooks = resultSet.getInt(1);
            System.out.println(totalBooks);
            response.addIntHeader("X-Total-count",totalBooks);

            statement.setInt(1,size);
            statement.setInt(2,(page-1)*size);
            ResultSet resultSet1=statement.executeQuery();

            ArrayList<BookDTO> bookDTOS = new ArrayList<>();
            while (resultSet1.next()) {
                String isbn = resultSet1.getString("isbn");
                String title = resultSet1.getString("title");
                String author = resultSet1.getString("author");
                int copies = resultSet1.getInt("copies");
                bookDTOS.add(new BookDTO(isbn, title, author, copies));
            }
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "X-Total-Count");
            response.addHeader("Access-Control-Expose-Headers", "X-Total-Count");
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(bookDTOS, response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't load data due to a bad request");
        }

    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getPathInfo()==null|| request.getPathInfo().matches("/")){
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        Matcher matcher = Pattern.compile("^/([0-9]{3}-){3}[0-9]{3}/?$").matcher(request.getPathInfo());
        if (matcher.matches()){
            updateBook(matcher.group(1),request,response);
        }else {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private void updateBook(String isbn, HttpServletRequest request, HttpServletResponse response){
        try{
            if (request.getContentType()==null || !request.getContentType().startsWith("application/json")){
                throw new JsonbException("Invalid Json");
            }
            BookDTO bookDTO = JsonbBuilder.create().fromJson(request.getReader(), BookDTO.class);

            if (bookDTO.getIsbn()==null || !bookDTO.getIsbn().matches("([0-9]{3}-){3}[0-9]{3}")){
                throw new JsonbException("ISBN is invalid or empty");
            } else if (bookDTO.getTitle()==null || !bookDTO.getIsbn().matches("[A-Za-z ]+")) {
                throw new JsonbException("Title is empty or invalid");
            } else if (bookDTO.getAuthor()==null || !bookDTO.getAuthor().matches("[A-Za-z ]")) {
                throw new JsonbException("Invaid Author name or empty ");
            }else if (bookDTO.getCopies()==0 || !(Integer.toString(bookDTO.getCopies())).matches("[0-9]")){
                throw new JsonbException("Zero number of copies or invalid number of copies, check the number of copies");
            }
            try (Connection connection = pool.getConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE book SET title=?,author=?,copies=? WHERE isbn=?");
                statement.setString(1, bookDTO.getTitle());
                statement.setString(2, bookDTO.getAuthor());
                statement.setString(3,Integer.toString(bookDTO.getCopies()));
                statement.setString(4, bookDTO.getIsbn());

                if (statement.executeUpdate() == 1) {
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Member does not exist");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST,GET,PATCH,DELETE, HEAD, OPTIONS, PUT");

        String header = req.getHeader("Access-Control-Request-Headers");
        if (header != null) {
            resp.setHeader("Access-Control-Allow-Headers", header);
            resp.setHeader("Access-Control-Expose-Header", header);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo()==null || req.getPathInfo().equals("/")){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;

        }
        Matcher matcher = Pattern.compile("^/([0-9]{3}-){3}[0-9]{3}").matcher(req.getPathInfo());
        if (matcher.matches()){
            deleteBook(matcher.group(1),resp);

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED,"Expected Valid UUID");
        }

    }
    private void deleteBook(String isbn,HttpServletResponse response){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM book WHERE isbn=?");
            statement.setString(1,isbn);
            int i = statement.executeUpdate();
            if (i==0){
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"Invalid ISBN");
            }else {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo()==null || request.getPathInfo().equals("/")){
            try {
                if (request.getContentType()==null||request.getContentType().equals("application/json")){
                    throw new JsonbException("Invalid UUID");
                }
                BookDTO bookDTO = JsonbBuilder.create().fromJson(request.getReader(), BookDTO.class);
                if (bookDTO.getIsbn()==null || !bookDTO.getIsbn().matches("^/([0-9]{3}-){3}[0-9]{3}")){
                    throw new JsonbException("Invavlid ISBN");
                } else if(bookDTO.getTitle()==null || !bookDTO.getTitle().matches("[A-Za-z0-9- ]+")){
                    throw new JsonbException("Title is Invalid");
                } else if (bookDTO.getAuthor()==null || !bookDTO.getAuthor().matches("[A-Za-z ]+")) {
                    throw new JsonbException("Author name is Invalid");
                } else if (bookDTO.getCopies()==0 || Integer.toString(bookDTO.getCopies()).matches("[0-9]+")) {
                    throw new JsonbException("Invalid amount of copies");
                }
                try (Connection connection = pool.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO book(isbn,title,author,copies) VALUES (?,?,?,?)");
                    statement.setString(1,bookDTO.getIsbn());
                    statement.setString(2, bookDTO.getTitle());
                    statement.setString(3, bookDTO.getAuthor());
                    statement.setString(4,Integer.toString(bookDTO.getCopies()));
                    int i = statement.executeUpdate();
                    if (i==1){
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.setContentType("application/json");
                        response.setHeader("Access-Control-Allow-Origin","*");
                        JsonbBuilder.create().toJson(bookDTO,response.getWriter());

                    }else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save the customer");
                }


            }catch (JsonbException e){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invalid JSON");
            }

        }else {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }

    }
}
