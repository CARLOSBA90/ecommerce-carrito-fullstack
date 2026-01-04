package com.ecommerce.carrito.service;

import com.ecommerce.carrito.dto.auth.LoginRequestDto;
import com.ecommerce.carrito.dto.auth.LoginResponseDto;

public interface IAuthService {

    LoginResponseDto login(LoginRequestDto request);
}
