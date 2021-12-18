package com.centit.platform.config;


import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.core.service.DataScopePowerManager;
import com.centit.framework.core.service.impl.DataScopePowerManagerImpl;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.product.oa.EmailMessageSenderImpl;
import com.centit.search.document.ObjectDocument;
import com.centit.search.service.ESServerConfig;
import com.centit.search.service.Impl.ESIndexer;
import com.centit.search.service.Impl.ESSearcher;
import com.centit.search.service.IndexerSearcherFactory;
import com.centit.support.security.AESSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author zhf
 */
@EnableAsync
@EnableScheduling
@Import({
    SpringSecurityDaoConfig.class,
    JdbcConfig.class})
@ComponentScan(basePackages = "com.centit",
    excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
        value = org.springframework.stereotype.Controller.class))
@Configuration
public class ServiceConfig {

    @Value("${app.home:./}")
    private String appHome;

    /**
     * 这个bean必须要有
     *
     * @return CentitPasswordEncoder 密码加密算法
     */
    @Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    public NotificationCenter notificationCenter(@Autowired PlatformEnvironment platformEnvironment) {
        EmailMessageSenderImpl messageManager = new EmailMessageSenderImpl();
        messageManager.setHostName("mail.centit.com");
        messageManager.setSmtpPort(25);
        messageManager.setUserName("alertmail2@centit.com");
        messageManager.setUserPassword(AESSecurityUtils.decryptBase64String("LZhLhIlJ6gtIlUZ6/NassA==", ""));
        messageManager.setServerEmail("alertmail2@centit.com");

        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        notificationCenter.setPlatformEnvironment(platformEnvironment);
        notificationCenter.registerMessageSender("email", messageManager);
        notificationCenter.appointDefaultSendType("email");
        return notificationCenter;
    }

    @Bean
    public DataScopePowerManager queryDataScopeFilter(){
        return new DataScopePowerManagerImpl();
    }

    @Bean
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl operationLog = new TextOperationLogWriterImpl();
        operationLog.setOptLogHomePath(appHome + "/logs");
        operationLog.init();
        return operationLog;
    }

    @Bean
    public ESServerConfig esServerConfig() {
        return IndexerSearcherFactory.loadESServerConfigFormProperties(
            SysParametersUtils.loadProperties()
        );
    }

    @Bean(name = "esIndexer")
    public ESIndexer esIndexer(@Autowired ESServerConfig esServerConfig) {
        return IndexerSearcherFactory.obtainIndexer(
            esServerConfig, ObjectDocument.class);
    }

    @Bean(name = "esSearcher")
    public ESSearcher esSearcher(@Autowired ESServerConfig esServerConfig) {
        return IndexerSearcherFactory.obtainSearcher(
            esServerConfig, ObjectDocument.class);
    }
}
