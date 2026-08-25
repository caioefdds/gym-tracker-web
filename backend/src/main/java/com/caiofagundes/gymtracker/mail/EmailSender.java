package com.caiofagundes.gymtracker.mail;

public interface EmailSender {

    void send(String to, String subject, String body);
}
