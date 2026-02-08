package com.kata.grouppurchase.service.Impl;

import com.kata.grouppurchase.config.propertie.SseProperties;
import com.kata.grouppurchase.dto.GroupPurchaseDto;
import com.kata.grouppurchase.service.SseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class SseServiceImpl implements SseService {

    private static final Logger log = LoggerFactory.getLogger(SseServiceImpl.class);

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private long timout ;

    public SseServiceImpl(SseProperties sseProperties) {
        this.timout = sseProperties.timeOut();
    }

    @Override
    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(timout);

        emitter.onCompletion(() -> {
            log.debug("SSE connection completed");
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE connection timed out");
            emitter.complete();
            emitters.remove(emitter);
        });

        emitter.onError((error) -> {
            log.error("SSE connection error: {}", error.getMessage());
            emitters.remove(emitter);
        });

        emitters.add(emitter);
        log.info("New SSE client connected. Total clients: {}", emitters.size());

        return emitter;
    }

    @Override
    public void broadcastGroupPurchaseCreated(GroupPurchaseDto groupPurchaseDto) {
        log.info("Broadcasting group purchase created event to {} clients", emitters.size());

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("group-purchase-created")
                    .data(groupPurchaseDto)
                    .id(groupPurchaseDto.id().toString())
                );
                log.debug("Successfully sent event to client");
            } catch (IOException e) {
                log.error("Failed to send SSE event to client: {}", e.getMessage());
                emitters.remove(emitter);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.debug("Error completing emitter with error: {}", ex.getMessage());
                }
            }
        }
    }


    @Override
    public int getActiveConnectionCount() {
        return emitters.size();
    }

    @Override
    public void closeAllConnections() {
        log.info("Closing all {} SSE connections", emitters.size());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.debug("Error completing emitter: {}", e.getMessage());
            }
        }
        emitters.clear();
    }
}
