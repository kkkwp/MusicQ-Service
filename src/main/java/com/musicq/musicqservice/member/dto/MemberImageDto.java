package com.musicq.musicqservice.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberImageDto {

	@Builder.Default
	private String uuid = "Default_Img_";

	@Builder.Default
	private String path = "C:\\Users\\user\\Pictures\\Saved Pictures";

	@Builder.Default
	private String profile_img = "MusicQ.png";
}