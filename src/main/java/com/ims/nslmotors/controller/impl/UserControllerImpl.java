package com.ims.nslmotors.controller.impl;

import com.ims.nslmotors.controller.IUserController;
import com.ims.nslmotors.controller.IUserController;
import com.ims.nslmotors.dto.DTOUser;
import com.ims.nslmotors.dto.DTOUser;
import com.ims.nslmotors.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// Admin endpoint'leri i?in genel bir yol tan?m?
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserControllerImpl implements IUserController {

    private final IUserService userService;

    // CRUD: READ - Tüm kullan?c?lar? getir
    @GetMapping("/users")
    @Override
    public ResponseEntity<List<DTOUser>> getAllUsers() {
        // Not: Yetkilendirme (Security) atland??? için herkes eri?ebilir.
        List<DTOUser> users = userService.getAllUsers();

        return ResponseEntity.ok(users); // HTTP 200 OK
    }
}

/*
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
                <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

 */