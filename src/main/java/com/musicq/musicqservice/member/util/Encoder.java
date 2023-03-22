package com.musicq.musicqservice.member.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encoder {
	// 암호화
	public static String encodeStr(String str){
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(str);
	}

	public static boolean isMatch(String str, String encodedStr){
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.matches(str, encodedStr);
	}
}
