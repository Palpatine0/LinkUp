package com.enchanted.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/time")
    public String getCurrentTime() {
        return "Current server time is: " + new Date();
    }
}

