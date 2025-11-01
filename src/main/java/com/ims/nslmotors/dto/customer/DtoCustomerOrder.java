// DtoCustomerOrder.java
package com.ims.nslmotors.dto.customer;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DtoCustomerOrder {

    private Long id;
    private String orderNumber;
    private String stageSelected;
    private LocalDateTime appointmentDate;
    private String status;

    // İlişkili Bilgiler (Kolay okuma için)
    private String carMakeAndModel;
    private String technicianShopName;
}