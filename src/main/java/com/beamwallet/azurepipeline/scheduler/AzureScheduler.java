package com.beamwallet.azurepipeline.scheduler;

import com.beamwallet.azurepipeline.service.AzureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AzureScheduler {

    @Autowired
    private AzureService azureService;

    Logger logger = LoggerFactory.getLogger(AzureScheduler.class);

    // check every 5 seconds if listener is running, if not try to close and run again
    @Scheduled(fixedDelay = 5000)
    public void ensureAzureIsRunning() {
        try {
            azureService.reenergiseListener();
        } catch (InterruptedException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }

    }
}
