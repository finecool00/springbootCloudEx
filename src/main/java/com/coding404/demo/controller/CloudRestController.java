package com.coding404.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coding404.demo.aws.service.S3Service;
import com.coding404.demo.aws.service.SesService;

import software.amazon.awssdk.services.sqs.model.Message;

@RestController
public class CloudRestController {
	
	//s3, 람다
	@Autowired
	private S3Service s3;
	
	//ses, sns, sqs
	@Autowired
	private SesService ses;
	
	
	@PostMapping("/cloudUpload")
	public ResponseEntity<String> cloudUpload(@RequestParam("file_data") MultipartFile file) {
		
		//System.out.println(file);
		
		try {

			//파일명
			String originName = file.getOriginalFilename();
			//파일데이터
			byte[] originData = file.getBytes();
			
			s3.putS3Object(originName, originData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return new ResponseEntity<>("성공적으로 업로드 되었습니다", HttpStatus.OK);
	}
	
	
	@GetMapping("/list_bucket_objects")
	public ResponseEntity<String> list_bucket_objects() {
		
		s3.listBucketObjects();
		
		return new ResponseEntity<>("총 ${", HttpStatus.OK);
	}
	
	
	@DeleteMapping("/delete_bucket_objects")
	public ResponseEntity<String> delete_bucket_objects(@RequestParam("bucket_obj_name") String bucket_obj_name) {
		
		//System.out.println(bucket_obj_name);
		s3.deleteBucketObjects(bucket_obj_name);
		
		return new ResponseEntity<>("안전하게 삭제되었습니다", HttpStatus.OK);
	}
	
	
	@GetMapping("/lambda_call")
	public ResponseEntity<String> lambda_call() {
		
		s3.invokeFunction();
		
		return new ResponseEntity<>("호출 성공하였습니다", HttpStatus.OK);
	}
	
	
	@GetMapping("/send_email")
	public ResponseEntity<String> send_email() {
		
		String sender = "baed9035@gmail.com"; //발신자 주소
		String recipient = "cjstkdeodyd@naver.com"; //수신자 주소
		String subject = "Amazon SES test (AWS SDK for Java)"; //제목
		String HTMLBODY = "<h1>Amazon SES test (AWS SDK for Java)</h1>"
			      + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
			      + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>" 
			      + "AWS SDK for Java</a>";
		
		ses.sendEmail(sender, recipient, subject, HTMLBODY);
		
		return new ResponseEntity<>("메일이 성공적으로 전송되었습니다", HttpStatus.OK);
	}
	
	//메시지 게시
	@GetMapping("/send_sns")
	public ResponseEntity<String> send_sns() {
		
		ses.sendSns();
		
		return new ResponseEntity<>("메시지가 성공적으로 게시되었습니다", HttpStatus.OK);
	}
	
	//메시지 게시 당기기
	@GetMapping("/poll_sqs")
	public ResponseEntity<String> poll_sqs() {
		
		List<Message> list = ses.pollSqs();
		
		System.out.println("======================================");
		for(Message m : list) {
			System.out.println(m.body());
		}
		
		//메시지 소비후에는 삭제 처리 작업(중복메시지 수신 방지)....
		
		return new ResponseEntity<>("메시지가 성공적으로 poll되었습니다", HttpStatus.OK);
	}
	
	
}
