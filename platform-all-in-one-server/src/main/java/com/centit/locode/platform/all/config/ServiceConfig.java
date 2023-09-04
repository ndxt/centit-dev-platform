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
import com.centit.framework.security.StandardPasswordEncoderImpl;
import com.centit.framework.system.config.SystemBeanConfig;
import com.centit.im.service.IntelligentRobotFactory;
import com.centit.im.service.impl.IntelligentRobotFactoryRpcImpl;
import com.centit.msgpusher.plugins.EMailMsgPusher;
import com.centit.msgpusher.plugins.SystemUserEmailSupport;
import com.centit.search.document.FileDocument;
import com.centit.search.document.ObjectDocument;
import com.centit.search.service.ESServerConfig;
import com.centit.search.service.Impl.ESIndexer;
import com.centit.search.service.Impl.ESSearcher;
import com.centit.search.service.IndexerSearcherFactory;
import com.centit.search.service.Searcher;
import com.centit.search.utils.ImagePdfTextExtractor;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.security.AESSecurityUtils;
import com.centit.workflow.service.impl.SystemUserUnitCalcContextFactoryImpl;
import io.lettuce.core.RedisClient;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
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
    excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
        value = org.springframework.stereotype.Controller.class))
@Import({
    JdbcConfig.class,
    SystemBeanConfig.class,
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
    public DataScopePowerManager queryDataScopeFilter() {
        return new DataScopePowerManagerImpl();
    }

    @Bean
    public IntelligentRobotFactory intelligentRobotFactory() {
        IntelligentRobotFactoryRpcImpl intelligentRobotFactory = new IntelligentRobotFactoryRpcImpl();
        return intelligentRobotFactory;
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
        @Autowired EncryptFileWithAesOpt encryptFileWithAesOpt,
        @Autowired DocumentIndexOpt documentIndexOpt) {
        FileOptTaskExecutor fileOptTaskExecutor = new FileOptTaskExecutor();
        fileOptTaskExecutor.setFileOptTaskQueue(fileOptTaskQueue);

        fileOptTaskExecutor.addFileOperator(saveFileOpt);
        fileOptTaskExecutor.addFileOperator(createPdfOpt);
        fileOptTaskExecutor.addFileOperator(pdfWatermarkOpt);
        fileOptTaskExecutor.addFileOperator(addThumbnailOpt);
        fileOptTaskExecutor.addFileOperator(zipFileOpt);
        fileOptTaskExecutor.addFileOperator(encryptFileWithAesOpt);
        fileOptTaskExecutor.addFileOperator(documentIndexOpt);
        return fileOptTaskExecutor;
    }

    @Bean
    public ImagePdfTextExtractor.OcrServerHost ocrServerHost() {
        ImagePdfTextExtractor.OcrServerHost ocrServer = ImagePdfTextExtractor.fetchDefaultOrrServer();
        String stemp = env.getProperty("ocr.server.author.url");
        if(StringUtils.isNotBlank(stemp)){
            ocrServer.setAuthorUrl(stemp);
        }
        stemp = env.getProperty("ocr.server.recognition.url");
        if(StringUtils.isNotBlank(stemp)){
            ocrServer.setOrcUrl(stemp);
        }
        stemp = env.getProperty("ocr.server.author.username");
        if(StringUtils.isNotBlank(stemp)){
            ocrServer.setUserName(stemp);
        }
        stemp = env.getProperty("ocr.server.author.password");
        if(StringUtils.isNotBlank(stemp)){
            ocrServer.setPassword(stemp);
        }
        return ocrServer;
    }

    @Bean
    public ESServerConfig esServerConfig() {
        return IndexerSearcherFactory.loadESServerConfigFormProperties(
            SysParametersUtils.loadProperties()
        );
    }

    @Bean(name = "esObjectIndexer")
    public ESIndexer esObjectIndexer(@Autowired ESServerConfig esServerConfig) {
        return IndexerSearcherFactory.obtainIndexer(
            esServerConfig, ObjectDocument.class);
    }

    @Bean
    public Searcher documentSearcher() {
        if (BooleanBaseOpt.castObjectToBoolean(
            env.getProperty("fulltext.index.enable"), false)) {
            return IndexerSearcherFactory.obtainSearcher(
                IndexerSearcherFactory.loadESServerConfigFormProperties(
                    SysParametersUtils.loadProperties()), FileDocument.class);
        }
        return null;
    }

    @Bean(name = "esObjectSearcher")
    public ESSearcher esObjectSearcher(@Autowired ESServerConfig esServerConfig) {
        return IndexerSearcherFactory.obtainSearcher(
            esServerConfig, ObjectDocument.class);
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

    @Bean
    public WxMpService wxMpService() {
        return new WxMpServiceImpl();
    }

}

