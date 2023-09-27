package com.coding404.demo.aws.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.transform.PublishRequestMarshaller;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
public class SesService {
	
	//어세스키
	@Value("${aws_access_key_id}")
	private String aws_access_key_id;
	//시크릿키
	@Value("${aws_secret_access_key}")
	private String aws_secret_access_key;

	/////////////////////ses//////////////////////
    public void sendEmail(String sender,
				          String recipient,
				          String subject,
				          String bodyHTML
				    		){

    	//자격증명객체
    	AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);
    	
    	//ses클라이언트
        SesV2Client sesv2Client = SesV2Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    	
    	
    	
		Destination destination = Destination.builder()
		.toAddresses(recipient)
		.build();
		
		Content content = Content.builder()
		.data(bodyHTML)
		.build();
		
		Content sub = Content.builder()
		.data(subject)
		.build();
		
		Body body = Body.builder()
		.html(content)
		.build();
		
		Message msg = Message.builder()
		.subject(sub)
		.body(body)
		.build();
		
		EmailContent emailContent = EmailContent.builder()
		.simple(msg)
		.build();
		
		SendEmailRequest emailRequest = SendEmailRequest.builder()
		.destination(destination)
		.content(emailContent)
		.fromEmailAddress(sender)
		.build();
		
		try {
		System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
		
		//ses호출
		sesv2Client.sendEmail(emailRequest);
		System.out.println("email was sent");
		
		} catch (SesV2Exception e) {
		System.err.println(e.awsErrorDetails().errorMessage());
		System.exit(1);
		}
		}
		// snippet-end:[ses.java2.sendmessage.sesv2.main]

    
    	/////////////////////sns주제게시//////////////////////
	    public void sendSns() {
	    	
	    	//자격증명객체
	    	AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);
	    	
	    	//sns클라이언트
	        SnsClient snsClient = SnsClient.builder()
	            .region(Region.AP_NORTHEAST_2)
	            .credentialsProvider(StaticCredentialsProvider.create(credentials))
	            .build();
	    	
	    	
	    	String topicArn = "arn:aws:sns:ap-northeast-2:295222417736:DemoMyTopic.fifo";
	    	
	    	
	        try {
	            String subject = "Price Update";
	            String dedupId = UUID.randomUUID().toString();
	            String groupId = "PID-200"; //동일한 내용들을 동일한 그룹명으로 묶어준다
	            String payload = "홍길동 님이 주문을 했습니다.";
	            String attributeName = "business"; //정책 키
	            String attributeValue = "wholesale"; //정책 값(해당 정책을 허용한 queue에게만 전달)

	            MessageAttributeValue msgAttValue = MessageAttributeValue.builder()
	                    .dataType("String")
	                    .stringValue(attributeValue)
	                    .build();

	            Map<String, MessageAttributeValue> attributes = new HashMap<>();
	            attributes.put(attributeName, msgAttValue);
	            PublishRequest pubRequest = PublishRequest.builder()
	                    .topicArn(topicArn)
	                    .subject(subject)
	                    .message(payload)
	                    .messageGroupId(groupId)
	                    .messageDeduplicationId(dedupId)
	                    .messageAttributes(attributes)
	                    .build();

	            final PublishResponse response = snsClient.publish(pubRequest);
	            System.out.println(response.messageId());
	            System.out.println(response.sequenceNumber());
	            System.out.println("Message was published to " + topicArn);

	        } catch (SnsException e) {
	            System.err.println(e.awsErrorDetails().errorMessage());
	            System.exit(1);
	        }
	    	
	    }
	    
	    
	    
	    public List<software.amazon.awssdk.services.sqs.model.Message> pollSqs() {
	    	
	    	//자격증명객체
	    	AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);
	    	
	    	//sqs클라이언트
	        SqsClient sqsClient = SqsClient.builder()
	            .region(Region.AP_NORTHEAST_2)
	            .credentialsProvider(StaticCredentialsProvider.create(credentials))
	            .build();
	    	
	        //당길 큐의 url 주소
	    	String queueUrl = "https://sqs.ap-northeast-2.amazonaws.com/295222417736/DemoQueue.fifo";
	    	
	    	try {	    		
	    		ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
	    				.queueUrl(queueUrl)
	    				.maxNumberOfMessages(5) //최대로 가져올 메시지 개수
	    				.build();
	    		
	    		return sqsClient.receiveMessage(receiveMessageRequest).messages();
	    	}
	    	catch (SqsException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                //System.exit(1);
            }
            return null;
	    }
	    
	    @SqsListener("${aws_sqs_url}")
	    public void listen(String message) {
	    	System.out.println("========sqsListener실행됨======");
	    	System.out.println("========메시지 수신후, 중복수신을 방지하기 위해 메시지는 자동 삭제됩니다.======");
	    	System.out.println("========@리스너 어노테이션은 다양한 인수사용이 가능합니다. 공식문서를 확인하세요======");
	      System.out.println(message);
	    }
	    
		}

		
