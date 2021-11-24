package com.centit.platform.all.config;

import com.centit.fileserver.task.FileOptTaskExecutor;
import com.centit.fileserver.utils.SystemTempFileUtils;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.system.service.OptLogManager;
import com.centit.framework.system.service.impl.DBPlatformEnvironment;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.quartz.JavaBeanJob;
import com.centit.support.quartz.QuartzJobUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.File;

/**
 * Created by codefan on 17-7-6.
 */
public class InstantiationServiceBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> , ApplicationContextAware {

    @Autowired
    protected NotificationCenter notificationCenter;

    @Autowired
    private OperationLogWriter optLogManager;

    @Autowired(required = false)
    private MessageSender innerMessageManager;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    protected SchedulerFactory schedulerFactory;

    @Autowired
    protected FileOptTaskExecutor fileOptTaskExecutor;

    @Autowired
    protected CodeRepositoryCache.EvictCacheExtOpt osInfoManager;

    @Value("${http.exception.notAsHttpError:false}")
    protected boolean httpExceptionNotAsHttpError;

    @Value("${app.support.tenant:false}")
    protected boolean supportTenant;

    private ApplicationContext applicationContext;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SystemTempFileUtils.setTempFileDirectory(
            SysParametersUtils.getTempHome() + File.separatorChar);
        DBPlatformEnvironment dbPlatformEnvironment = applicationContext.getBean("dbPlatformEnvironment", DBPlatformEnvironment.class);
        dbPlatformEnvironment.setSupportTenant(true);
        CodeRepositoryCache.setPlatformEnvironment(platformEnvironment);
        CodeRepositoryCache.setAllCacheFreshPeriod(CodeRepositoryCache.CACHE_FRESH_PERIOD_SECONDS);
        WebOptUtils.setExceptionNotAsHttpError(httpExceptionNotAsHttpError);
        WebOptUtils.setIsTenant(supportTenant);
        if (innerMessageManager != null) {
            notificationCenter.registerMessageSender("innerMsg", innerMessageManager);
            notificationCenter.appointDefaultSendType("innerMsg");
        }
        if (optLogManager != null) {
            OperationLogCenter.registerOperationLogWriter(optLogManager);
        }
        // 创建定时任务
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            QuartzJobUtils.registerJobType("bean", JavaBeanJob.class);
            QuartzJobUtils.createOrReplaceSimpleJob(scheduler, "fileOptJob",
                "default", "bean", 300,
                CollectionsOpt.createHashMap("bean", fileOptTaskExecutor,
                    "beanName", "fileOptTaskExecutor",
                    "methodName", "doFileOptJob"));
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
/*
        CodeRepositoryCache.setPlatformEnvironment(platformEnvironment);
        if(innerMessageManager!=null)
            notificationCenter.registerMessageSender("innerMsg", innerMessageManager);
        if(optLogManager!=null)
            OperationLogCenter.registerOperationLogWriter(optLogManager);*/

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
