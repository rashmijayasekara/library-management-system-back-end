package lk.ijse.dep9.dao.impl;

import lk.ijse.dep9.dao.exception.ContraintViolationException;
import lk.ijse.dep9.entity.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAOimpl {
    private Connection connection;
    public BookDAOimpl(Connection connection){  // cause connection should be injected by the person who uses the DAO
        this.connection=connection;
    }
    public long bookCount(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(isbn) FROM book");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean existBookByISBN(String isbn){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT isbn FROM book WHERE isbn=?");
            statement.setString(1,isbn);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Book> findAllBooks(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM book");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Book> bookList = new ArrayList<>();
            while (resultSet.next()){
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int copies = resultSet.getInt("copies");
                bookList.add(new Book(isbn,title,author,copies));
            }
            return bookList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Optional<Book> findBookByISBN(String isbn){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM book WHERE isbn=?");
            statement.setString(1,isbn);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int copies = resultSet.getInt("copies");
                return Optional.of(new Book(isbn,title,author,copies));
            }else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Book saveBook(Book book){
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO book (isbn,title,author,copies) VALUES (?,?,?,?)");
            statement.setString(1,book.getIsbn());
            statement.setString(2,book.getTitle());
            statement.setString(3,book.getAuthor());
            statement.setInt(4,book.getCopies());
            if (statement.executeUpdate()==1){
                return book;
            }else {
                throw new SQLException("Failed to save the book");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Book updateBook(Book book){
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE book SET title=?,author=?,copies=? WHERE isbn=?");
            statement.setString(1,book.getTitle());
            statement.setString(2,book.getAuthor());
            statement.setInt(3,book.getCopies());
            statement.setString(4, book.getIsbn());
            if (statement.executeUpdate()==1){
                return book;
            }else {
                throw new SQLException("Failed to update the book");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteBook(String isbn) throws ContraintViolationException {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM book WHERE isbn=?");
            statement.setString(1,isbn);
           statement.executeUpdate();
        } catch (SQLException e) {
            if (existBookByISBN(isbn)) throw new ContraintViolationException("Failed to ",e);
            throw new RuntimeException(e);
        }

    }
    public List<Book> findBooksByQuery(String query){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ?");
            query="%"+query+"%";
            statement.setString(1,query);
            statement.setString(2,query);
            statement.setString(3,query);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Book> books = new ArrayList<>();
            while (resultSet.next()){
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author= resultSet.getString("author");
                int copies = resultSet.getInt("copies");
                books.add(new Book(isbn,title,author,copies));

            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Book> findBooksByQuery(String query,int page, int size){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? LIMIT ? OFFSET ?");
            query="%"+query+"%";
            statement.setString(1,query);
            statement.setString(2,query);
            statement.setString(3,query);
            statement.setInt(4,size);
            statement.setInt(5,(page-1)*size);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Book> books = new ArrayList<>();
            while (resultSet.next()){
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author= resultSet.getString("author");
                int copies = resultSet.getInt("copies");
                books.add(new Book(isbn,title,author,copies));

            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Book> findAllBooks(int size, int page){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM book LIMIT ? OFFSET ?");
            statement.setInt(1,size);
            statement.setInt(2,(page-1)*size);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Book> bookList = new ArrayList<>();
            while (resultSet.next()){
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int copies = resultSet.getInt("copies");
                bookList.add(new Book(isbn,title,author,copies));
            }
            return bookList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
