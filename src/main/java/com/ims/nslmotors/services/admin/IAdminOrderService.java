package com.ims.nslmotors.services.admin;

import com.ims.nslmotors.dto.admin.DtoAdminOrder;
import com.ims.nslmotors.dto.admin.DtoAdminOrderIU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminOrderService {

    // CRUD: READ - Sayfaland?rma ve Arama
    Page<DtoAdminOrder> getOrders(DtoAdminOrder criteria, Pageable pageable);

    // CRUD: CREATE - Tekil
    DtoAdminOrder createOrder(DtoAdminOrderIU orderCreationDto);

    // CRUD: UPDATE
    DtoAdminOrder updateOrder(Long id, DtoAdminOrderIU updateDto);

    // CRUD: DELETE
    void deleteOrder(Long id);
}