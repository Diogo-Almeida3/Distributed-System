package pt.isec.webservice.controllers;

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
    public ArrayList<String> getUserMessages(@RequestHeader("Authorization") String token, @RequestParam(value = "contact", required = true) String contact)
    {
        DB db = null;
        try {
            db = new DB();
            return db.getMessages(db.getNameByToken(token),contact,200);
        } catch (SQLException e) {
            return null;
        }
    }

    // Messages/Group/id=1
    @GetMapping("Group")
    public ArrayList<String> getGroupMessages(@RequestHeader("Authorization") String token, @RequestParam(value = "group", required = true) Integer contact)
    {
        DB db = null;
        try {
            db = new DB();
            return db.getMessagesFromGroup(db.getNameByToken(token),contact,200);
        } catch (SQLException e) {
            return null;
        }
    }
}

