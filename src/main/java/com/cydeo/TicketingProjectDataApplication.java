package com.cydeo;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication  // includes @Configuration
public class TicketingProjectDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketingProjectDataApplication.class, args);
    }

// REVIEW - add Bean in the container through @Bean annotation:
    // create class annotated with @Configuration
    // write a method which returns Object which you are trying to add to container
    // annotate method with @Bean
    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

}
