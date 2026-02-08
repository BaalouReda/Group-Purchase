package com.kata.grouppurchase.web;

import com.kata.grouppurchase.dto.SseStatusDto;
import com.kata.grouppurchase.service.SseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
public record SseController(SseService sseService) {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);

    @GetMapping(value = "/group-purchases", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToGroupPurchases() {
        log.info("New SSE client subscribing to group purchase events");
        SseEmitter emitter = sseService.createEmitter();

        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data("Successfully connected to group purchase event stream")
            );
        } catch (Exception e) {
            log.error("Failed to send initial connection event: {}", e.getMessage());
        }

        return emitter;
    }


    @GetMapping("/status")
    public ResponseEntity<SseStatusDto> getSseStatus() {
        int activeConnections = sseService.getActiveConnectionCount();
        return ResponseEntity.ok(new SseStatusDto(activeConnections));
    }

}
