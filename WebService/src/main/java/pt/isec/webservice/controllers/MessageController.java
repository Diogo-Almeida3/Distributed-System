package pt.isec.webservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isec.webservice.Utils.DB;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("Message")
public class MessageController
{
    // Messsages/User?name=test
    @GetMapping("User")
    public ResponseEntity<ArrayList<String>> getUserMessages(@RequestHeader("Authorization") String token, @RequestParam(value = "contact", required = true) String contact)
    {
        DB db = null;
        try {
            db = new DB();
             ArrayList<String> userMessages = db.getMessages(db.getNameByToken(token),contact,200);
             if(userMessages == null)
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
             else
                 return ResponseEntity.status(HttpStatus.OK).body(userMessages);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Messages/Group/id=1
    @GetMapping("Group")
    public ResponseEntity<ArrayList<String>> getGroupMessages(@RequestHeader("Authorization") String token, @RequestParam(value = "group", required = true) Integer contact)
    {
        DB db = null;
        try {
            db = new DB();
            ArrayList<String> groupsMessages = db.getMessagesFromGroup(db.getNameByToken(token),contact,200);
            if( groupsMessages ==null)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            else
                return ResponseEntity.status(HttpStatus.OK).body(groupsMessages);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

