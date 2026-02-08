package com.kata.grouppurchase.service;

import com.kata.grouppurchase.dto.GroupPurchaseDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Service for managing Server-Sent Events (SSE) connections.
 * Handles client subscriptions and broadcasts group purchase events.
 */
public interface SseService {
    /**
     * Creates a new SSE emitter for a client subscription.
     *
     * @return SseEmitter configured with timeout and callbacks
     */
    SseEmitter createEmitter();

    /**
     * Broadcasts a group purchase creation event to all connected clients.
     *
     * @param groupPurchaseDto the created group purchase
     */
    void broadcastGroupPurchaseCreated(GroupPurchaseDto groupPurchaseDto);

    /**
     * Returns the number of currently connected SSE clients.
     *
     * @return number of active connections
     */
    int getActiveConnectionCount();

    /**
     * Closes all active SSE connections.
     * Useful for shutdown or testing scenarios.
     */
    void closeAllConnections();
}
