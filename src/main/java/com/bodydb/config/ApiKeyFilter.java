package com.bodydb.config;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.core.async.publisher.Publishers;
import org.reactivestreams.Publisher;

import java.util.Map;

@Filter("/**")
public class ApiKeyFilter implements HttpServerFilter {

    private static final String HEALTH_STATUS_PATH = "/api/health/status";
    private static final String HEADER = "X-API-Key";

    private final String apiKey;

    public ApiKeyFilter(@Value("${bodydb.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        if (HEALTH_STATUS_PATH.equals(request.getPath())) {
            return chain.proceed(request);
        }

        String provided = request.getHeaders().get(HEADER);
        if (apiKey == null || apiKey.isBlank() || provided == null || !provided.equals(apiKey)) {
            return Publishers.just(HttpResponse.unauthorized().body(Map.of("error", "invalid_api_key")));
        }

        return chain.proceed(request);
    }
}
