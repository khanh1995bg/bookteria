package com.bookteria.api_gateway.configuration;

import com.bookteria.api_gateway.dto.ApiResponse;
import com.bookteria.api_gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


//TẠO BEAN NÀY DÙNG ĐỂ XÁC THỰC TẤT CẢ API ĐI QUA SERVICE "API-GATEWAY"
// GlobalFilter trong Spring Cloud Gateway, dùng để xử lý
// xác thực (authentication) cho tất cả request đi qua API Gateway
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService identityService;
    ObjectMapper objectMapper;

    @NonFinal
    private String[] publicApiEndPoints = {"/identity/auth/.*", "/identity/users/registration"};

    @Value("${app.api-prefix}")
    @NonFinal
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("=======> AuthenticationFilter started");

//      If này dùng để check những endpoint không cần xác thực
        if (isPrivateEndpoint(exchange.getRequest()))
            return chain.filter(exchange);

//        Get token from authorization header
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
//       Nếu token rỗng thì trả mã lỗi 401
        if (CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse());

        String token = authHeader.getFirst().replace("Bearer ", "");

        log.info("=======> AuthenticationFilter finished: {}", token);

//        Verify token
        return identityService.introspect(token).flatMap(introspectResponse -> {
            if (introspectResponse.getResult().isValid())
                return chain.filter(exchange);
            else
                return unauthenticated(exchange.getResponse());
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    //    Hàm check endpoint public không cần xác thực token
    private boolean isPrivateEndpoint(ServerHttpRequest request) {
        log.info("=====> Check Path Endpoint: {}", request.getURI().getPath().matches(apiPrefix + "/identity/auth/token"));
        return Arrays.stream(publicApiEndPoints)
                .anyMatch(s -> request.getURI().getPath().matches(apiPrefix + s));
    }

//    Trả về mã lỗi 401 khi chưa xác thực
    Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message("Unauthorized")
                .build();
        String body = null;

        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
