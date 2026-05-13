//package com.bodydb.health.controller;
//
//import com.bodydb.health.dto.DailyHealthResponse;
//import com.bodydb.health.repository.DailyHealthRepository;
//import com.bodydb.support.PostgresIntegrationTestSupport;
//import io.micronaut.http.HttpRequest;
//import io.micronaut.http.client.HttpClient;
//import io.micronaut.http.client.annotation.Client;
//import io.micronaut.http.client.exceptions.HttpClientResponseException;
//import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
//import jakarta.inject.Inject;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@MicronautTest(transactional = false)
//class DailyHealthControllerTest extends PostgresIntegrationTestSupport {
//
//    @Inject
//    @Client("/")
//    HttpClient client;
//
//    @Inject
//    DailyHealthRepository dailyHealthRepository;
//
//    @BeforeEach
//    void cleanDatabase() {
//        dailyHealthRepository.deleteAll();
//    }
//
//    @Test
//    void postDailyHealthPersistsData() {
//        var request = HttpRequest.POST(
//            "/api/health/daily",
//            Map.of(
//                "date", "2026-04-28",
//                "steps", 12345,
//                "activeCalories", 650,
//                "hrvMs", 47.5
//            )
//        ).header("X-API-Key", API_KEY);
//
//        var response = client.toBlocking().exchange(request, DailyHealthResponse.class);
//
//        assertThat(response.getStatus().getCode()).isEqualTo(200);
//        assertThat(response.body()).isNotNull();
//        assertThat(response.body().date()).isEqualTo(LocalDate.of(2026, 4, 28));
//        assertThat(response.body().steps()).isEqualTo(12_345);
//        assertThat(response.body().activeCalories()).isEqualTo(650);
//
//        var fromDb = dailyHealthRepository.findByDate(LocalDate.of(2026, 4, 28)).orElseThrow();
//        assertThat(fromDb.getId()).isEqualTo(response.body().id());
//        assertThat(fromDb.getSteps()).isEqualTo(12_345);
//        assertThat(fromDb.getActiveCalories()).isEqualTo(650);
//        assertThat(fromDb.getHrvMs()).isEqualByComparingTo("47.5");
//    }
//
//    @Test
//    void postingSameDateTwiceUpdatesExistingRow() {
//        var firstRequest = HttpRequest.POST(
//            "/api/health/daily",
//            Map.of(
//                "date", "2026-04-28",
//                "steps", 1000
//            )
//        ).header("X-API-Key", API_KEY);
//
//        var firstResponse = client.toBlocking().exchange(firstRequest, DailyHealthResponse.class);
//
//        var secondRequest = HttpRequest.POST(
//            "/api/health/daily",
//            Map.of(
//                "date", "2026-04-28",
//                "steps", 2000,
//                "sleepTotalMin", 480
//            )
//        ).header("X-API-Key", API_KEY);
//
//        var secondResponse = client.toBlocking().exchange(secondRequest, DailyHealthResponse.class);
//
//        assertThat(secondResponse.body()).isNotNull();
//        assertThat(secondResponse.body().id()).isEqualTo(firstResponse.body().id());
//        assertThat(secondResponse.body().steps()).isEqualTo(2000);
//        assertThat(secondResponse.body().sleepTotalMin()).isEqualTo(480);
//        assertThat(dailyHealthRepository.count()).isEqualTo(1);
//
//        var fromDb = dailyHealthRepository.findByDate(LocalDate.of(2026, 4, 28)).orElseThrow();
//        assertThat(fromDb.getId()).isEqualTo(firstResponse.body().id());
//        assertThat(fromDb.getSteps()).isEqualTo(2000);
//        assertThat(fromDb.getSleepTotalMin()).isEqualTo(480);
//    }
//
//    @Test
//    void missingDateReturns400() {
//        var request = HttpRequest.POST(
//            "/api/health/daily",
//            Map.of("steps", 12345)
//        ).header("X-API-Key", API_KEY);
//
//        assertThatThrownBy(() -> client.toBlocking().exchange(request, Map.class))
//            .isInstanceOf(HttpClientResponseException.class)
//            .satisfies(e -> assertThat(((HttpClientResponseException) e).getStatus().getCode()).isEqualTo(400));
//    }
//}
