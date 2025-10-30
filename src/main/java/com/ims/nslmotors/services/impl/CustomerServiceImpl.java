package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.dto.DtoCustomerIU;
import com.ims.nslmotors.model.Customer;
import com.ims.nslmotors.repository.CustomerRepository;
import com.ims.nslmotors.services.ICustomerService;
import jakarta.persistence.criteria.Predicate; // Dinamik filtreleme i?in
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // Dinamik filtreleme i?in
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Page<DtoCustomer> getCustomersWithPaginationAndSearch(DtoCustomer dtoCustomer, Pageable pageable) {

        Specification<Customer> specification = buildSpecification(dtoCustomer);

        Page<Customer> customerPage = customerRepository.findAll(specification, pageable);

        return customerPage.map(this::convertToDto);
    }

    private Specification<Customer> buildSpecification(DtoCustomer dtoCustomer) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dtoCustomer.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), dtoCustomer.getId()));
            }

            if (dtoCustomer.getFirstName() != null && !dtoCustomer.getFirstName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        "%" + dtoCustomer.getFirstName().toLowerCase() + "%"));
            }
            if (dtoCustomer.getLastName() != null && !dtoCustomer.getLastName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                        "%" + dtoCustomer.getLastName().toLowerCase() + "%"));
            }
            if (dtoCustomer.getEmail() != null && !dtoCustomer.getEmail().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + dtoCustomer.getEmail().toLowerCase() + "%"));
            }
            if (dtoCustomer.getPhoneNumber() != null && !dtoCustomer.getPhoneNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phoneNumber"),
                        "%" + dtoCustomer.getPhoneNumber() + "%"));
            }

            // T?m ko?ullar? AND ile birle?tir
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
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

    @Override
    public List<DtoCustomer> createCustomersBulk(List<DtoCustomerIU> customerCreationDtos) {
        // 1. DTO Listesinden Entity Listesine dönüşüm
        List<Customer> customersToSave = customerCreationDtos.stream()
                .map(dto -> {
                    Customer customer = new Customer();
                    // DTO'dan Entity'ye kopyalama
                    BeanUtils.copyProperties(dto, customer);
                    // Not: Şifre hash'leme işlemi burada yapılmalıdır (Security aktif olunca).
                    return customer;
                })
                .collect(Collectors.toList());

        // 2. Entity Listesini JPA'nın saveAll metodu ile toplu kaydet
        List<Customer> savedCustomers = customerRepository.saveAll(customersToSave);

        // 3. Kaydedilen Entity Listesini Response DTO Listesine dönüştür
        return savedCustomers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DtoCustomer updateCustomer(Long id, DtoCustomerIU updateDto){
        Customer existingCustomer = customerRepository.findById(id).orElse(null);//BURADA EXCEPTION GEÇMEDİM GGÖZDEN KAÇMASIN

        String oldPassword = existingCustomer.getPassword();
        BeanUtils.copyProperties(updateDto, existingCustomer);

        if (updateDto.getPassword() == null || updateDto.getPassword().trim().isEmpty()) {
            existingCustomer.setPassword(oldPassword);
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);

        return convertToDto(updatedCustomer);

    }

    public void deleteCustomer(Long id){
        if (!customerRepository.existsById(id)) {
            // Var olmayan bir ID silinmeye çalışılırsa hata fırlat
            throw new NoSuchElementException("ID " + id + " ile müşteri bulunamadığı için silinemedi.");
        }

        // 2. Silme işlemini gerçekleştir
        // NOT: Customer Entity'nizde Order Entity'sine olan ilişki (cascade = CascadeType.ALL, orphanRemoval = true)
        // olduğu için, bu müşteriye ait tüm siparişler de otomatik olarak silinecektir.
        customerRepository.deleteById(id);
    }



    private DtoCustomer convertToDto(Customer customer) {
        DtoCustomer dto = new DtoCustomer();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }
}