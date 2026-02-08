package com.kata.grouppurchase.event.listener;

import com.kata.grouppurchase.config.propertie.RabbitMQProperties;
import com.kata.grouppurchase.dao.GroupPurchaseEntity;
import com.kata.grouppurchase.dto.GroupPurchaseMessage;
import com.kata.grouppurchase.event.dto.GroupPurchaseCreatedEvent;
import com.kata.grouppurchase.repository.GroupPurchaseEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
public class GPurchaseDeadlineListener {
    private static final Logger log = LoggerFactory.getLogger(GPurchaseDeadlineListener.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;
    private final GroupPurchaseEntityRepository groupPurchaseEntityRepository;

    public GPurchaseDeadlineListener(RabbitTemplate rabbitTemplate, RabbitMQProperties rabbitMQProperties, GroupPurchaseEntityRepository groupPurchaseEntityRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQProperties = rabbitMQProperties;
        this.groupPurchaseEntityRepository = groupPurchaseEntityRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,condition = "#event.eventType.name() == 'CREATE'")
    @Retryable(
            includes = {Exception.class},
            maxRetries = 4,
            delayString = "500ms",
            multiplier = 2,
            maxDelay = 3000)
    public void handleGroupPurchaseCreated(GroupPurchaseCreatedEvent event) {
        log.info("Group purchase created event received: {}", event.groupPurchaseId());
            groupPurchaseEntityRepository.findById(event.groupPurchaseId()).ifPresentOrElse(
                    this::handlePublishOfGpDeadline,
                    () -> log.warn("Group purchase {} not found for  deadline", event.groupPurchaseId()
            ));
    }

    private void handlePublishOfGpDeadline(GroupPurchaseEntity groupPurchase) {
        OffsetDateTime deadline = groupPurchase.getDeadline();
        OffsetDateTime now = OffsetDateTime.now(ZoneId.systemDefault())
                .withOffsetSameLocal(deadline.getOffset());
        Duration delay = Duration.between(now, deadline);
        long ttlMillis = delay.toMillis();

        if (ttlMillis <= 0) {
            log.error("Invalid deadline calculated: {} ms for group purchase {}. Deadline must be in the future.",
                ttlMillis, groupPurchase.getId());
            throw new IllegalStateException("Deadline must be in the future");
        }

        GroupPurchaseMessage message = new GroupPurchaseMessage(groupPurchase.getId());

        rabbitTemplate.convertAndSend(
            rabbitMQProperties.deadlineExchange(),
            rabbitMQProperties.deadlineRoutingKey(),
            message,
            msg -> {
                msg.getMessageProperties().setHeader("x-delay",delay);
                msg.getMessageProperties().setHeader(AmqpHeaders.CORRELATION_ID, groupPurchase.getId().toString());
                return msg;
            }
        );

        log.info("Successfully scheduled deadline for group purchase {} with delay {} ms and messageId {}",
            groupPurchase.getId(), ttlMillis, groupPurchase.getId());
    }
}
