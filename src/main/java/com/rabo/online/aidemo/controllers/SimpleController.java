package com.rabo.online.aidemo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {


    @GetMapping("/welcome")
    public String message(){
        return "Congrats ! your application deployed successfully in Azure Platform. !";
    }
}
