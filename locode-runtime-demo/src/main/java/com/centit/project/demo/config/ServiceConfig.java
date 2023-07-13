package com.centit.project.demo.config;

import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

/**
 * Created by codefan on 17-7-18.
 */
@Configuration
@PropertySource("classpath:system.properties")
@ComponentScan(basePackages = "com.centit",
    excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
        value = org.springframework.stereotype.Controller.class))
@Import({
//    JdbcConfig.class,
//    SystemBeanConfig.class,
    SpringSecurityDaoConfig.class,
    SpringSecurityCasConfig.class,})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableSpringHttpSession
public class ServiceConfig {

    @Autowired
    private Environment env;
    @Value("${app.home:./}")
    private String appHome;
    @Value("${redis.default.host}")
    private String redisHost;

}

