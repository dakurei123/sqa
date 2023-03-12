package com.example.sqa.dto;

import com.example.sqa.entity.Account;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private Account account;
}
