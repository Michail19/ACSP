package com.example.task4.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Message {
    private String from;
    private String text;

    public Message() {}

    public Message(String from, String text) {
        this.from = from;
        this.text = text;
    }
}
