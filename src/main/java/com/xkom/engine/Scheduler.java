package com.xkom.engine;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

@Component
public class Scheduler {

    private final Logger logger;
    private final String scheduleTime;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Scheduler(Logger logger, @Value("${app.schedule.time}") String scheduleTime) {
        this.logger = logger;
        this.scheduleTime = scheduleTime;
    }

    public void schedule(Runnable task) {
        LocalTime parsed = LocalTime.parse(scheduleTime);
        long delay = Duration.between(LocalTime.now(), parsed).toMillis();
        if (delay < 0) {
            //Its actually 24h - calculated window
            delay = HOURS.toMillis(24) + delay;
        }
        logger.info("Scheduling task every 24h at {}, next in {}", parsed, formatDuration(delay, "HH:mm", false));
        scheduler.scheduleAtFixedRate(task, delay, HOURS.toMillis(24), MILLISECONDS);
    }
}
