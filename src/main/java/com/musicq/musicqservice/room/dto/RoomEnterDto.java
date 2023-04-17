package com.musicq.musicqservice.room.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomEnterDto {
	@NotNull
	private String roomId;

	@NotNull
	private String roomTitle;

	@NotNull
	private String gameName;
}
