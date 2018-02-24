package org.mw.mongodb.data;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {

    @Override
    public String getDatabaseName() {
        return "test";
    }

    @Bean
    @Override
    public MongoClient mongoClient() {
        return new MongoClient("127.0.0.1");
    }
}