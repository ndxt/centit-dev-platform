package com.centit.platform.all.config;

import com.centit.framework.config.BaseSpringMvcConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = {"com.centit.task.controller"},
    includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,
        value = org.springframework.stereotype.Controller.class)},
    useDefaultFilters = false)
public class TaskSpringMvcConfig extends BaseSpringMvcConfig {

}
