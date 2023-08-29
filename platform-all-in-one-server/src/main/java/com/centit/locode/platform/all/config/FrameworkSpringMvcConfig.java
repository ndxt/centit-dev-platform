package com.centit.locode.platform.all.config;

import com.centit.framework.config.BaseSpringMvcConfig;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.centit.framework.system.controller",
                        "com.centit.framework.tenant.controller",
                        "com.centit.framework.users.controller"},
    includeFilters = {@ComponentScan.Filter(value= org.springframework.stereotype.Controller.class)},
    useDefaultFilters = false)
public class FrameworkSpringMvcConfig extends BaseSpringMvcConfig {

}
