package com.xkom;

import com.xkom.engine.IndexingEngine;
import com.xkom.engine.Scheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner { //TODO introduce throttling

    private final IndexingEngine indexingEngine;
    private final Scheduler scheduler;

    public App(IndexingEngine indexingEngine, Scheduler scheduler) {
        this.indexingEngine = indexingEngine;
        this.scheduler = scheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {
        scheduler.schedule(indexingEngine::index);
    }
}
