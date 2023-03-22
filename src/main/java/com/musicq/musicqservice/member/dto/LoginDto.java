package com.musicq.musicqservice.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LoginDto {
	@NotNull
	private String id;

	@NotNull
	private String password;
}
