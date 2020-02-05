package com.xkom.api;

import com.xkom.engine.IndexingEngine;
import io.vavr.collection.List;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;

//TODO might switch to 'admin api' when security enabled
@RestController
@RequestMapping("/debug-api/")
@Profile("local")
public class DebugApi {

    private final IndexingEngine engine;

    public DebugApi(IndexingEngine engine) {
        this.engine = engine;
    }

    @GetMapping("/test")
    public ResponseEntity<List<String>> test() {
        return ResponseEntity.ok(List.of("HI"));
    }

    @GetMapping("/reindex")
    public void forceReindex() {
        Executors.newSingleThreadExecutor().submit(engine::index);
    }
}
