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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AdminCustomerServiceImpl implements IAdminCustomerService {

    @Autowired
    private AdminCustomerRepository adminCustomerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        
        // KRİTİK DÜZELTME: Yeni entity oluştururken ID'yi null yaparak 
        // Hibernate'in merge yerine persist kullanmasını sağlıyoruz
        // Bu, optimistic locking hatasını önler
        customer.setId(null);

        // KRİTİK GÜVENLİK: Şifreyi BCrypt ile hashle
        if (customerCreationDto.getPassword() != null && !customerCreationDto.getPassword().trim().isEmpty()) {
            customer.setPassword(passwordEncoder.encode(customerCreationDto.getPassword()));
        }

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
                    // KRİTİK DÜZELTME: Yeni entity oluştururken ID'yi null yaparak 
                    // Hibernate'in merge yerine persist kullanmasını sağlıyoruz
                    customer.setId(null);
                    // Şifreyi BCrypt ile hashle
                    if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
                    }
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
        Customer existingCustomer = adminCustomerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID " + id + " ile müşteri bulunamadı."));

        String oldPassword = existingCustomer.getPassword();
        BeanUtils.copyProperties(updateDto, existingCustomer);
        
        // ID'yi koru
        existingCustomer.setId(id);

        // Şifre güncelleme kontrolü
        if (updateDto.getPassword() == null || updateDto.getPassword().trim().isEmpty()) {
            existingCustomer.setPassword(oldPassword);
        } else {
            // Yeni şifreyi BCrypt ile hashle
            existingCustomer.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        Customer updatedCustomer = adminCustomerRepository.save(existingCustomer);

        return convertToDto(updatedCustomer);

    }

    @Transactional
    public void deleteCustomer(Long id){
        Customer customer = adminCustomerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID " + id + " ile müşteri bulunamadığı için silinemedi."));

        // Verification codes'ları yükle (LAZY loading için)
        if (customer.getVerificationCodes() != null) {
            customer.getVerificationCodes().size(); // Lazy loading trigger
        }

        // Customer entity'sinde VerificationCode ilişkisi cascade = CascadeType.ALL, orphanRemoval = true
        // olduğu için, Customer silindiğinde verification codes'lar otomatik silinecek.
        // NOT: Customer Entity'de Order Entity'sine olan ilişki (cascade = CascadeType.ALL, orphanRemoval = true)
        // olduğu için, bu müşteriye ait tüm siparişler de otomatik olarak silinecektir.
        adminCustomerRepository.delete(customer);
    }



    private DtoAdminCustomer convertToDto(Customer customer) {
        DtoAdminCustomer dto = new DtoAdminCustomer();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }
}