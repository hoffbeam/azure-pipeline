package com.beamwallet.azurepipeline.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqsService {

    Logger logger = LoggerFactory.getLogger(SqsService.class);

    final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    public void sendMessage(String body) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl("https://sqs.eu-central-1.amazonaws.com/148323770033/hoff-test")
                .withMessageBody(body);
        logger.info("Before sending message to sqs");
        SendMessageResult response = sqs.sendMessage(send_msg_request);
        logger.info("Sent message to sqs messageId: " + response.getMessageId() + ", sequenceNumber: " + response.getSequenceNumber());



    }
}
