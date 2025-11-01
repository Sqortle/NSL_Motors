package com.ims.nslmotors.services.employee;

import com.ims.nslmotors.dto.employee.DtoEmployeeProfile;
import com.ims.nslmotors.dto.employee.DtoEmployeeRegistirationIU;

public interface IEmployeeAuthService {

    // Güvenlik gerektiren metodlar (Sadece imza)
    // DtoEmployeeRegistirationIU, yeni teknisyen oluşturmak veya profil güncellemek için kullanılır.
    void registerNewEmployee(DtoEmployeeRegistirationIU registrationDto);
    void updateEmployeeProfile(Long employeeId, DtoEmployeeRegistirationIU updateDto);

    // Kendi profilini çekme
    DtoEmployeeProfile getEmployeeProfile(Long employeeId);
}