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
        String sqlQueryRegist = "INSERT INTO Utilizador VALUES ('" + username.toLowerCase() + "','" + name + "','" + password + "','ONLINE','" + date + "')";

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
            if (loginUser(username, password)) {
                statement = dbConn.createStatement();
                String sqlQueryExist = "UPDATE Utilizador SET username = '" + newUsername.toLowerCase() + "' WHERE username like '" + username.toLowerCase() + "'";
                statement.executeUpdate(sqlQueryExist);
            } else
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
            String sqlQueryExist = "UPDATE Utilizador SET nome = '" + newName + "' WHERE username like '" + username + "'";
            statement.executeUpdate(sqlQueryExist);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean editPassword(String newPassword, String oldPassword, String username) throws SQLException {
        Statement statement = null;
        try {
            statement = dbConn.createStatement();

            if (loginUser(username, oldPassword)) {
                String sqlQueryExist = "UPDATE Utilizador SET password = '" + newPassword + "' WHERE username like '" + username + "'";
                statement.executeUpdate(sqlQueryExist);
            } else return false;

        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean loginUser(String username, String password) throws SQLException {
        Statement statement = dbConn.createStatement();

        java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());


        String sqlQuery = "SELECT username FROM Utilizador WHERE password like '" + password + "' AND username like '" + username + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);
        boolean aux = resultSet.next();
        if (aux)
            updateState(username, true);
        statement.close();
        return aux;
    }

    public void updateState(String username, boolean isOnline) throws SQLException {
        Statement statement = dbConn.createStatement();

        java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());

        String state = isOnline ? "online" : "offline";
        String sqlQuery = "UPDATE Utilizador SET estado='" + state + "',ultima_vez_online='" + date + "' WHERE username='" + username + "'";

        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public ArrayList<String> searchUser(String username) {
        Statement statement;
        ArrayList<String> userInfo = new ArrayList<>();
        try {
            statement = dbConn.createStatement();
            String sqlQuery = "SELECT username, nome, estado, ultima_vez_online FROM Utilizador";
            if (username != null)
                sqlQuery += " WHERE nome like '%" + username + "%' or username like '%" + username + "%'";

            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                String usernameInfo = resultSet.getString("username");
                String name = resultSet.getString("nome");
                String status = resultSet.getString("estado");
                Date lastTimeOnline = resultSet.getDate("ultima_vez_online");
                userInfo.add("[" + usernameInfo + "] - " + name + " - Status: " + status + " - Last Time Online: " + lastTimeOnline);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            return null;
        }

        return userInfo;
    }

    public ArrayList<String> listContacts(String reqUsername) throws SQLException {
        Statement statement = dbConn.createStatement();
        Statement statement1 = dbConn.createStatement();

        ArrayList<String> contacts = new ArrayList<>();

        String sqlQuery = "SELECT Utilizador_username1 FROM Utilizador_has_Utilizador";
        if (reqUsername != null)
            sqlQuery += " WHERE Utilizador_username like '" + reqUsername + "' AND isPendenteContacto=0";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {
            String sqlQueryList = "SELECT username, nome, estado, ultima_vez_online FROM Utilizador";
            sqlQueryList += " WHERE username like '" + resultSet.getString("Utilizador_username1") + "'";

            ResultSet resultSetList = statement1.executeQuery(sqlQueryList);
            resultSetList.next();

            String username = resultSetList.getString("username");
            String name = resultSetList.getString("nome");
            String status = resultSetList.getString("estado");
            Date lastTimeOnline = resultSetList.getDate("ultima_vez_online");
            contacts.add("[" + username + "] - " + name + " - Status: " + status + " - Last Time Online: " + lastTimeOnline);

            resultSetList.close();
        }
        resultSet.close();
        statement.close();
        statement1.close();
        return contacts;
    }

    public boolean addContact(String username, String addUsername) throws SQLException {
        boolean success = false;
        Statement statement = dbConn.createStatement();

        String sqlQueryRegist = "INSERT INTO Utilizador_has_Utilizador VALUES ('" + username.toLowerCase() + "','" + addUsername.toLowerCase() + "',true)";

        try {
            statement.executeUpdate(sqlQueryRegist);
            success = true;
        } catch (SQLException e) {
            success = false;
        }

        statement.close();
        return success;
    }

    public boolean deleteContact(String username, String usernameDel) throws SQLException {

        boolean success = false;
        Statement statement = dbConn.createStatement();

        String sqlQueryRegist = "DELETE FROM Utilizador_has_Utilizador WHERE Utilizador_username LIKE '" + username + "' AND Utilizador_username1 like '" + usernameDel + "'";

        try {
            statement.executeUpdate(sqlQueryRegist);
            success = true;
        } catch (SQLException e) {
            success = false;
        }

        statement.close();
        return success;
    }

    public ArrayList<String> listPendingContacts(String username) {
        Statement statement;
        ArrayList<String> userInfo = new ArrayList<>();
        try {
            statement = dbConn.createStatement();
            String sqlQuery = "SELECT Utilizador_username1 FROM Utilizador_has_Utilizador";
            sqlQuery += " WHERE Utilizador_username like '" + username + "' and isPendenteContacto=true";

            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                String usernameInfo = resultSet.getString("Utilizador_username1");
                userInfo.add("[" + usernameInfo + "]");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            return null;
        }

        return userInfo;
    }

    public boolean createGroup(String username, String groupName) throws SQLException {
        boolean success = false;
        Statement statement = dbConn.createStatement();
        Statement statement1 = dbConn.createStatement();

        String sqlQueryCheck = "SELECT * FROM Grupo WHERE nome like '" + groupName.toLowerCase() + "' AND Utilizador_username like '" + username + "'";

        ResultSet resultSet = statement.executeQuery(sqlQueryCheck);
        boolean aux = resultSet.next();
        if (!aux) {
            java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
            String sqlQueryRegist = "INSERT INTO Grupo (nome,data_criacao,Utilizador_username) VALUES ('" + groupName.toLowerCase() + "','" + date + "','" + username + "')";

            try {
                statement.executeUpdate(sqlQueryRegist);
                success = true;
            } catch (SQLException e) {
                success = false;
            }
            if (success) {
                String sqlQuery = "SELECT id FROM Grupo";
                if (username != null)
                    sqlQuery += " WHERE Utilizador_username like '" + username + "' and nome like '" + groupName + "'";

                ResultSet resultSetID = statement.executeQuery(sqlQuery);
                while (resultSetID.next()) {
                    String groupId = resultSetID.getString("id");
                    String sqlQueryID = "INSERT INTO Grupo_has_Utilizador VALUES ('" + groupId + "','" + username + "',false)";
                    try {
                        statement1.executeUpdate(sqlQueryID);
                        success = true;
                    } catch (SQLException e) {
                        //todo caso falhe apagar o grupo
                        Statement statement2 = dbConn.createStatement();
                        String sqlQueryDelete= "DELETE FROM Grupo WHERE Utilizador_username LIKE '" + username + "' AND nome like '" + groupName + "'";
                        statement2.executeQuery(sqlQueryDelete);
                        success = false;
                    }
                }
                statement.close();
                statement1.close();
                return success;
            }

        }
        statement.close();
        return false;
    }

    public boolean joinGroup(String username, String nameGroup) {

        return false;
    }

    public ArrayList<String> listGroups() {
        Statement statement;
        Statement statement1;
        ArrayList<String> groupInfo = new ArrayList<>();
        try {
            statement = dbConn.createStatement();
            String sqlQuery = "SELECT id,nome,data_criacao,Utilizador_username FROM Grupo ";

            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                String groupName = resultSet.getString("nome");
                String data = resultSet.getString("data_criacao");
                String creatorName = resultSet.getString("Utilizador_username");
                String groupID = resultSet.getString("id");

                groupInfo.add("[" + groupName + "] - Creator: " + creatorName + " - Creation date: " + data);
                
                String sqlQueryGetUsers = "Select Utilizador_username FROM Grupo_has_Utilizador WHERE isPendenteGrupo=false AND Group_id like '" + groupID + "'";
                statement1 = dbConn.createStatement();
                ResultSet resultSetUsers = statement1.executeQuery(sqlQueryGetUsers);
                while(resultSetUsers.next()){
                    String user = resultSetUsers.getString("Utilizador_username");
                    groupInfo.add("\t -> " + user);
                }
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            return null;
        }

        return groupInfo;
    }
}