package com.antra.evaluation.reporting_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.util.StringUtils;
@Configuration
public class DynamoDBConfig {
    @Value("${cloud.aws.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;
    @Value("${cloud.aws.credentials.accessKey}")
    private String amazonAWSAccessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String amazonAWSSecretKey;
    
    @Value("${cloud.aws.region.static}")
    private String amazonAWSRegion;
    
    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(buildAmazonDynamoDB());
    }

    private AmazonDynamoDB buildAmazonDynamoDB() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                        		amazonDynamoDBEndpoint,
                                amazonAWSRegion
                        )
                )
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                		amazonAWSAccessKey,
                                		amazonAWSSecretKey
                                )
                        )
                )
                .build();
    }

}