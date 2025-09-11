package com.bookteria.identity_service.dto.request;

import com.bookteria.identity_service.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
//    @Size(min = 8, message = "Password min 8 character")
    String password;
     String firstName;
     String lastName;

     @DobConstraint(min = 18, message = "INVALID_DOB")
     LocalDate dob;
     List<String> roles;
}
