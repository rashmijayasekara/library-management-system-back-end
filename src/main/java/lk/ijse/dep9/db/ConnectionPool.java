package lk.ijse.dep9.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private final int poolSize;
    private List<Connection> pool = new ArrayList<>();
    private List<Connection> consumberPool = new ArrayList<>();

    public ConnectionPool(int poolSize) {
        this.poolSize = poolSize;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            for (int i = 0; i < poolSize; i++) {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dep9_lms", "root", "Rashmi@1997");
                pool.add(connection);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public synchronized Connection getConnection() {  // synchronised cause wait has to be included in a synchronised context
        while (pool.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Connection connection = pool.get(0);
        consumberPool.add(connection);
        pool.remove(connection);
        return connection;
    }

    public synchronized void releaseAllConnections() {
        pool.addAll(consumberPool);
        pool.clear();
        // we need to notify somehow
        notifyAll();


    }

    public synchronized void releaseConnection(Connection connection) {
        pool.add(connection);
        consumberPool.remove(connection);
        // we need to notify somehow
        notify();
    }
}

