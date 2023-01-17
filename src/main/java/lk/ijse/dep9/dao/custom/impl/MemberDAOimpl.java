package lk.ijse.dep9.dao.impl;


import lk.ijse.dep9.dao.exception.ContraintViolationException;
import lk.ijse.dep9.entity.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAOimpl {
    private Connection connection;
    public MemberDAOimpl(Connection connection){  // cause connection should be injected by the person who uses the DAO
        this.connection=connection;
    }
    public long memberCount(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(id) FROM member");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean existMemberById(String id){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM member WHERE id=?");
            statement.setString(1,id);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Member> findAllMembers(){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM member");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Member> membersList = new ArrayList<>();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");
                membersList.add(new Member(id,name,address,contact));
            }
            return membersList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Optional<Member> findMembersById(String isbn){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM member WHERE id=?");
            statement.setString(1,isbn);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact= resultSet.getString("contact");
                return Optional.of(new Member(id,name,address,contact));
            }else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Member saveMember(Member member){
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO member (id,name,address,contact) VALUES (?,?,?,?)");
            statement.setString(1,member.getId());
            statement.setString(2,member.getName());
            statement.setString(3,member.getAddress());
            statement.setString(4,member.getContact());
            if (statement.executeUpdate()==1){
                return member;
            }else {
                throw new SQLException("Failed to save the member");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Member updateMember(Member member){
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE member SET name=?,address=?,contact=? WHERE id=?");
            statement.setString(4, member.getId());
            statement.setString(1,member.getName());
            statement.setString(2,member.getAddress());
            statement.setString(3, member.getContact());
            if (statement.executeUpdate()==1){
                return member;
            }else {
                throw new SQLException("Failed to update the member");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteMemberById(String id) throws ContraintViolationException {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM member WHERE id=?");
            statement.setString(1,id);
            statement.executeUpdate();
        } catch (SQLException e) {
            if (existMemberById(id)) throw new ContraintViolationException("Member already exists in other table",e);
            throw new RuntimeException(e);
        }

    }
    public List<Member> findMembersByQuery(String query){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            query="%"+query+"%";
            statement.setString(1,query);
            statement.setString(2,query);
            statement.setString(3,query);
            statement.setString(4,query);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Member> members = new ArrayList<>();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address= resultSet.getString("address");
                String contact = resultSet.getString("contact");
                members.add(new Member(id,name,address,contact));

            }
            return members;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Member> findMembersByQuery(String query,int page, int size){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE  OR contact LIKE ? LIMIT ? OFFSET ?");
            query="%"+query+"%";
            statement.setString(1,query);
            statement.setString(2,query);
            statement.setString(3,query);
            statement.setString(4,query);
            statement.setInt(5,size);
            statement.setInt(6,(page-1)*size);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Member> members = new ArrayList<>();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address= resultSet.getString("address");
                String contact = resultSet.getString("contact");
                members.add(new Member(id,name,address,contact));
            }
            return members;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Member> findAllMembers(int size, int page){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM member LIMIT ? OFFSET ?");
            statement.setInt(1,size);
            statement.setInt(2,(page-1)*size);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Member> membersList = new ArrayList<>();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");
                membersList.add(new Member(id,name,address,contact));
            }
            return membersList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
