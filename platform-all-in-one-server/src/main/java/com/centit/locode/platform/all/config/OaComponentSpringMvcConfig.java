package com.centit.locode.platform.all.config;

import com.centit.framework.config.BaseSpringMvcConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = {"com.centit.product.oa.controller"},
    includeFilters = {@ComponentScan.Filter(type= FilterType.ANNOTATION,
        value= org.springframework.stereotype.Controller.class)},
    useDefaultFilters = false)
public class OaComponentSpringMvcConfig extends BaseSpringMvcConfig {
}
