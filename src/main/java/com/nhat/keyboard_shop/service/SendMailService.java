package com.nhat.keyboard_shop.service;

import com.nhat.keyboard_shop.model.MailInfo;

import javax.mail.MessagingException;
import java.io.IOException;

public interface SendMailService {
    void run();

    void queue(String to, String subject, String body);

    void queue(MailInfo mail);

    void send(MailInfo mail) throws MessagingException, IOException;
}
