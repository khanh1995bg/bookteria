package com.bookteria.identity_service.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
/* Auto add bearer token vào trong header của api */
// Chỉ xủ dụng đối với microservice internal feign client, không sử dụng đối với service bên ngoài nên cần config thủ công
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        var authHeader = attributes.getRequest().getHeader("Authorization");

        log.info("====> Authorization header received: {}", authHeader);

        if(StringUtils.hasText(authHeader))
            requestTemplate.header("Authorization", authHeader);

    }
}
