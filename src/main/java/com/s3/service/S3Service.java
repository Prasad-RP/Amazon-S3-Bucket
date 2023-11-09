package com.s3.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

	public String uploadFile(MultipartFile file);

	public byte[] downloadFile(String fileName) throws Exception;

	public String deleteFile(String fileName);

	public Boolean isFileExistS3(String fileName);
	
	public List<String> getAllFilesFromBucket();

}
