package com.ssafy.finalproject.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    
    @NotBlank(message = "로그인 아이디는 필수입니다.")
    private String loginId;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    
    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;
    
    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "^[mfMF]$", message = "성별은 m 또는 f여야 합니다.")
    private String gender;
    
    @NotBlank(message = "달력 유형은 필수입니다.")
    @Pattern(regexp = "^(solar|lunarGeneral|lunarLeap)$", message = "달력 유형은 solar, lunarGeneral, lunarLeap 중 하나여야 합니다.")
    private String calendarType;
}

