package com.ims.nslmotors.services.admin.impl;

import com.ims.nslmotors.dto.admin.DtoAdminCustomer;
import com.ims.nslmotors.dto.admin.DtoAdminCustomerIU;
import com.ims.nslmotors.model.Customer;
import com.ims.nslmotors.repository.admin.AdminCustomerRepository;
import com.ims.nslmotors.services.admin.IAdminCustomerService;
import jakarta.persistence.criteria.Predicate; // Dinamik filtreleme i?in
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
public class AdminAdminCustomerServiceImpl implements IAdminCustomerService {

    @Autowired
    private AdminCustomerRepository adminCustomerRepository;

    @Override
    public Page<DtoAdminCustomer> getCustomersWithPaginationAndSearch(DtoAdminCustomer dtoAdminCustomer, Pageable pageable) {

        Specification<Customer> specification = buildSpecification(dtoAdminCustomer);

        Page<Customer> customerPage = adminCustomerRepository.findAll(specification, pageable);

        return customerPage.map(this::convertToDto);
    }

    private Specification<Customer> buildSpecification(DtoAdminCustomer dtoAdminCustomer) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dtoAdminCustomer.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), dtoAdminCustomer.getId()));
            }

            if (dtoAdminCustomer.getFirstName() != null && !dtoAdminCustomer.getFirstName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        "%" + dtoAdminCustomer.getFirstName().toLowerCase() + "%"));
            }
            if (dtoAdminCustomer.getLastName() != null && !dtoAdminCustomer.getLastName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                        "%" + dtoAdminCustomer.getLastName().toLowerCase() + "%"));
            }
            if (dtoAdminCustomer.getEmail() != null && !dtoAdminCustomer.getEmail().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + dtoAdminCustomer.getEmail().toLowerCase() + "%"));
            }
            if (dtoAdminCustomer.getPhoneNumber() != null && !dtoAdminCustomer.getPhoneNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phoneNumber"),
                        "%" + dtoAdminCustomer.getPhoneNumber() + "%"));
            }

            // T?m ko?ullar? AND ile birle?tir
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // YENİ: Müşteri oluşturma mantığı (CRUD: CREATE)
    @Override
    public DtoAdminCustomer createCustomer(DtoAdminCustomerIU customerCreationDto) {
        // 1. DTO'dan Entity'ye dönüştür
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerCreationDto, customer);

        // KRİTİK GÜVENLİK NOTU: Gerçek projede şifre hash'lenmelidir (passwordEncoder.encode(password))
        // Şu an Security'yi atladığımız için düz metin kaydediyoruz.

        // 2. Entity'yi veritabanına kaydet
        Customer savedCustomer = adminCustomerRepository.save(customer);

        // 3. Kaydedilen Entity'yi Response DTO'ya dönüştür ve döndür
        return convertToDto(savedCustomer);
    }

    @Override
    public List<DtoAdminCustomer> createCustomersBulk(List<DtoAdminCustomerIU> customerCreationDtos) {
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
        List<Customer> savedCustomers = adminCustomerRepository.saveAll(customersToSave);

        // 3. Kaydedilen Entity Listesini Response DTO Listesine dönüştür
        return savedCustomers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DtoAdminCustomer updateCustomer(Long id, DtoAdminCustomerIU updateDto){
        Customer existingCustomer = adminCustomerRepository.findById(id).orElse(null);//BURADA EXCEPTION GEÇMEDİM GGÖZDEN KAÇMASIN

        String oldPassword = existingCustomer.getPassword();
        BeanUtils.copyProperties(updateDto, existingCustomer);

        if (updateDto.getPassword() == null || updateDto.getPassword().trim().isEmpty()) {
            existingCustomer.setPassword(oldPassword);
        }

        Customer updatedCustomer = adminCustomerRepository.save(existingCustomer);

        return convertToDto(updatedCustomer);

    }

    public void deleteCustomer(Long id){
        if (!adminCustomerRepository.existsById(id)) {
            // Var olmayan bir ID silinmeye çalışılırsa hata fırlat
            throw new NoSuchElementException("ID " + id + " ile müşteri bulunamadığı için silinemedi.");
        }

        // 2. Silme işlemini gerçekleştir
        // NOT: Customer Entity'nizde Order Entity'sine olan ilişki (cascade = CascadeType.ALL, orphanRemoval = true)
        // olduğu için, bu müşteriye ait tüm siparişler de otomatik olarak silinecektir.
        adminCustomerRepository.deleteById(id);
    }



    private DtoAdminCustomer convertToDto(Customer customer) {
        DtoAdminCustomer dto = new DtoAdminCustomer();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }
}