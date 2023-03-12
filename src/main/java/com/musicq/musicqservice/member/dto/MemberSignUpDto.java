package com.musicq.musicqservice.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpDto {
    @NotNull
    private String id;

    @NotNull
    private String email;

    @NotNull
    private String nickname;

    @NotNull
    private String password;

    @Builder.Default
    private final MemberImageDto memberImageDto = MemberImageDto.builder().build();

}
