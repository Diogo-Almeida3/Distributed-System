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
        boolean success = false;
        Statement statement = dbConn.createStatement();

        java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
        String sqlQueryRegist = "INSERT INTO Utilizador VALUES ('"+ username.toLowerCase() +"','"+ name +"','"+password+"','ONLINE','"+date+"')";

        try {
            statement.executeUpdate(sqlQueryRegist);
            success = true;
        } catch (SQLException e) {
            success = false;
        }

        statement.close();
        return success;
    }

    public boolean editUsername(String newUsername, String username, String password) {
        Statement statement = null;
        try {
            if (loginUser(username,password)) {
                statement = dbConn.createStatement();
                String sqlQueryExist = "UPDATE Utilizador SET username = '" + newUsername.toLowerCase() + "' WHERE username like '" + username.toLowerCase() + "'";
                statement.executeUpdate(sqlQueryExist);
            }
            else
                return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean editName(String newName, String username) throws SQLException {
        Statement statement = null;
        try {
            statement = dbConn.createStatement();
            String sqlQueryExist = "UPDATE Utilizador SET nome = '"+newName+"' WHERE username like '" + username + "'";
            statement.executeUpdate(sqlQueryExist);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean editPassword(String newPassword,String oldPassword, String username) throws SQLException {
        Statement statement = null;
        try {
            statement = dbConn.createStatement();

            if (loginUser(username,oldPassword)) {
                String sqlQueryExist = "UPDATE Utilizador SET password = '"+newPassword+"' WHERE username like '" + username + "'";
                statement.executeUpdate(sqlQueryExist);
            } else return false;

        } catch (SQLException e) {
            return false;
        }
        return true;
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

    public ArrayList<String> searchUser(String username){
        Statement statement;
        ArrayList<String> userInfo = new ArrayList<>();
        try {
            statement = dbConn.createStatement();
            String sqlQuery = "SELECT username, nome, estado, ultima_vez_online FROM Utilizador";
            if (username != null)
                sqlQuery += " WHERE nome like '%" + username + "%' or username like '%" + username + "%'";

            ResultSet resultSet = statement.executeQuery(sqlQuery);
        while(resultSet.next()){
            String usernameInfo = resultSet.getString("username");
            String name = resultSet.getString("nome");
            String status = resultSet.getString("estado");
            Date lastTimeOnline = resultSet.getDate("ultima_vez_online");
            userInfo.add("[" + usernameInfo + "] - " + name + " - Status: " + status + " - Last Time Online: " + lastTimeOnline);
        }

        } catch (SQLException e) {
            return null;
        }
        return userInfo;
    }


    public ArrayList<String> listContacts(String reqUsername) throws SQLException {
        Statement statement = dbConn.createStatement();
        ArrayList<String> users = new ArrayList<>();

        /*
        * Vai à tabela de contactos -> Utilizador_has_Utilizador
        * Utilizador_username -> Quem está a tentar listar (reqUsername)
        * Utilizador_username1 -> é o que queremos
        * isPendenteContacto -> Tem de ser falso pois assim significa que o utilizador já está na lista de contactos
        */
        String sqlQuery = "SELECT username, nome, estado, ultima_vez_online FROM Utilizador";
        if (reqUsername != null)
            sqlQuery += " WHERE nome like '%" + reqUsername + "%'";

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
