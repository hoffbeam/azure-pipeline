package com.beamwallet.azurepipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AzurePipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzurePipelineApplication.class, args);
    }
}
