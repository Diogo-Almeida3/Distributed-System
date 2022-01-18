package pt.isec.webservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isec.webservice.Utils.DB;
import pt.isec.webservice.Utils.Token;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class GroupController {

    // Get Groups
    @GetMapping("Group")
    public ResponseEntity<ArrayList<String>> listGroups(@RequestHeader("Authorization") String token)
    {
        DB db = null;
        try {
            db = new DB();
            ArrayList<String> groups = db.getGroups(db.getNameByToken(token));
            if (groups != null)
                return ResponseEntity.status(HttpStatus.OK).body(groups);
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
