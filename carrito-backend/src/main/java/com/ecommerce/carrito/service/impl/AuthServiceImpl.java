package com.ecommerce.carrito.service.impl;

import com.ecommerce.carrito.dto.auth.LoginRequestDto;
import com.ecommerce.carrito.dto.auth.LoginResponseDto;
import com.ecommerce.carrito.exception.EntityNotFoundException;
import com.ecommerce.carrito.model.Customer;
import com.ecommerce.carrito.model.User;
import com.ecommerce.carrito.repository.CustomerRepository;
import com.ecommerce.carrito.repository.UserRepository;
import com.ecommerce.carrito.security.JwtUtil;
import com.ecommerce.carrito.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            Customer customer = customerRepository.findAll().stream()
                    .filter(c -> c.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .orElse(null);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toList());

            return LoginResponseDto.builder()
                    .token(token)
                    .type("Bearer")
                    .customerId(customer != null ? customer.getId() : null)
                    .username(user.getUsername())
                    .roles(roles)
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }
}
