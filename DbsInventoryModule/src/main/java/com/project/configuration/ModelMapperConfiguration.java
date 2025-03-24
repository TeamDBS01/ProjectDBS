package com.project.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Configuration class for setting up ModelMapper bean.
 */
@Configuration
public class ModelMapperConfiguration {

    /**
     * Creates and returns a ModelMapper bean.
     * @return a new instance of ModelMapper.
     */
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}