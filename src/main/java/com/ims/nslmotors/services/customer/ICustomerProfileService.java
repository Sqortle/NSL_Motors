package com.ims.nslmotors.services.customer;

import com.ims.nslmotors.dto.customer.DtoCustomerProfile;
import com.ims.nslmotors.dto.customer.DtoCustomerProfileUpdate;
import com.ims.nslmotors.dto.customer.DtoCustomerPasswordChange;

public interface ICustomerProfileService {
    
    /**
     * Kullanıcı ID'sine göre profil bilgilerini getirir
     */
    DtoCustomerProfile getProfile(Long customerId);
    
    /**
     * Profil bilgilerini günceller
     */
    DtoCustomerProfile updateProfile(Long customerId, DtoCustomerProfileUpdate updateDto);
    
    /**
     * Şifre değiştirme işlemi
     */
    void changePassword(Long customerId, DtoCustomerPasswordChange passwordChangeDto);
}

