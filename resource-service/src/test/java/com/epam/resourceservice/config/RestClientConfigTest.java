package com.epam.resourceservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class RestClientConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class))
            .withUserConfiguration(RestClientConfig.class);

    @Test
    void exposesSingleRestClientBuilderMarkedAsLoadBalanced() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RestClient.Builder.class);
            assertThat(context.getBeanFactory()
                    .findAnnotationOnBean("restClientBuilder", LoadBalanced.class)).isNotNull();
        });
    }
}
