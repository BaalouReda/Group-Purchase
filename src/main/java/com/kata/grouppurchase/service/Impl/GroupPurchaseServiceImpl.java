package com.kata.grouppurchase.service.Impl;

import com.kata.grouppurchase.dao.CustomerEntity;
import com.kata.grouppurchase.dao.GroupPurchaseEntity;
import com.kata.grouppurchase.dao.ParticipantEntity;
import com.kata.grouppurchase.dao.ProductEntity;
import com.kata.grouppurchase.dto.GroupPurchaseDto;
import com.kata.grouppurchase.enums.Status;
import com.kata.grouppurchase.enums.EventType;
import com.kata.grouppurchase.event.dto.GroupPurchaseCreatedEvent;
import com.kata.grouppurchase.helper.AuthenticationHelper;
import com.kata.grouppurchase.mapper.GroupPurchaseMapper;
import com.kata.grouppurchase.repository.GroupPurchaseEntityRepository;
import com.kata.grouppurchase.repository.ParticipantEntityRepository;
import com.kata.grouppurchase.repository.ProductEntityRepository;
import com.kata.grouppurchase.service.GroupPurchaseService;
import com.kata.grouppurchase.service.PriceCalculationService;
import com.kata.grouppurchase.web.exception.GroupPurchaseException;
import com.kata.grouppurchase.web.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class GroupPurchaseServiceImpl implements GroupPurchaseService {

    private static final Logger log = LoggerFactory.getLogger(GroupPurchaseServiceImpl.class);

    private final GroupPurchaseEntityRepository groupPurchaseRepository;
    private final ParticipantEntityRepository participantRepository;
    private final ProductEntityRepository productRepository;
    private final GroupPurchaseMapper groupPurchaseMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthenticationHelper authenticationHelper;
    private final PriceCalculationService priceCalculationService;

    public GroupPurchaseServiceImpl(
            GroupPurchaseEntityRepository groupPurchaseRepository,
            ParticipantEntityRepository participantRepository,
            ProductEntityRepository productRepository,
            GroupPurchaseMapper groupPurchaseMapper,
            ApplicationEventPublisher eventPublisher,
            AuthenticationHelper authenticationHelper, PriceCalculationService priceCalculationService) {
        this.groupPurchaseRepository = groupPurchaseRepository;
        this.participantRepository = participantRepository;
        this.productRepository = productRepository;
        this.groupPurchaseMapper = groupPurchaseMapper;
        this.eventPublisher = eventPublisher;
        this.authenticationHelper = authenticationHelper;
        this.priceCalculationService = priceCalculationService;
    }

    @Override
    @Transactional
    public GroupPurchaseDto createGroupPurchase(GroupPurchaseDto groupPurchaseDto) {
        CustomerEntity creator = authenticationHelper.getCurrentCustomer();
        log.debug("Creating group purchase for product {} by customer {}",
                groupPurchaseDto.productId(), creator.getId());

        ProductEntity product = productRepository.findById(groupPurchaseDto.productId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", groupPurchaseDto.productId().toString()));

        if (product.getActive() == null || !product.getActive()) {
            throw GroupPurchaseException.productNotActive();
        }

        GroupPurchaseEntity groupPurchase = groupPurchaseMapper.toEntity(groupPurchaseDto);
        groupPurchase.setProduct(product);
        groupPurchase.setCreator(creator);
        groupPurchase.setStatus(Status.PENDING);
        groupPurchase.setCurrentCount(1);

        var currentPrice = priceCalculationService.calculateCurrentPrice(product,1);
        groupPurchase.setCurrentPrice(currentPrice);

        groupPurchase = groupPurchaseRepository.save(groupPurchase);

        ParticipantEntity participant = new ParticipantEntity();
        participant.setId(UUID.randomUUID());
        participant.setGroup(groupPurchase);
        participant.setCustomer(creator);
        participantRepository.saveAndFlush(participant);

        eventPublisher.publishEvent(new GroupPurchaseCreatedEvent(
            groupPurchase.getId(),
            EventType.CREATE
        ));

        return groupPurchaseMapper.toDto(groupPurchase);
    }

    @Override
    @Transactional
    public void joinGroupPurchase(UUID groupPurchaseId) {
        CustomerEntity customer = authenticationHelper.getCurrentCustomer();
        log.debug("Customer {} attempting to join group purchase {}", customer.getId(), groupPurchaseId);

        GroupPurchaseEntity groupPurchase = groupPurchaseRepository.findById(groupPurchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Purchase", groupPurchaseId.toString()));

        if (participantRepository.existsParticipantEntitiesByGroupAndCustomer(groupPurchase, customer)) {
            log.warn("Customer {} already joined group purchase {}", customer.getId(), groupPurchaseId);
            throw GroupPurchaseException.alreadyJoined();
        }

        if (groupPurchase.getStatus() != Status.PENDING) {
            log.warn("Cannot join group purchase {} - invalid status: {}", groupPurchaseId, groupPurchase.getStatus());
            throw GroupPurchaseException.invalidStatus(groupPurchase.getStatus().toString());
        }

        OffsetDateTime deadline = groupPurchase.getDeadline();
        OffsetDateTime now = OffsetDateTime.now(ZoneId.systemDefault())
                .withOffsetSameLocal(deadline.getOffset());
        if (deadline.isBefore(now)) {
            log.warn("Cannot join group purchase {} - deadline passed", groupPurchaseId);
            throw GroupPurchaseException.deadlinePassed();
        }

        if (groupPurchase.getStatus() == Status.FULL ) {
            log.warn("Cannot join group purchase {} - already full", groupPurchaseId);
            throw GroupPurchaseException.groupFull();
        }

        ParticipantEntity participant = new ParticipantEntity();
        participant.setId(UUID.randomUUID());
        participant.setGroup(groupPurchase);
        participant.setCustomer(customer);
        participantRepository.save(participant);

        groupPurchase.setCurrentCount(groupPurchase.getCurrentCount() + 1);

        if (groupPurchase.getCurrentCount().equals(groupPurchase.getMaxParticipants())) {
            groupPurchase.setStatus(Status.FULL);
            log.info("Group purchase {} is now FULL", groupPurchaseId);
        }

        log.debug("Calculate the Price based on the number of participant");
        var currentPrice = priceCalculationService.calculateCurrentPrice(groupPurchase.getProduct(), groupPurchase.getCurrentCount());
        groupPurchase.setCurrentPrice(currentPrice);

        log.info("Customer {} successfully joined group purchase {} (count: {}/{})",
                customer.getId(), groupPurchaseId, groupPurchase.getCurrentCount(), groupPurchase.getMaxParticipants());

        eventPublisher.publishEvent(new GroupPurchaseCreatedEvent(
                groupPurchase.getId(),
                EventType.JOIN
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public GroupPurchaseDto getGroupPurchaseById(UUID groupPurchaseId) {
        log.debug("Fetching group purchase by ID: {}", groupPurchaseId);

        GroupPurchaseEntity groupPurchase = groupPurchaseRepository.findById(groupPurchaseId)
            .orElseThrow(() -> new ResourceNotFoundException("Group Purchase", groupPurchaseId.toString()));

        log.info("Retrieved group purchase {} with status {}", groupPurchaseId, groupPurchase.getStatus());

        return groupPurchaseMapper.toDto(groupPurchase);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<GroupPurchaseDto> getAllGroupPurchases(Status status,Pageable pageable) {
        log.debug("Fetching group purchases - status: {}, pagination: {}", status, pageable);

        Slice<GroupPurchaseEntity> groupPurchasePage;

        if (status != null) {
            groupPurchasePage = groupPurchaseRepository.findByStatus(status, pageable);
        } else {
            groupPurchasePage = groupPurchaseRepository.findAllBy(pageable);
        }

        log.info("Retrieved {} group purchases (page {} of {})",
            groupPurchasePage.getNumberOfElements(),
            groupPurchasePage.getNumber() + 1,
            groupPurchasePage.getSize());

        return groupPurchasePage.map(groupPurchaseMapper::toDto);
    }
}
