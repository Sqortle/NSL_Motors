package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.entities.Customer; // Entities yerine model paketini kullan?yoruz
import com.ims.nslmotors.repository.CustomerRepository;
import com.ims.nslmotors.services.ICustomerService; // Interface'i referans almal?
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service; // Service anotasyonu eklendi

import java.util.List;
import java.util.stream.Collectors;

@Service // Spring IoC konteynerine Service oldu?unu bildirir
@RequiredArgsConstructor // Lombok: final alanlar i?in otomatik constructor (DI i?in) olu?turur
public class CustomerServiceImpl implements ICustomerService {

    // Ba??ml?l?k Enjeksiyonu (Dependency Injection) i?in final ve private tan?mlama
    private final CustomerRepository customerRepository;

    // Not: Customer Service interface'ini de tan?mlamal?y?z

    @Override
    public List<DtoCustomer> getAllCustomers() { // Metot ad?n? de?i?tirdik
        List<Customer> customers = customerRepository.findAll();

        // Entity listesini DTO listesine d?n??t?r
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Customer Entity'den CustomerResponseDto'ya d?n??t?r?r.
     */
    private DtoCustomer convertToDto(Customer customer) {
        DtoCustomer dto = new DtoCustomer();

        // BeanUtils, Customer Entity'sinden DTO'ya ayn? isimdeki alanlar? kopyalar.
        BeanUtils.copyProperties(customer, dto);

        // ESKİ ROL KONTROLÜ SİLİNDİ:
        // if (customer.getRole() != null) { dto.setRoleName(...); }

        return dto;
    }
}