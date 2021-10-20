package com.centit.platform.config;


import com.centit.fileserver.common.FileStore;
import com.centit.fileserver.common.FileStoreContext;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.product.oa.EmailMessageSenderImpl;
import com.centit.support.security.AESSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;


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
    @Value("${fileserver.url}")
    private String fileserver;
    /**
     * 这个bean必须要有
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
        messageManager.setUserPassword(AESSecurityUtils.decryptBase64String("LZhLhIlJ6gtIlUZ6/NassA==",""));
        messageManager.setServerEmail("alertmail2@centit.com");

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

//    @Bean
//    public FileClientImpl fileClient() {
//        FileClientImpl fileClient = new FileClientImpl();
//        fileClient.init(fileserver,fileserver,"u0000000", "000000",fileserver);
//        return fileClient;
//    }
//    @Bean
//    public ClientAsFileStore fileStore(@Autowired FileClientImpl fileClient){
//        ClientAsFileStore fileStoreBean = new ClientAsFileStore();
//        fileStoreBean.setFileClient(fileClient);
//        return fileStoreBean;
//    }
//    @Bean
//    public FileStore fileStore(){
//        String baseHome = appHome + "/upload";
//        return new OsFileStore(baseHome);
//    }

    @Resource
    FileStore fileStore;

    @Bean
    public FileStoreContext fileStoreContext(){
        return new  FileStoreContext(fileStore);
    }
}
