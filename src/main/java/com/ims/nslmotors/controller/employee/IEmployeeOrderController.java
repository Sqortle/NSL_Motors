package com.ims.nslmotors.controller.employee;

import com.ims.nslmotors.dto.employee.DtoEmployeeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface IEmployeeOrderController {

    @GetMapping("/orders")
    ResponseEntity<Page<DtoEmployeeOrder>> getMyOrders(
            Pageable pageable,
            jakarta.servlet.http.HttpSession session
    );

    @PutMapping("/orders/{orderId}/status")
    ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            jakarta.servlet.http.HttpSession session
    );
}
