package com.s3.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

	@Value("${application.bucket.name}")
	private String bucketName;

	private final AmazonS3 s3Client;

	public String uploadFile(MultipartFile file) {
		log.info("uploading file");
		String originalFilename = generateFileName(file);
		File fileObject = fileConverter(file);
		s3Client.putObject(bucketName, originalFilename, fileObject);
		fileObject.delete();
		return originalFilename + " File uploaded Successfully.";
	}

	public byte[] downloadFile(String fileName) throws IOException {
		log.info("downloading file");
		S3Object s3Object = s3Client.getObject(bucketName, fileName);
		return IOUtils.toByteArray(s3Object.getObjectContent());
	}

	public String deleteFile(String fileName) {
		log.info("deleting file");
		s3Client.deleteObject(bucketName, fileName);
		return fileName + " File deleted Successfully.";
	}

	/**
	 * To convert MultipartFile into File.
	 * 
	 * @param MultipartFile
	 * @return File
	 */
	private File fileConverter(MultipartFile file) {
		File f = new File(file.getOriginalFilename());
		try (FileOutputStream os = new FileOutputStream(f)) {
			os.write(file.getBytes());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return f;
	}

	/**
	 * 
	 * @param MultipartFile
	 * @return uniqueFile Name
	 */
	private String generateFileName(MultipartFile file) {
		String st = file.getOriginalFilename() + System.currentTimeMillis();
		try {
			st = st.replace(" ", "_");
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return st;
	}
}
