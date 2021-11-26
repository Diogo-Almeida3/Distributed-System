package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DB {
    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/mydb";
    private final String USERNAME = "root";
    private final String PASSWORD = "vasco";

    private Connection dbConn;

    public DB() throws SQLException {
        dbConn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    public void close() throws SQLException
    {
        if (dbConn != null)
            dbConn.close();
    }

    public ArrayList<String> listUsers(String whereName) throws SQLException
    {
        Statement statement = dbConn.createStatement();
        ArrayList<String> users = new ArrayList<>();

        String sqlQuery = "SELECT username, nome, estado, ultima_vez_online FROM Utilizador";
        if (whereName != null)
            sqlQuery += " WHERE nome like '%" + whereName + "%'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            String username = resultSet.getString("username");
            String name = resultSet.getString("nome");
            String status = resultSet.getString("estado");
            Date lastTimeOnline = resultSet.getDate("ultima_vez_online");

            users.add("["+username+"] - " + name + " - Status: " + status + " - Last Time Online: " + lastTimeOnline);
        }

        resultSet.close();
        statement.close();
        return users;
    }

//    public void insertUser(String name, String birthdate) throws SQLException
//    {
//        Statement statement = dbConn.createStatement();
//
//        String sqlQuery = "INSERT INTO users VALUES (0,'" + name + "','" + birthdate + "')";
//        statement.executeUpdate(sqlQuery);
//        statement.close();
//    }
//
//    public void updateUser(int id, String name, String birthdate) throws SQLException
//    {
//        Statement statement = dbConn.createStatement();
//
//        String sqlQuery = "UPDATE users SET name='" + name + "', " +
//                "BIRTHDATE='" + birthdate + "' WHERE id=" + id;
//        statement.executeUpdate(sqlQuery);
//        statement.close();
//    }
//
//    public void deleteUser(int id) throws SQLException
//    {
//        Statement statement = dbConn.createStatement();
//
//        String sqlQuery = "DELETE FROM users WHERE id=" + id;
//        statement.executeUpdate(sqlQuery);
//        statement.close();
//    }
}
