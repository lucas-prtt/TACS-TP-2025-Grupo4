package org.middleware;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.util.List;


@ControllerAdvice
public class PageHeaderHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> paramType = returnType.getParameterType();
        if (ResponseEntity.class.isAssignableFrom(paramType)) {
            return returnType.getGenericParameterType().getTypeName().contains("PagedModel");
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (!(body instanceof PagedModel<?> page)) {
            return body;
        }

        response.getHeaders().add("Total-Pages", String.valueOf(page.getMetadata().totalPages()));
        response.getHeaders().add("Current-Page", String.valueOf(page.getMetadata().number()));
        response.getHeaders().add("Page-Size", String.valueOf(page.getMetadata().size()));
        response.getHeaders().add("Total-Elements", String.valueOf(page.getMetadata().totalElements()));

        return page.getContent();
    }
}
