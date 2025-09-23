package com.bookteria.api_gateway.configuration;

import com.bookteria.api_gateway.repository.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

//cấu hình để biến IdentityClient (interface) thành một Spring Bean, có thể
// @Autowired / @RequiredArgsConstructor nó trong service mà
// không bị lỗi No beans of 'IdentityClient' type found.

//Gọi đến microservice identity-service để xác thực token
@Configuration
public class WebClientConfiguration {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/identity")
                .build();
    }

    @Bean
    IdentityClient identityClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();

        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }
}
