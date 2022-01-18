package pt.isec.webservice.controllers;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;
import pt.isec.webservice.Utils.DB;
import pt.isec.webservice.Utils.Token;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("Contact")
public class ContactController
{
    // GET Contacts/List
    @GetMapping("List")
    public ArrayList<String> getContactList(@RequestHeader("Authorization") String token)
    {
        DB db = null;
        try {
            db = new DB();
            return db.getContactList(db.getNameByToken(token));
        } catch (SQLException e) {
            return null;
        }
    }

    @DeleteMapping("")
    public boolean deleteContact(@RequestHeader("Authorization") String token,@RequestParam(value = "contact", required = true) String contact)
    {
        DB db = null;
        try {
            db = new DB();
            return db.deleteContact(Token.getUsername(token),contact);
        } catch (SQLException e) {
            return false;
        }
    }
}
