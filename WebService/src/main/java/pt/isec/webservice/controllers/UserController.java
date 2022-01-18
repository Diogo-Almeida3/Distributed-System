package pt.isec.webservice.controllers;

import com.google.gson.Gson;
import com.mysql.cj.xdevapi.JsonArray;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import pt.isec.webservice.Utils.DB;
import pt.isec.webservice.Utils.Token;
import pt.isec.webservice.models.User;

import java.sql.SQLException;

@RestController
public class UserController
{
    @PostMapping("session")
    public User login(@RequestBody User user)
    {
        DB db = null;
        try {
            db = new DB();
            if (db.loginUser(user.getUsername(), user.getPassword())){
                user.setToken(Token.generateToken(user.getUsername()));
                user.setPassword("**********");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    @PutMapping("User")
    public boolean editName(@RequestHeader("Authorization") String token,@RequestParam(value = "name", required = true) String name) {
        DB db = null;
        try {
            db = new DB();
            return db.editName(name, db.getNameByToken(token));
        } catch (SQLException e) {
            return false;
        }
    }
}
