package com.bookteria.identity_service.controller;

import com.bookteria.identity_service.dto.request.AuthenticationRequest;
import com.bookteria.identity_service.dto.request.IntrospectRequest;
import com.bookteria.identity_service.dto.request.LogoutRequest;
import com.bookteria.identity_service.dto.response.ApiResponse;
import com.bookteria.identity_service.dto.response.AuthenticationResponse;
import com.bookteria.identity_service.dto.response.IntrospectResponse;
import com.bookteria.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticated(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws JOSEException, ParseException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws JOSEException, ParseException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }
}
