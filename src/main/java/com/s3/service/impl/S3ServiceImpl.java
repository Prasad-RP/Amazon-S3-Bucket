package com.s3.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.s3.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

	@Value("${application.bucket.name}")
	private String bucketName;

	private final AmazonS3 s3Client;

	@Override
	public String uploadFile(MultipartFile file) {
		log.info("uploading file");
		String originalFilename = generateFileName(file);
		File fileObject = fileConverter(file);
		s3Client.putObject(bucketName, originalFilename, fileObject);
		Boolean isDeleted = fileObject.delete();
		return originalFilename + " File uploaded Successfully." + "\n" + "File Deletion Status from LocalEnv:"
				+ isDeleted;
	}

	@Override
	public byte[] downloadFile(String fileName) throws Exception {
		log.info("downloading file");
		if (!isFileExistS3(fileName))
			throw new Exception(fileName + " File not found on server.");
		S3Object s3Object = s3Client.getObject(bucketName, fileName);
		return IOUtils.toByteArray(s3Object.getObjectContent());
	}

	@Override
	public String deleteFile(String fileName) {
		log.info("deleting file");
		if (!isFileExistS3(fileName))
			return fileName + " File not found on server.";
		s3Client.deleteObject(bucketName, fileName);
		return fileName + " File deleted Successfully.";
	}

	@Override
	public Boolean isFileExistS3(String fileName) {
		ObjectListing objects = s3Client
				.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(fileName));

		for (S3ObjectSummary s3Object : objects.getObjectSummaries()) {

			if (s3Object.getKey().equals(fileName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> getAllFilesFromBucket() {
		List<String> filesList = new ArrayList<>();
		ObjectListing objects = s3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName));

		for (S3ObjectSummary s3Object : objects.getObjectSummaries()) {
			filesList.add(s3Object.getKey());
		}
		return filesList;
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