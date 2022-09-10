package com.siewe.pos;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@Configuration
public class PosApplication {
    @Autowired
    private InitialData initialData;

    public static void main(String[] args) {
        SpringApplication.run(PosApplication.class, args);
    }

    @Bean
    InitializingBean init(){
        return initialData.load();
    }

}
