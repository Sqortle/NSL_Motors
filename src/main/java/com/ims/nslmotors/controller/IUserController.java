package com.ims.nslmotors.controller;

import com.ims.nslmotors.dto.DTOUser;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface IUserController {

    // Tüm kullan?c?lar? getiren (Read) operasyon
    ResponseEntity<List<DTOUser>> getAllUsers();

    // Admin CRUD i?lemleri i?in di?er metotlar buraya eklenecek
}