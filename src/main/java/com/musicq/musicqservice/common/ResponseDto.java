package com.musicq.musicqservice.common;

import com.musicq.musicqservice.common.Exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResponseDto<T> {
	boolean success;
	T data;
	ErrorCode error;
}
