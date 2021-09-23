package com.beamwallet.azurepipeline.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SqsService {

    @Value("${sqs.url}")
    private String queueUrl;

    Logger logger = LoggerFactory.getLogger(SqsService.class);

    final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    public void sendMessage(String body) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(body);
        logger.info("Before sending message to sqs");
        SendMessageResult response = sqs.sendMessage(send_msg_request);
        logger.info("Sent message to sqs messageId: " + response.getMessageId() + ", sequenceNumber: " + response.getSequenceNumber());



    }
}
