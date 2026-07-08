package com.campushub.user.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User profile update request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    @Size(max = 64)
    private String nickname;

    @Size(max = 255)
    private String avatarUrl;

    /**
     * Converts this DTO to the existing update request.
     *
     * @return profile update request
     */
    public UpdateProfileRequest toUpdateProfileRequest() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname(nickname);
        request.setAvatarUrl(avatarUrl);
        return request;
    }
}
