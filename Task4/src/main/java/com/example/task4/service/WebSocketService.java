package com.example.task4.service;

import com.example.task4.model.Message;
import com.example.task4.model.OutputMessage;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class WebSocketService {

    public OutputMessage processMessage(Message message) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }
}
