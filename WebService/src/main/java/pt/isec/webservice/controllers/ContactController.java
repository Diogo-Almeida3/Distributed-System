package pt.isec.webservice.controllers;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ArrayList<String>> getContactList(@RequestHeader("Authorization") String token)
    {
        DB db = null;
        try {
            db = new DB();
            ArrayList<String> contacts = db.getContactList(db.getNameByToken(token));
            if (contacts != null)
                return ResponseEntity.status(HttpStatus.OK).body(contacts);
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<Boolean> deleteContact(@RequestHeader("Authorization") String token,@RequestParam(value = "contact", required = true) String contact)
    {
        DB db = null;
        try {
            db = new DB();
            if (db.deleteContact(Token.getUsername(token),contact))
                return ResponseEntity.status(HttpStatus.OK).body(true);
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(true);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
