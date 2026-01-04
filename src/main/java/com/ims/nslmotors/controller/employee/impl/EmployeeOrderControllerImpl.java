package com.ims.nslmotors.controller.employee.impl;

import com.ims.nslmotors.controller.employee.IEmployeeOrderController;
import com.ims.nslmotors.dto.employee.DtoEmployeeOrder;
import com.ims.nslmotors.services.employee.IEmployeeOrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeOrderControllerImpl implements IEmployeeOrderController {

    private final IEmployeeOrderService employeeOrderService;

    @Override
    @GetMapping("/orders")
    public ResponseEntity<Page<DtoEmployeeOrder>> getMyOrders(
            Pageable pageable,
            HttpSession session) {

        Long employeeId = (Long) session.getAttribute("employeeId");
        if (employeeId == null) {
            return ResponseEntity.status(401).build();
        }

        Page<DtoEmployeeOrder> orders =
                employeeOrderService.getPagedFutureOrdersForTechnician(employeeId, pageable);

        return ResponseEntity.ok(orders);
    }

    @Override
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            HttpSession session) {

        Long employeeId = (Long) session.getAttribute("employeeId");
        if (employeeId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            employeeOrderService.updateOrderStatus(orderId, employeeId, status);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
