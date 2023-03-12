package com.musicq.musicqservice.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberImageDto {

    private String uuid;
    @Builder.Default
    private String path = "default";
    @Builder.Default
    private String profile_img = "default.img";
}

