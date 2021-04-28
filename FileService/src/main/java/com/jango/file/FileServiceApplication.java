package com.jango.file;

import com.amazonaws.SDKGlobalConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.aws.autoconfigure.cache.ElastiCacheAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {ElastiCacheAutoConfiguration.class, ContextInstanceDataAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients
public class FileServiceApplication {

	public static void main(String[] args) {
		System.setProperty(SDKGlobalConfiguration.AWS_EC2_METADATA_DISABLED_SYSTEM_PROPERTY, "true");
		SpringApplication.run(FileServiceApplication.class, args);
	}

}
