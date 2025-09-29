package com.bookteria.identity_service.service;

import java.util.HashSet;
import java.util.List;

import com.bookteria.identity_service.mapper.ProfileMapper;
import com.bookteria.identity_service.repository.httpclient.ProfileClient;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookteria.identity_service.dto.request.UserCreationRequest;
import com.bookteria.identity_service.dto.request.UserUpdateRequest;
import com.bookteria.identity_service.dto.response.UserResponse;
import com.bookteria.identity_service.entity.User;
import com.bookteria.identity_service.enums.Role;
import com.bookteria.identity_service.exception.AppException;
import com.bookteria.identity_service.exception.ErrorCode;
import com.bookteria.identity_service.mapper.UserMapper;
import com.bookteria.identity_service.repository.RoleRepository;
import com.bookteria.identity_service.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    ProfileMapper profileMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    ProfileClient profileClient;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        //        create ROLE
        HashSet<com.bookteria.identity_service.entity.Role> roles = new HashSet<>();
        roleRepository.findById(Role.USER.name()).ifPresent(roles::add);

        user.setRoles(roles);
        user = userRepository.save(user);

//        Khi tạo user thành công sẽ tạo profile
        var profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getId());
        profileClient.createProfile(profileRequest);

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')") // Check điều kiện hasRole xong thoả mãn thì mới đi tiếp
    //    @PreAuthorize(("hasAuthority('CREATE_POST')")) //Sử dụng với trường hợp get permission
    public List<UserResponse> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize(
            "returnObject.username == authentication.name") // CALL API THành công thì mới check điều kiện hasRole
    public UserResponse getUser(String id) {
        log.info("Getting user by id: {}", id);
        return userMapper.toUserResponse(userRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found")));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }
}
