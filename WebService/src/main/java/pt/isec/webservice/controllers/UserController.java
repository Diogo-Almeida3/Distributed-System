package pt.isec.webservice.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.webservice.models.User;

@RestController
public class UserController
{
    @PostMapping("session")
    public User login(@RequestBody User user)
    {
        user.setToken(user.getUsername() + "_123");
        user.setPassword("**********");
        return user;
    }

//    @PutMapping("user")
//    public User editName(@Re User user){
//        user.setUsername();
//    }
}
