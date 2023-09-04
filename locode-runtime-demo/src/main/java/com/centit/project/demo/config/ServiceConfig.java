package com.centit.project.demo.config;

import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.security.StandardPasswordEncoderImpl;
import com.centit.framework.staticsystem.config.StaticSystemBeanConfig;
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
    JdbcConfig.class,
    StaticSystemBeanConfig.class,
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

    /**
     * 这个bean必须要有
     * @return CentitPasswordEncoder 密码加密算法
     */
    @Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return  new StandardPasswordEncoderImpl();
    }

    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        //这个不是必须的,只是为了在没有真正的发送类时不报错
        notificationCenter.initDummyMsgSenders();
        //打开消息推送服务日志
        notificationCenter.setWriteNoticeLog(true);
        return notificationCenter;
    }

    @Bean
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl operationLog =  new TextOperationLogWriterImpl();
        operationLog.setOptLogHomePath(appHome+"/logs");
        operationLog.init();
        return operationLog;
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }
}

