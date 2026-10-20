package com.gtnix.inovagab;

import com.gtnix.inovagab.config.EnvFileLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class InovagabApplication {

    public static void main(String[] args) {
        EnvFileLoader.loadIfPresent();
        SpringApplication.run(InovagabApplication.class, args);
    }
}
