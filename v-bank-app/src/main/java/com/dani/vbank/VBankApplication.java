package com.dani.vbank;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Properties;
import java.util.function.Consumer;

@SpringBootApplication
@Log
public class VBankApplication {

    Consumer<String> logConsumer = log::info;

    public static void main(String[] args) {
        loadGitProps();
        SpringApplication.run(VBankApplication.class, args);
    }

    @SneakyThrows
    private static void loadGitProps() {
        Properties gitProps = new Properties();
        gitProps.load(VBankApplication.class.getResourceAsStream("/git.properties"));
        System.getProperties().putAll(gitProps);
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