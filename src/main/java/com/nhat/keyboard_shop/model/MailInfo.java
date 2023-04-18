package com.nhat.keyboard_shop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailInfo {
    String from;
    String to;
    String subject;
    String body;
    String attachments;

    public MailInfo(String to, String subject, String body) {
        this.from = "KeyBoard Shop <poly@fpt.edu.vn>";
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}
