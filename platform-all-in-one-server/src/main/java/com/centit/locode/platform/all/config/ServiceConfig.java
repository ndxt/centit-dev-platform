package com.centit.locode.platform.all.config;

import com.centit.fileserver.common.FileStore;
import com.centit.fileserver.common.FileTaskQueue;
import com.centit.fileserver.task.*;
import com.centit.fileserver.utils.OsFileStore;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.core.service.DataScopePowerManager;
import com.centit.framework.core.service.impl.DataScopePowerManagerImpl;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.model.security.ThirdPartyCheckUserDetails;
import com.centit.framework.security.StandardPasswordEncoderImpl;
import com.centit.framework.system.config.SystemBeanConfig;
import com.centit.im.service.IntelligentRobotFactory;
import com.centit.im.service.impl.IntelligentRobotFactoryRpcImpl;
import com.centit.locode.platform.plugins.ZjJttCheckUserPlugin;
import com.centit.msgpusher.plugins.EMailMsgPusher;
import com.centit.msgpusher.plugins.SystemUserEmailSupport;
import com.centit.search.service.ESServerConfig;
import com.centit.search.service.IndexerSearcherFactory;
import com.centit.search.utils.ImagePdfTextExtractor;
import com.centit.support.security.AESSecurityUtils;
import com.centit.workflow.service.impl.SystemUserUnitCalcContextFactoryImpl;
import io.lettuce.core.RedisClient;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

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
    SystemBeanConfig.class,
    SpringSecurityDaoConfig.class,
    SpringSecurityCasConfig.class,})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableSpringHttpSession
public class ServiceConfig implements EnvironmentAware {

    @Value("${app.home:./}")
    private String appHome;
    @Value("${redis.default.host}")
    private String redisHost;

    private Environment env;
    @Override
    public void setEnvironment(@Autowired Environment environment) {
        if (environment != null) {
            this.env = environment;
        }
    }

    @Bean
    public RedisClient redisClient() {
        return RedisClient.create(redisHost);
    }

    @Bean
    public FileStore fileStore() {
        String baseHome = env.getProperty("os.file.base.dir");
        if (StringUtils.isBlank(baseHome)) {
            baseHome = env.getProperty("app.home") + "/upload";
        }
        return new OsFileStore(baseHome);
    }

    @Bean
    public FileTaskQueue fileOptTaskQueue() throws Exception {
        return new LinkedBlockingQueueFileOptTaskQueue(appHome + "/task");
    }

    @Bean(name = "passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setUseCodeAsDefaultMessage(true);
        //"classpath:org/springframework/security/messages"
        ms.setBasenames("classpath:i18n/messages", "classpath:org/springframework/security/messages");
        ms.setDefaultEncoding("UTF-8");
        return ms;
    }

    @Bean
    public LocalValidatorFactoryBean validatorFactory() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public DataScopePowerManager queryDataScopeFilter() {
        return new DataScopePowerManagerImpl();
    }

    @Bean
    public IntelligentRobotFactory intelligentRobotFactory() {
        IntelligentRobotFactoryRpcImpl intelligentRobotFactory = new IntelligentRobotFactoryRpcImpl();
        return intelligentRobotFactory;
    }
    @Bean("thirdPartyCheckUserDetails")
    public ThirdPartyCheckUserDetails thirdPartyCheckUserDetails(){
        return new ZjJttCheckUserPlugin();
    }

    /* 这个定时任务 不能用run来做，应该用一个 定时任务容器
     */
    @Bean
    public FileOptTaskExecutor fileOptTaskExecutor(
        @Autowired FileTaskQueue fileOptTaskQueue,
        @Autowired SaveFileOpt saveFileOpt,
        @Autowired CreatePdfOpt createPdfOpt,
        @Autowired PdfWatermarkOpt pdfWatermarkOpt,
        @Autowired AddThumbnailOpt addThumbnailOpt,
        @Autowired ZipFileOpt zipFileOpt,
        @Autowired EncryptFileOpt encryptFileOpt,
        @Autowired DocumentIndexOpt documentIndexOpt) {
        FileOptTaskExecutor fileOptTaskExecutor = new FileOptTaskExecutor(saveFileOpt);
        fileOptTaskExecutor.setFileOptTaskQueue(fileOptTaskQueue);

        fileOptTaskExecutor.addFileOperator(saveFileOpt);
        fileOptTaskExecutor.addFileOperator(createPdfOpt);
        fileOptTaskExecutor.addFileOperator(pdfWatermarkOpt);
        fileOptTaskExecutor.addFileOperator(addThumbnailOpt);
        fileOptTaskExecutor.addFileOperator(zipFileOpt);
        fileOptTaskExecutor.addFileOperator(encryptFileOpt);
        fileOptTaskExecutor.addFileOperator(documentIndexOpt);
        return fileOptTaskExecutor;
    }

    @Bean
    public ImagePdfTextExtractor.OcrServerHost ocrServerHost() {
        ImagePdfTextExtractor.OcrServerHost ocrServer = ImagePdfTextExtractor.fetchDefaultOrrServer();
        String ocrServerHost = env.getProperty("ocr.server.url");
        if(StringUtils.isNotBlank(ocrServerHost)) {
            String stemp = env.getProperty("ocr.server.auth.api");
            if (StringUtils.isNotBlank(stemp)) {
                ocrServer.setAuthorUrl(ocrServerHost + stemp);
            }
            stemp = env.getProperty("ocr.server.ocr.api");
            if (StringUtils.isNotBlank(stemp)) {
                ocrServer.setOrcUrl(ocrServerHost + stemp);
            }
            stemp = env.getProperty("ocr.server.auth.username");
            if (StringUtils.isNotBlank(stemp)) {
                ocrServer.setUserName(stemp);
            }
            stemp = env.getProperty("ocr.server.auth.password");
            if (StringUtils.isNotBlank(stemp)) {
                ocrServer.setPassword(stemp);
            }
        }
        return ocrServer;
    }

    @Bean
    public ESServerConfig esServerConfig() {
        return IndexerSearcherFactory.loadESServerConfigFormProperties(
            SysParametersUtils.loadProperties()
        );
    }

    @Bean
    public SchedulerFactory schedulerFactory() {
        return new StdSchedulerFactory();
    }

    @Bean
    public NotificationCenter notificationCenter(@Autowired PlatformEnvironment platformEnvironment) {
        //messageManager.setServerEmail("alertmail2@centit.com");
        EMailMsgPusher messageManager = new EMailMsgPusher();
        messageManager.setEmailServerHost("mail.centit.com");
        messageManager.setEmailServerPort(25);
        messageManager.setEmailServerUser("alertmail2@centit.com");
        messageManager.setEmailServerPwd(AESSecurityUtils.decryptBase64String("LZhLhIlJ6gtIlUZ6/NassA==", ""));
        messageManager.setUserEmailSupport(new SystemUserEmailSupport());
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        //notificationCenter.initDummyMsgSenders();
        notificationCenter.setPlatformEnvironment(platformEnvironment);
        notificationCenter.registerMessageSender("email", messageManager);
        notificationCenter.appointDefaultSendType("email");
        return notificationCenter;
    }

    @Bean
    public UserUnitFilterCalcContextFactory userUnitFilterFactory() {
        return new SystemUserUnitCalcContextFactoryImpl();
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

}

