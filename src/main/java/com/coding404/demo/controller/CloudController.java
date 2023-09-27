package com.coding404.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.coding404.demo.aws.service.S3Service;

@Controller
public class CloudController {
	
	
	@Autowired
	private S3Service s3;
	
	////////////////////////////////s3,
	@GetMapping("/main")
	public String main() {
		return "main";
	}

	//버킷목록 확인
	@GetMapping("/S3Request")
	public String S3Request() {
		
		s3.getBucketList();
		
		return "redirect:/main";
	}
	
}
