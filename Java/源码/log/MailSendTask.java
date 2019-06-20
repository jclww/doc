package com.xxx.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailSendTask implements Runnable {
    private String body;

    public MailSendTask(String body) {
        this.body = body;
    }

    @Override
    public void run() {
        log.info(body);
    }
}
