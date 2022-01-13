package pt.isec.webservice.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Message")
public class MessageController
{
    // Messsages/User/name=test
    @GetMapping("User")
    public String getUserMessages(@RequestParam(value = "name", required = true) String name)
    {
        return "Mensagem user";
    }

    // Messages/Group/id=1
    @GetMapping("Group")
    public String getGroupMessages(@RequestParam(value = "id", required = true) String id)
    {
        return "Mensagem grupo";
    }
}

