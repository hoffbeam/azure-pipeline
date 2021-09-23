package com.beamwallet.azurepipeline.service;

import com.azure.messaging.servicebus.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;



@Service
public class AzureService {

    @Autowired
    private SqsService sqsService;

    static Logger logger = LoggerFactory.getLogger(AzureService.class);

    @Value("${azure.url}")
    private String connectionString;
    @Value("${azure.topic}")
    private String queueName;

    private CountDownLatch countdownLatch = new CountDownLatch(1);

    private ServiceBusProcessorClient processorClient = null;

    @PostConstruct
    public void init() {
        // Create an instance of the processor through the ServiceBusClientBuilder
        processorClient = null;new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .processor()
                .queueName(queueName)
                .processMessage(this::processMessage)
                .processError(context -> processError(context, countdownLatch))
                .buildProcessorClient();
    }

    public void runListener() throws InterruptedException {
        logger.info("Starting the processor");
        processorClient.start();
    }

    @PreDestroy
    public void closeListener () throws InterruptedException {
        logger.info("Stopping and closing the processor");
        processorClient.close();
    }

    public void reenergiseListener() throws InterruptedException {
        if (!processorClient.isRunning()) {
            closeListener();
            runListener();
        }
    }

    private void processMessage(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage message = context.getMessage();
        logger.info("Received Message id: " + message.getMessageId() + ", sequenceNumber: " + message.getSequenceNumber());
        sqsService.sendMessage(message.getBody().toString());
    }

    private void processError(ServiceBusErrorContext context, CountDownLatch countdownLatch) {
        logger.error("Error when receiving messages from namespace: " + context.getFullyQualifiedNamespace() + ", Entity: " + context.getEntityPath());


        if (!(context.getException() instanceof ServiceBusException)) {
            logger.error("Non-ServiceBusException occurred: " + context.getException());
            return;
        }

        ServiceBusException exception = (ServiceBusException) context.getException();
        ServiceBusFailureReason reason = exception.getReason();

        if (reason == ServiceBusFailureReason.MESSAGING_ENTITY_DISABLED
                || reason == ServiceBusFailureReason.MESSAGING_ENTITY_NOT_FOUND
                || reason == ServiceBusFailureReason.UNAUTHORIZED) {
            logger.error("An unrecoverable error occurred. Stopping processing with reason " + reason + ", message: " + exception.getMessage());

            countdownLatch.countDown();
        } else if (reason == ServiceBusFailureReason.MESSAGE_LOCK_LOST) {
            logger.error("Message lock lost for message: " + context.getException());
        } else if (reason == ServiceBusFailureReason.SERVICE_BUSY) {
            try {
                // Choosing an arbitrary amount of time to wait until trying again.
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error("Unable to sleep for period of time");
            }
        } else {
            logger.error("Error source %s, reason %s, message: %s%n", context.getErrorSource(),
                    reason, context.getException());
        }
    }

}
