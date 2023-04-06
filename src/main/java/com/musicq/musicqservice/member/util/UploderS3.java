package com.musicq.musicqservice.member.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.musicq.musicqservice.member.dto.MemberImageDto;
import com.musicq.musicqservice.member.dto.ResultResDto;

import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class UploderS3 {
	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	// 파일 업로드
	public ResponseEntity<?> uploadToS3(MultipartFile uploadFile) {
		MemberImageDto result = new MemberImageDto();

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(uploadFile.getContentType());
		objectMetadata.setContentLength(uploadFile.getSize());

		try {
			// 이미지 파일이 아니라면 업로드 수행 X
			if (!Objects.requireNonNull(uploadFile.getContentType()).startsWith("image")) {
				log.warn("이미지 파일이 아닌 업로드.");
				ResultResDto failedResult = new ResultResDto("Wrong Extension");
				return ResponseEntity.badRequest().body(failedResult);
			}

			// 업로드 된 파일의 실제 이름
			String originalName = uploadFile.getOriginalFilename();

			// IE 는 파일이름이 아니고 전체 경로를 전송하기 때문에 마지막 \ 부분만 추출한다.
			String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

			log.warn(originalName, fileName);

			// 이미지 업로드 S3 내 카테고리 생성
			String S3UploadCategory = "UserImg/";

			// UUID 생성
			String uuid = UUID.randomUUID().toString();

			String saveName = S3UploadCategory + uuid + fileName;

			try (InputStream inputStream = uploadFile.getInputStream()){
				amazonS3Client.putObject(new PutObjectRequest(bucket, saveName, inputStream, objectMetadata).withCannedAcl(
					CannedAccessControlList.PublicRead));
				result.setUuid(uuid);
				result.setPath(amazonS3Client.getUrl(bucket, S3UploadCategory).toString());
				result.setProfile_img(fileName);

			} catch (NullPointerException | IOException | AmazonS3Exception | IllegalArgumentException | SecurityException e){
				log.warn(e.getStackTrace());
				log.warn(e.getMessage());
				ResultResDto failResponse = new ResultResDto("Upload Failed");
				return ResponseEntity.badRequest().body(failResponse);
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
