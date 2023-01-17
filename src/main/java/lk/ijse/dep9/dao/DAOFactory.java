package lk.ijse.dep9.dao.custom;

import lk.ijse.dep9.dao.custom.impl.BookDAOimpl;
import lk.ijse.dep9.dao.custom.impl.MemberDAOimpl;

import java.sql.Connection;

public class DAOFactory {
    private static DAOFactory daoFactiory;
    private DAOFactory(){

    }
    public static DAOFactory getInstance(){
        return (daoFactiory==null)? daoFactiory=new DAOFactory():daoFactiory;
    }

    public MemberDAO getMemberDA(Connection connection){
        return new MemberDAOimpl(connection);
    }
    public BookDAO getBookDAO(Connection connection){
        return new BookDAOimpl(connection);
    }
    public static DAOFactory()
}

