package com.nolions.helloword.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class HellowordController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello, world!";
    }
}
