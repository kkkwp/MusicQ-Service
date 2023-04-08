package com.musicq.musicqservice.member.util;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.musicq.musicqservice.member.dto.MemberImageDto;
import com.musicq.musicqservice.member.dto.ResultResDto;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UploaderLocal {

	// 로컬에 디렉토리 생성하는 함수
	public String createLocalUploadDir(String uploadPath) {
		String dirName = "uploadImg";

		File uploadPathDir = new File(uploadPath, dirName);
		// 디렉토리가 없는 경우 생성
		if (!uploadPathDir.exists()) {
			uploadPathDir.mkdirs();
		}
		return dirName;
	}

	// 파일 업로드
	public ResponseEntity<Object> uploadToLocal(MultipartFile[] uploadFiles) {
		MemberImageDto result = new MemberImageDto();
		String uploadPath = result.getPath();
		try {
			for (MultipartFile uploadFile : uploadFiles) {
				// 이미지 파일이 아니라면 업로드 수행 X
				if (!Objects.requireNonNull(uploadFile.getContentType()).startsWith("image")) {
					log.warn("이미지 파일이 아닌 업로드.");
					ResultResDto failResponse = new ResultResDto("File is not Image");
					return ResponseEntity.badRequest().body(failResponse);
				}

				// 업로드 된 파일의 실제 이름
				String originalName = uploadFile.getOriginalFilename();

				// originalName에는 로컬 경로에 저장된 파일의 경로를 포함해서 가져오기 때문에 파일이름.확장자 형식을 추출하기위해
				String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

				log.warn(originalName, fileName);

				// 이미지 업로드 할 디렉토리 생성
				String realUploadDir = createLocalUploadDir(uploadPath);

				// UUID 생성
				String uuid = UUID.randomUUID().toString();

				//저장할 파일 이름 중간에 _를 이용해서 구분
				String saveName = uploadPath + realUploadDir + uuid + fileName;

				File saveFile = new File(saveName);

				try {
					uploadFile.transferTo(saveFile);

					result.setUuid(uuid);
					result.setPath(uploadPath + File.separator + realUploadDir);
					result.setProfile_img(fileName);

				} catch (IOException e) {
					log.warn(e.getMessage());
					log.warn(e.getStackTrace());
					log.warn("Upload Failed");
					ResultResDto failResponse = new ResultResDto("Upload Failed");
					return ResponseEntity.badRequest().body(failResponse);
				}
			}
		} catch (NullPointerException e) {
			log.warn(e.getStackTrace());
			log.warn(e.getMessage());
			ResultResDto failResponse = new ResultResDto("Upload Failed");
			return ResponseEntity.badRequest().body(failResponse);
		}
		log.info("Upload Success");
		return ResponseEntity.ok(result);
	}
}