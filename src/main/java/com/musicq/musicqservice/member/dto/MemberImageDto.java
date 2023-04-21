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
	private String uuid = "f4588bd1-0973-4779-950f-a5f6548c89b5";

	/*@Builder.Default
	private String path = "C:\\Users\\user\\Desktop\\UserImg";*/

	@Builder.Default
	private String path = "https://musicq-test-bucket.s3.ap-northeast-2.amazonaws.com/UserImg/";

	@Builder.Default
	private String profile_img = "default_profile.png";
}