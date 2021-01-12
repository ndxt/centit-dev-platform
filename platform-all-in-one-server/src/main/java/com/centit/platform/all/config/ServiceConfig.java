package com.centit.platform.all.config;

import com.centit.fileserver.common.FileStore;
import com.centit.fileserver.utils.OsFileStore;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.core.service.DataScopePowerManager;
import com.centit.framework.core.service.impl.DataScopePowerManagerImpl;
import com.centit.framework.ip.app.config.IPAppSystemBeanConfig;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.product.message.EmailMessageSenderImpl;
import com.centit.workflow.service.impl.SystemUserUnitCalcContextFactoryImpl;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
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
        excludeFilters = @ComponentScan.Filter(type= FilterType.ANNOTATION,
                value = org.springframework.stereotype.Controller.class))
@Import({IPAppSystemBeanConfig.class,
        JdbcConfig.class,
        SpringSecurityDaoConfig.class,
        SpringSecurityCasConfig.class,})
@EnableSpringHttpSession
public class ServiceConfig {

    @Autowired
    private Environment env;

    @Value("${app.home:./}")
    private String appHome;

    @Bean(name = "passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder(){
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    public DataScopePowerManager queryDataScopeFilter(){
        return new DataScopePowerManagerImpl();
    }

    @Bean
    public FileStore fileStore(){
        String baseHome = env.getProperty("os.file.base.dir");
        if(StringUtils.isBlank(baseHome)) {
            baseHome = appHome + "/upload";
        }
        return new OsFileStore(baseHome);
    }

//    @Bean
//    @Lazy(value = false)
//    public IntegrationEnvironment integrationEnvironment() {
//        OsInfo thisOsInfo = new OsInfo();
//        DatabaseInfo thisDatabaseInfo = new DatabaseInfo();
//        thisOsInfo.setOsId("case-tracking");
//        thisOsInfo.setOsName("案件跟踪");
//
//        thisDatabaseInfo.setOsId("case-tracking");
//        thisDatabaseInfo.setDatabaseCode(env.getProperty("ip.jdbc.databasename"));
//        thisDatabaseInfo.setDatabaseName(env.getProperty("ip.jdbc.name"));
//        thisDatabaseInfo.setDatabaseUrl(env.getProperty("ip.jdbc.url"));
//        thisDatabaseInfo.setUsername(env.getProperty("ip.jdbc.user"));
//        thisDatabaseInfo.setPassword(env.getProperty("ip.jdbc.password"));
//        thisDatabaseInfo.setDatabaseDesc("当前系统数据库，不能修改系统相关的表");
//        return new DummyIntegrationEnvironment(thisOsInfo, thisDatabaseInfo);
//    }

    @Bean
    public SchedulerFactory schedulerFactory()  {
        return new StdSchedulerFactory();
    }

    @Bean
    public NotificationCenter notificationCenter(@Autowired PlatformEnvironment platformEnvironment) {
        EmailMessageSenderImpl messageManager = new EmailMessageSenderImpl();
        messageManager.setHostName("mail.centit.com");
        messageManager.setSmtpPort(25);
        messageManager.setUserName("accounts");
        messageManager.setUserPassword("yhs@yhs1");
        messageManager.setServerEmail("noreplay@centit.com");

        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        //notificationCenter.initDummyMsgSenders();
        notificationCenter.setPlatformEnvironment(platformEnvironment);
        notificationCenter.registerMessageSender("email",messageManager);
        notificationCenter.appointDefaultSendType("email");
        return notificationCenter;
    }

    @Bean
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl operationLog = new TextOperationLogWriterImpl();
        operationLog.setOptLogHomePath(appHome+"/logs");
        operationLog.init();
        return operationLog;
    }

    @Bean
    public UserUnitFilterCalcContextFactory userUnitFilterFactory() {
        return new SystemUserUnitCalcContextFactoryImpl();
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

    /*@Bean
    public FindByIndexNameSessionRepository sessionRepository() {
        return new SimpleMapSessionRepository();
    }

    @Bean
    public SessionRegistry sessionRegistry(
        @Autowired FindByIndexNameSessionRepository sessionRepository){
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }*/
}

