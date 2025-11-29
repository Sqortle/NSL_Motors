package com.ims.nslmotors.services.admin.impl;

import com.ims.nslmotors.dto.admin.DtoAdminLogin;
import com.ims.nslmotors.model.Employee;
import com.ims.nslmotors.repository.admin.AdminEmployeeRepository;
import com.ims.nslmotors.services.admin.IAdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements IAdminAuthService {

    private final AdminEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void login(DtoAdminLogin loginDto) {
        // 1. Employee'yi email ile bul
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(loginDto.getEmail());
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Email veya şifre hatalı.");
        }

        Employee employee = employeeOpt.get();

        // 2. Rol kontrolü - sadece ADMIN rolü giriş yapabilir
        if (!"ADMIN".equals(employee.getRole())) {
            throw new RuntimeException("Bu hesap admin yetkisine sahip değil.");
        }

        // 3. Şifreyi kontrol et
        if (!passwordEncoder.matches(loginDto.getPassword(), employee.getPassword())) {
            throw new RuntimeException("Email veya şifre hatalı.");
        }
    }
}

