package com.ims.nslmotors.services;

import com.ims.nslmotors.dto.DTOUser;
import com.ims.nslmotors.dto.DTOUser;
import java.util.List;

public interface IUserService {

    // Tüm kullanıcıları çekme metodu (Read operasyonu)
    List<DTOUser> getAllUsers();

    // İleride eklenecek metotlar: registerNewCustomer, deleteUser, updateUser, vb.
}