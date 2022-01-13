package pt.isec.webservice.controllers;

import com.google.gson.Gson;
import com.mysql.cj.xdevapi.JsonArray;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.web.bind.annotation.*;
import pt.isec.webservice.Utils.DB;
import pt.isec.webservice.models.User;

import java.sql.SQLException;

@RestController
@RequestMapping("Contact")
public class ContactController
{
    // GET Contacts/List
    @GetMapping("List")
    public String getContactList()
    {
//        DB db = null;
//        try {
//            String token = httpServletRequest.getHeader("Authorization");
//            Gson gson = new Gson();
//            db = new DB();
//            return gson.toJson(db.getContactList(username));
//        } catch (SQLException e) {
//            return null;
//        }
        return null;
    }

    @DeleteMapping("")
    public String deleteContact(@RequestParam(value = "id", required = true) String id)
    {
        //TODO: Retornar todas as mensagens num grupo
        return "Mensagem grupo";
    }
}
