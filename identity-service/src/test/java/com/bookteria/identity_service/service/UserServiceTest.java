package com.bookteria.identity_service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bookteria.identity_service.dto.request.UserCreationRequest;
import com.bookteria.identity_service.dto.response.UserResponse;
import com.bookteria.identity_service.entity.User;
import com.bookteria.identity_service.exception.AppException;
import com.bookteria.identity_service.exception.ErrorCode;
import com.bookteria.identity_service.repository.UserRepository;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private UserCreationRequest request; // Data đầu vào
    private UserResponse userResponse; // Data đầu
    private User user;

    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1995, 1, 1);
        request = UserCreationRequest.builder()
                .username("khanh06")
                .password("12345678")
                .firstName("khanh")
                .lastName("duy")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("847482dff82")
                .username("khanh06")
                .firstName("khanh")
                .lastName("duy")
                .dob(dob)
                .build();

        user = User.builder()
                .id("847482dff82")
                .username("khanh06")
                .firstName("khanh")
                .lastName("duy")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        //    GIVEN
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(userRepository.save(any())).thenReturn(user);

        //        WHEN
        var response = userService.createUser(request);

        //        THEN
        Assertions.assertThat(response.getId()).isEqualTo("847482dff82");
        Assertions.assertThat(response.getUsername()).isEqualTo("khanh06");
    }

    @Test
    void createUser_userExisted_fail() {
        //        GIVEN
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(true);

        //       WHEN
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.USER_EXISTS.getCode());
    }

    @Test
    @WithMockUser(username = "khanh06") // mock username dùng depen:  spring-security-test
    void getMyInfo_valid_success() {
        //        GIVEN
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        //      WHEN
        var response = userService.getMyInfo();

        //       THEN
        Assertions.assertThat(response.getId()).isEqualTo("847482dff82");
        Assertions.assertThat(response.getUsername()).isEqualTo("khanh06");
    }

    @Test
    @WithMockUser(username = "khanh06")
    void getMyInfo_userExisted_fail() {
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));
        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
    }
}
