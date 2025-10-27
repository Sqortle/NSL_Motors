package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.dto.DTOUser;
import com.ims.nslmotors.dto.DTOUser;
import com.ims.nslmotors.entities.User;
import com.ims.nslmotors.repository.UserRepository;
import com.ims.nslmotors.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils; // Import edildi
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    // Admin, Vendor ve Customer için gerekli diğer Repository'ler buraya eklenecek
    // private final RoleRepository roleRepository;

    @Override
    public List<DTOUser> getAllUsers() {
        // 1. Repository'den tüm Entity'leri çek
        List<User> users = userRepository.findAll();

        // 2. Entity listesini DTO listesine dönüştür
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * User Entity'den UserResponseDto'ya dönü??üm metodu (BeanUtils ile)
     */
    private DTOUser convertToDto(User user) {
        DTOUser dto = new DTOUser();

        // Alan adlar? ayn? olanlar? kopyala (id, firstName, email vb.)
        BeanUtils.copyProperties(user, dto);

        // Rol ismini (ili?kisel alan) el ile ayarla
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getName());
        }

        return dto;
    }

    // ... Di?er metotlar buraya gelecek (Create, Update, Delete)
}