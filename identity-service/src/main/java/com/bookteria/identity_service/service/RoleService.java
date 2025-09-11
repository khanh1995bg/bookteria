package com.bookteria.identity_service.service;

import com.bookteria.identity_service.dto.request.RoleRequest;
import com.bookteria.identity_service.dto.response.RoleResponse;
import com.bookteria.identity_service.entity.Role;
import com.bookteria.identity_service.mapper.RoleMapper;
import com.bookteria.identity_service.repository.PermissionRepository;
import com.bookteria.identity_service.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse createRole(RoleRequest request) {
        var role = roleMapper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAllRoles() {
        var roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }

    public void deleteRole(String role) {
        roleRepository.deleteById(role);
    }

}
