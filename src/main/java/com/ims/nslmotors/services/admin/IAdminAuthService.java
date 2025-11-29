package com.ims.nslmotors.services.admin;

import com.ims.nslmotors.dto.admin.DtoAdminLogin;

public interface IAdminAuthService {
    
    void login(DtoAdminLogin loginDto);
}

