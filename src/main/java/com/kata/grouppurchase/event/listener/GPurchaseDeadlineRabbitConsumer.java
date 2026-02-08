package com.kata.grouppurchase.event.listener;

import jakarta.validation.Valid;
import com.kata.grouppurchase.dao.GroupPurchaseEntity;
import com.kata.grouppurchase.dao.ProcessedMessageEntity;
import com.kata.grouppurchase.dto.GroupPurchaseMessage;
import com.kata.grouppurchase.enums.Status;
import com.kata.grouppurchase.repository.GroupPurchaseEntityRepository;
import com.kata.grouppurchase.repository.ProcessedMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Component
@Validated
public class GPurchaseDeadlineRabbitConsumer {
    private static final Logger log = LoggerFactory.getLogger(GPurchaseDeadlineRabbitConsumer.class);
    private static final String MESSAGE_TYPE = "GROUP_PURCHASE_DEADLINE";

    private final GroupPurchaseEntityRepository groupPurchaseRepository;
    private final ProcessedMessageRepository processedMessageRepository;

    public GPurchaseDeadlineRabbitConsumer(
        GroupPurchaseEntityRepository groupPurchaseRepository,
        ProcessedMessageRepository processedMessageRepository) {
        this.groupPurchaseRepository = groupPurchaseRepository;
        this.processedMessageRepository = processedMessageRepository;
    }

    @RabbitListener(queues ="${app.rabbitmq.deadline-queue}")
    @Transactional
    public void handleExpiredGroupPurchase(@Payload @Valid GroupPurchaseMessage message, @Header(AmqpHeaders.CORRELATION_ID) String messageId) {

        log.info("Processing expired group purchase deadline: {} with messageId: {}",
            message.purchaseId(), messageId);

        if (messageId != null && processedMessageRepository.existsById(messageId)) {
            log.warn("Message {} already processed, skipping duplicate", messageId);
            return;
        }

        GroupPurchaseEntity groupPurchase = groupPurchaseRepository.findById(message.purchaseId()).orElse(null);

        if (Objects.isNull(groupPurchase)) {
            log.warn("Group purchase not found: {}", message.purchaseId());
            recordProcessedMessage(messageId);
            return;
        }

        OffsetDateTime deadline = groupPurchase.getDeadline();
        OffsetDateTime now = OffsetDateTime.now(ZoneId.systemDefault())
                .withOffsetSameLocal(deadline.getOffset());
        if (deadline.isAfter(now)) {
            log.warn("Group purchase {} deadline has not passed yet. Deadline: {}, Current time: {}. Skipping processing.",
                message.purchaseId(), deadline, now);
           throw new RuntimeException("retry the message latter");
        }

        if (groupPurchase.getCurrentCount() >= groupPurchase.getMinParticipants()) {
            groupPurchase.setStatus(Status.FINALIZED);
            log.info("Group purchase {} FINALIZED with {} participants (min: {})",
                message.purchaseId(),
                groupPurchase.getCurrentCount(),
                groupPurchase.getMinParticipants());
        } else {
            groupPurchase.setStatus(Status.CANCELLED);
            log.info("Group purchase {} CANCELLED with {} participants (min: {})",
                message.purchaseId(),
                groupPurchase.getCurrentCount(),
                groupPurchase.getMinParticipants());
        }

        groupPurchaseRepository.saveAndFlush(groupPurchase);

        recordProcessedMessage(messageId);

        log.info("Successfully processed group purchase deadline for {} with status {}",
            message.purchaseId(), groupPurchase.getStatus());
    }

    private void recordProcessedMessage(String messageId) {
        if (messageId == null) {
            log.warn("Message ID is null, cannot record for idempotency");
            return;
        }

        ProcessedMessageEntity processedMessage = new ProcessedMessageEntity();
        processedMessage.setMessageId(messageId);
        processedMessage.setProcessedAt(OffsetDateTime.now());
        processedMessage.setMessageType(MESSAGE_TYPE);
        processedMessageRepository.saveAndFlush(processedMessage);

        log.debug("Recorded processed message: {}", messageId);
    }
}
