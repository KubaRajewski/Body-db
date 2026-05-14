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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Filter("/**")
public class ApiKeyFilter implements HttpServerFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);

    private static final String HEALTH_STATUS_PATH = "/api/health/status";
    private static final String HEADER = "X-API-Key";

    private final String apiKey;

    public ApiKeyFilter(@Value("${bodydb.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String path = request.getPath();

        if (HEALTH_STATUS_PATH.equals(path)) {
            return chain.proceed(request);
        }

        String provided = request.getHeaders().get(HEADER);

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("BODYDB_API_KEY not configured — rejecting request to {}", path);
            return Publishers.just(HttpResponse.unauthorized().body(Map.of("error", "invalid_api_key")));
        }

        if (provided == null || provided.isBlank()) {
            log.warn("Request to {} rejected — missing X-API-Key header (from {})",
                    path, request.getRemoteAddress());
            return Publishers.just(HttpResponse.unauthorized().body(Map.of("error", "invalid_api_key")));
        }

        if (!provided.equals(apiKey)) {
            log.warn("Request to {} rejected — invalid X-API-Key (from {})",
                    path, request.getRemoteAddress());
            return Publishers.just(HttpResponse.unauthorized().body(Map.of("error", "invalid_api_key")));
        }

        log.debug("{} {} — auth OK", request.getMethod(), path);
        return chain.proceed(request);
    }
}
