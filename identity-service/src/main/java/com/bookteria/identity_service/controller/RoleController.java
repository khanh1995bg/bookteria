package com.bookteria.identity_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bookteria.identity_service.dto.request.RoleRequest;
import com.bookteria.identity_service.dto.response.ApiResponse;
import com.bookteria.identity_service.dto.response.RoleResponse;
import com.bookteria.identity_service.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(roleRequest))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/{role}")
    String deleteRole(@PathVariable String role) {
        roleService.deleteRole(role);
        return "Role deleted successfully";
    }
}
