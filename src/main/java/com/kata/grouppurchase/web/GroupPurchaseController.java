package com.kata.grouppurchase.web;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import com.kata.grouppurchase.dto.GroupPurchaseDto;
import com.kata.grouppurchase.dto.Views;
import com.kata.grouppurchase.enums.Status;
import com.kata.grouppurchase.helper.AuthenticationHelper;
import com.kata.grouppurchase.service.GroupPurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group-purchase")
public record GroupPurchaseController(
    GroupPurchaseService groupPurchaseService,
    AuthenticationHelper authenticationHelper
) {

    private static final Logger log = LoggerFactory.getLogger(GroupPurchaseController.class);

    @PostMapping
    @JsonView(Views.Response.class)
    public ResponseEntity<GroupPurchaseDto> createGroupPurchase(
        @Valid @RequestBody @JsonView(Views.Request.class) GroupPurchaseDto groupPurchaseDto
    ) {
        GroupPurchaseDto response = groupPurchaseService.createGroupPurchase(groupPurchaseDto);

        log.info("Group purchase {} created with {} participants",
            response.id(), response.currentCount());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/join")
    @JsonView(Views.Response.class)
    public ResponseEntity<GroupPurchaseDto> joinGroupPurchase(
        @PathVariable UUID id
    ) {
        groupPurchaseService.joinGroupPurchase(id);

        log.info("Customer joined group purchase {} is succefull",id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @JsonView(Views.Response.class)
    public ResponseEntity<GroupPurchaseDto> getGroupPurchaseById(
        @PathVariable UUID id
    ) {
        log.info("Fetching group purchase by ID: {}", id);

        GroupPurchaseDto groupPurchase = groupPurchaseService.getGroupPurchaseById(id);

        return ResponseEntity.ok(groupPurchase);
    }

    @GetMapping
    @JsonView(Views.Response.class)
    public ResponseEntity<List<GroupPurchaseDto>> listGroupPurchases(
        @RequestParam(required = false) Status status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Listing group purchases - status: {}, page: {}, size: {}", status, page, size);


        Pageable pageable = PageRequest.of(page, size);
        Slice<GroupPurchaseDto> groupPurchases = groupPurchaseService.getAllGroupPurchases(status, pageable);

        log.info("Returning {} group purchases", groupPurchases.getNumberOfElements());

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Has-Next", String.valueOf(groupPurchases.hasNext()));
        headers.add("X-Is-First", String.valueOf(groupPurchases.isFirst()));
        headers.add("X-Page-Number", String.valueOf(groupPurchases.getNumber()));
        headers.add("X-Page-Size", String.valueOf(groupPurchases.getSize()));

        return ResponseEntity.ok().headers(headers).body(groupPurchases.getContent());
    }
}
