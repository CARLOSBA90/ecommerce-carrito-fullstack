package com.ecommerce.carrito.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private Long customerId;
    private String username;
    private List<String> roles;
}
