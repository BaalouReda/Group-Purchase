package com.kata.grouppurchase.event.listener;

import com.kata.grouppurchase.dto.GroupPurchaseDto;
import com.kata.grouppurchase.event.dto.GroupPurchaseCreatedEvent;
import com.kata.grouppurchase.mapper.GroupPurchaseMapper;
import com.kata.grouppurchase.repository.GroupPurchaseEntityRepository;
import com.kata.grouppurchase.service.SseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
public class GPurchaseSseListener {

    private static final Logger log = LoggerFactory.getLogger(GPurchaseSseListener.class);

    private final SseService sseService;
    private final GroupPurchaseEntityRepository groupPurchaseRepository;
    private final GroupPurchaseMapper groupPurchaseMapper;

    public GPurchaseSseListener(
        SseService sseService,
        GroupPurchaseEntityRepository groupPurchaseRepository,
        GroupPurchaseMapper groupPurchaseMapper) {
        this.sseService = sseService;
        this.groupPurchaseRepository = groupPurchaseRepository;
        this.groupPurchaseMapper = groupPurchaseMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGroupPurchaseCreatedForSse(GroupPurchaseCreatedEvent event) {
        log.debug("SSE listener received group purchase created event: {}", event.groupPurchaseId());

        try {
            groupPurchaseRepository.findById(event.groupPurchaseId())
                .ifPresentOrElse(
                    entity -> {
                        GroupPurchaseDto dto = groupPurchaseMapper.toDto(entity);
                        sseService.broadcastGroupPurchaseCreated(dto);
                        log.info("Broadcasted group purchase {} via SSE", dto.id());
                    },
                    () -> log.warn("Group purchase {} not found for SSE broadcast", event.groupPurchaseId())
                );
        } catch (Exception e) {
            log.error("Error broadcasting group purchase {} via SSE: {}",
                event.groupPurchaseId(), e.getMessage(), e);
        }
    }
}
