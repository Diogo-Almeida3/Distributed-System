package pt.isec.webservice.controllers;

import com.google.gson.Gson;
import com.mysql.cj.xdevapi.JsonArray;
import org.springframework.web.bind.annotation.*;
import pt.isec.webservice.Utils.DB;
import pt.isec.webservice.models.User;

import java.sql.SQLException;

@RestController
public class UserController
{
    private DB db;

    public UserController(){
        try {
            db = new DB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("session")
    public User login(@RequestBody User user)
    {
        user.setToken(user.getUsername() + "_123");
        user.setPassword("**********");
        return user;
    }

    @PutMapping("user")
    public void editName(@RequestBody User user,String name){
        Gson gson = new Gson();
        gson.toJson(db.editName(user.getUsername(),name));
        user.setUsername(name);
    }
}
