package pt.isec.webservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.webservice.Utils.DB;
import pt.isec.webservice.Utils.Token;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class GroupController {

    // Get Groups
    @GetMapping("Group")
    public ArrayList<String> listGroups()
    {
        DB db = null;
        try {
            db = new DB();
            return db.listGroups();
        } catch (SQLException e) {
            return null;
        }
    }
}
