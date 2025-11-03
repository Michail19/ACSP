package com.example.task4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/websocket-client")
    public String websocketClient() {
        return "websocket-client";
    }
}