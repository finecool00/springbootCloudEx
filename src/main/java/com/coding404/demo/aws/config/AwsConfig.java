package com.coding404.demo.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

	//어세스키
	@Value("${aws_access_key_id}")
	private String aws_access_key_id;
	//시크릿키
	@Value("${aws_secret_access_key}")
	private String aws_secret_access_key;
	
	//자격증명객체
	@Bean
	public StaticCredentialsProvider staticCredentialsProvider() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);
		StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
		return credentialsProvider;
	}
	
	//s3클라이언트
	@Bean
	public S3Client s3Client() {
		
		Region region = Region.AP_NORTHEAST_2;
		
		S3Client s3 = S3Client.builder()
				.region(region)
				.credentialsProvider(staticCredentialsProvider())
				.build();
		
		return s3;
	}
	
	//lambda 클라이언트
	@Bean
	public LambdaClient lambdaClient() {
		Region region = Region.AP_NORTHEAST_2;
		
		LambdaClient awsLambda = LambdaClient.builder()
	            .region(region)
	            .credentialsProvider(staticCredentialsProvider())
	            .build();
		
		return awsLambda;
	}
}
