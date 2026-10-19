package br.com.fiap.clyvovet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ClyvovetApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClyvovetApplication.class, args);
    }
}
