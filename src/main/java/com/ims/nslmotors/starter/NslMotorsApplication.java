package com.ims.nslmotors.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // Gerekli import

@SpringBootApplication
@EntityScan("com.ims.nslmotors.model") // Not: Ben entity paketini "model" olarak varsaydım
@ComponentScan(basePackages = "com.ims.nslmotors")
@EnableJpaRepositories("com.ims.nslmotors.repository") // Repository paketini açıkça belirt
public class NslMotorsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NslMotorsApplication.class, args);
    }
}


//# brew services start postgresql@14
//# brew services stop postgresql@14