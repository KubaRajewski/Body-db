package com.bodydb.summary.controller;

import com.bodydb.summary.dto.WeekSummaryDto;
import com.bodydb.summary.service.SummaryService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

import java.time.LocalDate;

@Controller("/summary")
public class SummaryController {

    private final SummaryService service;

    public SummaryController(SummaryService service) { this.service = service; }

    @Get("/week")
    public WeekSummaryDto week(@QueryValue(defaultValue = "") String from) {
        LocalDate start = from.isBlank() ? LocalDate.now().minusDays(6) : LocalDate.parse(from);
        return service.week(start);
    }
}
