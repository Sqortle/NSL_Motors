package com.ims.nslmotors.services.employee.impl;

import com.ims.nslmotors.services.employee.IEmployeeAuthService;
import com.ims.nslmotors.dto.employee.DtoEmployeeProfile;
import com.ims.nslmotors.dto.employee.DtoEmployeeRegistirationIU;
import org.springframework.stereotype.Service;

@Service
public class EmployeeAuthServiceImpl implements IEmployeeAuthService {

    // Not: Bu servis katman?nda UserRepository, TechnicianDetailsRepository vb. enjekte edilmelidir.

    @Override
    public void registerNewEmployee(DtoEmployeeRegistirationIU registrationDto) {
        // Implementasyon güvenlik katman?nda (security layer) olacakt?r.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void updateEmployeeProfile(Long employeeId, DtoEmployeeRegistirationIU updateDto) {
        // Implementasyon güvenlik katman?nda olacakt?r.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public DtoEmployeeProfile getEmployeeProfile(Long employeeId) {
        // Kullan?c? ID'sine g?re User ve TechnicianDetails tablosundan verileri ?eker.
        // Ge?ici olarak bo? d?nd?r?lebilir.
        return null;
    }
}