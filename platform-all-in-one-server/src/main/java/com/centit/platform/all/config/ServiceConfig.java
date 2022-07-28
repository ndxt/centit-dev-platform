package com.centit.platform.all.config;

import com.centit.fileserver.common.FileStore;
import com.centit.fileserver.common.FileTaskQueue;
import com.centit.fileserver.task.*;
import com.centit.fileserver.utils.OsFileStore;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.core.service.DataScopePowerManager;
import com.centit.framework.core.service.impl.DataScopePowerManagerImpl;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.adapter.UserUnitFilterCalcContextFactory;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.framework.system.config.SystemBeanConfig;
import com.centit.im.service.IntelligentRobotFactory;
import com.centit.im.service.impl.IntelligentRobotFactoryRpcImpl;
import com.centit.product.oa.EmailMessageSenderImpl;
import com.centit.search.document.FileDocument;
import com.centit.search.document.ObjectDocument;
import com.centit.search.service.ESServerConfig;
import com.centit.search.service.Impl.ESIndexer;
import com.centit.search.service.Impl.ESSearcher;
import com.centit.search.service.IndexerSearcherFactory;
import com.centit.search.service.Searcher;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.security.AESSecurityUtils;
import com.centit.workflow.service.impl.SystemUserUnitCalcContextFactoryImpl;
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
import redis.clients.jedis.JedisPool;

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
    public JedisPool jedisPool() {
        return new JedisPool(redisHost);
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


   /* @Bean
    public FileClientImpl fileClient() {
        FileClientImpl fileClient = new FileClientImpl();
        fileClient.init(fileserver, fileserver, "u0000000", "000000", fileserver);
        return fileClient;
    }

    @Bean
    public ClientAsFileStore fileStore(@Autowired FileClientImpl fileClient) {
        ClientAsFileStore fileStoreBean = new ClientAsFileStore();
        fileStoreBean.setFileClient(fileClient);
        return fileStoreBean;
    }*/

//    @Bean
//    public FileStore fileStore(){
//        String baseHome = env.getProperty("os.file.base.dir");
//        if(StringUtils.isBlank(baseHome)) {
//            baseHome = appHome + "/upload";
//        }
//        return new OsFileStore(baseHome);
//    }
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
    public SchedulerFactory schedulerFactory() {
        return new StdSchedulerFactory();
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
        //notificationCenter.initDummyMsgSenders();
        notificationCenter.setPlatformEnvironment(platformEnvironment);
        notificationCenter.registerMessageSender("email", messageManager);
        notificationCenter.appointDefaultSendType("email");
        return notificationCenter;
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

