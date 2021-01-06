package com.centit.platform.all.config;

import com.centit.framework.config.BaseSpringMvcConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Created by zou_wy on 2017/3/29.
 */
@ComponentScan(basePackages = {"com.centit.casetracking.fileserver"},
        includeFilters =
                {@ComponentScan.Filter(type= FilterType.ANNOTATION,
                        value= org.springframework.stereotype.Controller.class)},
        useDefaultFilters = false)
public class FileServerSpringMvcConfig extends BaseSpringMvcConfig {

}