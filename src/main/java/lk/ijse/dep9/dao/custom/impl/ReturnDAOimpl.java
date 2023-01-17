package lk.ijse.dep9.dao.impl;

import lk.ijse.dep9.dto.ReturnItem;
import lk.ijse.dep9.entity.IssueItem;
import lk.ijse.dep9.entity.Return;
import lk.ijse.dep9.entity.ReturnPK;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReturnDAOimpl {
    private Connection connection;

    public ReturnDAOimpl(Connection connection){
        this.connection=connection;
    }

    public long countReturnItem(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(isbn) FROM return");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteReturnByPk(ReturnPK pk){
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM return WHERE isbn=? AND issue_id=?");
            statement.setString(1,pk.getIsbn());
            statement.setInt(2,pk.getIssueId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    public boolean existsReturnByPK(ReturnPK issueItemPK){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT isbn FROM return WHERE isbn=? AND issue_id=?");
            statement.setString(1, issueItemPK.getIsbn());
            statement.setInt(2,issueItemPK.getIssueId());
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<ReturnItem> findAllReturns(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM return");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<ReturnItem> returnItemList = new ArrayList<>();
            while (resultSet.next()){
                Date date=resultSet.getDate("date");
                String isbn = resultSet.getString("isbn");
                int id = resultSet.getInt("issue_id");
                returnItemList.add(new ReturnItem(id,isbn));
            }
            return returnItemList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Optional<ReturnItem> findReturnByPK(ReturnPK returnPK){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM return WHERE isbn=? AND issue_id=?");
            statement.setString(1,returnPK.getIsbn());
            statement.setInt(2,returnPK.getIssueId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                String isbn = resultSet.getString("isbn");
                int id = resultSet.getInt("issue_id");
                return Optional.of(new ReturnItem(id,isbn));
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    public Optional<ReturnItem> saveReturnItem(Return ret){
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO issue_item (issue_id,isbn) VALUES (?,?)");
            statement.setInt(1,ret.getReturnPK().getIssueId());
            statement.setString(2,ret.getReturnPK().getIsbn());
            if (statement.executeUpdate()==1){

                return Optional.of(new ReturnItem());
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public IssueItem updateReturnItem(IssueItem issueItem){
        return null;
    }
}
