package server.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class DB {
    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/mydb";
    private final String USERNAME = "root";
    private final String PASSWORD = "dbpd";

    private Connection dbConn;

    public DB() throws SQLException {
        dbConn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    public void close() throws SQLException {
        if (dbConn != null)
            dbConn.close();
    }

    public boolean registUser(String username, String name, String password) throws SQLException {
        Statement statement = dbConn.createStatement();

        java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
        String sqlQueryExist = "SELECT username FROM Utilizador WHERE username like '" + username + "'";

        ResultSet resultSet = statement.executeQuery(sqlQueryExist);
        boolean aux = resultSet.next();
        statement.close();
        if(aux){
            String sqlQueryRegist = "INSERT INTO Utilizador VALUES ('"+ username.toLowerCase() +"','"+ name +"','"+password+"','ONLINE','"+date+"')";
            statement.executeUpdate(sqlQueryRegist);
            statement.close();
            return true;
        }
        return false;
    }

    public boolean loginUser(String username, String password) throws SQLException{
        Statement statement = dbConn.createStatement();

        java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());


        String sqlQuery = "SELECT username FROM Utilizador WHERE password like '"+ password + "' AND username like '" + username + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);
        boolean aux = resultSet.next();
        if (aux)
            updateState(username,true);
        statement.close();
        return aux;
    }

    public void updateState(String username, boolean isOnline) throws SQLException {
        Statement statement = dbConn.createStatement();

        java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());

        String state = isOnline ? "online" : "offline";
        String sqlQuery = "UPDATE Utilizador SET estado='" + state + "',ultima_vez_online='" + date + "' WHERE username='"+username+"'";

        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public ArrayList<String> listUsers(String whereName) throws SQLException {
        Statement statement = dbConn.createStatement();
        ArrayList<String> users = new ArrayList<>();

        String sqlQuery = "SELECT username, nome, estado, ultima_vez_online FROM Utilizador";
        if (whereName != null)
            sqlQuery += " WHERE nome like '%" + whereName + "%'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {
            String username = resultSet.getString("username");
            String name = resultSet.getString("nome");
            String status = resultSet.getString("estado");
            Date lastTimeOnline = resultSet.getDate("ultima_vez_online");

            users.add("[" + username + "] - " + name + " - Status: " + status + " - Last Time Online: " + lastTimeOnline);
        }

        resultSet.close();
        statement.close();
        return users;
    }



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
