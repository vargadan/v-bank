package com.dani.vbank;

import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.function.Consumer;

@SpringBootApplication
@Log
public class VBankApplication {

    Consumer<String> logConsumer = log::fine;

    public static void main(String[] args) {
        SpringApplication.run(VBankApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            logConsumer.accept("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                logConsumer.accept(beanName + " : " + ctx.getBean(beanName).getClass().getName());
            }

        };
    }

}