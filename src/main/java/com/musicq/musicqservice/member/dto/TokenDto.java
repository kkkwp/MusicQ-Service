package com.musicq.musicqservice.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
	private String accessToken;
	private String refreshToken;
	private String grantType;
}
