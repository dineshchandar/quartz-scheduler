package com.spring.scheduler.job;

import com.spring.scheduler.model.EmailRequest;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;

@Component
public class EmailJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);


    @Value("${EMAIL_SUBJECT}")
    private String emailSubject;
    @Value("${EMAIL_BODY}")
    private String emailBody;
    @Value("${EMAIL_RECIPIENTS}")
    private String emailRecipients;
    @Value("${EMAIL_BASE_URL}")
    private String emailBaseUrl;
    @Value("${EMAIL_URI}")
    private String emailUri;



    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

        logger.info("Executing Job... ", jobExecutionContext
                .getJobDetail().getKey());
        triggerJob();

    }

    private void triggerJob() {

        logger.info("Triggering job at {}", new Date()
                .toString());

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmailSubject(emailSubject);
        emailRequest.setEmailBody(emailBody);
        emailRequest.setToEmailId(emailRecipients);

        WebClient webClient = WebClient.builder().baseUrl(emailBaseUrl).build();

        webClient.post().uri(emailUri)
                 .bodyValue(emailRequest)
    //                 .header("User-Agent", "Apache-HttpClient/4.5.5 (Java/13.0.2)")
                 .exchange()
                 .subscribe();
    }
}
