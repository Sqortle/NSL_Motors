package com.ims.nslmotors.controller.admin.impl;

import com.ims.nslmotors.controller.admin.IAdminOrderController;
import com.ims.nslmotors.dto.admin.DtoAdminOrder;
import com.ims.nslmotors.dto.admin.DtoAdminOrderIU;
import com.ims.nslmotors.services.admin.IAdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderControllerImpl implements IAdminOrderController {

    private final IAdminOrderService orderService;

    // --- CRUD: READ (Sayfaland?rma ve Esnek Arama) ---
    @Override
    @GetMapping("/list")
    public ResponseEntity<Page<DtoAdminOrder>> getOrders(
            @ModelAttribute DtoAdminOrder criteria,
            Pageable pageable) {

        // Eğer sort parametresi yoksa, default olarak orderDate'e göre en yeni önce
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("orderDate").descending());
        }

        Page<DtoAdminOrder> orderPage = orderService.getOrders(criteria, pageable);

        return ResponseEntity.ok(orderPage);
    }

    // --- CRUD: CREATE ---
    @Override
    @PostMapping("/add")
    public ResponseEntity<DtoAdminOrder> createOrder(@Valid @RequestBody DtoAdminOrderIU orderCreationDto) {
        DtoAdminOrder createdOrder = orderService.createOrder(orderCreationDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    // --- CRUD: UPDATE ---
    @Override
    @PutMapping("/update/{id}")
    public ResponseEntity<DtoAdminOrder> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody DtoAdminOrderIU updateDto) {

        DtoAdminOrder updatedOrder = orderService.updateOrder(id, updateDto);
        return ResponseEntity.ok(updatedOrder);
    }

    // --- CRUD: DELETE ---
    @Override
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}