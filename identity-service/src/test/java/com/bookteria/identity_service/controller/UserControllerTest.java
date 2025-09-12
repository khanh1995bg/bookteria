package com.bookteria.identity_service.controller;

import com.bookteria.identity_service.dto.request.UserCreationRequest;
import com.bookteria.identity_service.dto.response.UserResponse;
import com.bookteria.identity_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@TestPropertySource("/test.properties") //Override file application.yaml
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserCreationRequest request; //Data đầu vào
    private UserResponse userResponse; //Data đầu

    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1995,1, 1);
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
    }

    @Test
    void createUser_validRequest_success() throws Exception {
//  GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); //Hỗ trợ data LocalDate bằng depen: jackson-datatype-jsr310
        String content = objectMapper.writeValueAsString(request);
        log.debug("content: {}", content);

        // Gọi đến service
        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);

//  WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk()) //Dòng này để bắt httpStatus
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("847482dff82")); //Dòng này để bắt StatusCode
    }

    @Test
    void createUser_usernameInvalid_failed() throws Exception {
//        GIVEN
        request.setUsername("kh"); //Nhập tên sai so với request đầu vào được tạo trên hàm initData
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

//        WHEN,THEN
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 3 characters"));
    }
}
