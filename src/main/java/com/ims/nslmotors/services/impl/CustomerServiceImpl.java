package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.dto.DtoCustomerIU; // Yeni import
import com.ims.nslmotors.entities.Customer;
import com.ims.nslmotors.repository.CustomerRepository;
import com.ims.nslmotors.services.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<DtoCustomer> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // YENİ: Müşteri oluşturma mantığı (CRUD: CREATE)
    @Override
    public DtoCustomer createCustomer(DtoCustomerIU customerCreationDto) {
        // 1. DTO'dan Entity'ye dönüştür
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerCreationDto, customer);

        // KRİTİK GÜVENLİK NOTU: Gerçek projede şifre hash'lenmelidir (passwordEncoder.encode(password))
        // Şu an Security'yi atladığımız için düz metin kaydediyoruz.

        // 2. Entity'yi veritabanına kaydet
        Customer savedCustomer = customerRepository.save(customer);

        // 3. Kaydedilen Entity'yi Response DTO'ya dönüştür ve döndür
        return convertToDto(savedCustomer);
    }

    /**
     * Customer Entity'den DtoCustomer'a dönüştürür (Helper Metot)
     */
    private DtoCustomer convertToDto(Customer customer) {
        DtoCustomer dto = new DtoCustomer();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }
}