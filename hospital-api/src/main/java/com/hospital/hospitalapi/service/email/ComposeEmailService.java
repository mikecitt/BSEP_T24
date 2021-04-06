package com.hospital.hospitalapi.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static com.hospital.hospitalapi.service.email.EmailConstants.FILE_FORGOT_PASSWORD;
import static com.hospital.hospitalapi.service.email.EmailConstants.FILE_ACTIVATE_ACCOUNT;

@Service
public class ComposeEmailService {

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    SendEmailService sendEmailService;

    public void createForgotPasswordMail(String[] to, String url) {

        final Context forgotPasswordContext = new Context();
        forgotPasswordContext.setVariable("url", url);

        String body = templateEngine.process(FILE_FORGOT_PASSWORD, forgotPasswordContext);

        sendEmailService.sendHtmlMail(to, EmailConstants.SUBJECT_FORGOT_PASSWORD, body);
    }

    public void createActivateAccountMail(String[] to, String url) {

        final Context activateAccountContext = new Context();
        activateAccountContext.setVariable("url", url);

        String body = templateEngine.process(FILE_ACTIVATE_ACCOUNT, activateAccountContext);

        sendEmailService.sendHtmlMail(to, EmailConstants.SUBJECT_ACTIVATE_ACCOUNT, body);
    }
}
