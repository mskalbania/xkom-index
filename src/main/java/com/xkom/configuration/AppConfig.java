package com.xkom.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.jackson.datatype.VavrModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class AppConfig {

    @Bean
    @Scope("prototype")
    Logger logger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                                          .modules(new VavrModule())
                                          .build();
    }
}
