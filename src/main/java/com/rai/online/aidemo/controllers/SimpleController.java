package com.rai.online.aidemo.controllers;

import com.rai.online.aidemo.model.DemoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SimpleController {

    //    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/welcome")
    public ResponseEntity<DemoMessage> message() {
        log.info("called from angular!...");
        DemoMessage demoMessage = new DemoMessage("AIDemo", "Congrats ! your application deployed successfully in Azure Platform. !",null);
        return new ResponseEntity<>(demoMessage, HttpStatus.OK);
    }
}
