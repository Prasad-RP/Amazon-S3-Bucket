package com.s3.api;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.s3.service.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aws")
public class AwsApi {

	private final S3Service s3Service;

	@PostMapping("/upload")
	public ResponseEntity<Object> uploadFile(@RequestParam(value = "file") MultipartFile file) {
		return ResponseEntity.ok(s3Service.uploadFile(file));
	}

	@GetMapping("/download/{fileName}")
	public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) throws IOException {
		byte[] bytes = s3Service.downloadFile(fileName);
		ByteArrayResource resource = new ByteArrayResource(bytes);
		return ResponseEntity.ok()
				.contentLength(bytes.length)
				.header("Content-type", "application/octet-stream")
				.header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}

	@DeleteMapping("/delete/{fileName}")
	public ResponseEntity<Object> deleteFile(@PathVariable String fileName) {
		return ResponseEntity.ok(s3Service.deleteFile(fileName));
	}
}
