package com.ims.nslmotors.controller.admin;

import com.ims.nslmotors.dto.admin.DtoAdminOrder;
import com.ims.nslmotors.dto.admin.DtoAdminOrderIU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import java.util.List;

public interface IAdminOrderController {

    // CRUD: READ
    ResponseEntity<Page<DtoAdminOrder>> getOrders(DtoAdminOrder criteria, Pageable pageable);

    // CRUD: CREATE
    ResponseEntity<DtoAdminOrder> createOrder(@Valid DtoAdminOrderIU orderCreationDto);

    // CRUD: UPDATE
    ResponseEntity<DtoAdminOrder> updateOrder(Long id, @Valid DtoAdminOrderIU updateDto);

    // CRUD: DELETE
    ResponseEntity<Void> deleteOrder(Long id);
}