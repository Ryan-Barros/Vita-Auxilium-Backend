package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.mapper.ActivityMapper;
import com.vitaauxilium.vitaauxilium.services.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityMapper activityMapper;
}
