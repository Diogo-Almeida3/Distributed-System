package pt.isec.webservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.isec.webservice.Utils.DB;

public class GroupController {

    // Get Groups
    @GetMapping("Group")
    public String listGroups(@RequestParam(value = "name", required = true) String name)
    {
        //TODO: Retornar todos os grupos
        return "Nome dos grupos";
    }
}
