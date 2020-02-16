package com.xkom.engine;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

@Component
public class Scheduler {

    private final Logger logger;
    private final long scheduleRate;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Scheduler(Logger logger, @Value("${app.schedule.fixedRate}") long scheduleRate) {
        this.logger = logger;
        this.scheduleRate = scheduleRate;
    }

    public void schedule(Runnable task) {
        logger.info("Scheduling task at rate : {}", formatDuration(Duration.ofHours(scheduleRate).toMillis(), "ddD:HHh", false));
        scheduler.scheduleAtFixedRate(task, scheduleRate, scheduleRate, HOURS);
    }
}
