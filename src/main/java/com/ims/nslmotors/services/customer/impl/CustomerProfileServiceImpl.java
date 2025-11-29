package com.ims.nslmotors.services.customer.impl;

import com.ims.nslmotors.dto.customer.DtoCustomerProfile;
import com.ims.nslmotors.dto.customer.DtoCustomerProfileUpdate;
import com.ims.nslmotors.dto.customer.DtoCustomerPasswordChange;
import com.ims.nslmotors.model.Customer;
import com.ims.nslmotors.repository.admin.AdminCustomerRepository;
import com.ims.nslmotors.services.customer.ICustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements ICustomerProfileService {

    private final AdminCustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public DtoCustomerProfile getProfile(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı."));
        
        DtoCustomerProfile profile = new DtoCustomerProfile();
        BeanUtils.copyProperties(customer, profile);
        return profile;
    }

    @Override
    @Transactional
    public DtoCustomerProfile updateProfile(Long customerId, DtoCustomerProfileUpdate updateDto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı."));
        
        // Email değişikliği kontrolü - başka bir kullanıcı bu email'i kullanıyorsa hata ver
        if (!customer.getEmail().equals(updateDto.getEmail())) {
            customerRepository.findByEmail(updateDto.getEmail())
                    .ifPresent(existingCustomer -> {
                        if (!existingCustomer.getId().equals(customerId)) {
                            throw new RuntimeException("Bu email adresi başka bir kullanıcı tarafından kullanılıyor.");
                        }
                    });
        }
        
        // Profil bilgilerini güncelle
        customer.setFirstName(updateDto.getFirstName());
        customer.setLastName(updateDto.getLastName());
        customer.setEmail(updateDto.getEmail());
        customer.setPhoneNumber(updateDto.getPhoneNumber());
        
        Customer updatedCustomer = customerRepository.save(customer);
        
        DtoCustomerProfile profile = new DtoCustomerProfile();
        BeanUtils.copyProperties(updatedCustomer, profile);
        return profile;
    }

    @Override
    @Transactional
    public void changePassword(Long customerId, DtoCustomerPasswordChange passwordChangeDto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı."));
        
        // Mevcut şifreyi kontrol et
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), customer.getPassword())) {
            throw new RuntimeException("Mevcut şifre hatalı.");
        }
        
        // Yeni şifre ve tekrarı eşleşiyor mu kontrol et
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new RuntimeException("Yeni şifre ve şifre tekrarı eşleşmiyor.");
        }
        
        // Yeni şifreyi hashle ve kaydet
        customer.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        customerRepository.save(customer);
    }
}

