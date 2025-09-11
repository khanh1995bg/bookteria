package com.bookteria.profile_service.service;

import com.bookteria.profile_service.dto.request.ProfileCreationRequest;
import com.bookteria.profile_service.dto.response.UserProfileResponse;
import com.bookteria.profile_service.entity.UserProfile;
import com.bookteria.profile_service.mapper.UserProfileMapper;
import com.bookteria.profile_service.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);

        userProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse getProfile(String id) {
        UserProfile userProfile = userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("UserProfile not found"));

        return userProfileMapper.toUserProfileResponse(userProfile);
    }
}
