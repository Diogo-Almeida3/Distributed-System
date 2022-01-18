package pt.isec.webservice.Utils;

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

    public boolean isContact(String name1, String name2) {
        Statement statement = null;
        try {
            statement = dbConn.createStatement();

            String sqlQueryCheck = "SELECT * FROM Utilizador_has_Utilizador" +
                    " WHERE isPendenteContacto = FALSE AND " +
                    "(Utilizador_username = '" + name1 + "' AND Utilizador_username1 = '" + name2 + "' " +
                    "OR Utilizador_username = '" + name2 + "' AND Utilizador_username1 = '" + name1 + "')";

            ResultSet resultSet = statement.executeQuery(sqlQueryCheck);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean editName(String newName, String username) {
        try {
            Statement statement = dbConn.createStatement();
            statement.executeUpdate("UPDATE Utilizador SET nome='" + newName + "' WHERE username='" + username + "'");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean deleteContact(String username, String contactName) {
        if (username.equals(contactName) || !isContact(username, contactName))
            return false;

        try {
            Statement statement = dbConn.createStatement();
            Statement statement1 = dbConn.createStatement();

            String sqlQueryDeleteContact = "DELETE FROM Utilizador_has_Utilizador  WHERE( Utilizador_username LIKE '" + username + "' AND " +
                    "Utilizador_username1 like '" + contactName + "')OR (Utilizador_username1 LIKE '" + username + "' AND Utilizador_username like '" + contactName + "')";
            String sqlQueryDeleteMessages = "DELETE FROM Mensagem WHERE ( Utilizador_username LIKE '" + username + "' AND " +
                    "Utilizador_username1 LIKE '" + contactName + "' ) OR (Utilizador_username1 LIKE '" + username + "' AND Utilizador_username LIKE '" + contactName + "') AND Grupo_id='0'";
            statement.executeUpdate(sqlQueryDeleteContact);
            statement1.executeUpdate(sqlQueryDeleteMessages);

        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public ArrayList<String> getContactList(String username) {
        ArrayList<String> contactRows = new ArrayList<>();

        ArrayList<String> contactList = new ArrayList<>();
        try {
            Statement statement = dbConn.createStatement();
            String getContactsRows = "SELECT * from Utilizador_has_Utilizador where (Utilizador_username='" + username + "' or " +
                    "Utilizador_username1='" + username + "') and isPendenteContacto=" + false;

            ResultSet resultSet = statement.executeQuery(getContactsRows);

            while (resultSet.next()) {
                String name1 = resultSet.getString("Utilizador_username");
                String name2 = resultSet.getString("Utilizador_username1");
                contactRows.add(name1.equals(username) ? name2 : name1);
            }

            for (String contactName : contactRows) {
                String getContactInfo = "SELECT * FROM Utilizador WHERE username = '" + contactName + "'";
                resultSet = statement.executeQuery(getContactInfo);
                if (resultSet.next()) { // Success at finding more user information
                    String name = resultSet.getString("nome");
                    String status = resultSet.getString("estado");
                    Date lastTimeOnline = resultSet.getDate("ultima_vez_online");
                    contactList.add("[" + contactName + "] - " + name + " - Status: " + status + " - Last Time Online: " + lastTimeOnline);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return contactList;
    }

    public ArrayList<String> getGroups(String username) {
        ArrayList<String> userGroups = new ArrayList<>();
        try {
            Statement statement = dbConn.createStatement();
            String getGroupsId = "SELECT Grupo_id from Grupo_has_Utilizador " +
                    "WHERE Utilizador_username='" + username + "' AND isPendenteGrupo='" + 0 + "'";

            ResultSet resultSet = statement.executeQuery(getGroupsId);

            while (resultSet.next()) {
                String groupId = resultSet.getString("id");
                String getGroups = "SELECT * FROM Grupo WHERE id='" + groupId + "'";
                ResultSet resultSet1 = statement.executeQuery(getGroups);

                while (resultSet1.next()) {
                    String groupName = resultSet.getString("nome");
                    String data = resultSet.getString("data_criacao");
                    String creatorName = resultSet.getString("Utilizador_username");
                    String groupID = resultSet.getString("id");

                    userGroups.add("Group ID: " + groupID + " -> [" + groupName + "] - Creator: " + creatorName + " - Creation date: " + data);

                    String sqlQueryGetUsers = "Select Utilizador_username FROM Grupo_has_Utilizador WHERE isPendenteGrupo=" + false + " AND Grupo_id like '" + groupID + "'";
                    Statement statement1 = dbConn.createStatement();
                    ResultSet resultSetUsers = statement1.executeQuery(sqlQueryGetUsers);
                    while (resultSetUsers.next()) {
                        String user = resultSetUsers.getString("Utilizador_username");
                        userGroups.add("\t -> " + user);
                    }
                }
            }


        } catch (SQLException e) {
            return null;
        }
        return userGroups;
    }

    public ArrayList<String> getMessages(String sender, String receiver, int limit) {
        ArrayList<String> messages = new ArrayList<>();
        if (limit < 1)
            limit = 20;
        try {
            Statement statement = dbConn.createStatement();
            String sqlQuery = "select  * from Mensagem where tipo='texto' AND (Utilizador_username='" + sender + "' and Utilizador_username1='" + receiver + "'" +
                    " OR Utilizador_username='" + receiver + "' and Utilizador_username1='" + sender + "') order by data_envio LIMIT " + limit;
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                StringBuilder aux = new StringBuilder();
                aux.append("[" + resultSet.getString("id") + "] ");
                String username = resultSet.getString("Utilizador_username");

                if (!username.equals(sender)) {
                    String dateOfView = resultSet.getString("data_visualizacao");
                    if (dateOfView == null)
                        aux.append("[Delivered]\t");
                    else
                        aux.append("[View at " + dateOfView + "]\t");
                } else
                    aux.append("[Sent at " + resultSet.getString("data_envio") + "]\t");


                aux.append(username);
                aux.append(": ");
                if (resultSet.getString("tipo").equals("ficheiro"))
                    aux.append("[FILE] ");
                aux.append(resultSet.getString("conteudo"));

                messages.add(aux.toString());
            }

            //Update messages view date
            sqlQuery = "UPDATE Mensagem SET data_visualizacao = '" + new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()) + "' " +
                    "WHERE data_visualizacao IS NULL AND Utilizador_username='" + sender + "' and Utilizador_username1='" + receiver + "'";
            statement.executeUpdate(sqlQuery);
            statement.close();
        } catch (SQLException e) {
            return null;
        }
        return messages;
    }

    public ArrayList<String> getMessagesFromGroup(String sender, int groupId, int limit) {
        ArrayList<String> messages = new ArrayList<>();
        if (limit < 1)
            limit = 20;
        try {
            Statement statement = dbConn.createStatement();
            String sqlQuery = "select * from Mensagem where  tipo='texto' AND Grupo_id =" + groupId + " order by data_envio LIMIT " + limit;
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                StringBuilder aux = new StringBuilder();
                aux.append("[" + resultSet.getString("id") + "] ");
                String username = resultSet.getString("Utilizador_username");

                if (!username.equals(sender)) {
                    String dateOfView = resultSet.getString("data_visualizacao");
                    if (dateOfView == null)
                        aux.append("[Delivered]\t");
                    else
                        aux.append("[View at " + dateOfView + "]\t");
                } else
                    aux.append("[Sent at " + resultSet.getString("data_envio") + "]\t");

                aux.append(username);
                aux.append(": ");
                if (resultSet.getString("tipo").equals("ficheiro"))
                    aux.append("[FILE] ");
                aux.append(resultSet.getString("conteudo"));

                messages.add(aux.toString());
            }


            //Update messages view date
            sqlQuery = "UPDATE Mensagem SET data_visualizacao = '" + new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()) + "' WHERE data_visualizacao IS NULL AND Grupo_id =" + groupId;
            statement.executeUpdate(sqlQuery);
            statement.close();
        } catch (SQLException e) {
            return null;
        }
        return messages;
    }

    public boolean loginUser(String username, String password) {

        try{
            Statement statement = dbConn.createStatement();

            String sqlQuery = "SELECT username FROM Utilizador WHERE password like '" + password + "' AND username like '" + username + "'";

            ResultSet resultSet = statement.executeQuery(sqlQuery);
            boolean aux = resultSet.next();
            if (aux)
                updateState(username, true);
            statement.close();

            return aux;
        } catch (SQLException e){
            return false;
        }
    }

    public void updateState(String username, boolean isOnline) {
        try {
            Statement statement = dbConn.createStatement();

            java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());

            String state = isOnline ? "online" : "offline";
            String sqlQuery = "UPDATE Utilizador SET estado='" + state + "',ultima_vez_online='" + date + "' WHERE username='" + username + "'";

            statement.executeUpdate(sqlQuery);
            statement.close();
        } catch (SQLException e){
            return;
        }
    }


    public void saveToken(String username, String token) throws SQLException{

        Statement statement = dbConn.createStatement();

        String insertToken = "UPDATE Utilizador SET token='" + token +"', token_date='"
                +  new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()) + "' WHERE username='" + username + "'";

        statement.executeUpdate(insertToken);
        statement.close();
    }

    public boolean isValidToken(String token){
        Statement statement = null;
        try{
            statement = dbConn.createStatement();

            String isValidToken = "SELECT token_date FROM Utilizador WHERE token ='" + token + "'";

            ResultSet resultSet = statement.executeQuery(isValidToken);

            if (resultSet.next()) {
                Timestamp token_date = resultSet.getTimestamp("token_date");
                return  (Calendar.getInstance().getTime().getTime() - token_date.getTime()) < 200 * 60 * 1000;
            }
        }catch(SQLException e){
            return false;
        }
        return false;
    }

    public String getNameByToken(String token) throws SQLException {
        Statement statement = dbConn.createStatement();

        String usernameQuery = "SELECT username FROM Utilizador WHERE token ='" + token + "'";

        ResultSet resultSet = statement.executeQuery(usernameQuery);
        boolean hasResult = resultSet.next();
        String username = null;
        if (hasResult)
             username = resultSet.getString("username");
        statement.close();
        return username;
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

                groupInfo.add("Group ID: " + groupID + " -> [" + groupName + "] - Creator: " + creatorName + " - Creation date: " + data);

                String sqlQueryGetUsers = "Select Utilizador_username FROM Grupo_has_Utilizador WHERE isPendenteGrupo=" + false + " AND Grupo_id like '" + groupID + "'";
                statement1 = dbConn.createStatement();
                ResultSet resultSetUsers = statement1.executeQuery(sqlQueryGetUsers);
                while (resultSetUsers.next()) {
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
