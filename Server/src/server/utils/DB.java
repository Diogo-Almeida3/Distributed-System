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

    public boolean editName(String newName, String username) {
        try {
            Statement statement = dbConn.createStatement();
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
        ArrayList<String> contacts = new ArrayList<>();
        ArrayList<String> listOfContacts = new ArrayList<>();

        String sqlQuery = "SELECT * from Utilizador_has_Utilizador where Utilizador_username='"+reqUsername+"' or Utilizador_username1='"+reqUsername+"' and isPendenteContacto="+false;

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {
            String name1 = resultSet.getString("Utilizador_username");
            String name2 = resultSet.getString("Utilizador_username1");
            contacts.add(name1.equals(reqUsername) ? name2 : name1);
        }

        for (String username : contacts) {
            sqlQuery = "SELECT * FROM Utilizador WHERE username = '" +username+"'";
            resultSet = statement.executeQuery(sqlQuery);
            if (resultSet.next()) { // Success at finding more user information
                String name = resultSet.getString("nome");
                String status = resultSet.getString("estado");
                Date lastTimeOnline = resultSet.getDate("ultima_vez_online");
                listOfContacts.add("[" +username+ "] - " + name + " - Status: " + status + " - Last Time Online: " + lastTimeOnline);
            }
        }
        statement.close();
        return listOfContacts;
    }

    public boolean addContact(String username, String addUsername) {
        boolean success = false;
        if (username.equals(addUsername))       // check self adding
            return false;
        try {
            Statement statement = dbConn.createStatement();

            String sqlQueryCheck = "SELECT * FROM Utilizador_has_Utilizador WHERE Utilizador_username like '" + username + "' AND Utilizador_username1 like '" + addUsername + "' OR Utilizador_username like '" + addUsername + "' AND Utilizador_username1 like '" + username + "'";

            ResultSet resultSet = statement.executeQuery(sqlQueryCheck);
            boolean aux = resultSet.next();
            if (!aux) {
                String sqlQueryRegist = "INSERT INTO Utilizador_has_Utilizador VALUES ('" + username.toLowerCase() + "','" + addUsername.toLowerCase() + "',true)";
                statement.executeUpdate(sqlQueryRegist);
            } else {
                String sqlQuery = "UPDATE Utilizador_has_Utilizador SET isPendenteContacto=false WHERE Utilizador_username like '" + username + "' AND Utilizador_username1 like '" + addUsername + "' OR Utilizador_username like '" + addUsername + "' AND Utilizador_username1 like '" + username + "'";
                statement.executeUpdate(sqlQuery);
            }
            statement.close();
            success = true;
        } catch (SQLException e) {
            success = false;
        }
        return success;
    }

    public boolean refuseContact(String username, String refuseUsername) {
        if (username.equals(refuseUsername))
            return false;
        try {
            Statement statement = dbConn.createStatement();

            String sqlQueryCheck = "DELETE FROM Utilizador_has_Utilizador WHERE Utilizador_username like '" + username + "' AND Utilizador_username1 like '" + refuseUsername + "' OR Utilizador_username like '" + refuseUsername + "' AND Utilizador_username1 like '" + username + "'";
            statement.executeUpdate(sqlQueryCheck);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteContact(String username, String usernameDel) {
        if (username.equals(usernameDel))       // check self adding
            return false;
        try {
            Statement statement = dbConn.createStatement();
            Statement statement1 = dbConn.createStatement();

            String sqlQueryDeleteContact = "DELETE FROM Utilizador_has_Utilizador WHERE Utilizador_username LIKE '" + username + "' AND Utilizador_username1 like '" + usernameDel + "'";
            String sqlQueryDeleteMessages = "DELETE FROM Mensagem WHERE Utilizador_username LIKE '" + username + "' AND Utilizador_username1 LIKE '" + usernameDel + "' AND Grupo_id='0'";
            statement.executeUpdate(sqlQueryDeleteContact);
            statement1.executeUpdate(sqlQueryDeleteMessages);


            statement.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public ArrayList<String> listPendingContacts(String username) {
        Statement statement;
        ArrayList<String> userInfo = new ArrayList<>();
        try {
            statement = dbConn.createStatement();
            String sqlQuery = "SELECT Utilizador_username FROM Utilizador_has_Utilizador";
            sqlQuery += " WHERE Utilizador_username1 like '" + username + "' and isPendenteContacto= " + true;

            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                String usernameInfo = resultSet.getString("Utilizador_username");
                userInfo.add("[" + usernameInfo + "]");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            return null;
        }

        return userInfo;
    }

    public boolean createGroup(String username, String groupName) {
        boolean success = false;
        try {
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
                            Statement statement2 = dbConn.createStatement();
                            String sqlQueryDelete = "DELETE FROM Grupo WHERE Utilizador_username LIKE '" + username + "' AND nome like '" + groupName + "'";
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
        } catch (SQLException ex) {
            return false;
        }
        return false;
    }

    public boolean joinGroup(String username, int groupId) {
        try {
            Statement statement = dbConn.createStatement();
            Statement statement1 = dbConn.createStatement();
            String sqlQuery = "SELECT * FROM Grupo WHERE id=" + groupId;

            ResultSet resultSet = statement1.executeQuery(sqlQuery);
            boolean aux = resultSet.next();
            if (aux) {
                String sqlQueryVerify = "Select * FROM Grupo_has_Utilizador Where Grupo_id=" + groupId + " AND Utilizador_username like '" + username + "'";
                ResultSet resultSet1 = statement1.executeQuery(sqlQueryVerify);
                boolean verify = resultSet1.next();
                if (!verify) {
                    String sqlQueryJoin = "INSERT INTO Grupo_has_Utilizador VALUES ('" + groupId + "','" + username + "',true)";

                    statement.executeUpdate(sqlQueryJoin);
                    statement.close();
                    return true;
                }
            }
        } catch (SQLException e) {
            return false;
        }
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

    public boolean leaveGroup(String username, int groupId) {
        try {
            if (username.equals(getGroupAdminUsername(groupId))) // If the leaving member is the group's creator, the group is deleted
                return deleteGroup(groupId);

            Statement statement = dbConn.createStatement();
            String sqlQuery = "DELETE FROM Grupo_has_Utilizador WHERE Utilizador_username='" + username + "' AND Grupo_id=" + groupId;
            statement.executeUpdate(sqlQuery);
            statement.close();
            statement = dbConn.createStatement();
            sqlQuery = "DELETE FROM Mensagem WHERE Utilizador_username='" + username + "' AND Grupo_id=" + groupId;
            statement.executeUpdate(sqlQuery);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public String getGroupAdminUsername(int groupId) {
        String adminUsername = null;
        try {
            Statement statement = dbConn.createStatement();
            String sqlQuery = "SELECT Utilizador_username FROM Grupo WHERE id=" + groupId;

            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                adminUsername = resultSet.getString("Utilizador_username");
            }
        } catch (SQLException e) {
            return null;
        }
        return adminUsername;
    }

    public boolean getGroupAdminBool(int groupId, String username) {
        try {
            Statement statement = dbConn.createStatement();

            String sqlQueryCheck = "SELECT * FROM Grupo WHERE id= '" + groupId + "' AND Utilizador_username like '" + username + "'";

            ResultSet resultSet = statement.executeQuery(sqlQueryCheck);
            boolean aux = resultSet.next();
            return aux;
        } catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<String> getGroupUsers(int groupId) {
        ArrayList<String> groupUsers = new ArrayList<>();
        try {
            Statement statement = dbConn.createStatement();
            String sqlQuery = "SELECT Utilizador_username FROM Grupo_has_Utilizador WHERE Grupo_id= '" + groupId + "' AND isPendenteGrupo=" + false;
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                groupUsers.add(resultSet.getString("Utilizador_username"));
            }
        } catch (SQLException e) {
            return null;
        }
        return groupUsers;
    }

    public boolean editGroupName(int groupId, String groupName) {
        try {
            Statement statement = dbConn.createStatement();
            String sqlQueryExist = "UPDATE Grupo SET nome = '" + groupName.toLowerCase() + "' WHERE id= '" + groupId + "'";
            statement.executeUpdate(sqlQueryExist);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean deleteGroup(int groupId) {
        try {
            Statement statement = dbConn.createStatement();
            Statement statement1 = dbConn.createStatement();
            Statement statement2 = dbConn.createStatement();


            String sqlQueryDeleteGroup = "DELETE FROM Grupo WHERE id= '" + groupId + "'";
            String sqlQueryDeleteGroupUsers = "DELETE FROM Grupo_has_Utilizador WHERE Grupo_id= '" + groupId + "'";
            String sqlQueryDeleteGroupMessages = "DELETE FROM Mensagem WHERE Grupo_id= '" + groupId + "'";


            statement1.executeUpdate(sqlQueryDeleteGroupUsers);
            statement2.executeUpdate(sqlQueryDeleteGroupMessages);
            statement.executeUpdate(sqlQueryDeleteGroup);

            statement.close();
            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean kickGroupMember(int groupId, String username) {
        if (getGroupAdminUsername(groupId).equals(username))
            return false;
        try {
            Statement statement = dbConn.createStatement();
            Statement statement1 = dbConn.createStatement();

            String sqlQueryUser = "DELETE FROM Grupo_has_Utilizador WHERE Grupo_id LIKE '" + groupId + "' AND Utilizador_username LIKE '" + username + "'";
            String sqlQueryUserMessages = "DELETE FROM Mensagem WHERE Grupo_id LIKE '" + groupId + "' AND Utilizador_username LIKE '" + username + "'";


            statement.executeUpdate(sqlQueryUser);
            statement1.executeUpdate(sqlQueryUserMessages);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean acceptMember(int groupId, String username) {
        try {
            Statement statement = dbConn.createStatement();

            String sqlQueryCheck = "SELECT * FROM Grupo_has_Utilizador WHERE Utilizador_username like '" + username.toLowerCase() + "' AND Grupo_id like '" + groupId + "' AND isPendenteGrupo=" + true;

            ResultSet resultSet = statement.executeQuery(sqlQueryCheck);
            boolean aux = resultSet.next();
            if (aux) {
                String sqlQueryUpdate = "UPDATE Grupo_has_Utilizador SET isPendenteGrupo=false WHERE Utilizador_username like '" + username + "' AND Grupo_id= '" + groupId + "'";
                statement.executeUpdate(sqlQueryUpdate);
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    public boolean refuseMember(int groupId, String refuseUser) {
        try {
            Statement statement = dbConn.createStatement();

            String sqlQueryCheck = "SELECT * FROM Grupo_has_Utilizador WHERE Utilizador_username like '" + refuseUser.toLowerCase() + "' AND Grupo_id like '" + groupId + "' AND isPendenteGrupo=" + true;

            ResultSet resultSet = statement.executeQuery(sqlQueryCheck);
            boolean existUserWithContactRequest = resultSet.next();
            if (existUserWithContactRequest) {
                String sqlQueryUpdate = "DELETE FROM Grupo_has_Utilizador WHERE Utilizador_username = '" + refuseUser.toLowerCase() + "' AND Grupo_id = " + groupId;
                statement.executeUpdate(sqlQueryUpdate);
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    public ArrayList<String> listGroupWaitingList(int groupId) {
        ArrayList<String> groupWaitingList = new ArrayList<>();
        try {
            Statement statement = dbConn.createStatement();
            String sqlQuery = "SELECT Utilizador_username FROM Grupo_has_Utilizador WHERE Grupo_id= '" + groupId + "' AND isPendenteGrupo=" + true;
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                groupWaitingList.add(resultSet.getString("Utilizador_username"));
            }
        } catch (SQLException e) {
            return null;
        }
        return groupWaitingList;
    }

    public boolean sendMessage(String sender, String receiver, String message) {
        try {
            Statement statement = dbConn.createStatement();

            String sqlQueryCheck = "SELECT * FROM Utilizador_has_Utilizador" +
                    " WHERE isPendenteContacto = FALSE AND " +
                    "(Utilizador_username = '" + sender + "' AND Utilizador_username1 = '" + receiver + "' OR Utilizador_username = '" + receiver + "' AND Utilizador_username1 = '" + sender + "')";

            ResultSet resultSet = statement.executeQuery(sqlQueryCheck);
            boolean isContact = resultSet.next();
            if (isContact) {
                String sqlQueryUpdate = "INSERT INTO Mensagem (data_envio, Utilizador_username, Grupo_id, Utilizador_username1, data_visualizacao, tipo, conteudo)";
                java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
                sqlQueryUpdate += " VALUES ('" + date + "','" + sender + "',null,'" + receiver + "',null,'texto','" + message + "')";
                statement.executeUpdate(sqlQueryUpdate);
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    public boolean sendMessage2Group(String sender, int groupId, String message) {
        try {
            Statement statement = dbConn.createStatement();

            String sqlQueryCheck = "SELECT * FROM Grupo_has_Utilizador" +
                    " WHERE Grupo_id=1 AND Utilizador_username='lims' AND isPendenteGrupo=false";

            ResultSet resultSet = statement.executeQuery(sqlQueryCheck);
            boolean isOnGroup = resultSet.next();
            if (isOnGroup) {
                String sqlQueryUpdate = "INSERT INTO Mensagem (data_envio, Utilizador_username, Grupo_id, Utilizador_username1, data_visualizacao, tipo, conteudo)";
                java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
                sqlQueryUpdate += " VALUES ('" + date + "','" + sender + "'," + groupId + ",null,null,'texto','" + message + "')";
                statement.executeUpdate(sqlQueryUpdate);
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    public ArrayList<String> getMessages(String sender, String receiver, int limit) {
        ArrayList<String> messages = new ArrayList<>();
        if (limit < 1)
            limit = 20;
        try {
            Statement statement = dbConn.createStatement();
            String sqlQuery = "select  id,Utilizador_username,conteudo,data_envio,data_visualizacao from Mensagem where Utilizador_username='" + sender + "' and Utilizador_username1='" + receiver + "'" +
                    " OR Utilizador_username='" + receiver + "' and Utilizador_username1='" + sender + "' order by data_envio LIMIT " + limit;
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

    public ArrayList<String> getMessagesFromGroup(int groupId, int limit) {
        ArrayList<String> messages = new ArrayList<>();
        if (limit < 1)
            limit = 20;
        try {
            Statement statement = dbConn.createStatement();
            String sqlQuery = "select id,Utilizador_username,conteudo from Mensagem where Grupo_id =" + groupId + " order by data_envio LIMIT " + limit;
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                StringBuilder aux = new StringBuilder();
                aux.append("[" + resultSet.getString("id") + "] ");
                aux.append(resultSet.getString("Utilizador_username") + ": ");
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

    public ArrayList<String> listContactsWithMessages(String receiver) {
        ArrayList<String> cantactsWithMessages = new ArrayList<>();
        try {
            Statement statement = dbConn.createStatement();
            String sqlQuery = "SELECT DISTINCT Utilizador_username FROM Mensagem WHERE Utilizador_username1 = '" + receiver + "' AND  tipo = 'texto'";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                cantactsWithMessages.add(resultSet.getString("Utilizador_username"));
            }
        } catch (SQLException e) {
            return null;
        }
        return cantactsWithMessages;
    }

    public ArrayList<String> listGroupsWithMessages(String receiver) {
        ArrayList<String> groupsWithMessages = new ArrayList<>();
        try {
            Statement statement = dbConn.createStatement();
            String sqlQuery = "SELECT DISTINCT  Mensagem.Grupo_id FROM Mensagem,Grupo_has_Utilizador" +
                    " WHERE Grupo_has_Utilizador.Utilizador_username = '" + receiver + "' AND Grupo_has_Utilizador.isPendenteGrupo=false AND tipo='texto' AND Mensagem.Grupo_id IS NOT NULL";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                groupsWithMessages.add("Group " + resultSet.getString("Grupo_id"));
            }
        } catch (SQLException e) {
            return null;
        }
        return groupsWithMessages;
    }

    public boolean deleteMessage(String sender, int messageID) {
        try {
            Statement statement = dbConn.createStatement();
            String sqlDeleteQuery = "DELETE FROM Mensagem WHERE Utilizador_username= '" + sender + "' AND id=" + messageID;
            statement.executeUpdate(sqlDeleteQuery);

            statement.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public String getReceiverDeleteMessage(int idMessage){
        try{
            Statement statement = dbConn.createStatement();

            // Get data to notify users
            String sqlQuery = "SELECT Utilizador_username1,Grupo_id FROM Mensagem WHERE id="+ idMessage;
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            resultSet.next();

            String receiver = resultSet.getString("Utilizador_username1");

            if (receiver == null) { // Is a group
                receiver = "Group:" + resultSet.getString("Grupo_id");
            }
            return receiver;
        } catch (SQLException e) {
            return null;
        }
    }

    public int sendFile(String sender, String receiver, String filename) {
        int fileID = -1;
        try {
            Statement statement = dbConn.createStatement();
            String sqlQueryCheck = "SELECT * FROM Utilizador_has_Utilizador" +
                    " WHERE isPendenteContacto = FALSE AND " +
                    "(Utilizador_username = '" + sender + "' AND Utilizador_username1 = '" + receiver + "' " +
                    "OR Utilizador_username = '" + receiver + "' AND Utilizador_username1 = '" + sender + "')";

            ResultSet resultSet = statement.executeQuery(sqlQueryCheck);
            boolean isContact = resultSet.next();

            if (isContact) {
                String sqlQueryUpdate = "INSERT INTO Mensagem (data_envio, Utilizador_username, Grupo_id, Utilizador_username1, data_visualizacao, tipo, conteudo)";
                java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
                date.setNanos(0);
                sqlQueryUpdate += " VALUES ('" + date + "','" + sender + "',null,'" + receiver + "',null,'ficheiro','" + filename + "')";
                statement.executeUpdate(sqlQueryUpdate);

                String sqlQueryMsgId = "SELECT id FROM Mensagem where Utilizador_username='"+sender +"' AND Utilizador_username1='"+ receiver +"' AND data_envio='" + date+ "'";
                ResultSet resultSet1 = statement.executeQuery(sqlQueryMsgId);

                if(resultSet1.next())
                    fileID = resultSet1.getInt("id");
            }
            statement.close();
        } catch (SQLException e) {
            return -1;
        }
        return fileID;
    }

    public String getFileDirectory(int id) {
        try {
            Statement statement = dbConn.createStatement();

            String sqlQuery = "SELECT id,Utilizador_username,conteudo FROM Mensagem WHERE id="+id + " AND conteudo='ficheiro'";
            statement.executeQuery(sqlQuery);

            ResultSet resultSet = statement.executeQuery(sqlQuery);

            if(resultSet.next()) {
                String dir = "/" +resultSet.getString("Utilizador_username")+"/"+id+"-"+resultSet.getString("conteudo");
                statement.close();
                return dir;
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }
}