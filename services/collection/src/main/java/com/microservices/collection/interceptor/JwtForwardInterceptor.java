package com.microservices.collection.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component          // tự động áp dụng cho mọi Feign client
public class JwtForwardInterceptor implements RequestInterceptor {

    private static final String AUTH_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (ra instanceof ServletRequestAttributes sra) {
            String auth = sra.getRequest().getHeader(AUTH_HEADER);
            if (auth != null && !auth.isBlank()) {
                template.header(AUTH_HEADER, auth);   // relay nguyên giá trị “Bearer …”
            }
        }
    }
}
