package com.bodydb.profile.controller;

import com.bodydb.profile.dto.UserProfileRequest;
import com.bodydb.profile.dto.UserProfileResponse;
import com.bodydb.profile.service.UserProfileService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

@Controller("/config/profile")
public class ProfileController {

    private final UserProfileService service;

    public ProfileController(UserProfileService service) { this.service = service; }

    @Get
    public HttpResponse<UserProfileResponse> get() {
        return service.getProfile()
            .map(p -> HttpResponse.ok(UserProfileResponse.from(p)))
            .orElse(HttpResponse.notFound());
    }

    @Put
    public UserProfileResponse upsert(@Body UserProfileRequest req) {
        return UserProfileResponse.from(service.upsert(req));
    }
}
