package pt.isec.webservice.Utils;

import org.apache.tomcat.util.codec.binary.Base64;

import java.sql.SQLException;
import java.util.Calendar;

public class Token {
    public static String generateToken(String username) throws SQLException {
        DB db = new DB();
        StringBuilder sb = new StringBuilder();

        sb.append(username);
        sb.append('-');
        sb.append(Calendar.getInstance().getTimeInMillis());

        byte[] token = HMAC.calcHmacSha256(username, sb.toString());
        String token64 = Base64.encodeBase64String(token);

        db.saveToken(username,token64);

        return (token64);
    }

    public static String getUsername(String token) throws SQLException {
        DB db = new DB();
        return db.getNameByToken(token);
    }

    public static boolean isValid(String token) {
        return true;
//        DB db = null;
//        try {
//            db = new DB();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return db.isValidToken(token);
    }
}
