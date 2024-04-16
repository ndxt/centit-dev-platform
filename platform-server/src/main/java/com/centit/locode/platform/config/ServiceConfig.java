package com.centit.locode.platform.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.centit.fileserver.client.FileClientImpl;
import com.centit.fileserver.client.FileInfoOptClient;
import com.centit.fileserver.common.FileInfoOpt;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.core.service.DataScopePowerManager;
import com.centit.framework.core.service.impl.DataScopePowerManagerImpl;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.security.CentitUserDetailsService;
import com.centit.framework.security.StandardPasswordEncoderImpl;
import com.centit.framework.security.UserDetailsServiceImpl;
import com.centit.msgpusher.plugins.EMailMsgPusher;
import com.centit.msgpusher.plugins.SystemUserEmailSupport;
import com.centit.search.service.ESServerConfig;
import com.centit.search.utils.ImagePdfTextExtractor;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.security.AESSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


/**
 * @author zhf
 */
@Configuration
@EnableAsync
@EnableScheduling
@PropertySource("classpath:system.properties")
@Import({
    SpringSecurityDaoConfig.class,
    JdbcConfig.class})
@ComponentScan(basePackages = "com.centit",
    excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
        value = org.springframework.stereotype.Controller.class))
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.server-addr}"))
@NacosPropertySources({@NacosPropertySource(dataId = "${nacos.system-dataid}",groupId = "CENTIT", autoRefreshed = true)})
public class ServiceConfig {

    @Value("${app.home:./}")
    private String appHome;
    @Value("${fileserver.url}")
    private String fileserver;

    @Autowired
    protected Environment environment;
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
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
        return new AutowiredAnnotationBeanPostProcessor();
    }

    @Bean
    public CentitUserDetailsService centitUserDetailsService(@Autowired PlatformEnvironment platformEnvironment) {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPlatformEnvironment(platformEnvironment);
        return userDetailsService;
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
    public NotificationCenter notificationCenter(@Autowired PlatformEnvironment platformEnvironment) {
        EMailMsgPusher messageManager = new EMailMsgPusher();
        messageManager.setEmailServerHost("mail.centit.com");
        messageManager.setEmailServerPort(25);
        messageManager.setEmailServerUser("alertmail2@centit.com");
        messageManager.setEmailServerPwd(AESSecurityUtils.decryptBase64String("LZhLhIlJ6gtIlUZ6/NassA==", ""));
        //messageManager.setTopUnit(dataOptContext.getTopUnit());
        messageManager.setUserEmailSupport(new SystemUserEmailSupport());
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
    public ImagePdfTextExtractor.OcrServerHost ocrServerHost() {
        ImagePdfTextExtractor.OcrServerHost ocrServer = ImagePdfTextExtractor.fetchDefaultOrrServer();
        String ocrServerHost = environment.getProperty("ocr.server.url");
        if(StringUtils.isNotBlank(ocrServerHost)) {
            String stemp = environment.getProperty("ocr.server.auth.api");
            if (StringUtils.isNotBlank(stemp)) {
                ocrServer.setAuthorUrl(ocrServerHost + stemp);
            }
            stemp = environment.getProperty("ocr.server.ocr.api");
            if (StringUtils.isNotBlank(stemp)) {
                ocrServer.setOrcUrl(ocrServerHost + stemp);
            }
            stemp = environment.getProperty("ocr.server.auth.username");
            if (StringUtils.isNotBlank(stemp)) {
                ocrServer.setUserName(stemp);
            }
            stemp = environment.getProperty("ocr.server.auth.password");
            if (StringUtils.isNotBlank(stemp)) {
                ocrServer.setPassword(stemp);
            }
        }
        return ocrServer;
    }

    @Bean
    public ESServerConfig esServerConfig() {
        ESServerConfig config = new ESServerConfig();
        config.setServerHostIp(environment.getProperty("elasticsearch.server.ip"));
        config.setServerHostPort(environment.getProperty("elasticsearch.server.port"));
        config.setUsername(environment.getProperty("elasticsearch.server.username"));
        config.setPassword(environment.getProperty("elasticsearch.server.password"));
        config.setClusterName(environment.getProperty("elasticsearch.server.cluster"));
        config.setOsId(environment.getProperty("elasticsearch.osId"));
        config.setMinScore(NumberBaseOpt.parseFloat(environment.getProperty("elasticsearch.filter.minScore"), 0.5f));
        return config;
    }

    @Bean
    public FileInfoOpt fileInfoOpt() {
        FileClientImpl fileClient = new FileClientImpl();
        fileClient.init(fileserver, fileserver, "u0000000", "000000", fileserver);
        FileInfoOptClient fileStoreBean = new FileInfoOptClient();
        fileStoreBean.setFileClient(fileClient);
        return fileStoreBean;
    }
}
