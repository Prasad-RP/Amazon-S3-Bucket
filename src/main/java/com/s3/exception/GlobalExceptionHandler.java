package com.s3.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<Object, Object>> exception(Exception ex) {
		Map<Object, Object> map = new HashMap<>();
		map.put("SUCCESS", false);
		map.put("Message", ex.getMessage());
		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
	}
}
