package com.musicq.musicqservice.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpDto {
    @NotNull
    private String id;

    @Setter
    private String email;

    @NotNull
    @Setter
    private String nickname;

    @NotNull
    @Setter
    private String password;

    @Builder.Default
    private final MemberImageDto memberImageDto = MemberImageDto.builder().build();

}
