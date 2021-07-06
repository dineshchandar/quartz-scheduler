package com.spring.scheduler.controller;

import com.spring.scheduler.job.EmailJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

@Component
@RestController
@RequestMapping("/v1/scheduler")
public class SchedulerController implements CommandLineRunner {

    Logger log = LoggerFactory.getLogger(this.getClass());

    //Time format
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
    @Value("${SCHEDULE_START}")
    String scheduleStart;
    @Value("${CRON_SCHEDULE}")
    String cronSchedule;
    @Autowired
    private Scheduler scheduler;

    //Healthcheck
    @GetMapping("/healthcheck")
    public ResponseEntity<?> healthCheck() {
        log.info("Healthcheck");
        return new ResponseEntity<>("server time: " + dateFormat
                .format(System.currentTimeMillis()), HttpStatus.OK);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("inside run method");
        JobDetail jobDetail = buildJobDetail();
        Trigger jobTrigger = buildJobTrigger(jobDetail);
        scheduler.scheduleJob(jobDetail, jobTrigger);

    }

    private JobDetail buildJobDetail() {

        return JobBuilder.newJob(EmailJob.class)
                         .withIdentity(UUID.randomUUID().toString(), "Email")
                         .withDescription("Email Job").storeDurably().build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail) {

        String[] startSchedule = scheduleStart.split("-");

        return TriggerBuilder.newTrigger().forJob(jobDetail)
                             .withIdentity(jobDetail.getKey().getName(), "Email")
                             .withDescription("Email Job Triger")
                                .startNow()
//                             .startAt(DateBuilder
//                                     .dateOf(Integer.parseInt(startSchedule[3]), Integer
//                                             .parseInt(startSchedule[4]), Integer
//                                             .parseInt(startSchedule[5]), Integer
//                                             .parseInt(startSchedule[2]), Integer
//                                             .parseInt(startSchedule[1]), Integer
//                                             .parseInt(startSchedule[0])))
//                             .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInMinutes(1))
//                             .withSchedule(CronScheduleBuilder
//                                     .cronSchedule(cronSchedule)
//                                     .inTimeZone(TimeZone.getTimeZone("GMT+05:30"))
//                                     .withMisfireHandlingInstructionDoNothing())
        .build();

    }
}
