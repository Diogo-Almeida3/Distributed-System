package pt.isec.webservice.controllers;

import com.google.gson.Gson;
import com.mysql.cj.xdevapi.JsonArray;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isec.webservice.Utils.DB;
import pt.isec.webservice.Utils.Token;
import pt.isec.webservice.models.User;

import java.sql.SQLException;

@RestController
public class UserController
{
    @PostMapping("Session")
    public ResponseEntity<User> login(@RequestBody User user)
    {
        DB db = null;
        try {
            db = new DB();
            if (db.loginUser(user.getUsername(), user.getPassword())){
                user.setToken(Token.generateToken(user.getUsername()));
                user.setPassword("**********");
            }
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("User")
    public ResponseEntity<Boolean> editName(@RequestHeader("Authorization") String token,
                                            @RequestParam(value = "name", required = true) String name) {
        DB db = null;
        try {
            db = new DB();
            if (db.editName(name, db.getNameByToken(token)))
                return ResponseEntity.status(HttpStatus.OK).body(true);
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
