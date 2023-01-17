package lk.ijse.dep9.dao.impl;

import lk.ijse.dep9.dao.exception.ContraintViolationException;
import lk.ijse.dep9.entity.IssueItem;
import lk.ijse.dep9.entity.IssueItemPK;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IssueItemDAOimpl {
    private Connection connection;
    public IssueItemDAOimpl(Connection connection){
        this.connection=connection;
    }
    public long countIssueItem(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(isbn) FROM issue_item");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteIssueItemByPk(IssueItemPK pk) throws ContraintViolationException {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM issue_item WHERE isbn=? AND issue_id=?");
            statement.setString(1,pk.getIsbn());
            statement.setInt(2,pk.getIssueId());
            statement.executeUpdate();
        } catch (SQLException e) {
            if (existsIssueItemByPK(pk)) throw new ContraintViolationException("Constraints violation",e);
            throw new RuntimeException(e);
        }


    }
    public boolean existsIssueItemByPK(IssueItemPK issueItemPK){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT isbn FROM issue_item WHERE isbn=? AND issue_id=?");
            statement.setString(1, issueItemPK.getIsbn());
            statement.setInt(2,issueItemPK.getIssueId());
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<IssueItem> findAllIssueItems(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM issue_item");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<IssueItem> issueItemList = new ArrayList<>();
            while (resultSet.next()){
                String isbn = resultSet.getString("isbn");
                int id = resultSet.getInt("issue_id");
                issueItemList.add(new IssueItem(id,isbn));
            }
            return issueItemList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Optional<IssueItem> findIssueItemByPK(IssueItemPK issueItemPK){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM issue_item WHERE isbn=? AND issue_id=?");
            statement.setString(1,issueItemPK.getIsbn());
            statement.setInt(2,issueItemPK.getIssueId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                String isbn = resultSet.getString("isbn");
                int id = resultSet.getInt("issue_id");
                return Optional.of(new IssueItem(id,isbn));
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    public Optional<IssueItem> saveIssueItem(IssueItem issueItem){
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO issue_item (issue_id,isbn) VALUES (?,?)");
            statement.setInt(1,issueItem.getIssueItemPK().getIssueId());
            statement.setString(2,issueItem.getIssueItemPK().getIsbn());
            if (statement.executeUpdate()==1){

                return Optional.of(new IssueItem());
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public IssueItem updateIssueItem(IssueItem issueItem){
        return null;
    }

}
