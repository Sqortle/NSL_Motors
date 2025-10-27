package com.ims.nslmotors.services;

import com.ims.nslmotors.dto.UserResponseDto;
import java.util.List;

public interface IUserService {

    // Tüm kullanıcıları çekme metodu (Read operasyonu)
    List<UserResponseDto> getAllUsers();

    // İleride eklenecek metotlar: registerNewCustomer, deleteUser, updateUser, vb.
}