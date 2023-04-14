package com.musicq.musicqservice.room.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateDto {

	@NotNull
	private String roomTitle;

	@NotNull
	private String gameName;
}
