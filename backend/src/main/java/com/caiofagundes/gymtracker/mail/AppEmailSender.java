package com.caiofagundes.gymtracker.mail;

import com.caiofagundes.gymtracker.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AppEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(AppEmailSender.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final AppProperties props;
    private final String mailHost;

    public AppEmailSender(
            ObjectProvider<JavaMailSender> mailSender,
            AppProperties props,
            @Value("${spring.mail.host:}") String mailHost) {
        this.mailSender = mailSender;
        this.props = props;
        this.mailHost = mailHost;
    }

    @Override
    public void send(String to, String subject, String body) {
        JavaMailSender sender = this.mailSender.getIfAvailable();
        if (sender == null || !StringUtils.hasText(this.mailHost)) {
            log.warn("SMTP not configured; email to {} [{}]:\n{}", to, subject, body);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.props.mail().from());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        sender.send(message);
        log.info("Password reset email sent to {}", to);
    }
}
