package com.bookteria.profile_service.controller;

import com.bookteria.profile_service.dto.request.ProfileCreationRequest;
import com.bookteria.profile_service.dto.response.UserProfileResponse;
import com.bookteria.profile_service.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {

    UserProfileService userProfileService;

    @GetMapping("/users/{profileId}")
    UserProfileResponse getProfile(@PathVariable String profileId) {
        return userProfileService.getProfile(profileId);
    }

    @PostMapping("/users")
    UserProfileResponse createProfile(@RequestBody ProfileCreationRequest request) {
        return userProfileService.createProfile(request);
    }

}
