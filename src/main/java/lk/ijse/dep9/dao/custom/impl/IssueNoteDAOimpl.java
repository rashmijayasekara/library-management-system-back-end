package lk.ijse.dep9.dao.impl;


import lk.ijse.dep9.dao.exception.ContraintViolationException;
import lk.ijse.dep9.entity.IssueNote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueNoteDAOimpl {
    private Connection connection;

    public IssueNoteDAOimpl(Connection connection){
        this.connection=connection;
    }
    public long issueCount(){
    try {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(id) FROM issue_note");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getLong(1);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    }
    public boolean existIssueById(int id){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM issue_note WHERE id=?");
            statement.setInt(1,id);
            return statement.executeQuery().next();

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    public List<IssueNote> findAllIssueNote(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM issue_note");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<IssueNote> issueNoteList = new ArrayList<>();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                Date date = resultSet.getDate("date");
                String member_id = resultSet.getString("member_id");
                issueNoteList.add(new IssueNote(id,date,member_id));
            }
            return issueNoteList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public IssueNote saveIssueNote(IssueNote issueNote){
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO issue_note (id,date,member_id) VALUES (?,?,?)");
            statement.setInt(1,issueNote.getId());
            statement.setDate(2,issueNote.getDate());
            statement.setString(3,issueNote.getMemberId());
            if (statement.executeUpdate()==1){
                return issueNote;
            }else {
                throw new SQLException("Failed to save the book");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void deleteIssueNoteByPk(int id) throws ContraintViolationException {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM issue_note WHERE id=?");

            statement.setInt(1,id);
            statement.executeUpdate();
        } catch (SQLException e) {
            if (existIssueById(id)) throw new ContraintViolationException("Constraints violation",e);
            throw new RuntimeException(e);
        }


    }
    public IssueNote updateIssueNote(IssueNote issueNote){
        return null;
    }
}

