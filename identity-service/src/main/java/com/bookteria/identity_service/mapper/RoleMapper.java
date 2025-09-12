package com.bookteria.identity_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookteria.identity_service.dto.request.RoleRequest;
import com.bookteria.identity_service.dto.response.RoleResponse;
import com.bookteria.identity_service.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true) // Không hiển thị permission
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
